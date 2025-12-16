package com.example.springboot.controller;

import com.example.springboot.enums.Role;
import com.example.springboot.model.User;
import com.example.springboot.model.Admin;
import com.example.springboot.model.Superadmin;
import com.example.springboot.dto.response.MFAStatusDTO;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.service.MFAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/mfa")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class MFAController {

    private final MFAService mfaService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SuperadminRepository superadminRepository;

    /**
     * Helper method to safely parse role from auth string
     */
    private Role parseRoleFromAuthority(String authority) {
        try {
            String roleStr = authority.replace("ROLE_", "");
            return Role.valueOf(roleStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Invalid role value in authority '{}', defaulting to USER", authority);
            return Role.USER;
        }
    }

    // ... setupMFA, getMFAStatus ... (keep existing methods as is, logic updated in
    // Service)

    @PostMapping("/setup")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> setupMFA() {
        // ... (Keep existing implementation)
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();

            String roleStr = auth.getAuthorities().iterator().next().getAuthority();
            Role userRole = parseRoleFromAuthority(roleStr);

            log.info("MFA setup request for user: {}", userId);

            MFAStatusDTO mfaStatus = mfaService.setupMFA(userId, userRole);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "MFA setup successful. Scan QR code with authenticator app.");
            response.put("data", mfaStatus);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("MFA setup failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMFAStatus() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();

            String roleStr = auth.getAuthorities().iterator().next().getAuthority();
            Role userRole = parseRoleFromAuthority(roleStr);

            log.info("MFA status request for user: {}", userId);

            MFAStatusDTO mfaStatus = mfaService.getMFAStatus(userId, userRole);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mfaStatus);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get MFA status: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ... disableMFA, regenerateBackupCodes ... (Keep existing)

    @PostMapping("/disable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> disableMFA(@RequestParam String confirmationCode) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();

            String roleStr = auth.getAuthorities().iterator().next().getAuthority();
            Role userRole = parseRoleFromAuthority(roleStr);

            log.info("MFA disable request for user: {}", userId);

            boolean isValid = mfaService.validateMFACode(userId, userRole, confirmationCode);

            if (!isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid MFA code. Cannot disable MFA.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            mfaService.disableMFA(userId, userRole);

            // [FIX] Also update User/Admin/Superadmin entity
            updateUserMfaStatus(userId, userRole, false);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "MFA disabled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to disable MFA: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/regenerate-backup-codes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> regenerateBackupCodes(@RequestParam String confirmationCode) {
        // ... (Keep existing implementation)
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();

            String roleStr = auth.getAuthorities().iterator().next().getAuthority();
            Role userRole = parseRoleFromAuthority(roleStr);

            log.info("Backup codes regeneration request for user: {}", userId);

            boolean isValid = mfaService.validateMFACode(userId, userRole, confirmationCode);

            if (!isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid MFA code. Cannot regenerate backup codes.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String[] backupCodes = mfaService.regenerateBackupCodes(userId, userRole);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Backup codes regenerated successfully");
            response.put("backupCodes", backupCodes);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to regenerate backup codes: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> validateMFACode(@RequestParam String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();

            String roleStr = auth.getAuthorities().iterator().next().getAuthority();
            Role userRole = parseRoleFromAuthority(roleStr);

            // [FIX] Use verifyAndEnableMFA to ensure setup completion
            boolean isValid = mfaService.verifyAndEnableMFA(userId, userRole, code);

            if (isValid) {
                // [FIX] Update User/Admin/Superadmin entity mfaEnabled = true
                updateUserMfaStatus(userId, userRole, true);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", isValid);
            response.put("message", isValid ? "Code is valid" : "Code is invalid");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("MFA validation error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method to update entity MFA status
    private void updateUserMfaStatus(String userId, Role role, boolean enabled) {
        try {
            switch (role) {
                case USER:
                    Optional<User> user = userRepository.findById(userId);
                    if (user.isPresent()) {
                        user.get().setMfaEnabled(enabled);
                        userRepository.save(user.get());
                    }
                    break;
                case ADMIN:
                    Optional<Admin> admin = adminRepository.findById(userId);
                    if (admin.isPresent()) {
                        admin.get().setMfaEnabled(enabled);
                        adminRepository.save(admin.get());
                    }
                    break;
                case SUPERADMIN:
                    Optional<Superadmin> sa = superadminRepository.findById(userId);
                    if (sa.isPresent()) {
                        sa.get().setMfaEnabled(enabled);
                        superadminRepository.save(sa.get());
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to update user MFA status: {}", e.getMessage());
        }
    }
}