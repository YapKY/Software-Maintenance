package com.example.maintenance.service;

import com.example.maintenance.domain.model.Admin;
import com.example.maintenance.domain.model.Superadmin;
import com.example.maintenance.domain.model.User;
import com.example.maintenance.dto.request.PasswordChangeRequestDTO;
import com.example.maintenance.dto.response.AdminProfileDTO;
import com.example.maintenance.dto.response.MessageResponseDTO;
import com.example.maintenance.dto.response.SuperadminProfileDTO;
import com.example.maintenance.dto.response.UserProfileDTO;
import com.example.maintenance.exception.InvalidCredentialsException;
import com.example.maintenance.exception.UnauthorizedException;
import com.example.maintenance.exception.UserNotFoundException;
import com.example.maintenance.repository.AdminRepository;
import com.example.maintenance.repository.SuperadminRepository;
import com.example.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserManagementService - Handles user profile and management operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {
    
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Get current user's profile (for User role)
     */
    public UserProfileDTO getCurrentUserProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName(); 
            
            log.info("Fetching profile for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            return UserProfileDTO.builder()
                .id(user.getCustId())           // FIX: getId -> getCustId
                .email(user.getEmail())
                .fullName(user.getName())       // FIX: getFullName -> getName
                .custIcNo(user.getCustIcNo())   // NEW: Map IC
                .gender(user.getGender())       // NEW: Map Gender
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .mfaEnabled(user.getMfaEnabled())
                .emailVerified(user.getEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to fetch user profile: {}", e.getMessage());
            throw new UserNotFoundException("Failed to fetch user profile");
        }
    }
    
    /**
     * Get current admin's profile (for Admin role)
     */
public AdminProfileDTO getCurrentAdminProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminId = auth.getName();
            
            Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("Staff not found"));
            
            return AdminProfileDTO.builder()
                .staffId(admin.getStaffId())  // Renamed
                .email(admin.getEmail())
                .name(admin.getName())        // Renamed
                .phoneNumber(admin.getPhoneNumber())
                .gender(admin.getGender())    // New
                .position(admin.getPosition())// New
                .role(admin.getRole())
                .mfaEnabled(admin.getMfaEnabled())
                .createdBy(admin.getCreatedBy())
                .lastLoginAt(admin.getLastLoginAt())
                .createdAt(admin.getCreatedAt())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to fetch staff profile: {}", e.getMessage());
            throw new UserNotFoundException("Failed to fetch staff profile");
        }
    }
    
    /**
     * Get current superadmin's profile (for Superadmin role)
     */
    public SuperadminProfileDTO getCurrentSuperadminProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String superadminId = auth.getName();
            
            log.info("Fetching profile for superadmin ID: {}", superadminId);
            
            Superadmin superadmin = superadminRepository.findById(superadminId)
                .orElseThrow(() -> new UserNotFoundException("Superadmin not found"));
            
            // Get count of admins created by this superadmin
            Integer totalAdminsCreated = adminRepository.countAdminsCreatedBy(superadminId);
            
            return SuperadminProfileDTO.builder()
                .id(superadmin.getId())
                .email(superadmin.getEmail())
                .fullName(superadmin.getFullName())
                .role(superadmin.getRole())
                .mfaEnabled(superadmin.getMfaEnabled())
                .lastLoginAt(superadmin.getLastLoginAt())
                .createdAt(superadmin.getCreatedAt())
                .totalAdminsCreated(totalAdminsCreated)
                .build();
                
        } catch (Exception e) {
            log.error("Failed to fetch superadmin profile: {}", e.getMessage());
            throw new UserNotFoundException("Failed to fetch superadmin profile");
        }
    }

    /**
     * Change Password for the currently authenticated user (Any Role)
     */
    public MessageResponseDTO changePassword(PasswordChangeRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        String roleStr = auth.getAuthorities().iterator().next().getAuthority();
        
        log.info("Password change request for user ID: {} with role: {}", userId, roleStr);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        switch (roleStr) {
            case "ROLE_USER":
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
                
                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getCustPassword())) {
                    throw new InvalidCredentialsException("Current password is incorrect");
                }
                user.setCustPassword(encodedNewPassword);
                userRepository.save(user);
                break;

            case "ROLE_ADMIN":
                Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Admin not found"));
                
                if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getStaffPass())) {
                    throw new InvalidCredentialsException("Current password is incorrect");
                }
                admin.setStaffPass(encodedNewPassword);
                adminRepository.save(admin);
                break;

            case "ROLE_SUPERADMIN":
                Superadmin superadmin = superadminRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Superadmin not found"));
                
                if (!passwordEncoder.matches(request.getCurrentPassword(), superadmin.getPassword())) {
                    throw new InvalidCredentialsException("Current password is incorrect");
                }
                superadmin.setPassword(encodedNewPassword);
                superadminRepository.save(superadmin);
                break;

            default:
                throw new UnauthorizedException("Unknown role");
        }

        return MessageResponseDTO.builder()
            .success(true)
            .message("Password changed successfully")
            .build();
    }
    
    /**
     * Get user profile by ID (admin/superadmin only)
     */
    public UserProfileDTO getUserProfileById(String userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth.getAuthorities().iterator().next().getAuthority();
            
            if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPERADMIN")) {
                throw new UnauthorizedException("Access denied");
            }
            
            log.info("Fetching profile for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            return UserProfileDTO.builder()
                .id(user.getCustId())         // FIX: getId -> getCustId
                .email(user.getEmail())
                .fullName(user.getName())     // FIX: getFullName -> getName
                .custIcNo(user.getCustIcNo()) // NEW
                .gender(user.getGender())     // NEW
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .mfaEnabled(user.getMfaEnabled())
                .emailVerified(user.getEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
                
        } catch (UnauthorizedException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch user profile by ID: {}", e.getMessage());
            throw new UserNotFoundException("Failed to fetch user profile");
        }
    }
    
    /**
     * Get all admins created by current superadmin
     */
    public List<AdminProfileDTO> getAdminsCreatedBySuperadmin() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String superadminId = auth.getName();
            
            List<Admin> admins = adminRepository.findByCreatedBy(superadminId);
            
            return admins.stream()
                .map(admin -> AdminProfileDTO.builder()
                    .staffId(admin.getStaffId())   // Renamed
                    .email(admin.getEmail())
                    .name(admin.getName())         // Renamed
                    .phoneNumber(admin.getPhoneNumber())
                    .gender(admin.getGender())     // New
                    .position(admin.getPosition()) // New
                    .role(admin.getRole())
                    .mfaEnabled(admin.getMfaEnabled())
                    .createdBy(admin.getCreatedBy())
                    .lastLoginAt(admin.getLastLoginAt())
                    .createdAt(admin.getCreatedAt())
                    .build())
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to fetch staff: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch staff");
        }
    }
    
    /**
     * Update user profile
     */
    public UserProfileDTO updateUserProfile(String userId, String fullName, String phoneNumber) {
        try {
            log.info("Updating profile for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            if (fullName != null && !fullName.isEmpty()) {
                user.setName(fullName);
            }
            
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                if (userRepository.existsByPhoneNumber(phoneNumber) && 
                    !phoneNumber.equals(user.getPhoneNumber())) {
                    throw new IllegalArgumentException("Phone number already in use");
                }
                user.setPhoneNumber(phoneNumber);
            }
            
            User updatedUser = userRepository.save(user);
            
            log.info("Profile updated for user ID: {}", userId);
            
            return UserProfileDTO.builder()
                .id(updatedUser.getCustId())       // FIX
                .email(updatedUser.getEmail())
                .fullName(updatedUser.getName())   // FIX
                .custIcNo(updatedUser.getCustIcNo()) // NEW
                .gender(updatedUser.getGender())     // NEW
                .phoneNumber(updatedUser.getPhoneNumber())
                .role(updatedUser.getRole())
                .mfaEnabled(updatedUser.getMfaEnabled())
                .emailVerified(updatedUser.getEmailVerified())
                .lastLoginAt(updatedUser.getLastLoginAt())
                .createdAt(updatedUser.getCreatedAt())
                .build();
                
        } catch (UserNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user profile: {}", e.getMessage());
            throw new RuntimeException("Failed to update user profile");
        }
    }
    
    /**
     * Delete user account (user can delete their own account)
     */
    public void deleteUserAccount(String userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();
            
            if (!currentUserId.equals(userId)) {
                throw new UnauthorizedException("You can only delete your own account");
            }
            
            log.info("Deleting account for user ID: {}", userId);
            
            if (userRepository.findById(userId).isEmpty()) {
                throw new UserNotFoundException("User not found");
            }
            
            userRepository.deleteById(userId);
            
            log.info("Account deleted for user ID: {}", userId);
            
        } catch (UnauthorizedException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete user account: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user account");
        }
    }
}