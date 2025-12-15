package com.example.springboot.controller;

import com.example.springboot.dto.request.PasswordChangeRequestDTO;
import com.example.springboot.dto.request.PasswordResetConfirmRequestDTO;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void testGetUserDashboard() throws Exception {
        UserProfileDTO profile = UserProfileDTO.builder().email("user@test.com").build();
        when(userManagementService.getCurrentUserProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/dashboard/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void testGetAdminDashboard() throws Exception {
        AdminProfileDTO profile = AdminProfileDTO.builder().email("admin@test.com").build();
        when(userManagementService.getCurrentAdminProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void testGetSuperadminDashboard() throws Exception {
        SuperadminProfileDTO profile = SuperadminProfileDTO.builder().email("super@test.com").build();
        when(userManagementService.getCurrentSuperadminProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/dashboard/superadmin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("super@test.com"));
    }

        @Test
        void testChangePassword_Success() throws Exception {
        // 1. Use the Correct DTO
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setCurrentPassword("oldPass"); // Required field
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");

        // 2. Prepare Response
        MessageResponseDTO response = MessageResponseDTO.builder()
                .success(true)
                .message("Password changed successfully")
                .build();

        // 3. Mock Service
        when(userManagementService.changePassword(any(PasswordChangeRequestDTO.class)))
                .thenReturn(response);

        // 4. Perform Request
        mockMvc.perform(post("/api/dashboard/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }
}