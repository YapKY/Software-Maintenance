package com.example.springboot.adapter.facebook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacebookAuthAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FacebookAuthAdapter facebookAuthAdapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(facebookAuthAdapter, "facebookAppId", "test-app-id");
        ReflectionTestUtils.setField(facebookAuthAdapter, "facebookAppSecret", "test-app-secret");
    }

    @Test
    @DisplayName("Should validate token and return user info successfully")
    void testValidateTokenAndGetUserInfo_Success() {
        // Arrange
        // 1. Mock Debug Token Response
        Map<String, Object> debugResponse = new HashMap<>();
        ResponseEntity<Map> debugEntity = new ResponseEntity<>(debugResponse, HttpStatus.OK);
        
        // 2. Mock User Info Response
        Map<String, Object> userInfoResponse = new HashMap<>();
        userInfoResponse.put("id", "fb-123");
        userInfoResponse.put("email", "fb@example.com");
        userInfoResponse.put("name", "FB User");
        ResponseEntity<Map> userEntity = new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

        // We use strict stubbing order or distinct URL matching logic
        when(restTemplate.getForEntity(org.mockito.ArgumentMatchers.contains("debug_token"), eq(Map.class)))
                .thenReturn(debugEntity);
        
        when(restTemplate.getForEntity(org.mockito.ArgumentMatchers.contains("fields=id,name,email"), eq(Map.class)))
                .thenReturn(userEntity);

        // Act
        Map<String, String> result = facebookAuthAdapter.validateTokenAndGetUserInfo("valid-fb-token");

        // Assert
        assertEquals("fb-123", result.get("id"));
        assertEquals("fb@example.com", result.get("email"));
        assertEquals("FB User", result.get("name"));
    }

    @Test
    @DisplayName("Should fail when debug token call fails")
    void testValidateTokenAndGetUserInfo_DebugTokenFail() {
        // Arrange
        ResponseEntity<Map> debugEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.getForEntity(org.mockito.ArgumentMatchers.contains("debug_token"), eq(Map.class)))
                .thenReturn(debugEntity);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            facebookAuthAdapter.validateTokenAndGetUserInfo("bad-token")
        );
        assertEquals("Facebook authentication failed", exception.getMessage());
    }

    @Test
    @DisplayName("Should fail when user info call fails")
    void testValidateTokenAndGetUserInfo_UserInfoFail() {
        // Arrange
        // Debug token succeeds
        ResponseEntity<Map> debugEntity = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.getForEntity(org.mockito.ArgumentMatchers.contains("debug_token"), eq(Map.class)))
                .thenReturn(debugEntity);

        // User info fails
        ResponseEntity<Map> userEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(org.mockito.ArgumentMatchers.contains("fields=id"), eq(Map.class)))
                .thenReturn(userEntity);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            facebookAuthAdapter.validateTokenAndGetUserInfo("valid-token")
        );
        assertEquals("Facebook authentication failed", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle exceptions during API calls")
    void testValidateTokenAndGetUserInfo_Exception() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            facebookAuthAdapter.validateTokenAndGetUserInfo("token")
        );
        assertEquals("Facebook authentication failed", exception.getMessage());
    }
}