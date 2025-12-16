package com.example.springboot.service;

import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.request.MFARequestDTO;
import com.example.springboot.dto.request.SocialLoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.factory.AuthStrategyFactory;
import com.example.springboot.security.jwt.JwtTokenProvider;
import com.example.springboot.strategy.authentication.AuthStrategy;
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
class AuthExecutionServiceTest {

    @Mock
    private AuthStrategyFactory authStrategyFactory;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private MFAService mfaService;

    @Mock
    private AuthStrategy authStrategy;

    @InjectMocks
    private AuthExecutionService authExecutionService;

    private LoginRequestDTO loginRequest;
    private SocialLoginRequestDTO socialLoginRequest;
    private MFARequestDTO mfaRequest;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password")
                .recaptchaToken("token")
                .build();

        socialLoginRequest = SocialLoginRequestDTO.builder()
                .accessToken("access_token")
                .provider(AuthProvider.GOOGLE)
                .recaptchaToken("token")
                .build();

        mfaRequest = MFARequestDTO.builder()
                .email("test@example.com")
                .code("123456")
                .sessionToken("session_token")
                .build();

        authResponse = AuthResponseDTO.builder()
                .success(true)
                .build();
    }

    @Test
    void testAuthenticateWithEmail_Success() {
        when(authStrategyFactory.getAuthStrategy(AuthProvider.EMAIL)).thenReturn(authStrategy);
        when(authStrategy.authenticate(loginRequest, "token")).thenReturn(authResponse);

        AuthResponseDTO response = authExecutionService.authenticateWithEmail(loginRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(authStrategyFactory).getAuthStrategy(AuthProvider.EMAIL);
        verify(authStrategy).authenticate(loginRequest, "token");
    }

    @Test
    void testAuthenticateWithSocial_Success() {
        when(authStrategyFactory.getAuthStrategy(AuthProvider.GOOGLE)).thenReturn(authStrategy);
        when(authStrategy.authenticate(socialLoginRequest, "token")).thenReturn(authResponse);

        AuthResponseDTO response = authExecutionService.authenticateWithSocial(socialLoginRequest, AuthProvider.GOOGLE);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(authStrategyFactory).getAuthStrategy(AuthProvider.GOOGLE);
        verify(authStrategy).authenticate(socialLoginRequest, "token");
    }

    @Test
    void testVerifyMFA_Success() {
        String userId = "user123";
        Role role = Role.USER;
        JWTResponseDTO tokens = JWTResponseDTO.builder().accessToken("access").build();

        when(jwtTokenProvider.validateToken("session_token")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("session_token")).thenReturn(userId);
        when(jwtTokenProvider.getRoleFromToken("session_token")).thenReturn(role);
        when(mfaService.validateMFACode(userId, role, "123456")).thenReturn(true);
        when(jwtTokenProvider.generateTokens(userId, "test@example.com", role)).thenReturn(tokens);

        AuthResponseDTO response = authExecutionService.verifyMFA(mfaRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(tokens, response.getTokens());
        assertFalse(response.getRequiresMfa());
    }

    @Test
    void testVerifyMFA_InvalidSessionToken() {
        when(jwtTokenProvider.validateToken("session_token")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authExecutionService.verifyMFA(mfaRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid or expired MFA session"));
        verify(mfaService, never()).validateMFACode(any(), any(), any());
    }

    @Test
    void testVerifyMFA_InvalidCode() {
        String userId = "user123";
        Role role = Role.USER;

        when(jwtTokenProvider.validateToken("session_token")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("session_token")).thenReturn(userId);
        when(jwtTokenProvider.getRoleFromToken("session_token")).thenReturn(role);
        when(mfaService.validateMFACode(userId, role, "123456")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authExecutionService.verifyMFA(mfaRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid MFA code"));
    }

    @Test
    void testVerifyMFA_GenericException() {
        when(jwtTokenProvider.validateToken("session_token")).thenThrow(new RuntimeException("Unexpected error"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authExecutionService.verifyMFA(mfaRequest);
        });

        assertTrue(exception.getMessage().contains("MFA verification failed"));
    }

    @Test
    void testLogout_Success() {
        String jwt = "valid_jwt_token";
        doNothing().when(tokenService).revokeToken(jwt);

        authExecutionService.logout(jwt);

        verify(tokenService, times(1)).revokeToken(jwt);
    }

    @Test
    void testLogout_Exception() {
        String jwt = "valid_jwt_token";
        doThrow(new RuntimeException("Revoke failed")).when(tokenService).revokeToken(jwt);

        // Should not throw exception
        assertDoesNotThrow(() -> authExecutionService.logout(jwt));
        verify(tokenService, times(1)).revokeToken(jwt);
    }
}