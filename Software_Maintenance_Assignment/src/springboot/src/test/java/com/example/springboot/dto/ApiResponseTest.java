package com.example.springboot.dto;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testBuilderAndGetters() {
        Map<String, Object> initialMeta = new HashMap<>();
        initialMeta.put("version", "1.0");

        ApiResponse response = new ApiResponse.Builder()
                .success(true)
                .message("Test Message")
                .data("Test Data")
                .metadata(initialMeta)
                .metadata("traceId", "12345")
                .build();

        assertTrue(response.isSuccess());
        assertEquals("Test Message", response.getMessage());
        assertEquals("Test Data", response.getData());
        assertEquals("1.0", response.getMetadata().get("version"));
        assertEquals("12345", response.getMetadata().get("traceId"));
    }

    @Test
    void testConvenienceSuccessWithMessageAndData() {
        ApiResponse response = ApiResponse.success("Operation Successful", 100);
        
        assertTrue(response.isSuccess());
        assertEquals("Operation Successful", response.getMessage());
        assertEquals(100, response.getData());
        assertNotNull(response.getMetadata());
        assertTrue(response.getMetadata().isEmpty());
    }

    @Test
    void testConvenienceSuccessWithMessageOnly() {
        ApiResponse response = ApiResponse.success("Created");
        
        assertTrue(response.isSuccess());
        assertEquals("Created", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testConvenienceErrorWithMessageOnly() {
        ApiResponse response = ApiResponse.error("Not Found");
        
        assertFalse(response.isSuccess());
        assertEquals("Not Found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testConvenienceErrorWithMessageAndData() {
        ApiResponse response = ApiResponse.error("Validation Failed", "Field X is required");
        
        assertFalse(response.isSuccess());
        assertEquals("Validation Failed", response.getMessage());
        assertEquals("Field X is required", response.getData());
    }
}