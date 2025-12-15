package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT Response DTO - Contains access and refresh tokens
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTResponseDTO {
    
    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn; // Seconds until expiration
    private Role role;
    private String email;
    private String fullName;
}
