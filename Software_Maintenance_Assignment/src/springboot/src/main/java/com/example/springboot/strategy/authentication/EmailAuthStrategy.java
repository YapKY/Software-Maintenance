package com.example.springboot.strategy.authentication;

import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.decorator.AuthServiceComponent;
import com.example.springboot.model.User;
import com.example.springboot.model.Admin;
import com.example.springboot.model.Superadmin;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.RateLimitExceededException;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.security.jwt.JwtTokenProvider;
import com.example.springboot.service.MFAService;
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