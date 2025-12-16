package com.example.springboot.decorator;

import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.dto.response.MFAStatusDTO;
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
import com.example.springboot.service.MFAService;
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
    private User mockUser;
    private Admin mockAdmin;
    private Superadmin mockSuperadmin;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
        loginRequest.setRecaptchaToken("valid-token");

        mockUser = new User();
        mockUser.setCustId("u1");
        mockUser.setEmail("test@example.com");
        mockUser.setCustPassword("encodedPwd"); // Corrected field name
        mockUser.setRole(Role.USER);
        mockUser.setAccountLocked(false);
        mockUser.setEmailVerified(true);
        mockUser.setFailedLoginAttempts(0);
        
        mockAdmin = new Admin();
        mockAdmin.setStaffId("a1"); // Corrected field name
        mockAdmin.setEmail("admin@example.com");
        mockAdmin.setStaffPass("encodedPwd"); // Corrected field name
        mockAdmin.setRole(Role.ADMIN);
        
        mockSuperadmin = new Superadmin();
        mockSuperadmin.setId("sa1");
        mockSuperadmin.setEmail("super@example.com");
        mockSuperadmin.setPassword("encodedPwd");
        mockSuperadmin.setRole(Role.SUPERADMIN);
        mockSuperadmin.setMfaEnabled(true);
    }

    // --- Rate Limit Tests ---

    @Test
    @DisplayName("Login - Rate Limit Exceeded")
    void testLogin_RateLimitExceeded() {
        when(rateLimiter.isBlocked("test@example.com")).thenReturn(true);
        when(rateLimiter.getBlockTimeRemaining("test@example.com")).thenReturn(300L);

        assertThrows(RateLimitExceededException.class, () -> authService.performAuthentication(loginRequest));
    }

    // --- User Login Tests ---

    @Test
    @DisplayName("Login User - Success (No MFA)")
    void testLogin_User_Success_NoMFA() {
        // Setup: User found in repository
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        // User has MFA disabled by default in setup
        
        JWTResponseDTO jwt = JWTResponseDTO.builder().accessToken("access").build();
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any())).thenReturn(jwt);

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertTrue(response.getSuccess());
        assertFalse(response.getRequiresMfa());
        assertNotNull(response.getTokens());
        
        verify(userRepository).save(mockUser); // Updates last login
        verify(rateLimiter).clearAttempts(loginRequest.getEmail());
    }

    @Test
    @DisplayName("Login User - Failure (Wrong Password)")
    void testLogin_User_WrongPassword() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPwd")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));

        verify(rateLimiter).recordFailedAttempt(loginRequest.getEmail());
        verify(userRepository).save(mockUser);
        assertEquals(1, mockUser.getFailedLoginAttempts());
    }
    
    @Test
    @DisplayName("Login User - Account Locked")
    void testLogin_User_AccountLocked() {
        mockUser.setAccountLocked(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        // Password matching isn't checked if locked check comes first, but strict stubbing might require handling
        // Based on implementation: Locked check comes first.
        
        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }

    @Test
    @DisplayName("Login User - MFA Required (Step 1)")
    void testLogin_User_MFARequired() {
        mockUser.setMfaEnabled(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        // LoginRequest has no MFA code by default
        
        when(jwtTokenProvider.generateMFASessionToken(anyString(), anyString(), any())).thenReturn("mfa-session-token");

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertFalse(response.getSuccess()); // Success is false because MFA is needed
        assertTrue(response.getRequiresMfa());
        assertEquals("mfa-session-token", response.getMfaSessionToken());
    }

    @Test
    @DisplayName("Login User - MFA Validation (Step 2)")
    void testLogin_User_MFAValidation() {
        mockUser.setMfaEnabled(true);
        loginRequest.setMfaCode("123456"); // Code provided
        
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mfaService.validateMFACode(anyString(), any(), eq("123456"))).thenReturn(true);
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any())).thenReturn(JWTResponseDTO.builder().build());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertTrue(response.getSuccess());
        assertNotNull(response.getTokens());
    }

    @Test
    @DisplayName("Login User - MFA Invalid Code")
    void testLogin_User_MFAInvalid() {
        mockUser.setMfaEnabled(true);
        loginRequest.setMfaCode("000000");
        
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mfaService.validateMFACode(anyString(), any(), eq("000000"))).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }

    // --- Admin Login Tests ---

    @Test
    @DisplayName("Login Admin - Success")
    void testLogin_Admin_Success() {
        loginRequest.setEmail("admin@example.com");
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(mockAdmin));
        
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any())).thenReturn(JWTResponseDTO.builder().build());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);
        assertTrue(response.getSuccess());
        verify(adminRepository).save(mockAdmin);
    }

    @Test
    @DisplayName("Login Admin - Not Found")
    void testLogin_Admin_NotFound() {
        loginRequest.setEmail("admin@example.com");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }

    // --- Superadmin Login Tests ---

    @Test
    @DisplayName("Login Superadmin - Success")
    void testLogin_Superadmin_Success() {
        loginRequest.setEmail("super@example.com");
        loginRequest.setMfaCode("123456"); // Superadmin needs MFA code in request to succeed
        
        when(userRepository.findByEmail("super@example.com")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("super@example.com")).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail("super@example.com")).thenReturn(Optional.of(mockSuperadmin));
        
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mfaService.validateMFACode(anyString(), any(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateTokens(anyString(), anyString(), any())).thenReturn(JWTResponseDTO.builder().build());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);
        assertTrue(response.getSuccess());
    }

    @Test
    @DisplayName("Handle Failed Login - Locking Logic")
    void testLogin_User_Locking() {
        mockUser.setFailedLoginAttempts(4); // Next failure should lock it
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));

        assertEquals(5, mockUser.getFailedLoginAttempts());
        assertTrue(mockUser.getAccountLocked());
        verify(userRepository).save(mockUser);
    }
}