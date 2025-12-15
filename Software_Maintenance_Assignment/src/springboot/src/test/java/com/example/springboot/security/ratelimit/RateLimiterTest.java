package com.example.springboot.security.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    private RateLimiter rateLimiter;
    private final String IP = "127.0.0.1";

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter();
    }

    @Test
    @DisplayName("Basic Attempt Recording")
    void testRecordAttempt() {
        assertEquals(0, rateLimiter.getAttemptCount(IP));
        rateLimiter.recordFailedAttempt(IP);
        assertEquals(1, rateLimiter.getAttemptCount(IP));
        assertFalse(rateLimiter.isBlocked(IP));
    }

    @Test
    @DisplayName("Blocking Mechanism")
    void testBlocking() {
        // Max attempts is 5
        for (int i = 0; i < 5; i++) {
            assertFalse(rateLimiter.isBlocked(IP), "Should not be blocked at attempt " + i);
            rateLimiter.recordFailedAttempt(IP);
        }
        
        assertTrue(rateLimiter.isBlocked(IP), "Should be blocked after 5 failed attempts");
        assertEquals(0, rateLimiter.getRemainingAttempts(IP));
    }

    @Test
    @DisplayName("Clear Attempts")
    void testClearAttempts() {
        rateLimiter.recordFailedAttempt(IP);
        rateLimiter.recordFailedAttempt(IP);
        assertEquals(2, rateLimiter.getAttemptCount(IP));

        rateLimiter.clearAttempts(IP);
        assertEquals(0, rateLimiter.getAttemptCount(IP));
        assertFalse(rateLimiter.isBlocked(IP));
    }

    @Test
    @DisplayName("Manual Unblock")
    void testUnblock() {
        // Force block
        for(int i=0; i<5; i++) rateLimiter.recordFailedAttempt(IP);
        assertTrue(rateLimiter.isBlocked(IP));

        rateLimiter.unblock(IP);
        assertFalse(rateLimiter.isBlocked(IP));
        assertEquals(0, rateLimiter.getAttemptCount(IP));
    }

    @Test
    @DisplayName("Get Block Time Remaining")
    void testBlockTimeRemaining() {
        assertEquals(0, rateLimiter.getBlockTimeRemaining(IP));
        
        // Force block
        for(int i=0; i<5; i++) rateLimiter.recordFailedAttempt(IP);
        
        long remaining = rateLimiter.getBlockTimeRemaining(IP);
        assertTrue(remaining > 0);
        assertTrue(remaining <= 15 * 60); // Should be <= 15 minutes in seconds
    }
}