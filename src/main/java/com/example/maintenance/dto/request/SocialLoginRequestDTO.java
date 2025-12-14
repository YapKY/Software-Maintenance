package com.example.maintenance.dto.request;

import com.example.maintenance.domain.enums.AuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Social Login Request DTO - For Google/Facebook login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginRequestDTO {
    
    @NotNull(message = "Auth provider is required")
    private AuthProvider provider;
    
    @NotBlank(message = "Access token is required")
    private String accessToken;
    
    @NotBlank(message = "reCAPTCHA token is required")
    private String recaptchaToken;
}