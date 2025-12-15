package com.example.springboot.service;

import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.*;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.factory.AuthStrategyFactory;
import com.example.springboot.security.jwt.JwtTokenProvider;
import com.example.springboot.strategy.authentication.AuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final JwtTokenProvider jwtTokenProvider; // [FIX] Added dependency
    private final MFAService mfaService;             // [FIX] Added dependency
    
    // ... authenticateWithEmail and authenticateWithSocial methods remain the same ...
    public AuthResponseDTO authenticateWithEmail(LoginRequestDTO request) {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(AuthProvider.EMAIL);
        return strategy.authenticate(request, request.getRecaptchaToken());
    }

    public AuthResponseDTO authenticateWithSocial(SocialLoginRequestDTO request, AuthProvider provider) {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(provider);
        return strategy.authenticate(request, request.getRecaptchaToken());
    }
    
    /**
     * Verify MFA code and issue final tokens
     * Used for both Email (optional flow) and Social Login (mandatory flow for 2FA)
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