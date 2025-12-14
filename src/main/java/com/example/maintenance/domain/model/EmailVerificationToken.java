package com.example.maintenance.domain.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * EmailVerificationToken - Token for email verification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {
    
    private String id;
    private String token;
    private String userId;
    private String email;
    private LocalDateTime expiryDate;

    @Builder.Default
    private Boolean used = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
