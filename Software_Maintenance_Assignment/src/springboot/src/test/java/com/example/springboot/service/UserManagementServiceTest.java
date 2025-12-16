package com.example.springboot.service;

import com.example.springboot.dto.request.PasswordChangeRequestDTO;
import com.example.springboot.dto.response.AdminProfileDTO;
import com.example.springboot.dto.response.MessageResponseDTO;
import com.example.springboot.dto.response.SuperadminProfileDTO;
import com.example.springboot.dto.response.UserProfileDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.UnauthorizedException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.model.Admin;
import com.example.springboot.model.Superadmin;
import com.example.springboot.model.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Helper to mock security context.
     * Uses lenient() to prevent UnnecessaryStubbingException if a test doesn't use
     * a specific call.
     */
    private void mockSecurityContext(String userId, String role) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(userId);
        if (role != null) {
            lenient().doReturn(Collections.singletonList(new SimpleGrantedAuthority(role)))
                    .when(authentication).getAuthorities();
        }
    }

    @Test
    void testGetCurrentUserProfile_Success() {
        String userId = "user123";
        mockSecurityContext(userId, null);

        User user = User.builder()
                .custId(userId)
                .email("user@test.com")
                .name("User Name")
                .role(Role.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileDTO result = userManagementService.getCurrentUserProfile();

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("user@test.com", result.getEmail());
    }

    @Test
    void testGetCurrentUserProfile_UserNotFound() {
        mockSecurityContext("unknown", null);
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userManagementService.getCurrentUserProfile());
    }

    @Test
    void testGetCurrentUserProfile_Exception() {
        mockSecurityContext("user123", null);
        when(userRepository.findById("user123")).thenThrow(new RuntimeException("DB Error"));

        assertThrows(UserNotFoundException.class, () -> userManagementService.getCurrentUserProfile());
    }

    @Test
    void testGetCurrentAdminProfile_Success() {
        String adminId = "admin123";
        mockSecurityContext(adminId, null);

        Admin admin = Admin.builder()
                .staffId(adminId)
                .email("admin@test.com")
                .role(Role.ADMIN)
                .build();

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        AdminProfileDTO result = userManagementService.getCurrentAdminProfile();

        assertNotNull(result);
        assertEquals(adminId, result.getStaffId());
    }

    @Test
    void testGetCurrentAdminProfile_NotFound() {
        mockSecurityContext("unknown", null);
        when(adminRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userManagementService.getCurrentAdminProfile());
    }

    @Test
    void testGetCurrentSuperadminProfile_Success() {
        String saId = "sa123";
        mockSecurityContext(saId, null);

        Superadmin superadmin = Superadmin.builder()
                .id(saId)
                .email("sa@test.com")
                .role(Role.SUPERADMIN)
                .build();

        when(superadminRepository.findById(saId)).thenReturn(Optional.of(superadmin));
        when(adminRepository.countAdminsCreatedBy(saId)).thenReturn(5);

        SuperadminProfileDTO result = userManagementService.getCurrentSuperadminProfile();

        assertNotNull(result);
        assertEquals(saId, result.getId());
        assertEquals(5, result.getTotalAdminsCreated());
    }

    @Test
    void testChangePassword_User_Success() {
        String userId = "user123";
        mockSecurityContext(userId, "ROLE_USER");

        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("oldPass", "newPass", "newPass");
        User user = User.builder().custId(userId).custPassword("encodedOldPass").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        MessageResponseDTO response = userManagementService.changePassword(request);

        assertTrue(response.getSuccess());
        verify(userRepository).save(user);
        assertEquals("encodedNewPass", user.getCustPassword());
    }

    @Test
    void testChangePassword_Admin_Success() {
        String adminId = "admin123";
        mockSecurityContext(adminId, "ROLE_ADMIN");

        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("oldPass", "newPass", "newPass");
        Admin admin = Admin.builder().staffId(adminId).staffPass("encodedOldPass").build();

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        MessageResponseDTO response = userManagementService.changePassword(request);

        assertTrue(response.getSuccess());
        verify(adminRepository).save(admin);
        assertEquals("encodedNewPass", admin.getStaffPass());
    }

    @Test
    void testChangePassword_Superadmin_Success() {
        String saId = "sa123";
        mockSecurityContext(saId, "ROLE_SUPERADMIN");

        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("oldPass", "newPass", "newPass");
        Superadmin sa = Superadmin.builder().id(saId).password("encodedOldPass").build();

        when(superadminRepository.findById(saId)).thenReturn(Optional.of(sa));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        MessageResponseDTO response = userManagementService.changePassword(request);

        assertTrue(response.getSuccess());
        verify(superadminRepository).save(sa);
    }

    @Test
    void testChangePassword_Mismatch() {
        mockSecurityContext("user1", "ROLE_USER");
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("old", "new1", "new2");

        assertThrows(IllegalArgumentException.class, () -> userManagementService.changePassword(request));
    }

    @Test
    void testChangePassword_WrongCurrentPassword() {
        String userId = "user123";
        mockSecurityContext(userId, "ROLE_USER");

        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("wrong", "new", "new");
        User user = User.builder().custId(userId).custPassword("encodedOld").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedOld")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userManagementService.changePassword(request));
    }

    @Test
    void testGetUserProfileById_AdminAccess_Success() {
        mockSecurityContext("admin1", "ROLE_ADMIN");
        String targetUserId = "userTarget";
        User user = User.builder().custId(targetUserId).email("target@test.com").build();

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(user));

        UserProfileDTO result = userManagementService.getUserProfileById(targetUserId);

        assertNotNull(result);
        assertEquals(targetUserId, result.getId());
    }

    @Test
    void testGetUserProfileById_Unauthorized() {
        mockSecurityContext("user1", "ROLE_USER"); // User trying to access

        assertThrows(UnauthorizedException.class, () -> userManagementService.getUserProfileById("someId"));
    }

    @Test
    void testGetAdminsCreatedBySuperadmin_Success() {
        String saId = "sa123";
        mockSecurityContext(saId, null);

        List<Admin> admins = Collections.singletonList(Admin.builder().staffId("admin1").build());
        when(adminRepository.findByCreatedBy(saId)).thenReturn(admins);

        List<AdminProfileDTO> result = userManagementService.getAdminsCreatedBySuperadmin();

        assertFalse(result.isEmpty());
        assertEquals("admin1", result.get(0).getStaffId());
    }

    @Test
    void testUpdateUserProfile_Success() {
        String userId = "user1";
        User user = User.builder().custId(userId).name("Old Name").phoneNumber("111").build();
        User updatedUser = User.builder().custId(userId).name("New Name").phoneNumber("222").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByPhoneNumber("222")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserProfileDTO result = userManagementService.updateUserProfile(userId, "New Name", "222");

        assertEquals("New Name", result.getFullName());
        assertEquals("222", result.getPhoneNumber());
    }

    @Test
    void testUpdateUserProfile_DuplicatePhone() {
        String userId = "user1";
        User user = User.builder().custId(userId).phoneNumber("111").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByPhoneNumber("222")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userManagementService.updateUserProfile(userId, "Name", "222"));
    }

    @Test
    void testDeleteUserAccount_Success() {
        String userId = "user1";
        mockSecurityContext(userId, "ROLE_USER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        userManagementService.deleteUserAccount(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUserAccount_Unauthorized() {
        // This was likely causing the error because it checks the ID before checking
        // the role (authorities)
        // so getAuthorities was never called, but we stubbed it.
        mockSecurityContext("user1", "ROLE_USER");

        assertThrows(UnauthorizedException.class, () -> userManagementService.deleteUserAccount("user2") // Trying to
                                                                                                         // delete
                                                                                                         // different
                                                                                                         // user
        );
    }
}
