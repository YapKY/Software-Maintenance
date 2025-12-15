package com.example.springboot.exception;

/**
 * Exception thrown when MFA validation fails
 */
public class MFAValidationException extends RuntimeException {
    
    public MFAValidationException(String message) {
        super(message);
    }
    
    public MFAValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
