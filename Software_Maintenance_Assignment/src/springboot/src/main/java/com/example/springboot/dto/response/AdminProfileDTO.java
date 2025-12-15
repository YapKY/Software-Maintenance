package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Admin (Staff) Profile Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProfileDTO {
    
    private String staffId;   // Renamed
    private String email;
    private String name;      // Renamed
    private String phoneNumber;
    private Gender gender;    // New
    private String position;  // New
    private Role role;
    private Boolean mfaEnabled;
    private String createdBy;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}