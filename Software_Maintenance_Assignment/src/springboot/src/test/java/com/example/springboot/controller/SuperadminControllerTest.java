package com.example.springboot.controller;

import com.example.springboot.dto.response.AdminListResponseDTO;
import com.example.springboot.dto.response.AdminProfileDTO;
import com.example.springboot.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SuperadminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private SuperadminController superadminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(superadminController).build();
    }

    @Test
    void testGetAdminsList_Success() throws Exception {
        AdminProfileDTO admin = AdminProfileDTO.builder().email("admin@test.com").build();
        List<AdminProfileDTO> admins = Collections.singletonList(admin);

        when(userManagementService.getAdminsCreatedBySuperadmin()).thenReturn(admins);

        mockMvc.perform(get("/api/superadmin/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.totalAdmins").value(1));
    }

    @Test
    void testGetAdminsList_Failure() throws Exception {
        when(userManagementService.getAdminsCreatedBySuperadmin()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/superadmin/admins"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
}