package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RateLimitExceededExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Too many requests";
        RateLimitExceededException exception = new RateLimitExceededException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Rate limit hit";
        Throwable cause = new RuntimeException("Redis error");
        RateLimitExceededException exception = new RateLimitExceededException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}