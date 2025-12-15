package com.example.springboot.controller;

import com.example.springboot.dto.request.*;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.enums.AuthProvider;
import com.example.springboot.service.AuthExecutionService;
import com.example.springboot.service.PasswordResetService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthExecutionService authExecutionService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // --- Login Tests ---

    @Test
    void testLogin_Success() throws Exception {
        // FIX: Match LoginRequestDTO fields
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRecaptchaToken("valid-recaptcha"); // Required field

        AuthResponseDTO response = AuthResponseDTO.builder()
                .success(true)
                .message("Login successful")
                .build();

        when(authExecutionService.authenticateWithEmail(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLogin_Failure() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("wrongpass");
        request.setRecaptchaToken("valid-recaptcha");

        when(authExecutionService.authenticateWithEmail(any())).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    // --- Social Login Tests ---

    @Test
    void testGoogleLogin_Success() throws Exception {
        // FIX: Match SocialLoginRequestDTO fields
        SocialLoginRequestDTO request = new SocialLoginRequestDTO();
        request.setProvider(AuthProvider.GOOGLE);
        request.setAccessToken("google-token"); // Changed from 'token'
        request.setRecaptchaToken("valid-recaptcha");

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).build();

        when(authExecutionService.authenticateWithSocial(any(SocialLoginRequestDTO.class), eq(AuthProvider.GOOGLE)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testFacebookLogin_Success() throws Exception {
        SocialLoginRequestDTO request = new SocialLoginRequestDTO();
        request.setProvider(AuthProvider.FACEBOOK);
        request.setAccessToken("fb-token"); // Changed from 'token'
        request.setRecaptchaToken("valid-recaptcha");

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).build();

        when(authExecutionService.authenticateWithSocial(any(SocialLoginRequestDTO.class), eq(AuthProvider.FACEBOOK)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login/facebook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- MFA Verification Tests ---

    @Test
    void testVerifyMFA_Success() throws Exception {
        // 1. Prepare the Request (Must include ALL @NotBlank fields)
        MFARequestDTO request = new MFARequestDTO();
        request.setEmail("test@example.com");
        request.setCode("123456"); // Any 6 digits work because we Mock the service
        request.setSessionToken("dummy-session-token-xyz"); // REQUIRED field

        // 2. Prepare the Expected Response
        AuthResponseDTO response = AuthResponseDTO.builder()
                .success(true)
                .message("Authentication successful")
                .build();

        // 3. Mock the Service Behavior
        // This bypasses the real TOTP time check, solving the "changing code" issue
        when(authExecutionService.verifyMFA(any(MFARequestDTO.class))).thenReturn(response);

        // 4. Perform Request & Verify
        mockMvc.perform(post("/api/auth/verify-mfa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- Password Reset Tests ---

    @Test
    void testForgotPassword_Success() throws Exception {
        PasswordResetRequestDTO request = new PasswordResetRequestDTO();
        request.setEmail("test@example.com");
        
        MessageResponseDTO response = MessageResponseDTO.builder().success(true).message("Email sent").build();

        when(passwordResetService.requestPasswordReset("test@example.com")).thenReturn(response);

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent"));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO();
        request.setToken("token");
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass"); // This will now work

        MessageResponseDTO response = MessageResponseDTO.builder()
                .success(true)
                .message("Password changed")
                .build();

        when(passwordResetService.confirmPasswordReset(any(PasswordResetConfirmRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}