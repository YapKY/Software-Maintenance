package com.example.springboot.controller;

import com.example.springboot.dto.request.PasswordChangeRequestDTO;
import com.example.springboot.dto.response.*;
import com.example.springboot.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * DashboardController - Role-specific dashboard endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class DashboardController {
    
    private final UserManagementService userManagementService;
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileDTO> getUserDashboard() {
        try {
            UserProfileDTO profile = userManagementService.getCurrentUserProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to load user dashboard: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminProfileDTO> getAdminDashboard() {
        try {
            AdminProfileDTO profile = userManagementService.getCurrentAdminProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to load admin dashboard: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/superadmin")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SuperadminProfileDTO> getSuperadminDashboard() {
        try {
            SuperadminProfileDTO profile = userManagementService.getCurrentSuperadminProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to load superadmin dashboard: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Change Password - Accessible by any authenticated user
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> changePassword(
        @Valid @RequestBody PasswordChangeRequestDTO request
    ) {
        try {
            log.info("Password change request received");
            MessageResponseDTO response = userManagementService.changePassword(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | com.example.springboot.exception.InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(MessageResponseDTO.builder()
                .success(false)
                .message(e.getMessage())
                .build());
        } catch (Exception e) {
            log.error("Failed to change password: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MessageResponseDTO.builder()
                .success(false)
                .message(e.getMessage())
                .build());
        }
    }
}