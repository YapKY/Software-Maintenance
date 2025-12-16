package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidTokenExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Token expired";
        InvalidTokenException exception = new InvalidTokenException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Invalid JWT signature";
        Throwable cause = new IllegalArgumentException("Signature verification failed");
        InvalidTokenException exception = new InvalidTokenException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}