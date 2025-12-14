package com.example.maintenance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MFA Request DTO - For MFA verification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MFARequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "MFA code is required")
    @Pattern(regexp = "^\\d{6}$", message = "MFA code must be 6 digits")
    private String code;
    
    @NotBlank(message = "Session token is required")
    private String sessionToken; // Temporary token from initial auth
}