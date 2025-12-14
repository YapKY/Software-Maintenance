package com.example.maintenance.strategy.registration;

import com.example.maintenance.dto.response.AuthResponseDTO;

/**
 * RegisterStrategy Interface - Strategy Pattern for different registration methods
 * Implementations: EmailRegisterStrategy, AdminRegisterStrategy
 */
public interface RegisterStrategy {
    
    /**
     * Register a new account
     * @param registrationData Registration details
     * @param recaptchaToken reCAPTCHA validation token
     * @return AuthResponseDTO with registration result
     */
    AuthResponseDTO register(Object registrationData, String recaptchaToken);
    
    /**
     * Get the strategy name for logging/debugging
     */
    String getStrategyName();
}
