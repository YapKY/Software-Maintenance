package com.example.maintenance.dto.response;

import com.example.maintenance.domain.enums.Role;
import com.example.maintenance.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    
    private String id;        // Maps to custId
    private String email;
    private String fullName;  // Maps to name
    private String custIcNo;  // NEW
    private Gender gender;    // NEW
    private String phoneNumber;
    private Role role;
    private Boolean mfaEnabled;
    private Boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}