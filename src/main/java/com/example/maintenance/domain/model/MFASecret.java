package com.example.maintenance.domain.model;

import com.example.maintenance.domain.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MFASecret Entity - Plain POJO without JPA annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MFASecret {
    
    private String id;
    private String userId;
    private Role userRole;
    private String secret;
    private String backupCodes;
    
    @Builder.Default
    private Boolean verified = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
