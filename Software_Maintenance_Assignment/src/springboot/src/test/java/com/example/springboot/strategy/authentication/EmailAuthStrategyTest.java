package com.example.springboot.strategy.authentication;

import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.decorator.AuthServiceComponent;
import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailAuthStrategyTest {

    @Mock
    private RecaptchaAdapter recaptchaAdapter;

    @Mock
    private AuthServiceComponent authService;

    @InjectMocks
    private EmailAuthStrategy emailAuthStrategy;

    private LoginRequestDTO loginRequest;
    private String recaptchaToken;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        recaptchaToken = "valid-token";
    }

    // --- POSITIVE TESTS ---

    @Test
    void testAuthenticate_Success() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(recaptchaToken)).thenReturn(true);
        
        AuthResponseDTO expectedResponse = AuthResponseDTO.builder()
                .success(true)
                .message("Login successful")
                .build();
        when(authService.performAuthentication(loginRequest)).thenReturn(expectedResponse);

        // Act
        AuthResponseDTO result = emailAuthStrategy.authenticate(loginRequest, recaptchaToken);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        verify(recaptchaAdapter).validateRecaptcha(recaptchaToken);
        verify(authService).performAuthentication(loginRequest);
    }

    @Test
    void testGetStrategyName() {
        assertEquals("EMAIL_PASSWORD_AUTH", emailAuthStrategy.getStrategyName());
    }

    // --- NEGATIVE TESTS ---

    @Test
    void testAuthenticate_InvalidRecaptcha() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(recaptchaToken)).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            emailAuthStrategy.authenticate(loginRequest, recaptchaToken);
        });

        assertEquals("reCAPTCHA validation failed", exception.getMessage());
        // Verify we never attempted authentication if recaptcha failed
        verify(authService, never()).performAuthentication(any());
    }

    @Test
    void testAuthenticate_InvalidCredentialsType() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(recaptchaToken)).thenReturn(true);
        Object invalidCredentials = "Just a string, not a DTO";

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            emailAuthStrategy.authenticate(invalidCredentials, recaptchaToken);
        });

        assertEquals("Invalid credentials format", exception.getMessage());
    }

    @Test
    void testAuthenticate_AuthServiceFailure() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(recaptchaToken)).thenReturn(true);
        when(authService.performAuthentication(loginRequest))
                .thenThrow(new InvalidCredentialsException("Wrong password"));

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            emailAuthStrategy.authenticate(loginRequest, recaptchaToken);
        });

        assertEquals("Wrong password", exception.getMessage());
    }
}