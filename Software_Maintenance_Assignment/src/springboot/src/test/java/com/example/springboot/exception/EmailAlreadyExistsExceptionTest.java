package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailAlreadyExistsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Email already in use";
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Duplicate email";
        Throwable cause = new RuntimeException("DB Constraint Violation");
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}