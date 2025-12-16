package com.example.springboot.service;

import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.*;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.model.RefreshToken;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.factory.AuthStrategyFactory;
import com.example.springboot.security.jwt.JwtTokenProvider;
import com.example.springboot.strategy.authentication.AuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * AuthExecutionService - Orchestrates authentication
 * Updated to handle MFA verification for all providers
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthExecutionService {
    
    private final AuthStrategyFactory authStrategyFactory;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MFAService mfaService;
    
    public AuthResponseDTO authenticateWithEmail(LoginRequestDTO request) {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(AuthProvider.EMAIL);
        return strategy.authenticate(request, request.getRecaptchaToken());
    }

    public AuthResponseDTO authenticateWithSocial(SocialLoginRequestDTO request, AuthProvider provider) {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(provider);
        return strategy.authenticate(request, request.getRecaptchaToken());
    }
    
    /**
     * Refresh Token Flow
     * Validates old token, revokes it (rotation), and issues a new pair.
     */
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        try {
            String requestToken = request.getRefreshToken();
            
            // 1. Validate format/signature
            if (!jwtTokenProvider.validateToken(requestToken)) {
                throw new InvalidCredentialsException("Invalid refresh token signature");
            }
            
            // 2. Check DB for existence and revocation
            RefreshToken storedToken = tokenService.findByToken(requestToken)
                .orElseThrow(() -> new InvalidCredentialsException("Refresh token not found in database"));
                
            if (storedToken.getRevoked()) {
                throw new InvalidCredentialsException("Refresh token has been revoked");
            }

            if (storedToken.isExpired()) {
                 throw new InvalidCredentialsException("Refresh token has expired");
            }
            
            // 3. Extract user info
            String userId = jwtTokenProvider.getUserIdFromToken(requestToken);
            Role role = jwtTokenProvider.getRoleFromToken(requestToken);
            String email = jwtTokenProvider.getEmailFromToken(requestToken);
            
            // 4. Revoke old token (Rotation)
            tokenService.revokeToken(requestToken);
            
            // 5. Generate new tokens
            JWTResponseDTO tokens = jwtTokenProvider.generateTokens(userId, email, role);
            
            // 6. Save new refresh token
            RefreshToken newRefreshToken = RefreshToken.builder()
                .token(tokens.getRefreshToken())
                .userId(userId)
                .userRole(role)
                .expiryDate(LocalDateTime.now().plusDays(7)) // Default 7 days matching standard config
                .build();
            tokenService.save(newRefreshToken);
            
            return AuthResponseDTO.builder()
                .success(true)
                .message("Token refreshed successfully")
                .tokens(tokens)
                .build();
                
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Failed to refresh token: " + e.getMessage());
        }
    }

    /**
     * Verify MFA code and issue final tokens
     */
    public AuthResponseDTO verifyMFA(MFARequestDTO request) {
        try {
            log.info("Executing MFA verification for: {}", request.getEmail());
            
            // 1. Validate Session Token
            if (!jwtTokenProvider.validateToken(request.getSessionToken())) {
                throw new InvalidCredentialsException("Invalid or expired MFA session");
            }
            
            // 2. Extract User Details from Session Token
            String userId = jwtTokenProvider.getUserIdFromToken(request.getSessionToken());
            Role role = jwtTokenProvider.getRoleFromToken(request.getSessionToken());
            
            // 3. Validate MFA Code
            if (!mfaService.validateMFACode(userId, role, request.getCode())) {
                throw new InvalidCredentialsException("Invalid MFA code");
            }
            
            // 4. Generate Final Access/Refresh Tokens
            JWTResponseDTO tokens = jwtTokenProvider.generateTokens(userId, request.getEmail(), role);

             // 5. Save Refresh Token for Stateful Management
            RefreshToken newRefreshToken = RefreshToken.builder()
                .token(tokens.getRefreshToken())
                .userId(userId)
                .userRole(role)
                .expiryDate(LocalDateTime.now().plusDays(7)) 
                .build();
            tokenService.save(newRefreshToken);
            
            log.info("MFA verification successful for user: {}", userId);
            
            return AuthResponseDTO.builder()
                .success(true)
                .message("Authentication successful")
                .tokens(tokens)
                .requiresMfa(false)
                .build();
                
        } catch (Exception e) {
            log.error("MFA verification failed: {}", e.getMessage());
            throw new InvalidCredentialsException("MFA verification failed: " + e.getMessage());
        }
    }
    
    public void logout(String jwt) {
        try {
            tokenService.revokeToken(jwt);
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
        }
    }
}