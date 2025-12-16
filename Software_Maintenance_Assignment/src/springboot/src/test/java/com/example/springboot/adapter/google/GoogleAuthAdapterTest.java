package com.example.springboot.adapter.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleAuthAdapter googleAuthAdapter;

    private final String GOOGLE_CLIENT_ID = "test-client-id";
    private final String TEST_TOKEN = "dummy-token";

    @BeforeEach
    void setUp() {
        // Inject the @Value property using ReflectionTestUtils
        ReflectionTestUtils.setField(googleAuthAdapter, "googleClientId", GOOGLE_CLIENT_ID);
    }

    /**
     * Test Case 1: Successful Validation using Google ID Token.
     * Use mockConstruction to intercept the new GoogleIdTokenVerifier.Builder() call.
     */
    @Test
    void testValidateTokenAndGetUserInfo_Success_WithIdToken() {
        try (MockedConstruction<GoogleIdTokenVerifier.Builder> mockedBuilder = Mockito.mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    // Mock the Builder methods
                    GoogleIdTokenVerifier mockVerifier = mock(GoogleIdTokenVerifier.class);
                    when(mock.setAudience(any())).thenReturn(mock);
                    when(mock.build()).thenReturn(mockVerifier);

                    // Mock the GoogleIdToken and Payload
                    GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
                    GoogleIdToken.Payload mockPayload = new GoogleIdToken.Payload();
                    mockPayload.setSubject("google-id-123");
                    mockPayload.setEmail("test@example.com");
                    mockPayload.set("name", "Test User");
                    mockPayload.set("picture", "http://profile.pic");
                    mockPayload.setEmailVerified(true);

                    when(mockIdToken.getPayload()).thenReturn(mockPayload);
                    when(mockVerifier.verify(TEST_TOKEN)).thenReturn(mockIdToken);
                })) {

            // Act
            Map<String, String> result = googleAuthAdapter.validateTokenAndGetUserInfo(TEST_TOKEN);

            // Assert
            assertNotNull(result);
            assertEquals("google-id-123", result.get("id"));
            assertEquals("test@example.com", result.get("email"));
            assertEquals("Test User", result.get("name"));
            assertEquals("http://profile.pic", result.get("picture"));
            assertEquals("true", result.get("emailVerified"));

            // Ensure RestTemplate was NOT called (Optimization check)
            verify(restTemplate, never()).exchange(anyString(), any(), any(), eq(Map.class));
        }
    }

    /**
     * Test Case 2: ID Token Validation Fails, Fallback to Access Token Success.
     */
    @Test
    void testValidateTokenAndGetUserInfo_Success_WithAccessToken_Fallback() {
        // Mock ID Token verification to fail (return null or throw exception)
        try (MockedConstruction<GoogleIdTokenVerifier.Builder> mockedBuilder = Mockito.mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    GoogleIdTokenVerifier mockVerifier = mock(GoogleIdTokenVerifier.class);
                    when(mock.setAudience(any())).thenReturn(mock);
                    when(mock.build()).thenReturn(mockVerifier);
                    
                    // Simulate Invalid ID Token -> Exception forces fallback
                    when(mockVerifier.verify(anyString())).thenThrow(new IllegalArgumentException("Invalid ID Token"));
                })) {

            // Prepare RestTemplate Mock for Access Token flow
            Map<String, Object> googleApiResponse = new HashMap<>();
            googleApiResponse.put("sub", "access-token-user-id");
            googleApiResponse.put("email", "access@example.com");
            googleApiResponse.put("name", "Access User");
            googleApiResponse.put("picture", "http://access.pic");
            
            ResponseEntity<Map> responseEntity = new ResponseEntity<>(googleApiResponse, HttpStatus.OK);
            
            when(restTemplate.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenReturn(responseEntity);

            // Act
            Map<String, String> result = googleAuthAdapter.validateTokenAndGetUserInfo(TEST_TOKEN);

            // Assert
            assertNotNull(result);
            assertEquals("access-token-user-id", result.get("id"));
            assertEquals("access@example.com", result.get("email"));
            assertEquals("true", result.get("emailVerified"));
            
            // Verify RestTemplate WAS called
            verify(restTemplate).exchange(anyString(), any(), any(), eq(Map.class));
        }
    }

    /**
     * Test Case 3: Both ID Token and Access Token Validation Fail.
     * Expect: RuntimeException("Google authentication failed")
     */
    @Test
    void testValidateTokenAndGetUserInfo_Failure_AllMethodsFailed() {
        try (MockedConstruction<GoogleIdTokenVerifier.Builder> mockedBuilder = Mockito.mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    GoogleIdTokenVerifier mockVerifier = mock(GoogleIdTokenVerifier.class);
                    when(mock.setAudience(any())).thenReturn(mock);
                    when(mock.build()).thenReturn(mockVerifier);
                    when(mockVerifier.verify(anyString())).thenThrow(new GeneralSecurityException("Bad signature"));
                })) {

            // Mock RestTemplate to also fail (e.g., 401 Unauthorized)
            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenThrow(new RestClientException("Invalid Token"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                googleAuthAdapter.validateTokenAndGetUserInfo(TEST_TOKEN)
            );

            assertEquals("Google authentication failed", exception.getMessage());
        }
    }

    /**
     * Test Case 4: Access Token endpoint returns non-OK status.
     * This hits the explicit "throw new RuntimeException('Invalid Google token')" inside the try block.
     */
    @Test
    void testValidateTokenAndGetUserInfo_Failure_AccessToken_BadStatus() {
        try (MockedConstruction<GoogleIdTokenVerifier.Builder> mockedBuilder = Mockito.mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    GoogleIdTokenVerifier mockVerifier = mock(GoogleIdTokenVerifier.class);
                    when(mock.setAudience(any())).thenReturn(mock);
                    when(mock.build()).thenReturn(mockVerifier);
                    when(mockVerifier.verify(anyString())).thenReturn(null); // Return null also triggers fallback
                })) {

            // Mock RestTemplate to return 400 Bad Request (technically handled by exception usually, but strictly status check here)
            // Note: RestTemplate.exchange usually throws exception for 4xx/5xx unless configured otherwise.
            // Here we simulate a scenario where it returns an entity but with bad status/null body if handler suppresses error
            ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenReturn(responseEntity);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                googleAuthAdapter.validateTokenAndGetUserInfo(TEST_TOKEN)
            );

            assertEquals("Google authentication failed", exception.getMessage());
        }
    }
    
    /**
     * Test Case 5: Access Token endpoint returns OK but null body.
     */
    @Test
    void testValidateTokenAndGetUserInfo_Failure_AccessToken_NullBody() {
        try (MockedConstruction<GoogleIdTokenVerifier.Builder> mockedBuilder = Mockito.mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    GoogleIdTokenVerifier mockVerifier = mock(GoogleIdTokenVerifier.class);
                    when(mock.setAudience(any())).thenReturn(mock);
                    when(mock.build()).thenReturn(mockVerifier);
                    // Simulate exception to force fallback
                    when(mockVerifier.verify(anyString())).thenThrow(new IOException("Network error"));
                })) {

            // Mock response with OK status but NULL body
            ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.exchange(
                    anyString(),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenReturn(responseEntity);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                googleAuthAdapter.validateTokenAndGetUserInfo(TEST_TOKEN)
            );
            
            assertEquals("Google authentication failed", exception.getMessage());
        }
    }
}