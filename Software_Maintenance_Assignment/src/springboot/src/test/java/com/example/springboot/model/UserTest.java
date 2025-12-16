package com.example.springboot.model;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model Tests - 100% Coverage")
class UserTest {

    @Test
    @DisplayName("Should create User with all fields using builder")
    void testBuilderAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .custId("user123")
                .email("user@example.com")
                .custPassword("hashedPassword123")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .authProvider(AuthProvider.EMAIL)
                .providerId(null)
                .mfaEnabled(false)
                .emailVerified(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .lastLoginAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        assertEquals("user123", user.getCustId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("hashedPassword123", user.getCustPassword());
        assertEquals("John Doe", user.getName());
        assertEquals("123456-12-1234", user.getCustIcNo());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals("012-3456789", user.getPhoneNumber());
        assertEquals(Role.USER, user.getRole());
        assertEquals(AuthProvider.EMAIL, user.getAuthProvider());
        assertFalse(user.getMfaEnabled());
        assertTrue(user.getEmailVerified());
        assertFalse(user.getAccountLocked());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNotNull(user.getLastLoginAt());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test default values")
    void testDefaultValues() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test User")
                .build();
        
        assertEquals(Role.USER, user.getRole());
        assertEquals(AuthProvider.EMAIL, user.getAuthProvider());
        assertFalse(user.getMfaEnabled());
        assertFalse(user.getEmailVerified());
        assertFalse(user.getAccountLocked());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test Google OAuth user")
    void testGoogleOAuthUser() {
        User user = User.builder()
                .custId("google-user-123")
                .email("user@gmail.com")
                .name("Google User")
                .authProvider(AuthProvider.GOOGLE)
                .providerId("google-provider-id-123")
                .emailVerified(true)
                .build();
        
        assertEquals(AuthProvider.GOOGLE, user.getAuthProvider());
        assertEquals("google-provider-id-123", user.getProviderId());
        assertTrue(user.getEmailVerified());
        assertNull(user.getCustPassword()); // OAuth users don't have passwords
    }

    @Test
    @DisplayName("Should test Facebook OAuth user")
    void testFacebookOAuthUser() {
        User user = User.builder()
                .custId("fb-user-456")
                .email("user@facebook.com")
                .name("Facebook User")
                .authProvider(AuthProvider.FACEBOOK)
                .providerId("fb-provider-id-456")
                .emailVerified(true)
                .build();
        
        assertEquals(AuthProvider.FACEBOOK, user.getAuthProvider());
        assertEquals("fb-provider-id-456", user.getProviderId());
        assertTrue(user.getEmailVerified());
    }

    @Test
    @DisplayName("Should test female gender")
    void testFemaleGender() {
        User user = User.builder()
                .custId("user-female")
                .email("jane@example.com")
                .name("Jane Doe")
                .custIcNo("654321-11-4321")
                .gender(Gender.FEMALE)
                .build();
        
        assertEquals(Gender.FEMALE, user.getGender());
    }

    @Test
    @DisplayName("Should test no-args constructor")
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getCustId());
        assertNull(user.getEmail());
        assertNull(user.getCustPassword());
        assertNull(user.getName());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(
            "user123", "user@example.com", "password123", "John Doe",
            "123456-12-1234", Gender.MALE, "012-3456789", Role.USER,
            AuthProvider.EMAIL, null, false, true, false, 0, now, now, now
        );
        
        assertEquals("user123", user.getCustId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password123", user.getCustPassword());
        assertEquals("John Doe", user.getName());
        assertEquals("123456-12-1234", user.getCustIcNo());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals("012-3456789", user.getPhoneNumber());
        assertEquals(Role.USER, user.getRole());
        assertEquals(AuthProvider.EMAIL, user.getAuthProvider());
        assertFalse(user.getMfaEnabled());
        assertTrue(user.getEmailVerified());
        assertFalse(user.getAccountLocked());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should test all setters")
    void testAllSetters() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        
        user.setCustId("123");
        user.setEmail("test@example.com");
        user.setCustPassword("password");
        user.setName("Test User");
        user.setCustIcNo("123456-12-1234");
        user.setGender(Gender.FEMALE);
        user.setPhoneNumber("012-3456789");
        user.setRole(Role.USER);
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setProviderId("google-123");
        user.setMfaEnabled(true);
        user.setEmailVerified(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(2);
        user.setLastLoginAt(now);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        assertEquals("123", user.getCustId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getCustPassword());
        assertEquals("Test User", user.getName());
        assertEquals("123456-12-1234", user.getCustIcNo());
        assertEquals(Gender.FEMALE, user.getGender());
        assertEquals("012-3456789", user.getPhoneNumber());
        assertEquals(Role.USER, user.getRole());
        assertEquals(AuthProvider.GOOGLE, user.getAuthProvider());
        assertEquals("google-123", user.getProviderId());
        assertTrue(user.getMfaEnabled());
        assertTrue(user.getEmailVerified());
        assertFalse(user.getAccountLocked());
        assertEquals(2, user.getFailedLoginAttempts());
        assertEquals(now, user.getLastLoginAt());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle failed login attempts increment")
    void testFailedLoginAttemptsIncrement() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .failedLoginAttempts(0)
                .build();
        
        assertEquals(0, user.getFailedLoginAttempts());
        
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        assertEquals(1, user.getFailedLoginAttempts());
        
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        assertEquals(2, user.getFailedLoginAttempts());
        
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        assertEquals(3, user.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should handle account locking")
    void testAccountLocking() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();
        
        assertFalse(user.getAccountLocked());
        
        // Simulate failed login attempts
        user.setFailedLoginAttempts(5);
        user.setAccountLocked(true);
        
        assertTrue(user.getAccountLocked());
        assertEquals(5, user.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should handle account unlocking and reset attempts")
    void testAccountUnlocking() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .accountLocked(true)
                .failedLoginAttempts(5)
                .build();
        
        assertTrue(user.getAccountLocked());
        assertEquals(5, user.getFailedLoginAttempts());
        
        // Unlock account
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        
        assertFalse(user.getAccountLocked());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should handle email verification")
    void testEmailVerification() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .emailVerified(false)
                .build();
        
        assertFalse(user.getEmailVerified());
        
        user.setEmailVerified(true);
        assertTrue(user.getEmailVerified());
    }

    @Test
    @DisplayName("Should handle MFA enablement")
    void testMfaEnablement() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .mfaEnabled(false)
                .build();
        
        assertFalse(user.getMfaEnabled());
        
        user.setMfaEnabled(true);
        assertTrue(user.getMfaEnabled());
    }

    @Test
    @DisplayName("Should test equals - same object")
    void testEqualsSameObject() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .name("Test")
                .build();
        
        assertEquals(user, user);
    }

    @Test
    @DisplayName("Should test equals - equal objects")
    void testEqualsEqualObjects() {
        LocalDateTime now = LocalDateTime.now();
        
        User user1 = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test User")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .authProvider(AuthProvider.EMAIL)
                .mfaEnabled(false)
                .emailVerified(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .lastLoginAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        User user2 = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test User")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .authProvider(AuthProvider.EMAIL)
                .mfaEnabled(false)
                .emailVerified(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .lastLoginAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Should test equals - different objects")
    void testEqualsDifferentObjects() {
        User user1 = User.builder()
                .custId("123")
                .email("test1@example.com")
                .build();
        
        User user2 = User.builder()
                .custId("456")
                .email("test2@example.com")
                .build();
        
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("Should test equals - null")
    void testEqualsNull() {
        User user = User.builder()
                .custId("123")
                .build();
        
        assertNotEquals(null, user);
    }

    @Test
    @DisplayName("Should test equals - different class")
    void testEqualsDifferentClass() {
        User user = User.builder()
                .custId("123")
                .build();
        
        assertNotEquals(user, new Object());
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .name("Test")
                .build();
        
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        User user = User.builder()
                .custId("user123")
                .email("user@example.com")
                .custPassword("password123")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .authProvider(AuthProvider.EMAIL)
                .build();
        
        String toString = user.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("user@example.com"));
        assertTrue(toString.contains("John Doe"));
    }

    @Test
    @DisplayName("Should test toString with null values")
    void testToStringWithNulls() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .name("Test")
                .build();
        
        String toString = user.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("123"));
    }

    @Test
    @DisplayName("Should test with different roles")
    void testDifferentRoles() {
        User userWithUserRole = User.builder()
                .custId("user1")
                .email("user@example.com")
                .name("User")
                .role(Role.USER)
                .build();
        
        User userWithAdminRole = User.builder()
                .custId("admin1")
                .email("admin@example.com")
                .name("Admin")
                .role(Role.ADMIN)
                .build();
        
        assertEquals(Role.USER, userWithUserRole.getRole());
        assertEquals(Role.ADMIN, userWithAdminRole.getRole());
    }

    @Test
    @DisplayName("Should test IC number variations")
    void testIcNumberVariations() {
        String[] icNumbers = {
            "123456-12-1234",
            "000000-00-0000",
            "999999-99-9999",
            "111111-11-1111"
        };
        
        for (String icNo : icNumbers) {
            User user = User.builder()
                    .custId("user" + icNo)
                    .email("user@example.com")
                    .name("Test User")
                    .custIcNo(icNo)
                    .build();
            
            assertEquals(icNo, user.getCustIcNo());
        }
    }

    @Test
    @DisplayName("Should test phone number variations")
    void testPhoneNumberVariations() {
        String[] phoneNumbers = {
            "012-3456789",
            "013-12345678",
            "011-1234567",
            "019-99999999"
        };
        
        for (String phone : phoneNumbers) {
            User user = User.builder()
                    .custId("user")
                    .email("user@example.com")
                    .name("Test User")
                    .phoneNumber(phone)
                    .build();
            
            assertEquals(phone, user.getPhoneNumber());
        }
    }

    @Test
    @DisplayName("Should handle null providerId for email auth")
    void testNullProviderIdForEmailAuth() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .authProvider(AuthProvider.EMAIL)
                .providerId(null)
                .build();
        
        assertEquals(AuthProvider.EMAIL, user.getAuthProvider());
        assertNull(user.getProviderId());
        assertNotNull(user.getCustPassword());
    }

    @Test
    @DisplayName("Should handle password change scenario")
    void testPasswordChange() {
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("oldPassword")
                .name("Test")
                .build();
        
        assertEquals("oldPassword", user.getCustPassword());
        
        user.setCustPassword("newHashedPassword");
        assertEquals("newHashedPassword", user.getCustPassword());
    }

    @Test
    @DisplayName("Should test last login tracking")
    void testLastLoginTracking() {
        LocalDateTime firstLogin = LocalDateTime.now().minusDays(1);
        LocalDateTime secondLogin = LocalDateTime.now();
        
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .lastLoginAt(firstLogin)
                .build();
        
        assertEquals(firstLogin, user.getLastLoginAt());
        
        user.setLastLoginAt(secondLogin);
        assertEquals(secondLogin, user.getLastLoginAt());
    }

    @Test
    @DisplayName("Should test updatedAt tracking")
    void testUpdatedAtTracking() {
        LocalDateTime created = LocalDateTime.now().minusDays(7);
        LocalDateTime updated = LocalDateTime.now();
        
        User user = User.builder()
                .custId("123")
                .email("test@example.com")
                .custPassword("password")
                .name("Test")
                .createdAt(created)
                .updatedAt(created)
                .build();
        
        assertEquals(created, user.getUpdatedAt());
        
        user.setUpdatedAt(updated);
        assertEquals(updated, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test empty string values")
    void testEmptyStringValues() {
        User user = User.builder()
                .custId("")
                .email("")
                .custPassword("")
                .name("")
                .custIcNo("")
                .phoneNumber("")
                .providerId("")
                .build();
        
        assertEquals("", user.getCustId());
        assertEquals("", user.getEmail());
        assertEquals("", user.getCustPassword());
        assertEquals("", user.getName());
        assertEquals("", user.getCustIcNo());
        assertEquals("", user.getPhoneNumber());
        assertEquals("", user.getProviderId());
    }

    @Test
    @DisplayName("Should test special characters in fields")
    void testSpecialCharacters() {
        User user = User.builder()
                .custId("user-123_test")
                .email("test+user@example.com")
                .name("O'Brien-Smith")
                .custIcNo("123456-12-1234")
                .phoneNumber("012-3456789")
                .build();
        
        assertEquals("user-123_test", user.getCustId());
        assertEquals("test+user@example.com", user.getEmail());
        assertEquals("O'Brien-Smith", user.getName());
    }
}