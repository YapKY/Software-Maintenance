package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Access denied";
        UnauthorizedException exception = new UnauthorizedException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Unauthorized access attempt";
        Throwable cause = new SecurityException("Insufficient privileges");
        UnauthorizedException exception = new UnauthorizedException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}