package com.example.springboot.model;

import com.example.springboot.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Superadmin Entity - Plain POJO without JPA annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Superadmin {
    
    private String id;
    private String email;
    private String password;
    private String fullName;
    @Builder.Default
    private Role role = Role.SUPERADMIN;

    @Builder.Default
    private Boolean mfaEnabled = true;

    @Builder.Default
    private Boolean accountLocked = false;
    private LocalDateTime lastLoginAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}