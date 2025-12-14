package com.example.maintenance.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * reCAPTCHA Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecaptchaRequestDTO {
    
    @NotBlank(message = "reCAPTCHA token is required")
    private String recaptchaToken;
}
