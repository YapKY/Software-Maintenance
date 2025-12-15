package com.example.springboot.service;

import com.example.springboot.dto.request.PasswordChangeRequestDTO;
import com.example.springboot.dto.response.AdminProfileDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.dto.response.SuperadminProfileDTO;
import com.example.springboot.dto.response.UserProfileDTO;
import com.example.springboot.enums.Gender;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.model.Admin;
import com.example.springboot.model.Superadmin;
import com.example.springboot.model.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for UserManagementService
 * Tests user/staff profile management and password operations
 */
@DisplayName("User Management Service Tests")
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private SuperadminRepository superadminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserManagementService userManagementService;

    private User testUser;
    private Admin testAdmin;
    private Superadmin testSuperadmin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);

        // Setup test User
        testUser = new User();
        testUser.setCustId("user123");
        testUser.setEmail("user@example.com");
        testUser.setName("John Doe");
        testUser.setCustIcNo("123456-12-1234");
        testUser.setGender(Gender.MALE);
        testUser.setPhoneNumber("0123456789");
        testUser.setRole(Role.USER);
        testUser.setMfaEnabled(false);
        testUser.setEmailVerified(true);
        testUser.setLastLoginAt(LocalDateTime.now());
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setCustPassword("encodedPassword");

        // Setup test Admin
        testAdmin = new Admin();
        testAdmin.setStaffId("admin123");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setName("Jane Smith");
        testAdmin.setPhoneNumber("0123456780");
        testAdmin.setGender(Gender.FEMALE);
        testAdmin.setPosition("Manager");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setMfaEnabled(true);
        testAdmin.setCreatedBy("superadmin1");
        testAdmin.setLastLoginAt(LocalDateTime.now());
        testAdmin.setCreatedAt(LocalDateTime.now());
        testAdmin.setStaffPass("encodedPassword");

        // Setup test Superadmin
        testSuperadmin = new Superadmin();
        testSuperadmin.setId("superadmin1");
        testSuperadmin.setEmail("super@example.com");
        testSuperadmin.setFullName("Super Admin");
        testSuperadmin.setRole(Role.SUPERADMIN);
        testSuperadmin.setMfaEnabled(true);
        testSuperadmin.setLastLoginAt(LocalDateTime.now());
        testSuperadmin.setCreatedAt(LocalDateTime.now());
        testSuperadmin.setPassword("encodedPassword");
    }

    // ==================== GET USER PROFILE TESTS ====================

    @Test
    @DisplayName("Should get current user profile successfully")
    void testGetCurrentUserProfile_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // Act
        UserProfileDTO result = userManagementService.getCurrentUserProfile();

        // Assert
        assertNotNull(result);
        assertEquals("user123", result.getId());
        assertEquals("user@example.com", result.getEmail());
        assertEquals("John Doe", result.getFullName());
        assertEquals("123456-12-1234", result.getCustIcNo());
        assertEquals(Gender.MALE, result.getGender());
        assertEquals("0123456789", result.getPhoneNumber());
        assertEquals(Role.USER, result.getRole());
        assertFalse(result.getMfaEnabled());
        assertTrue(result.getEmailVerified());

        verify(userRepository, times(1)).findById("user123");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void testGetCurrentUserProfile_UserNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userManagementService.getCurrentUserProfile());
    }

    // ==================== GET ADMIN PROFILE TESTS ====================

    @Test
    @DisplayName("Should get current admin profile successfully")
    void testGetCurrentAdminProfile_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin123");
        when(adminRepository.findById("admin123")).thenReturn(Optional.of(testAdmin));

        // Act
        AdminProfileDTO result = userManagementService.getCurrentAdminProfile();

        // Assert
        assertNotNull(result);
        assertEquals("admin123", result.getStaffId());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("Jane Smith", result.getName());
        assertEquals("0123456780", result.getPhoneNumber());
        assertEquals(Gender.FEMALE, result.getGender());
        assertEquals("Manager", result.getPosition());
        assertEquals(Role.ADMIN, result.getRole());
        assertTrue(result.getMfaEnabled());
        assertEquals("superadmin1", result.getCreatedBy());

        verify(adminRepository, times(1)).findById("admin123");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when admin not found")
    void testGetCurrentAdminProfile_AdminNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        when(adminRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userManagementService.getCurrentAdminProfile());
    }

    // ==================== GET SUPERADMIN PROFILE TESTS ====================

    @Test
    @DisplayName("Should get current superadmin profile successfully")
    void testGetCurrentSuperadminProfile_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("superadmin1");
        when(superadminRepository.findById("superadmin1")).thenReturn(Optional.of(testSuperadmin));
        when(adminRepository.countAdminsCreatedBy("superadmin1")).thenReturn(5);

        // Act
        SuperadminProfileDTO result = userManagementService.getCurrentSuperadminProfile();

        // Assert
        assertNotNull(result);
        assertEquals("superadmin1", result.getId());
        assertEquals("super@example.com", result.getEmail());
        assertEquals("Super Admin", result.getFullName());
        assertEquals(Role.SUPERADMIN, result.getRole());
        assertTrue(result.getMfaEnabled());
        assertEquals(5, result.getTotalAdminsCreated());

        verify(superadminRepository, times(1)).findById("superadmin1");
        verify(adminRepository, times(1)).countAdminsCreatedBy("superadmin1");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when superadmin not found")
    void testGetCurrentSuperadminProfile_SuperadminNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        when(superadminRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userManagementService.getCurrentSuperadminProfile());
    }

    // ==================== CHANGE PASSWORD TESTS - USER ====================

    @Test
    @DisplayName("Should change password for user successfully")
    void testChangePassword_User_Success() {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user123");
        when(authentication.getAuthorities()).thenAnswer(inv -> authorities);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        MessageResponseDTO result = userManagementService.changePassword(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("Password changed successfully", result.getMessage());

        verify(userRepository, times(1)).findById("user123");
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail when new passwords do not match")
    void testChangePassword_PasswordMismatch() {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("differentPassword")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user123");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userManagementService.changePassword(request));
    }

    @Test
    @DisplayName("Should fail when current password is incorrect for user")
    void testChangePassword_User_IncorrectCurrentPassword() {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user123");
        when(authentication.getAuthorities()).thenAnswer(inv -> authorities);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userManagementService.changePassword(request));
    }

    // ==================== CHANGE PASSWORD TESTS - ADMIN ====================

    @Test
    @DisplayName("Should change password for admin successfully")
    void testChangePassword_Admin_Success() {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin123");
        when(authentication.getAuthorities()).thenAnswer(inv -> authorities);
        when(adminRepository.findById("admin123")).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        // Act
        MessageResponseDTO result = userManagementService.changePassword(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("Password changed successfully", result.getMessage());

        verify(adminRepository, times(1)).findById("admin123");
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should fail when current password is incorrect for admin")
    void testChangePassword_Admin_IncorrectCurrentPassword() {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin123");
        when(authentication.getAuthorities()).thenAnswer(inv -> authorities);
        when(adminRepository.findById("admin123")).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userManagementService.changePassword(request));
    }

    // ==================== CHANGE PASSWORD TESTS - SUPERADMIN ====================

    @Test
    @DisplayName("Should change password for superadmin successfully")
    void testChangePassword_Superadmin_Success() {
        // Arrange
        PasswordChangeRequestDTO request = PasswordChangeRequestDTO.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_SUPERADMIN"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("superadmin1");
        when(authentication.getAuthorities()).thenAnswer(inv -> authorities);
        when(superadminRepository.findById("superadmin1")).thenReturn(Optional.of(testSuperadmin));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(superadminRepository.save(any(Superadmin.class))).thenReturn(testSuperadmin);

        // Act
        MessageResponseDTO result = userManagementService.changePassword(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("Password changed successfully", result.getMessage());

        verify(superadminRepository, times(1)).findById("superadmin1");
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(superadminRepository, times(1)).save(any(Superadmin.class));
    }

    // ==================== GET ADMINS BY SUPERADMIN TESTS ====================

    @Test
    @DisplayName("Should get list of admins created by superadmin")
    void testGetAdminsCreatedBySuperadmin_Success() {
        // Arrange
        Admin admin1 = new Admin();
        admin1.setStaffId("admin1");
        admin1.setEmail("admin1@example.com");
        admin1.setName("Admin One");

        Admin admin2 = new Admin();
        admin2.setStaffId("admin2");
        admin2.setEmail("admin2@example.com");
        admin2.setName("Admin Two");

        List<Admin> admins = List.of(admin1, admin2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("superadmin1");
        when(adminRepository.findByCreatedBy("superadmin1")).thenReturn(admins);

        // Act
        List<AdminProfileDTO> result = userManagementService.getAdminsCreatedBySuperadmin();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("admin1", result.get(0).getStaffId());
        assertEquals("admin2", result.get(1).getStaffId());

        verify(adminRepository, times(1)).findByCreatedBy("superadmin1");
    }

    @Test
    @DisplayName("Should return empty list when no admins created")
    void testGetAdminsCreatedBySuperadmin_EmptyList() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("superadmin1");
        when(adminRepository.findByCreatedBy("superadmin1")).thenReturn(Collections.emptyList());

        // Act
        List<AdminProfileDTO> result = userManagementService.getAdminsCreatedBySuperadmin();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
