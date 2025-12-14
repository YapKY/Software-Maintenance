package com.example.maintenance.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Login Request DTO - For email/password login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "reCAPTCHA token is required")
    private String recaptchaToken;
    
    private String mfaCode; // Optional, required if MFA is enabled
}