package com.example.springboot.exception;

import com.example.springboot.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Provides centralized exception handling across all controllers
 * Uses @ControllerAdvice for cross-cutting concerns
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle IllegalArgumentException
     * Typically thrown for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ApiResponse response = new ApiResponse.Builder()
                .success(false)
                .message(ex.getMessage())
                .metadata("error", "VALIDATION_ERROR")
                .metadata("path", request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle RuntimeException
     * General runtime errors
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        ApiResponse response = new ApiResponse.Builder()
                .success(false)
                .message("An internal error occurred: " + ex.getMessage())
                .metadata("error", "RUNTIME_ERROR")
                .metadata("path", request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle ResourceNotFoundException
     * Custom exception for resource not found scenarios
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ApiResponse response = new ApiResponse.Builder()
                .success(false)
                .message(ex.getMessage())
                .metadata("error", "RESOURCE_NOT_FOUND")
                .metadata("path", request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle DuplicateResourceException
     * Custom exception for duplicate resource scenarios
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {

        ApiResponse response = new ApiResponse.Builder()
                .success(false)
                .message(ex.getMessage())
                .metadata("error", "DUPLICATE_RESOURCE")
                .metadata("path", request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle all other exceptions
     * Catch-all for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        ApiResponse response = new ApiResponse.Builder()
                .success(false)
                .message("An unexpected error occurred")
                .metadata("error", "INTERNAL_ERROR")
                .metadata("details", ex.getMessage())
                .metadata("path", request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
