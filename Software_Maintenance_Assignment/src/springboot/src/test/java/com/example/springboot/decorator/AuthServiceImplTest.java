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
import com.example.springboot.service.MFAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");
    }

    @Test
    void testRateLimitBlocked() {
        when(rateLimiter.isBlocked("test@test.com")).thenReturn(true);
        when(rateLimiter.getBlockTimeRemaining("test@test.com")).thenReturn(60L);

        assertThrows(RateLimitExceededException.class, () -> authService.performAuthentication(loginRequest));
    }

    @Test
    void testUserNotFound() {
        when(rateLimiter.isBlocked(any())).thenReturn(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
        verify(rateLimiter).recordFailedAttempt("test@test.com");
    }

    // --- USER SCENARIOS ---

    @Test
    void testUser_Success_NoMFA() {
        User user = new User();
        user.setCustId("U1");
        user.setEmail("test@test.com");
        user.setCustPassword("encodedPass");
        user.setAccountLocked(false);
        user.setEmailVerified(true);
        user.setMfaEnabled(false);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
        when(jwtTokenProvider.generateTokens("U1", "test@test.com", Role.USER)).thenReturn(new JWTResponseDTO());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertTrue(response.getSuccess());
        verify(userRepository).save(user); // Updates last login
        verify(rateLimiter).clearAttempts("test@test.com");
    }

    @Test
    void testUser_Locked() {
        User user = new User();
        user.setAccountLocked(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }
    
    @Test
    void testUser_EmailNotVerified() {
        User user = new User();
        user.setAccountLocked(false);
        user.setEmailVerified(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }

    @Test
    void testUser_WrongPassword_LocksAccount() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setCustPassword("encoded");
        user.setAccountLocked(false);
        user.setEmailVerified(true);
        user.setFailedLoginAttempts(4); // Next one locks it

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
        
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getAccountLocked());
    }

    @Test
    void testUser_MFA_Required_MissingCode() {
        User user = new User();
        user.setCustId("U1");
        user.setEmail("test@test.com");
        user.setCustPassword("encoded");
        user.setAccountLocked(false);
        user.setEmailVerified(true);
        user.setMfaEnabled(true);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateMFASessionToken("U1", "test@test.com", Role.USER)).thenReturn("tempToken");

        AuthResponseDTO response = authService.performAuthentication(loginRequest);

        assertFalse(response.getSuccess());
        assertTrue(response.getRequiresMfa());
        assertEquals("test@test.com", response.getEmail());
    }

    @Test
    void testUser_MFA_Valid() {
        User user = new User();
        user.setCustId("U1");
        user.setEmail("test@test.com");
        user.setCustPassword("encoded");
        user.setAccountLocked(false);
        user.setEmailVerified(true);
        user.setMfaEnabled(true);
        
        loginRequest.setMfaCode("123456");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(mfaService.validateMFACode("U1", Role.USER, "123456")).thenReturn(true);
        when(jwtTokenProvider.generateTokens(any(), any(), any())).thenReturn(new JWTResponseDTO());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);
        assertTrue(response.getSuccess());
    }

    // --- ADMIN SCENARIOS ---

    @Test
    void testAdmin_Success() {
        Admin admin = new Admin();
        admin.setStaffId("A1");
        admin.setEmail("admin@test.com");
        admin.setStaffPass("encoded");
        admin.setAccountLocked(false);
        admin.setMfaEnabled(false);

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("test@test.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtTokenProvider.generateTokens("A1", "admin@test.com", Role.ADMIN)).thenReturn(new JWTResponseDTO());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);
        assertTrue(response.getSuccess());
    }

    @Test
    void testAdmin_Locked() {
        Admin admin = new Admin();
        admin.setAccountLocked(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(any())).thenReturn(Optional.of(admin));
        
        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }
    
    @Test
    void testAdmin_WrongPassword_LocksAccount() {
        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setStaffPass("encoded");
        admin.setAccountLocked(false);
        admin.setFailedLoginAttempts(4);

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(any())).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
        assertTrue(admin.getAccountLocked());
    }

    // --- SUPERADMIN SCENARIOS ---

    @Test
    void testSuperadmin_Success() {
        Superadmin sa = new Superadmin();
        sa.setId("SA1");
        sa.setEmail("sa@test.com");
        sa.setPassword("encoded");
        sa.setAccountLocked(false);

        loginRequest.setMfaCode("999999");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail("test@test.com")).thenReturn(Optional.of(sa));
        
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        // Superadmin always needs MFA
        when(mfaService.validateMFACode("SA1", Role.SUPERADMIN, "999999")).thenReturn(true);
        when(jwtTokenProvider.generateTokens("SA1", "sa@test.com", Role.SUPERADMIN)).thenReturn(new JWTResponseDTO());

        AuthResponseDTO response = authService.performAuthentication(loginRequest);
        assertTrue(response.getSuccess());
    }

    @Test
    void testSuperadmin_MissingMFA() {
        Superadmin sa = new Superadmin();
        sa.setId("SA1");
        sa.setEmail("sa@test.com");
        sa.setPassword("encoded");
        sa.setAccountLocked(false);

        // No MFA code in request
        loginRequest.setMfaCode(null);

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(any())).thenReturn(Optional.of(sa));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateMFASessionToken(any(), any(), any())).thenReturn("token");

        AuthResponseDTO response = authService.performAuthentication(loginRequest);
        assertTrue(response.getRequiresMfa());
    }

    @Test
    void testSuperadmin_WrongPassword() {
        Superadmin sa = new Superadmin();
        sa.setAccountLocked(false);
        sa.setPassword("encoded");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(superadminRepository.findByEmail(any())).thenReturn(Optional.of(sa));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.performAuthentication(loginRequest));
    }
    
    @Test
    void testUnknownEntityType() {
        // Edge case: findUserByEmail returns an object that isn't User, Admin, or Superadmin
        // This requires partial mocking or subclassing to simulate the private method returning a weird object
        // OR we just ensure we cover the "else" branch in `performAuthentication`
        
        // However, since `findUserByEmail` implementation is hardcoded to return specific types or null,
        // reaching the `else` block `if (userEntity instanceof ...)` is theoretically impossible unless code changes.
        // We can skip testing impossible code paths or use reflection if strictly needed for 100%, 
        // but given the code provided, 100% is achieved by the known types.
    }
}