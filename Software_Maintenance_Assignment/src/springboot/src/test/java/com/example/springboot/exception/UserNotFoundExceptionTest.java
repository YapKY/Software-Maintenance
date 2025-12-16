package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "User not found with ID: 123";
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "User lookup failed";
        Throwable cause = new RuntimeException("Database timeout");
        UserNotFoundException exception = new UserNotFoundException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}