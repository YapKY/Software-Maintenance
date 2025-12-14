package com.example.maintenance.controller;

import com.example.maintenance.domain.enums.AuthProvider;
import com.example.maintenance.dto.request.LoginRequestDTO;
import com.example.maintenance.dto.request.MFARequestDTO;
import com.example.maintenance.dto.request.PasswordResetConfirmRequestDTO;
import com.example.maintenance.dto.request.PasswordResetRequestDTO;
import com.example.maintenance.dto.request.SocialLoginRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.dto.response.ErrorResponseDTO;
import com.example.maintenance.dto.response.MessageResponseDTO;
import com.example.maintenance.service.AuthExecutionService;
import com.example.maintenance.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * AuthController - Handles all authentication requests
 * Uses Strategy + Factory patterns via AuthExecutionService
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Validated
public class AuthController {
    
    private final AuthExecutionService authExecutionService;
    private final PasswordResetService passwordResetService;
    
    /**
     * Email/Password Login - Uses EmailAuthStrategy
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            log.info("Login request received for: {}", request.getEmail());
            AuthResponseDTO response = authExecutionService.authenticateWithEmail(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    /**
     * Google OAuth Login - Uses GoogleAuthStrategy
     */
    @PostMapping("/login/google")
    public ResponseEntity<AuthResponseDTO> loginWithGoogle(
        @Valid @RequestBody SocialLoginRequestDTO request
    ) {
        try {
            log.info("Google login request received");
            AuthResponseDTO response = authExecutionService.authenticateWithSocial(
                request, AuthProvider.GOOGLE
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Google login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    /**
     * Facebook OAuth Login - Uses FacebookAuthStrategy
     */
    @PostMapping("/login/facebook")
    public ResponseEntity<AuthResponseDTO> loginWithFacebook(
        @Valid @RequestBody SocialLoginRequestDTO request
    ) {
        try {
            log.info("Facebook login request received");
            AuthResponseDTO response = authExecutionService.authenticateWithSocial(
                request, AuthProvider.FACEBOOK
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Facebook login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    /**
     * MFA Verification
     */
    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponseDTO> verifyMFA(
        @Valid @RequestBody MFARequestDTO request
    ) {
        try {
            log.info("MFA verification request for: {}", request.getEmail());
            AuthResponseDTO response = authExecutionService.verifyMFA(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("MFA verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    /**
     * Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer "
            authExecutionService.logout(jwt);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Forgot Password - Step 1: Send Email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(
        @Valid @RequestBody PasswordResetRequestDTO request
    ) {
        try {
            log.info("Forgot password request for: {}", request.getEmail());
            MessageResponseDTO response = passwordResetService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Forgot password failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MessageResponseDTO.builder()
                .success(false)
                .message(e.getMessage())
                .build());
        }
    }

    /**
     * Reset Password - Step 2: Update Password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(
        @Valid @RequestBody PasswordResetConfirmRequestDTO request
    ) {
        try {
            log.info("Password reset confirmation request");
            MessageResponseDTO response = passwordResetService.confirmPasswordReset(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MessageResponseDTO.builder()
                .success(false)
                .message(e.getMessage())
                .build());
        }
    }
}
