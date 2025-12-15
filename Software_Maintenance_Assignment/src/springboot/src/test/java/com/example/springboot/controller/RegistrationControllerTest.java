package com.example.springboot.controller;

import com.example.springboot.dto.request.AdminRegisterRequestDTO;
import com.example.springboot.dto.request.UserRegisterRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.enums.Gender;
import com.example.springboot.service.RegistrationExecutionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegistrationExecutionService registrationExecutionService;

    @InjectMocks
    private RegistrationController registrationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Prepare valid request
        UserRegisterRequestDTO request = UserRegisterRequestDTO.builder()
                .email("user@test.com")
                .password("StrongPass1!")
                .name("John Doe")
                .custIcNo("900101-14-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("valid-token")
                .build();

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).message("User registered").build();

        when(registrationExecutionService.registerUser(any(UserRegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/register/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered"));
    }

    @Test
    void testRegisterUser_Failure() throws Exception {
        UserRegisterRequestDTO request = UserRegisterRequestDTO.builder()
                .email("user@test.com")
                .password("StrongPass1!")
                .name("John Doe")
                .custIcNo("900101-14-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("valid-token")
                .build();

        when(registrationExecutionService.registerUser(any())).thenThrow(new RuntimeException("Email exists"));

        mockMvc.perform(post("/api/register/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email exists"));
    }

    @Test
    void testRegisterAdmin_Success() throws Exception {
        // Prepare valid Admin request
        AdminRegisterRequestDTO request = AdminRegisterRequestDTO.builder()
                .email("admin@test.com")
                .staffPass("StrongPass1!")
                .name("Admin User")
                .position("Manager")
                .gender(Gender.FEMALE)
                .phoneNumber("012-3456789")
                .mfaEnabled(true)
                .build();

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).message("Admin registered").build();

        when(registrationExecutionService.registerAdmin(any(AdminRegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegisterAdmin_Failure() throws Exception {
        AdminRegisterRequestDTO request = AdminRegisterRequestDTO.builder()
                .email("admin@test.com")
                .staffPass("StrongPass1!")
                .name("Admin User")
                .position("Manager")
                .gender(Gender.FEMALE)
                .build();

        when(registrationExecutionService.registerAdmin(any())).thenThrow(new RuntimeException("Unauthorized"));

        mockMvc.perform(post("/api/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }
}