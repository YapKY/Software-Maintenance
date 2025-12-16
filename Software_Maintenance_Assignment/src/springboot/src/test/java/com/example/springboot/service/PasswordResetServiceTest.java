package com.example.springboot.service;

import com.example.springboot.dto.request.PasswordResetConfirmRequestDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidTokenException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.model.Admin;
import com.example.springboot.model.PasswordResetToken;
import com.example.springboot.model.Superadmin;
import com.example.springboot.model.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.PasswordResetTokenRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private SuperadminRepository superadminRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    // --- Request Password Reset Tests ---

    @Test
    void testRequestPasswordReset_UserFound() {
        String email = "user@test.com";
        User user = User.builder().custId("u1").name("User").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        MessageResponseDTO response = passwordResetService.requestPasswordReset(email);

        assertTrue(response.getSuccess());
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void testRequestPasswordReset_AdminFound() {
        String email = "admin@test.com";
        Admin admin = Admin.builder().staffId("a1").name("Admin").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        MessageResponseDTO response = passwordResetService.requestPasswordReset(email);

        assertTrue(response.getSuccess());
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void testRequestPasswordReset_SuperadminFound() {
        String email = "sa@test.com";
        Superadmin sa = Superadmin.builder().id("s1").fullName("Super").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(email)).thenReturn(Optional.of(sa));

        MessageResponseDTO response = passwordResetService.requestPasswordReset(email);

        assertTrue(response.getSuccess());
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void testRequestPasswordReset_NotFound() {
        String email = "unknown@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(email)).thenReturn(Optional.empty());

        MessageResponseDTO response = passwordResetService.requestPasswordReset(email);

        assertTrue(response.getSuccess()); // Should succeed to prevent enumeration
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    // --- Confirm Password Reset Tests ---

    @Test
    void testConfirmPasswordReset_User_Success() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "newPass", "newPass");
        PasswordResetToken token = PasswordResetToken.builder()
                .userId("u1")
                .userRole(Role.USER)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        User user = new User();

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        MessageResponseDTO response = passwordResetService.confirmPasswordReset(request);

        assertTrue(response.getSuccess());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
        assertTrue(token.getUsed());
    }

    @Test
    void testConfirmPasswordReset_Admin_Success() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "newPass", "newPass");
        PasswordResetToken token = PasswordResetToken.builder()
                .userId("a1")
                .userRole(Role.ADMIN)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        Admin admin = new Admin();

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(adminRepository.findById("a1")).thenReturn(Optional.of(admin));

        MessageResponseDTO response = passwordResetService.confirmPasswordReset(request);

        assertTrue(response.getSuccess());
        verify(adminRepository).save(admin);
    }

    @Test
    void testConfirmPasswordReset_Superadmin_Success() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "newPass", "newPass");
        PasswordResetToken token = PasswordResetToken.builder()
                .userId("s1")
                .userRole(Role.SUPERADMIN)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        Superadmin sa = new Superadmin();

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(superadminRepository.findById("s1")).thenReturn(Optional.of(sa));

        MessageResponseDTO response = passwordResetService.confirmPasswordReset(request);

        assertTrue(response.getSuccess());
        verify(superadminRepository).save(sa);
    }

    @Test
    void testConfirmPasswordReset_PasswordsDoNotMatch() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "pass1", "pass2");
        
        assertThrows(IllegalArgumentException.class, () -> 
            passwordResetService.confirmPasswordReset(request)
        );
    }

    @Test
    void testConfirmPasswordReset_InvalidToken() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("invalid", "pass", "pass");
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> 
            passwordResetService.confirmPasswordReset(request)
        );
    }

    @Test
    void testConfirmPasswordReset_TokenUsed() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "pass", "pass");
        PasswordResetToken token = PasswordResetToken.builder().used(true).build();
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> 
            passwordResetService.confirmPasswordReset(request)
        );
    }

    @Test
    void testConfirmPasswordReset_TokenExpired() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "pass", "pass");
        PasswordResetToken token = PasswordResetToken.builder()
                .used(false)
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> 
            passwordResetService.confirmPasswordReset(request)
        );
    }

    @Test
    void testConfirmPasswordReset_UserNotFound() {
        PasswordResetConfirmRequestDTO request = new PasswordResetConfirmRequestDTO("token", "pass", "pass");
        PasswordResetToken token = PasswordResetToken.builder()
                .userId("u1")
                .userRole(Role.USER)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> 
            passwordResetService.confirmPasswordReset(request)
        );
    }
}