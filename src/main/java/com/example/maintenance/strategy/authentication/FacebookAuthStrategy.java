package com.example.maintenance.strategy.authentication;

import com.example.maintenance.adapter.facebook.FacebookAuthAdapter;
import com.example.maintenance.adapter.recaptcha.RecaptchaAdapter;
import com.example.maintenance.domain.model.User;
import com.example.maintenance.domain.enums.AuthProvider;
import com.example.maintenance.domain.enums.Role;
import com.example.maintenance.dto.request.SocialLoginRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.dto.response.JWTResponseDTO;
import com.example.maintenance.exception.InvalidCredentialsException;
import com.example.maintenance.repository.UserRepository;
import com.example.maintenance.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * FacebookAuthStrategy - Handles Facebook OAuth authentication
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FacebookAuthStrategy implements AuthStrategy {
    
    private final FacebookAuthAdapter facebookAuthAdapter;
    private final RecaptchaAdapter recaptchaAdapter;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public AuthResponseDTO authenticate(Object credentials, String recaptchaToken) {
        try {
            log.info("=== Starting Facebook OAuth Authentication ===");
            
            if (recaptchaToken != null && !recaptchaToken.isEmpty()) {
                if (!recaptchaAdapter.validateRecaptcha(recaptchaToken)) {
                    throw new InvalidCredentialsException("reCAPTCHA validation failed");
                }
            }
            
            SocialLoginRequestDTO socialLogin = (SocialLoginRequestDTO) credentials;
            
            Map<String, String> fbUserInfo = facebookAuthAdapter.validateTokenAndGetUserInfo(
                socialLogin.getAccessToken()
            );
            
            String email = fbUserInfo.get("email");
            String providerId = fbUserInfo.get("id");
            String fullName = fbUserInfo.get("name");
            
            User user = userRepository.findByProviderIdAndAuthProvider(providerId, AuthProvider.FACEBOOK)
                .orElseGet(() -> createFacebookUser(email, fullName, providerId));
            
            if (user.getAccountLocked()) {
                throw new InvalidCredentialsException("Account is locked");
            }

            // Check MFA
            if (Boolean.TRUE.equals(user.getMfaEnabled())) {
                log.info("MFA required for Facebook user: {}", email);
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
            
            user.setLastLoginAt(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            
            JWTResponseDTO tokens = jwtTokenProvider.generateTokens(
                user.getCustId(), user.getEmail(), user.getRole()
            );
            
            return AuthResponseDTO.builder()
                .success(true)
                .message("Facebook login successful")
                .tokens(tokens)
                .requiresMfa(false)
                .build();
                
        } catch (Exception e) {
            log.error("Facebook auth failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Facebook authentication failed: " + e.getMessage());
        }
    }
    
    private User createFacebookUser(String email, String fullName, String providerId) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidCredentialsException("Email already exists with a different provider.");
        }
        
        User newUser = User.builder()
            .email(email)
            .name(fullName) 
            .custPassword(passwordEncoder.encode(UUID.randomUUID().toString())) 
            .authProvider(AuthProvider.FACEBOOK)
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
        return "FACEBOOK_OAUTH_AUTH";
    }
}