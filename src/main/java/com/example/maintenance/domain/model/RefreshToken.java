package com.example.maintenance.domain.model;

import com.example.maintenance.domain.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * RefreshToken Entity - Plain POJO without JPA annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    
    private String id;
    private String token;
    private String userId;
    private Role userRole;
    private LocalDateTime expiryDate;
    @Builder.Default
    private Boolean revoked = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}