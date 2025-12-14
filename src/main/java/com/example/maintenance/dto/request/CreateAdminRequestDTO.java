package com.example.maintenance.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Create Admin Request DTO - Used by Superadmin to create admin accounts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAdminRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;
    
    private String phoneNumber;
    
    @Builder.Default
    private Boolean mfaEnabled = true; // Default to true for admins
}