package com.example.maintenance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MFA Status DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MFAStatusDTO {
    
    private Boolean mfaEnabled;
    private String secret; // Only sent during setup
    private String qrCodeUrl; // Only sent during setup
    private String[] backupCodes; // Only sent during setup
}
