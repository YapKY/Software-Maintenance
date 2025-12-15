package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
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