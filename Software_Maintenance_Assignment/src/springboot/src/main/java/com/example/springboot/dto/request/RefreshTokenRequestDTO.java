package com.example.springboot.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Refresh Token Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequestDTO {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
