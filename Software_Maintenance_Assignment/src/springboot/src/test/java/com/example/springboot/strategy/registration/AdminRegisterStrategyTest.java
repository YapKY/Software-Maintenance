package com.example.springboot.strategy.registration;

import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.dto.request.AdminRegisterRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.enums.Gender;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.UnauthorizedException;
import com.example.springboot.model.Admin;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.service.MFAService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminRegisterStrategyTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MFAService mfaService;
    @Mock
    private RecaptchaAdapter recaptchaAdapter;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminRegisterStrategy adminRegisterStrategy;

    private AdminRegisterRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // Setup Security Context Mocking
        SecurityContextHolder.setContext(securityContext);

        // Setup common valid request data
        validRequest = AdminRegisterRequestDTO.builder()
                .email("newadmin@example.com")
                .staffPass("password123")
                .name("New Admin")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE) // Assuming Gender Enum exists
                .position("Manager")
                .mfaEnabled(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- Helper to mock Security Context Roles ---
    private void mockSecurityContext(String role, String username) {
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        doReturn(Collections.singletonList(authority)).when(authentication).getAuthorities();
        lenient().when(authentication.getName()).thenReturn(username); // use lenient for cases where name isn't checked
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    // --- Positive Tests ---

    @Test
    void testRegister_Success_AsSuperadmin() {
        // Arrange
        mockSecurityContext("ROLE_SUPERADMIN", "superadmin-id");
        when(adminRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        
        Admin savedAdmin = Admin.builder()
                .email(validRequest.getEmail())
                .role(Role.ADMIN)
                .build();
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        // Act
        AuthResponseDTO response = adminRegisterStrategy.register(validRequest, "dummy-token");

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Staff account created successfully.", response.getMessage());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testGetStrategyName() {
        assertEquals("SUPERADMIN_ADMIN_CREATION", adminRegisterStrategy.getStrategyName());
    }

    // --- Negative Tests ---

    @Test
    void testRegister_Fail_UnauthorizedCaller() {
        // Arrange: Caller is just a regular ADMIN, not SUPERADMIN
        mockSecurityContext("ROLE_ADMIN", "admin-id");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            adminRegisterStrategy.register(validRequest, "dummy-token")
        );
        verify(adminRepository, never()).save(any());
    }

    @Test
    void testRegister_Fail_EmailAlreadyExists() {
        // Arrange
        mockSecurityContext("ROLE_SUPERADMIN", "superadmin-id");
        when(adminRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
            adminRegisterStrategy.register(validRequest, "dummy-token")
        );
        assertEquals("Staff creation failed: Email already registered", exception.getMessage());
    }

    @Test
    void testRegister_Fail_InvalidDataType() {
        // Arrange
        mockSecurityContext("ROLE_SUPERADMIN", "superadmin-id");
        Object invalidData = new Object(); // Not an AdminRegisterRequestDTO

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
            adminRegisterStrategy.register(invalidData, "dummy-token")
        );
        assertEquals("Invalid registration format", exception.getMessage());
    }

    @Test
    void testRegister_Fail_RepositoryException() {
        // Arrange
        mockSecurityContext("ROLE_SUPERADMIN", "superadmin-id");
        when(adminRepository.existsByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
            adminRegisterStrategy.register(validRequest, "dummy-token")
        );
        assertTrue(exception.getMessage().contains("Staff creation failed"));
    }
}