package com.example.springboot.adapter.recaptcha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
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
class RecaptchaAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecaptchaAdapter recaptchaAdapter;

    @BeforeEach
    void setUp() {
        // Inject the secret key value
        ReflectionTestUtils.setField(recaptchaAdapter, "recaptchaSecretKey", "test-secret-key");
    }

    @Test
    @DisplayName("Should pass validation with test token bypass")
    void testValidateRecaptcha_TestTokenBypass() {
        // Act
        boolean result = recaptchaAdapter.validateRecaptcha("test-token");

        // Assert
        assertTrue(result, "Should return true for 'test-token'");
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Should pass validation with test site key bypass")
    void testValidateRecaptcha_TestSiteKeyBypass() {
        // Act
        // Using the TEST_SITE_KEY defined in the class
        boolean result = recaptchaAdapter.validateRecaptcha("6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI");

        // Assert
        assertTrue(result, "Should return true for known test site key");
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Should validate successfully via API")
    void testValidateRecaptcha_ApiSuccess() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = recaptchaAdapter.validateRecaptcha("real-token");

        // Assert
        assertTrue(result);
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("Should fail validation when API returns success: false")
    void testValidateRecaptcha_ApiFailure() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", false);
        responseBody.put("error-codes", "invalid-input-response");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = recaptchaAdapter.validateRecaptcha("invalid-token");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should fail validation when API returns non-OK status")
    void testValidateRecaptcha_ApiErrorStatus() {
        // Arrange
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = recaptchaAdapter.validateRecaptcha("token");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false on Exception")
    void testValidateRecaptcha_Exception() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection timed out"));

        // Act
        boolean result = recaptchaAdapter.validateRecaptcha("token");

        // Assert
        assertFalse(result);
    }
}