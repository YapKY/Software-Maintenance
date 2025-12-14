package com.example.maintenance.controller;

import com.example.maintenance.domain.model.User;
import com.example.maintenance.repository.UserRepository;
import com.example.maintenance.service.EmailService;
import com.example.maintenance.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * EmailVerificationController - Handles email verification
 */
@Slf4j
@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class EmailVerificationController {
    
    private final EmailVerificationTokenService tokenService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    /**
     * Verify email with token
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String token) {
        try {
            log.info("Email verification request with token");
            
            Map<String, Object> response = new HashMap<>();
            
            // Validate token
            if (!tokenService.validateToken(token)) {
                response.put("success", false);
                response.put("message", "Invalid or expired verification token");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Get user ID from token
            Optional<String> userIdOpt = tokenService.getUserIdFromToken(token);
            if (userIdOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update user email verified status
            String userId = userIdOpt.get();
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = userOpt.get();
            user.setEmailVerified(true);
            userRepository.save(user);
            
            // Mark token as used
            tokenService.markTokenAsUsed(token);
            
            // Send welcome email
            emailService.sendWelcomeEmail(user);
            
            log.info("Email verified successfully for user: {}", user.getEmail());
            
            response.put("success", true);
            response.put("message", "Email verified successfully! You can now login.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Email verification failed: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Email verification failed");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Resend verification email
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerificationEmail(@RequestParam String email) {
        try {
            log.info("Resend verification email request for: {}", email);
            
            Map<String, Object> response = new HashMap<>();
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = userOpt.get();
            
            if (user.getEmailVerified()) {
                response.put("success", false);
                response.put("message", "Email already verified");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generate new verification token
            String verificationToken = tokenService.createVerificationToken(
                user.getCustId(), 
                user.getEmail()
            );
            
            // Send verification email
            emailService.sendVerificationEmail(user, verificationToken);
            
            log.info("Verification email resent to: {}", email);
            
            response.put("success", true);
            response.put("message", "Verification email sent! Please check your inbox.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to resend verification email: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send verification email");
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
