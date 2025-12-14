package com.example.maintenance.adapter.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * FirebaseAdapter - ADAPTER PATTERN
 * Wraps Firebase SDK for email verification and user management
 * IMPORTANT: This is a Spring-managed component, NOT a Singleton
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAdapter {
    
    private final FirebaseAuth firebaseAuth;

    /**
     * Create a new user in Firebase Auth
     * @param email User email
     * @param password Raw password
     * @param fullName User display name
     * @return The Firebase UID of the created user
     */
    public String createUser(String email, String password, String fullName) {
        try {
            log.info("Creating user in Firebase Auth: {}", email);
            
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setDisplayName(fullName)
                .setDisabled(false);
                
            UserRecord userRecord = firebaseAuth.createUser(request);
            log.info("Successfully created user in Firebase Auth: {}", userRecord.getUid());
            
            return userRecord.getUid();
            
        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth creation failed: {} - {}", e.getAuthErrorCode(), e.getMessage());
            throw new RuntimeException("Failed to create user in Firebase: " + e.getMessage());
        }
    }

    /**
     * Send password reset email
     * NOTE: This requires proper Firebase setup with email templates
     */
    public void sendPasswordResetEmail(String email) {
        try {
            log.info("Attempting to send password reset email to: {}", email);
            
            // Check if user exists in Firebase Auth
            try {
                firebaseAuth.getUserByEmail(email);
                log.info("User found in Firebase Auth");
            } catch (FirebaseAuthException e) {
                if (e.getAuthErrorCode().name().equals("USER_NOT_FOUND")) {
                    log.warn("User not found in Firebase Auth, skipping password reset");
                    return;
                }
                throw e;
            }
            
            String link = firebaseAuth.generatePasswordResetLink(email);
            log.info("Password reset link generated for: {}", email);
            
            // TODO: In production, integrate with email service
            log.info("Password reset link (LOG ONLY - USE EMAIL SERVICE IN PRODUCTION): {}", link);
            
        } catch (FirebaseAuthException e) {
            log.error("Firebase error while sending password reset email: {} - {}", 
                     e.getAuthErrorCode(), e.getMessage());
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending password reset email: {}", e.getMessage());
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }
}