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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock private AuthExecutionService authExecutionService;
    @Mock private PasswordResetService passwordResetService;

    @InjectMocks private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // ==========================================
    // Email Login
    // ==========================================

    @Test
    void testLogin_Success() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setRecaptchaToken("token");

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).build();
        when(authExecutionService.authenticateWithEmail(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLogin_Failure() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setRecaptchaToken("token");

        when(authExecutionService.authenticateWithEmail(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    // ==========================================
    // Google Login
    // ==========================================

    @Test
    void testLoginWithGoogle_Success() throws Exception {
        SocialLoginRequestDTO request = new SocialLoginRequestDTO();
        request.setProvider(AuthProvider.GOOGLE);
        request.setAccessToken("token");
        request.setRecaptchaToken("recaptcha");

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).build();
        when(authExecutionService.authenticateWithSocial(any(), eq(AuthProvider.GOOGLE))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLoginWithGoogle_Failure() throws Exception {
        SocialLoginRequestDTO request = new SocialLoginRequestDTO();
        request.setProvider(AuthProvider.GOOGLE);
        request.setAccessToken("token");
        request.setRecaptchaToken("recaptcha");

        when(authExecutionService.authenticateWithSocial(any(), eq(AuthProvider.GOOGLE)))
                .thenThrow(new RuntimeException("Google Auth Failed"));

        mockMvc.perform(post("/api/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Google Auth Failed"));
    }

    // ==========================================
    // Facebook Login
    // ==========================================

    @Test
    void testLoginWithFacebook_Success() throws Exception {
        SocialLoginRequestDTO request = new SocialLoginRequestDTO();
        request.setProvider(AuthProvider.FACEBOOK);
        request.setAccessToken("token");
        request.setRecaptchaToken("recaptcha");

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).build();
        when(authExecutionService.authenticateWithSocial(any(), eq(AuthProvider.FACEBOOK))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login/facebook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLoginWithFacebook_Failure() throws Exception {
        SocialLoginRequestDTO request = new SocialLoginRequestDTO();
        request.setProvider(AuthProvider.FACEBOOK);
        request.setAccessToken("token");
        request.setRecaptchaToken("recaptcha");

        when(authExecutionService.authenticateWithSocial(any(), eq(AuthProvider.FACEBOOK)))
                .thenThrow(new RuntimeException("FB Auth Failed"));

        mockMvc.perform(post("/api/auth/login/facebook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("FB Auth Failed"));
    }

    // ==========================================
    // MFA Verification
    // ==========================================

    @Test
    void testVerifyMFA_Success() throws Exception {
        MFARequestDTO request = new MFARequestDTO();
        request.setEmail("test@test.com");
        request.setCode("123456");
        request.setSessionToken("session");

        AuthResponseDTO response = AuthResponseDTO.builder().success(true).build();
        when(authExecutionService.verifyMFA(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/verify-mfa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testVerifyMFA_Failure() throws Exception {
        MFARequestDTO request = new MFARequestDTO();
        request.setEmail("test@test.com");
        request.setCode("123456");
        request.setSessionToken("session");

        when(authExecutionService.verifyMFA(any())).thenThrow(new RuntimeException("Invalid Code"));

        mockMvc.perform(post("/api/auth/verify-mfa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid Code"));
    }

    // ==========================================
    // Logout
    // ==========================================

    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogout_Failure() throws Exception {
        doThrow(new RuntimeException("Logout Error")).when(authExecutionService).logout(anyString());

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // Forgot Password
    // ==========================================

    @Test
    void testForgotPassword_Success() throws Exception {
        PasswordResetRequestDTO request = new PasswordResetRequestDTO();
        request.setEmail("test@test.com");

        MessageResponseDTO response = MessageResponseDTO.builder().success(true).build();
        when(passwordResetService.requestPasswordReset(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testForgotPassword_Failure() throws Exception {
        PasswordResetRequestDTO request = new PasswordResetRequestDTO();
        request.setEmail("test@test.com");

        when(passwordResetService.requestPasswordReset(anyString())).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    // ==========================================
    // Reset Password
    // ==========================================

    @Test
    void testResetPassword_Success() throws Exception {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO();
        request.setToken("token");
        request.setNewPassword("pass");
        request.setConfirmPassword("pass");

        MessageResponseDTO response = MessageResponseDTO.builder().success(true).build();
        when(passwordResetService.confirmPasswordReset(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testResetPassword_Failure() throws Exception {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO();
        request.setToken("token");
        request.setNewPassword("pass");
        request.setConfirmPassword("pass");

        when(passwordResetService.confirmPasswordReset(any())).thenThrow(new RuntimeException("Token expired"));

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token expired"));
    }
}