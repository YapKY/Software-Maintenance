package com.example.springboot.model;

import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilderAndDefaults() {
        User user = User.builder()
                .email("user@test.com")
                .build();

        // Check assigned values
        assertEquals("user@test.com", user.getEmail());

        // Check @Builder.Default values
        assertEquals(Role.USER, user.getRole());
        assertEquals(AuthProvider.EMAIL, user.getAuthProvider());
        assertFalse(user.getMfaEnabled());
        assertFalse(user.getEmailVerified());
        assertFalse(user.getAccountLocked());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNotNull(user.getCreatedAt());
    }
}