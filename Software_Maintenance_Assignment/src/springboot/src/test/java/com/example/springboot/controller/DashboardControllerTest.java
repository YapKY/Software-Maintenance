package com.example.springboot.controller;

import com.example.springboot.dto.request.PasswordChangeRequestDTO;
import com.example.springboot.dto.response.AdminProfileDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.dto.response.SuperadminProfileDTO;
import com.example.springboot.dto.response.UserProfileDTO;
import com.example.springboot.service.UserManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private DashboardController dashboardController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    // ==========================================
    // Dashboard Retrieval Tests
    // ==========================================

    @Test
    void testGetUserDashboard_Success() throws Exception {
        UserProfileDTO profile = UserProfileDTO.builder().email("user@test.com").build();
        when(userManagementService.getCurrentUserProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void testGetUserDashboard_Failure() throws Exception {
        when(userManagementService.getCurrentUserProfile()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetAdminDashboard_Success() throws Exception {
        AdminProfileDTO profile = AdminProfileDTO.builder().email("admin@test.com").build();
        when(userManagementService.getCurrentAdminProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void testGetSuperadminDashboard_Success() throws Exception {
        SuperadminProfileDTO profile = SuperadminProfileDTO.builder().email("super@test.com").build();
        when(userManagementService.getCurrentSuperadminProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/dashboard/superadmin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("super@test.com"));
    }

    // ==========================================
    // Change Password Tests
    // ==========================================

    @Test
    void testChangePassword_Success() throws Exception {
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setCurrentPassword("oldPass"); 
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");

        MessageResponseDTO response = MessageResponseDTO.builder()
                .success(true)
                .message("Password changed successfully")
                .build();

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/dashboard/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testChangePassword_Mismatch() throws Exception {
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("mismatch");

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Passwords do not match"));

        mockMvc.perform(post("/api/dashboard/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    void testChangePassword_InternalError() throws Exception {
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");

        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenThrow(new RuntimeException("System error"));

        mockMvc.perform(post("/api/dashboard/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // Controller catches generic Exception and returns 400
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("System error"));
    }
}