package com.example.springboot.model;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Gender; // Import Gender
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    private String custId;        // Renamed from id
    private String email;         // Unchanged
    private String custPassword;  // Renamed from password
    private String name;          // Renamed from fullName
    private String custIcNo;      // New Field
    private Gender gender;        // New Field
    private String phoneNumber;   // Unchanged
    
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private AuthProvider authProvider = AuthProvider.EMAIL;
    private String providerId;

    @Builder.Default
    private Boolean mfaEnabled = false;

    @Builder.Default
    private Boolean emailVerified = false;

    @Builder.Default
    private Boolean accountLocked = false;

    @Builder.Default
    private Integer failedLoginAttempts = 0;
    private LocalDateTime lastLoginAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}