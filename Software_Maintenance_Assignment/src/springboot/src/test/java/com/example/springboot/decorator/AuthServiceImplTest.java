package com.example.springboot.decorator;

import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.RateLimitExceededException;
import com.example.springboot.model.Admin;
import com.example.springboot.model.Superadmin;
import com.example.springboot.model.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.security.jwt.JwtTokenProvider;
import com.example.springboot.security.ratelimit.RateLimiter;
import com.example.springboot.service.MFAService; // IMPORT ADDED
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private SuperadminRepository superadminRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private MFAService mfaService;
    @Mock private RateLimiter rateLimiter;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequestDTO loginRequest;
    private User user;
    private Admin admin;
    private Superadmin superadmin;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        user = new User();
        user.setEmail("test@example.com");
        user.setCustPassword("encodedPass");
        user.setCustId("user-1");
        user.setEmailVerified(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setMfaEnabled(false);

        admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setStaffPass("encodedPass");
        admin.setStaffId("admin-1");
        admin.setAccountLocked(false);
        admin.setFailedLoginAttempts(0);
        admin.setMfaEnabled(false);

        superadmin = new Superadmin();
        superadmin.setEmail("super@example.com");
        superadmin.setPassword("encodedPass");
        superadmin.setId("super-1");
        superadmin.setAccountLocked(false);
    }

    @Test
    @DisplayName("Should throw RateLimitExceededException when blocked")
    void testPerformAuthentication_RateLimitExceeded() {
        when(rateLimiter.isBlocked(anyString())).thenReturn(true);
        when(rateLimiter.getBlockTimeRemaining(anyString())).thenReturn(60L);

        assertThrows(RateLimitExceededException.class, () ->
            authService.performAuthentication(loginRequest)
        );
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should authenticate User successfully")
    void testPerformAuthentication_UserSuccess() {
        when(rateLimiter.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // FIX: Use Builder
        JWTResponseDTO jwtResponse = JWTResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .role(Role.USER)
                .build();
                
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any(Role.class))).thenReturn(jwtResponse);

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertTrue(response.getSuccess());
        assertNotNull(response.getTokens());
        verify(rateLimiter).clearAttempts(anyString());
    }

    @Test
    @DisplayName("Should authenticate Admin successfully")
    void testPerformAuthentication_AdminSuccess() {
        loginRequest.setEmail("admin@example.com");
        when(rateLimiter.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(anyString())).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // FIX: Use Builder
        JWTResponseDTO jwtResponse = JWTResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .role(Role.ADMIN)
                .build();
                
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any(Role.class))).thenReturn(jwtResponse);

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertTrue(response.getSuccess());
        verify(rateLimiter).clearAttempts(anyString());
    }

    @Test
    @DisplayName("Should authenticate Superadmin successfully with MFA")
    void testPerformAuthentication_SuperadminSuccess() {
        loginRequest.setEmail("super@example.com");
        loginRequest.setMfaCode("123456"); 
        
        when(rateLimiter.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(anyString())).thenReturn(Optional.of(superadmin));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mfaService.validateMFACode(anyString(), eq(Role.SUPERADMIN), anyString())).thenReturn(true);

        // FIX: Use Builder
        JWTResponseDTO jwtResponse = JWTResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .role(Role.SUPERADMIN)
                .build();
                
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any(Role.class))).thenReturn(jwtResponse);

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertTrue(response.getSuccess());
    }
}