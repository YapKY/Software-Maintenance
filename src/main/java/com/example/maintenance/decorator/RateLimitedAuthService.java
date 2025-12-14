package com.example.maintenance.decorator;

import com.example.maintenance.dto.request.LoginRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.exception.RateLimitExceededException;
import com.example.maintenance.security.ratelimit.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * RateLimitedAuthService - DECORATOR PATTERN
 * Wraps AuthServiceImpl with rate limiting functionality
 * This is the primary AuthServiceComponent bean used throughout the application
 */
@Slf4j
@Service("authService")
public class RateLimitedAuthService implements AuthServiceComponent {
    
    private final AuthServiceComponent baseAuthService;
    private final LoginAttemptService loginAttemptService;
    
    public RateLimitedAuthService(
        @Qualifier("baseAuthService") AuthServiceComponent baseAuthService,
        LoginAttemptService loginAttemptService
    ) {
        this.baseAuthService = baseAuthService;
        this.loginAttemptService = loginAttemptService;
    }
    
    @Override
    public AuthResponseDTO performAuthentication(LoginRequestDTO loginRequest) {
        String email = loginRequest.getEmail();
        
        // DECORATOR: Add rate limiting before calling base service
        if (loginAttemptService.isBlocked(email)) {
            log.warn("Rate limit exceeded for: {}", email);
            throw new RateLimitExceededException(
                "Too many login attempts. Please try again later."
            );
        }
        
        try {
            // Call the base authentication service
            AuthResponseDTO response = baseAuthService.performAuthentication(loginRequest);
            
            // On successful auth, clear rate limit
            if (response.getSuccess()) {
                loginAttemptService.loginSucceeded(email);
            }
            
            return response;
            
        } catch (Exception e) {
            // On failed auth, record attempt for rate limiting
            loginAttemptService.loginFailed(email);
            throw e;
        }
    }
}
