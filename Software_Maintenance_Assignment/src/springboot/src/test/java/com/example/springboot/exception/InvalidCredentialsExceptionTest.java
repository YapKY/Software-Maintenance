package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Invalid username or password";
        InvalidCredentialsException exception = new InvalidCredentialsException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
        // Verify inheritance
        assertTrue(exception instanceof AuthenticationException);
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Invalid credentials";
        Throwable cause = new IllegalArgumentException("Bad input");
        InvalidCredentialsException exception = new InvalidCredentialsException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}