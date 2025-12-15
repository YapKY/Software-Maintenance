package com.example.springboot.strategy.authentication;

import com.example.springboot.adapter.google.GoogleAuthAdapter;
import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.model.User;
import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.SocialLoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * GoogleAuthStrategy - Handles Google OAuth authentication
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleAuthStrategy implements AuthStrategy {
    
    private final GoogleAuthAdapter googleAuthAdapter;
    private final RecaptchaAdapter recaptchaAdapter;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public AuthResponseDTO authenticate(Object credentials, String recaptchaToken) {
        try {
            log.info("=== Starting Google OAuth Authentication ===");
            
            // Validate reCAPTCHA
            if (recaptchaToken != null && !recaptchaToken.isEmpty()) {
                 if (!recaptchaAdapter.validateRecaptcha(recaptchaToken)) {
                    throw new InvalidCredentialsException("reCAPTCHA validation failed");
                }
            }
            
            SocialLoginRequestDTO socialLogin = (SocialLoginRequestDTO) credentials;
            
            // Validate Google token
            Map<String, String> googleUserInfo = googleAuthAdapter.validateTokenAndGetUserInfo(
                socialLogin.getAccessToken()
            );
            
            String email = googleUserInfo.get("email");
            String providerId = googleUserInfo.get("id");
            String fullName = googleUserInfo.get("name");
            
            // Find or create user
            User user = userRepository.findByProviderIdAndAuthProvider(providerId, AuthProvider.GOOGLE)
                .orElseGet(() -> createGoogleUser(email, fullName, providerId));
            
            if (user.getAccountLocked()) {
                throw new InvalidCredentialsException("Account is locked");
            }

            // Check MFA
            if (Boolean.TRUE.equals(user.getMfaEnabled())) {
                log.info("MFA required for Google user: {}", email);
                String mfaSessionToken = jwtTokenProvider.generateMFASessionToken(
                    user.getCustId(), user.getEmail(), user.getRole()
                );
                
                return AuthResponseDTO.builder()
                    .success(false)
                    .message("MFA code required")
                    .requiresMfa(true)
                    .mfaSessionToken(mfaSessionToken)
                    .email(user.getEmail())
                    .build();
            }
            
            // Update stats
            user.setLastLoginAt(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            
            // Generate full tokens
            JWTResponseDTO tokens = jwtTokenProvider.generateTokens(
                user.getCustId(), user.getEmail(), user.getRole()
            );
            
            return AuthResponseDTO.builder()
                .success(true)
                .message("Google login successful")
                .tokens(tokens)
                .requiresMfa(false)
                .build();
                
        } catch (Exception e) {
            log.error("Google auth failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Google authentication failed: " + e.getMessage());
        }
    }
    
    private User createGoogleUser(String email, String fullName, String providerId) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidCredentialsException("Email already exists with a different provider.");
        }
        
        User newUser = User.builder()
            .email(email)
            .name(fullName) 
            .custPassword(passwordEncoder.encode(UUID.randomUUID().toString())) 
            .authProvider(AuthProvider.GOOGLE)
            .providerId(providerId)
            // [FIX] Add placeholders for mandatory fields
            .custIcNo("Not Provided") 
            .phoneNumber("Not Provided")
            .gender(null) // Handled safely by UserRepository now
            .role(Role.USER)
            .emailVerified(true)
            .mfaEnabled(false)
            .build();
        
        return userRepository.save(newUser);
    }
    
    @Override
    public String getStrategyName() {
        return "GOOGLE_OAUTH_AUTH";
    }
}