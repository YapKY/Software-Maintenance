package com.example.springboot.utils;

import com.example.springboot.dto.response.ErrorResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ResponseBuilderTest {

    @Test
    @DisplayName("Build Bad Request Response")
    void testBuildBadRequest() {
        ResponseEntity<ErrorResponseDTO> response = ResponseBuilder.buildBadRequest("Bad input", "/api/test");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Bad input", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Build Unauthorized Response")
    void testBuildUnauthorized() {
        ResponseEntity<ErrorResponseDTO> response = ResponseBuilder.buildUnauthorized("Please login", "/api/auth");
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Please login", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Build Forbidden Response")
    void testBuildForbidden() {
        ResponseEntity<ErrorResponseDTO> response = ResponseBuilder.buildForbidden("Access denied", "/api/admin");
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
    }

    @Test
    @DisplayName("Build Not Found Response")
    void testBuildNotFound() {
        ResponseEntity<ErrorResponseDTO> response = ResponseBuilder.buildNotFound("User not found", "/api/user/1");
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    @DisplayName("Build Internal Error Response")
    void testBuildInternalError() {
        ResponseEntity<ErrorResponseDTO> response = ResponseBuilder.buildInternalError("Server crash", "/api/data");
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
    
    @Test
    @DisplayName("Build Custom Error Response")
    void testBuildErrorResponse() {
        ResponseEntity<ErrorResponseDTO> response = ResponseBuilder.buildErrorResponse(
            HttpStatus.SERVICE_UNAVAILABLE, "Maintenance mode", "/api/health"
        );
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(503, response.getBody().getStatus());
        assertEquals("Service Unavailable", response.getBody().getError());
    }
}