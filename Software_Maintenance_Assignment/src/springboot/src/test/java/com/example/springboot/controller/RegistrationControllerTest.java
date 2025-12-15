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

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // FIX: Match UserRegisterRequestDTO fields
        UserRegisterRequestDTO request = new UserRegisterRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("StrongPass1!");
        request.setName("John Doe"); // changed from setFullName
        request.setCustIcNo("900101-14-1234"); // Added
        request.setGender(Gender.MALE); // Added
        request.setPhoneNumber("012-3456789"); // Added
        request.setRecaptchaToken("valid-recaptcha"); // Added

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).message("User registered").build();

        when(registrationExecutionService.registerUser(any(UserRegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/register/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegisterUser_Failure() throws Exception {
        UserRegisterRequestDTO request = new UserRegisterRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("StrongPass1!");
        request.setName("John Doe");
        request.setCustIcNo("900101-14-1234");
        request.setGender(Gender.MALE);
        request.setPhoneNumber("012-3456789");
        request.setRecaptchaToken("valid-recaptcha");

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
        // FIX: Match AdminRegisterRequestDTO fields
        AdminRegisterRequestDTO request = new AdminRegisterRequestDTO();
        request.setEmail("admin@test.com");
        request.setStaffPass("StrongPass1!"); // changed from setPassword
        request.setName("Admin User"); // changed from setFullName
        request.setPosition("Manager"); // changed from setDepartment
        request.setGender(Gender.FEMALE); // Added
        request.setPhoneNumber("012-3456789"); // Added

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).message("Admin registered").build();

        when(registrationExecutionService.registerAdmin(any(AdminRegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}