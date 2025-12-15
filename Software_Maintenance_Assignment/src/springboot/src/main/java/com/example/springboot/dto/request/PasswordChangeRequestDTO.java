package com.example.springboot.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Password Change Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeRequestDTO {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
