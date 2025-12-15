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

    @Test
    void testVerifyEmail_Success() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");

        when(tokenService.validateToken("valid-token")).thenReturn(true);
        when(tokenService.getUserIdFromToken("valid-token")).thenReturn(Optional.of("user-id"));
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/email/verify").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(any(User.class));
    }

    @Test
    void testVerifyEmail_InvalidToken() throws Exception {
        when(tokenService.validateToken("bad-token")).thenReturn(false);

        mockMvc.perform(get("/api/email/verify").param("token", "bad-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testResendVerification_Success() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEmailVerified(false);
        user.setCustId("cust-123");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(tokenService.createVerificationToken(anyString(), anyString())).thenReturn("new-token");

        mockMvc.perform(post("/api/email/resend-verification").param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(emailService).sendVerificationEmail(eq(user), eq("new-token"));
    }
}