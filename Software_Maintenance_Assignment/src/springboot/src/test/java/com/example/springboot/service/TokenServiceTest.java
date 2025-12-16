package com.example.springboot.service;

import com.example.springboot.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void testRevokeToken_Success() {
        String token = "valid_refresh_token";
        
        // No return value, just verify interaction
        doNothing().when(refreshTokenRepository).revokeToken(token);

        tokenService.revokeToken(token);

        verify(refreshTokenRepository, times(1)).revokeToken(token);
    }

    @Test
    void testRevokeToken_Exception() {
        String token = "invalid_token";
        
        doThrow(new RuntimeException("DB error")).when(refreshTokenRepository).revokeToken(token);

        // Service catches exception and logs it, should not throw
        tokenService.revokeToken(token);

        verify(refreshTokenRepository, times(1)).revokeToken(token);
    }
}