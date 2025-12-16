package com.example.springboot.decorator;

import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.exception.RateLimitExceededException;
import com.example.springboot.security.ratelimit.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitedAuthServiceTest {

    // Mocking the base service interface
    @Mock
    @Qualifier("baseAuthService")
    private AuthServiceComponent baseAuthService;

    @Mock
    private LoginAttemptService loginAttemptService;

    private RateLimitedAuthService rateLimitedAuthService;

    private LoginRequestDTO request;

    @BeforeEach
    void setUp() {
        // Constructing manually to control injection
        rateLimitedAuthService = new RateLimitedAuthService(baseAuthService, loginAttemptService);
        
        request = new LoginRequestDTO();
        request.setEmail("test@test.com");
    }

    @Test
    @DisplayName("Should block request if LoginAttemptService says blocked")
    void testPerformAuthentication_Blocked() {
        when(loginAttemptService.isBlocked("test@test.com")).thenReturn(true);

        assertThrows(RateLimitExceededException.class, () -> 
            rateLimitedAuthService.performAuthentication(request)
        );

        verify(baseAuthService, never()).performAuthentication(any());
    }

    @Test
    @DisplayName("Should call base service and record success")
    void testPerformAuthentication_Success() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        
        AuthResponseDTO successResponse = AuthResponseDTO.builder().success(true).build();
        when(baseAuthService.performAuthentication(request)).thenReturn(successResponse);

        AuthResponseDTO result = rateLimitedAuthService.performAuthentication(request);

        assertTrue(result.getSuccess());
        verify(loginAttemptService).loginSucceeded("test@test.com");
    }

    @Test
    @DisplayName("Should record failure when base service throws exception")
    void testPerformAuthentication_Failure() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(baseAuthService.performAuthentication(request)).thenThrow(new RuntimeException("Auth failed"));

        assertThrows(RuntimeException.class, () -> 
            rateLimitedAuthService.performAuthentication(request)
        );

        verify(loginAttemptService).loginFailed("test@test.com");
    }
}