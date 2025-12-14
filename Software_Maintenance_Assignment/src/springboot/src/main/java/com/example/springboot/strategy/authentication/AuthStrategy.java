package com.example.springboot.strategy.authentication;

import com.example.springboot.dto.response.AuthResponseDTO;

/**
 * AuthStrategy Interface - Strategy Pattern for different authentication methods
 * Implementations: EmailAuthStrategy, GoogleAuthStrategy, FacebookAuthStrategy
 */
public interface AuthStrategy {
    
    /**
     * Authenticate user with provided credentials
     * @param credentials Authentication credentials (email/password or social token)
     * @param recaptchaToken reCAPTCHA validation token
     * @return AuthResponseDTO containing JWT tokens or MFA challenge
     */
    AuthResponseDTO authenticate(Object credentials, String recaptchaToken);
    
    /**
     * Get the strategy name for logging/debugging
     */
    String getStrategyName();
}

