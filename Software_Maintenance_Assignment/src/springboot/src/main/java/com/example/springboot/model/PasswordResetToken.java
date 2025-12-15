package com.example.springboot.model;

import com.example.springboot.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PasswordResetToken - Token for password reset flow
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    
    private String id;
    private String token;
    private String userId;
    private Role userRole;
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