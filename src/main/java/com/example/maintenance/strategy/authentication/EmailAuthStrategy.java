package com.example.maintenance.strategy.authentication;

import com.example.maintenance.adapter.recaptcha.RecaptchaAdapter;
import com.example.maintenance.decorator.AuthServiceComponent;
import com.example.maintenance.domain.model.User;
import com.example.maintenance.domain.model.Admin;
import com.example.maintenance.domain.model.Superadmin;
import com.example.maintenance.domain.enums.Role;
import com.example.maintenance.dto.request.LoginRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.dto.response.JWTResponseDTO;
import com.example.maintenance.exception.InvalidCredentialsException;
import com.example.maintenance.exception.RateLimitExceededException;
import com.example.maintenance.repository.UserRepository;
import com.example.maintenance.repository.AdminRepository;
import com.example.maintenance.repository.SuperadminRepository;
import com.example.maintenance.security.jwt.JwtTokenProvider;
import com.example.maintenance.service.MFAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * EmailAuthStrategy - Handles traditional email/password authentication
 * Uses DECORATOR PATTERN for rate limiting (AuthServiceComponent)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAuthStrategy implements AuthStrategy {
    
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MFAService mfaService;
    private final RecaptchaAdapter recaptchaAdapter;
    private final AuthServiceComponent authService; // Uses Decorator Pattern
    
    @Override
    public AuthResponseDTO authenticate(Object credentials, String recaptchaToken) {
        try {
            // Validate reCAPTCHA
            if (!recaptchaAdapter.validateRecaptcha(recaptchaToken)) {
                throw new InvalidCredentialsException("reCAPTCHA validation failed");
            }
            
            LoginRequestDTO loginRequest = (LoginRequestDTO) credentials;
            log.info("Email auth attempt for: {}", loginRequest.getEmail());
            
            // Use decorated auth service (includes rate limiting)
            return authService.performAuthentication(loginRequest);
            
        } catch (ClassCastException e) {
            log.error("Invalid credentials type for EmailAuthStrategy");
            throw new InvalidCredentialsException("Invalid credentials format");
        } catch (InvalidCredentialsException | RateLimitExceededException e) {
            // Propagate known authentication and rate limit exceptions
            throw e;
        } catch (Exception e) {
            log.error("Email authentication failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Authentication failed");
        }
    }
    
    @Override
    public String getStrategyName() {
        return "EMAIL_PASSWORD_AUTH";
    }
}