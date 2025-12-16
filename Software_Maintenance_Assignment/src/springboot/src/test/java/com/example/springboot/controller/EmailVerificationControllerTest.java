package com.example.springboot.controller;

import com.example.springboot.model.User;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.EmailService;
import com.example.springboot.service.EmailVerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailVerificationControllerTest {

    private MockMvc mockMvc;

    @Mock private EmailVerificationTokenService tokenService;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    @InjectMocks private EmailVerificationController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ==========================================
    // Verify Email Tests
    // ==========================================

    @Test
    void testVerifyEmail_Success() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEmailVerified(false);

        when(tokenService.validateToken("valid-token")).thenReturn(true);
        when(tokenService.getUserIdFromToken("valid-token")).thenReturn(Optional.of("user-id"));
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/email/verify").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully! You can now login."));

        verify(userRepository).save(any(User.class));
        verify(tokenService).markTokenAsUsed("valid-token");
        verify(emailService).sendWelcomeEmail(any(User.class));
    }

    @Test
    void testVerifyEmail_InvalidToken() throws Exception {
        when(tokenService.validateToken("bad-token")).thenReturn(false);

        mockMvc.perform(get("/api/email/verify").param("token", "bad-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired verification token"));
        
        verify(userRepository, never()).save(any());
    }

    @Test
    void testVerifyEmail_UserNotFound() throws Exception {
        when(tokenService.validateToken("valid-token")).thenReturn(true);
        when(tokenService.getUserIdFromToken("valid-token")).thenReturn(Optional.of("user-id"));
        when(userRepository.findById("user-id")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/email/verify").param("token", "valid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testVerifyEmail_Exception() throws Exception {
        when(tokenService.validateToken(anyString())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/email/verify").param("token", "token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email verification failed"));
    }

    // ==========================================
    // Resend Verification Tests
    // ==========================================

    @Test
    void testResendVerification_Success() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEmailVerified(false);
        user.setCustId("cust-123");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(tokenService.createVerificationToken("cust-123", "test@test.com")).thenReturn("new-token");

        mockMvc.perform(post("/api/email/resend-verification").param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Verification email sent! Please check your inbox."));
        
        verify(emailService).sendVerificationEmail(eq(user), eq("new-token"));
    }

    @Test
    void testResendVerification_UserNotFound() throws Exception {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/email/resend-verification").param("email", "unknown@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
        
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void testResendVerification_AlreadyVerified() throws Exception {
        User user = new User();
        user.setEmail("verified@test.com");
        user.setEmailVerified(true);

        when(userRepository.findByEmail("verified@test.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/email/resend-verification").param("email", "verified@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already verified"));
        
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void testResendVerification_Exception() throws Exception {
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Service down"));

        mockMvc.perform(post("/api/email/resend-verification").param("email", "test@test.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to send verification email"));
    }
}