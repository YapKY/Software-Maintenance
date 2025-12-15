package com.example.springboot.service;

import com.example.springboot.model.EmailVerificationToken;
import com.example.springboot.repository.EmailVerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @InjectMocks
    private EmailVerificationTokenService tokenService;

    @Test
    void testCreateVerificationToken() {
        String userId = "user123";
        String email = "test@example.com";

        String token = tokenService.createVerificationToken(userId, email);

        assertNotNull(token);
        verify(tokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    void testValidateToken_Success() {
        String tokenStr = "valid-token";
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(tokenStr)
                .used(false)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        assertTrue(tokenService.validateToken(tokenStr));
    }

    @Test
    void testValidateToken_NotFound() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertFalse(tokenService.validateToken("invalid"));
    }

    @Test
    void testValidateToken_Used() {
        String tokenStr = "used-token";
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(tokenStr)
                .used(true)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        assertFalse(tokenService.validateToken(tokenStr));
    }

    @Test
    void testValidateToken_Expired() {
        String tokenStr = "expired-token";
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(tokenStr)
                .used(false)
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        assertFalse(tokenService.validateToken(tokenStr));
    }

    @Test
    void testMarkTokenAsUsed_Success() {
        String tokenStr = "valid-token";
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(tokenStr)
                .used(false)
                .build();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        tokenService.markTokenAsUsed(tokenStr);

        assertTrue(token.getUsed());
        verify(tokenRepository).save(token);
    }

    @Test
    void testMarkTokenAsUsed_NotFound() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        tokenService.markTokenAsUsed("invalid");

        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testGetUserIdFromToken_Found() {
        String tokenStr = "token";
        EmailVerificationToken token = EmailVerificationToken.builder()
                .userId("user123")
                .build();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        Optional<String> result = tokenService.getUserIdFromToken(tokenStr);

        assertTrue(result.isPresent());
        assertEquals("user123", result.get());
    }

    @Test
    void testGetUserIdFromToken_NotFound() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        Optional<String> result = tokenService.getUserIdFromToken("invalid");

        assertTrue(result.isEmpty());
    }
}