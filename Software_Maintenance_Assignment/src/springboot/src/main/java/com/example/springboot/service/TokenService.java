package com.example.springboot.service;

// import com.example.springboot.domain.entity.RefreshToken;
import com.example.springboot.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TokenService - Manages JWT and refresh tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    public void revokeToken(String token) {
        try {
            refreshTokenRepository.revokeToken(token);
            log.info("Token revoked successfully");
        } catch (Exception e) {
            log.error("Failed to revoke token: {}", e.getMessage());
        }
    }
}
