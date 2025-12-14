package com.example.springboot.security.mfa;

import com.example.springboot.service.MFAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.example.springboot.enums.Role;

/**
 * MFAValidator - Validates MFA codes and handles MFA-related operations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MFAValidator {
    
    private final MFAService mfaService;
    
    /**
     * Validate MFA code for a user
     */
    public boolean validateMFACode(String userId, Role userRole, String code) {
        try {
            return mfaService.validateMFACode(userId, userRole, code);
        } catch (Exception e) {
            log.error("MFA validation error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if MFA is enabled for a user
     */
    public boolean isMFAEnabled(String userId, Role userRole) {
        try {
            return mfaService.getMFAStatus(userId, userRole).getMfaEnabled();
        } catch (Exception e) {
            log.error("Error checking MFA status: {}", e.getMessage());
            return false;
        }
    }
}
