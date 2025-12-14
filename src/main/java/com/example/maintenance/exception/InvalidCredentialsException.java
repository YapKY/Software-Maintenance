package com.example.maintenance.exception;

/**
 * Exception thrown when invalid credentials are provided
 */
public class InvalidCredentialsException extends AuthenticationException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}

