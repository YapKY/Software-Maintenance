package com.example.springboot.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MFAValidationExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Invalid OTP code";
        MFAValidationException exception = new MFAValidationException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "MFA verification failed";
        Throwable cause = new RuntimeException("TOTP provider error");
        MFAValidationException exception = new MFAValidationException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}