package com.example.springboot.utils;

import com.example.springboot.dto.response.ErrorResponseDTO;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * ResponseBuilder - Utility class for building standardized responses
 */
@UtilityClass
public class ResponseBuilder {
    
    /**
     * Build error response
     */
    public static ResponseEntity<ErrorResponseDTO> buildErrorResponse(
        HttpStatus status,
        String message,
        String path
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
            
        return ResponseEntity.status(status).body(error);
    }
    
    /**
     * Build bad request response
     */
    public static ResponseEntity<ErrorResponseDTO> buildBadRequest(String message, String path) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, path);
    }
    
    /**
     * Build unauthorized response
     */
    public static ResponseEntity<ErrorResponseDTO> buildUnauthorized(String message, String path) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, path);
    }
    
    /**
     * Build forbidden response
     */
    public static ResponseEntity<ErrorResponseDTO> buildForbidden(String message, String path) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, message, path);
    }
    
    /**
     * Build not found response
     */
    public static ResponseEntity<ErrorResponseDTO> buildNotFound(String message, String path) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, message, path);
    }
    
    /**
     * Build internal server error response
     */
    public static ResponseEntity<ErrorResponseDTO> buildInternalError(String message, String path) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, path);
    }
}
