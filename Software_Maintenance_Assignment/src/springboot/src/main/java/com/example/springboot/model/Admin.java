package com.example.springboot.model;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Admin (Staff) Entity - Mapped to "staff" collection
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    
    private String staffId;       // Renamed from id
    private String email;
    private String staffPass;     // Renamed from password
    private String name;          // Renamed from fullName
    private String phoneNumber;
    
    // New Fields
    private Gender gender;
    private String position;

    @Builder.Default
    private Role role = Role.ADMIN;

    @Builder.Default
    private Boolean mfaEnabled = false;

    @Builder.Default
    private Boolean accountLocked = false;

    @Builder.Default
    private Integer failedLoginAttempts = 0;
    private String createdBy;
    private LocalDateTime lastLoginAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}