package com.example.springboot.service;

import com.example.springboot.model.*;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.PasswordResetConfirmRequestDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.exception.InvalidTokenException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * PasswordResetService - Handles the forgotten password flow
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SuperadminRepository superadminRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int EXPIRATION_HOURS = 1;

    /**
     * Step 1: Request Password Reset
     */
    public MessageResponseDTO requestPasswordReset(String email) {
        // Check all repositories to find the user
        String userId = null;
        Role role = null;
        String fullName = null;

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            userId = user.get().getCustId();
            role = Role.USER;
            fullName = user.get().getName();
        } else {
            Optional<Admin> admin = adminRepository.findByEmail(email);
            if (admin.isPresent()) {
                userId = admin.get().getStaffId();
                role = Role.ADMIN;
                fullName = admin.get().getName();
            } else {
                Optional<Superadmin> superadmin = superadminRepository.findByEmail(email);
                if (superadmin.isPresent()) {
                    userId = superadmin.get().getId();
                    role = Role.SUPERADMIN;
                    fullName = superadmin.get().getFullName();
                }
            }
        }

        if (userId == null) {
            // Don't reveal that the user doesn't exist for security reasons
            log.warn("Password reset requested for non-existent email: {}", email);
            return MessageResponseDTO.builder()
                .success(true)
                .message("If an account exists with this email, a reset link has been sent.")
                .build();
        }

        // Create token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
            .token(token)
            .userId(userId)
            .userRole(role)
            .email(email)
            .expiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
            .used(false)
            .build();

        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, token);

        return MessageResponseDTO.builder()
            .success(true)
            .message("Password reset link has been sent to your email.")
            .build();
    }

    /**
     * Step 2: Confirm Password Reset
     */
    public MessageResponseDTO confirmPasswordReset(PasswordResetConfirmRequestDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (token.getUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }

        // Update Password based on Role
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        
        switch (token.getUserRole()) {
            case USER:
                User user = userRepository.findById(token.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
                user.setCustPassword(encodedPassword);
                userRepository.save(user);
                break;
            case ADMIN:
                Admin admin = adminRepository.findById(token.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Admin not found"));
                admin.setStaffPass(encodedPassword);
                adminRepository.save(admin);
                break;
            case SUPERADMIN:
                Superadmin superadmin = superadminRepository.findById(token.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Superadmin not found"));
                superadmin.setPassword(encodedPassword);
                superadminRepository.save(superadmin);
                break;
        }

        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);

        return MessageResponseDTO.builder()
            .success(true)
            .message("Password has been reset successfully. You can now login.")
            .build();
    }
}