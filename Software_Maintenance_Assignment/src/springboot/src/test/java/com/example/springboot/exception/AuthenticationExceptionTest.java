package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Auth failed";
        AuthenticationException exception = new AuthenticationException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Auth failed root cause";
        Throwable cause = new RuntimeException("Root cause");
        AuthenticationException exception = new AuthenticationException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}