package com.example.springboot.security.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;
    private final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    @DisplayName("Login Failed - Increments Attempts")
    void testLoginFailed() {
        assertFalse(loginAttemptService.isBlocked(EMAIL));
        
        loginAttemptService.loginFailed(EMAIL); // 1
        assertFalse(loginAttemptService.isBlocked(EMAIL));
        
        loginAttemptService.loginFailed(EMAIL); // 2
        loginAttemptService.loginFailed(EMAIL); // 3
        loginAttemptService.loginFailed(EMAIL); // 4
        assertFalse(loginAttemptService.isBlocked(EMAIL));
        
        loginAttemptService.loginFailed(EMAIL); // 5 (Max)
        assertTrue(loginAttemptService.isBlocked(EMAIL));
    }

    @Test
    @DisplayName("Login Succeeded - Clears Attempts")
    void testLoginSucceeded() {
        loginAttemptService.loginFailed(EMAIL);
        loginAttemptService.loginFailed(EMAIL);
        
        // Should have attempts in cache (internal state)
        
        loginAttemptService.loginSucceeded(EMAIL);
        
        // Cache invalidated, so attempts count should reset to 0 (default loader)
        assertFalse(loginAttemptService.isBlocked(EMAIL));
    }
}