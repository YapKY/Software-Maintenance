package com.example.springboot.adapter.google;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleAuthAdapter googleAuthAdapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthAdapter, "googleClientId", "test-client-id");
    }

    @Test
    @DisplayName("Should validate token and return user info successfully")
    void testValidateTokenAndGetUserInfo_Success() {
        // Arrange
        Map<String, Object> mockResponseBody = new HashMap<>();
        mockResponseBody.put("sub", "google-user-123");
        mockResponseBody.put("email", "test@gmail.com");
        mockResponseBody.put("name", "Test User");
        mockResponseBody.put("picture", "http://picture.url");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        Map<String, String> result = googleAuthAdapter.validateTokenAndGetUserInfo("valid-access-token");

        // Assert
        assertNotNull(result);
        assertEquals("google-user-123", result.get("id"));
        assertEquals("test@gmail.com", result.get("email"));
        assertEquals("Test User", result.get("name"));
        assertEquals("true", result.get("emailVerified"));
    }

    @Test
    @DisplayName("Should throw exception when API returns non-OK status")
    void testValidateTokenAndGetUserInfo_ApiError() {
        // Arrange
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
                googleAuthAdapter.validateTokenAndGetUserInfo("invalid-token")
            );
            // CHANGE THIS LINE:
        assertEquals("Google authentication failed", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when RestTemplate throws exception")
    void testValidateTokenAndGetUserInfo_NetworkError() {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
                googleAuthAdapter.validateTokenAndGetUserInfo("token")
            );
        // CHANGE THIS LINE:
        assertEquals("Google authentication failed", exception.getMessage());
    }
}