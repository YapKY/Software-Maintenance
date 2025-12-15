package com.example.springboot.controller;

import com.example.springboot.dto.request.PasswordChangeRequestDTO;
import com.example.springboot.dto.response.AdminProfileDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.dto.response.SuperadminProfileDTO;
import com.example.springboot.dto.response.UserProfileDTO;
import com.example.springboot.enums.Gender;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.service.UserManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for DashboardController
 * Tests profile retrieval and password change endpoints
 */
@WebMvcTest(DashboardController.class)
@DisplayName("Dashboard Controller Integration Tests")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileDTO userProfileDTO;
    private AdminProfileDTO adminProfileDTO;
    private SuperadminProfileDTO superadminProfileDTO;

    @BeforeEach
    void setUp() {
        // Setup User Profile DTO
        userProfileDTO = UserProfileDTO.builder()
                .id("user123")
                .email("user@example.com")
                .fullName("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("0123456789")
                .role(Role.USER)
                .mfaEnabled(false)
                .emailVerified(true)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        // Setup Admin Profile DTO
        adminProfileDTO = AdminProfileDTO.builder()
                .staffId("admin123")
                .email("admin@example.com")
                .name("Jane Smith")
                .phoneNumber("0123456780")
                .gender(Gender.FEMALE)
                .position("Manager")
                .role(Role.ADMIN)
                .mfaEnabled(true)
                .createdBy("superadmin1")
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        // Setup Superadmin Profile DTO
        superadminProfileDTO = SuperadminProfileDTO.builder()
                .id("superadmin1")
                .email("super@example.com")
                .fullName("Super Admin")
                .role(Role.SUPERADMIN)
                .mfaEnabled(true)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .totalAdminsCreated(5)
                .build();
    }

    // ==================== USER DASHBOARD TESTS ====================

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get user dashboard successfully")
    void testGetUserDashboard_Success() throws Exception {
        // Arrange
        when(userManagementService.getCurrentUserProfile()).thenReturn(userProfileDTO);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.phoneNumber").value("0123456789"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.mfaEnabled").value(false))
                .andExpect(jsonPath("$.emailVerified").value(true));

        verify(userManagementService, times(1)).getCurrentUserProfile();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle user profile not found")
    void testGetUserDashboard_UserNotFound() throws Exception {
        // Arrange
        when(userManagementService.getCurrentUserProfile())
                .thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isInternalServerError());

        verify(userManagementService, times(1)).getCurrentUserProfile();
    }

    @Test
    @DisplayName("Should deny access to user dashboard without authentication")
    void testGetUserDashboard_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).getCurrentUserProfile();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should deny access to user dashboard with wrong role")
    void testGetUserDashboard_WrongRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).getCurrentUserProfile();
    }

    // ==================== ADMIN DASHBOARD TESTS ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get admin dashboard successfully")
    void testGetAdminDashboard_Success() throws Exception {
        // Arrange
        when(userManagementService.getCurrentAdminProfile()).thenReturn(adminProfileDTO);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value("admin123"))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.phoneNumber").value("0123456780"))
                .andExpect(jsonPath("$.gender").value("FEMALE"))
                .andExpect(jsonPath("$.position").value("Manager"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.mfaEnabled").value(true))
                .andExpect(jsonPath("$.createdBy").value("superadmin1"));

        verify(userManagementService, times(1)).getCurrentAdminProfile();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle admin profile not found")
    void testGetAdminDashboard_AdminNotFound() throws Exception {
        // Arrange
        when(userManagementService.getCurrentAdminProfile())
                .thenThrow(new UserNotFoundException("Staff not found"));

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isInternalServerError());

        verify(userManagementService, times(1)).getCurrentAdminProfile();
    }

    @Test
    @DisplayName("Should deny access to admin dashboard without authentication")
    void testGetAdminDashboard_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).getCurrentAdminProfile();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access to admin dashboard with wrong role")
    void testGetAdminDashboard_WrongRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).getCurrentAdminProfile();
    }

    // ==================== SUPERADMIN DASHBOARD TESTS ====================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    @DisplayName("Should get superadmin dashboard successfully")
    void testGetSuperadminDashboard_Success() throws Exception {
        // Arrange
        when(userManagementService.getCurrentSuperadminProfile()).thenReturn(superadminProfileDTO);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/superadmin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("superadmin1"))
                .andExpect(jsonPath("$.email").value("super@example.com"))
                .andExpect(jsonPath("$.fullName").value("Super Admin"))
                .andExpect(jsonPath("$.role").value("SUPERADMIN"))
                .andExpect(jsonPath("$.mfaEnabled").value(true))
                .andExpect(jsonPath("$.totalAdminsCreated").value(5));

        verify(userManagementService, times(1)).getCurrentSuperadminProfile();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    @DisplayName("Should handle superadmin profile not found")
    void testGetSuperadminDashboard_SuperadminNotFound() throws Exception {
        // Arrange
        when(userManagementService.getCurrentSuperadminProfile())
                .thenThrow(new UserNotFoundException("Superadmin not found"));

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/superadmin"))
                .andExpect(status().isInternalServerError());

        verify(userManagementService, times(1)).getCurrentSuperadminProfile();
    }

    @Test
    @DisplayName("Should deny access to superadmin dashboard without authentication")
    void testGetSuperadminDashboard_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/superadmin"))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).getCurrentSuperadminProfile();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should deny access to superadmin dashboard with wrong role")
    void testGetSuperadminDashboard_WrongRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/superadmin"))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).getCurrentSuperadminProfile();
    }

    // ==================== CHANGE PASSWORD TESTS ====================

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        MessageResponseDTO response = MessageResponseDTO.builder()
                .success(true)
                .message("Password changed successfully")
                .build();

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(userManagementService, times(1)).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "admin123", roles = "ADMIN")
    @DisplayName("Should change password for admin successfully")
    void testChangePassword_Admin_Success() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        MessageResponseDTO response = MessageResponseDTO.builder()
                .success(true)
                .message("Password changed successfully")
                .build();

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(userManagementService, times(1)).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    @DisplayName("Should fail when passwords do not match")
    void testChangePassword_PasswordMismatch() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("differentPassword")
                .build();

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("New passwords do not match"));

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("New passwords do not match"));

        verify(userManagementService, times(1)).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    @DisplayName("Should fail when current password is incorrect")
    void testChangePassword_IncorrectCurrentPassword() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException("Current password is incorrect"));

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));

        verify(userManagementService, times(1)).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @DisplayName("Should deny password change without authentication")
    void testChangePassword_Unauthorized() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    @DisplayName("Should handle empty password fields")
    void testChangePassword_EmptyFields() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("")
                .newPassword("")
                .confirmPassword("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user123", roles = "USER")
    @DisplayName("Should handle service exception during password change")
    void testChangePassword_ServiceException() throws Exception {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/dashboard/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userManagementService, times(1)).changePassword(any(PasswordChangeRequestDTO.class));
    }
}
