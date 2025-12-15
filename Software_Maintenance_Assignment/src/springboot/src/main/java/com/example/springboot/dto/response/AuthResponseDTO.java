package com.example.springboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auth Response DTO - Generic authentication response
 * Updated to include email for MFA auto-fill
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    
    private Boolean success;
    private String message;
    private JWTResponseDTO tokens;
    private Boolean requiresMfa;
    private String mfaSessionToken; // Temporary token if MFA required
    private String email; // [FIX] Added to support auto-filling email on frontend
}