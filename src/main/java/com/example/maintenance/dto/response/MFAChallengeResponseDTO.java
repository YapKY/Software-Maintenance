package com.example.maintenance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MFA Challenge Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MFAChallengeResponseDTO {
    
    private Boolean mfaRequired;
    private String sessionToken;
    private String message;
    private String qrCodeUrl; // For MFA setup
}
