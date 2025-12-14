package com.example.maintenance.dto.response;

import com.example.maintenance.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Superadmin Profile Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperadminProfileDTO {
    
    private String id;
    private String email;
    private String fullName;
    private Role role;
    private Boolean mfaEnabled;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private Integer totalAdminsCreated;
}
