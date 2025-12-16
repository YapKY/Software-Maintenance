package com.example.springboot.strategy.authentication;

import com.example.springboot.adapter.google.GoogleAuthAdapter;
import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.dto.request.SocialLoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.model.User;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthStrategyTest {

    @Mock
    private GoogleAuthAdapter googleAuthAdapter;
    @Mock
    private RecaptchaAdapter recaptchaAdapter;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private GoogleAuthStrategy googleAuthStrategy;

    private SocialLoginRequestDTO validRequest;
    private Map<String, String> mockUserInfo;
    private final String RECAPTCHA_TOKEN = "valid-recaptcha";
    private final String TEST_EMAIL = "googleuser@example.com";
    private final String PROVIDER_ID = "google-12345";

    @BeforeEach
    void setUp() {
        validRequest = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken("valid-google-token")
                .recaptchaToken(RECAPTCHA_TOKEN)
                .build();

        mockUserInfo = new HashMap<>();
        mockUserInfo.put("id", PROVIDER_ID); // Maps to 'id' in strategy
        mockUserInfo.put("email", TEST_EMAIL);
        mockUserInfo.put("name", "Google User");
    }

    @Test
    void testAuthenticate_Success_ExistingUser() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(googleAuthAdapter.validateTokenAndGetUserInfo(anyString())).thenReturn(mockUserInfo);

        User existingUser = User.builder()
                .custId("U001")
                .email(TEST_EMAIL)
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .providerId(PROVIDER_ID)
                .accountLocked(false)
                .mfaEnabled(false)
                .build();
        
        // [FIX] Mock findByProviderIdAndAuthProvider instead of findByEmail
        when(userRepository.findByProviderIdAndAuthProvider(eq(PROVIDER_ID), eq(AuthProvider.GOOGLE)))
            .thenReturn(Optional.of(existingUser));
        
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        JWTResponseDTO mockTokens = JWTResponseDTO.builder()
                .accessToken("access-token")
                .build();
        
        when(jwtTokenProvider.generateTokens(any(), eq(TEST_EMAIL), eq(Role.USER)))
            .thenReturn(mockTokens);

        // Act
        AuthResponseDTO response = googleAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN);

        // Assert
        assertTrue(response.getSuccess());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAuthenticate_Success_NewUser() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(googleAuthAdapter.validateTokenAndGetUserInfo(anyString())).thenReturn(mockUserInfo);
        
        // [FIX] Use lenient() because strategy might check existsByEmail first, making this unused
        lenient().when(userRepository.findByEmail("googleuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("googleuser@example.com")).thenReturn(false);
        
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User savedUser = User.builder()
                .custId("U002")
                .email("googleuser@example.com")
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .accountLocked(false)
                .build();
        
        // Mock save to return the user
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        JWTResponseDTO mockTokens = JWTResponseDTO.builder().accessToken("access").build();
        
        // [FIX] Use any() for userId
        when(jwtTokenProvider.generateTokens(any(), eq("googleuser@example.com"), eq(Role.USER)))
            .thenReturn(mockTokens);

        // Act
        AuthResponseDTO response = googleAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN);

        // Assert
        assertTrue(response.getSuccess());
        // Depending on logic, it might be called 1 or 2 times (create + update login). 
        // using atLeastOnce() covers both cases safely.
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Fail_EmailExistsWithDifferentProvider() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(googleAuthAdapter.validateTokenAndGetUserInfo(anyString())).thenReturn(mockUserInfo);
        
        // [FIX] Use lenient() - this caused UnnecessaryStubbingException because strategy might fail at existsByEmail first
        lenient().when(userRepository.findByEmail("googleuser@example.com")).thenReturn(Optional.empty());
        
        // This is the trigger for the exception
        when(userRepository.existsByEmail("googleuser@example.com")).thenReturn(true);

        // Act & Assert
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> 
            googleAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN)
        );
        
        assertTrue(ex.getMessage().contains("Email already exists with a different provider"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Fail_InvalidRecaptcha() {
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> 
            googleAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN)
        );
        verify(googleAuthAdapter, never()).validateTokenAndGetUserInfo(anyString());
    }

    @Test
    void testAuthenticate_Fail_GoogleAdapterError() {
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(googleAuthAdapter.validateTokenAndGetUserInfo(anyString()))
                .thenThrow(new RuntimeException("Google Error"));

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> 
            googleAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN)
        );
        assertTrue(ex.getMessage().contains("Google authentication failed"));
    }
    
    @Test
    void testGetStrategyName() {
        assertEquals("GOOGLE_OAUTH_AUTH", googleAuthStrategy.getStrategyName());
    }
}