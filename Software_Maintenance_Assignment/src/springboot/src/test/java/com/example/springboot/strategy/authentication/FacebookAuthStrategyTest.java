package com.example.springboot.strategy.authentication;

import com.example.springboot.adapter.facebook.FacebookAuthAdapter;
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
class FacebookAuthStrategyTest {

    @Mock
    private FacebookAuthAdapter facebookAuthAdapter;
    @Mock
    private RecaptchaAdapter recaptchaAdapter;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FacebookAuthStrategy facebookAuthStrategy;

    private SocialLoginRequestDTO validRequest;
    private Map<String, String> mockUserInfo;
    private final String RECAPTCHA_TOKEN = "valid-recaptcha";

    @BeforeEach
    void setUp() {
        validRequest = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.FACEBOOK)
                .accessToken("valid-fb-token")
                .recaptchaToken(RECAPTCHA_TOKEN)
                .build();

        mockUserInfo = new HashMap<>();
        mockUserInfo.put("id", "fb-12345");
        mockUserInfo.put("email", "fbuser@example.com");
        mockUserInfo.put("name", "Facebook User");
    }

    @Test
    void testAuthenticate_Success_ExistingUser() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(facebookAuthAdapter.validateTokenAndGetUserInfo(anyString())).thenReturn(mockUserInfo);

        User existingUser = User.builder()
                .custId("U001")
                .email("fbuser@example.com")
                .role(Role.USER)
                .authProvider(AuthProvider.FACEBOOK)
                .accountLocked(false)
                .build();
        
        lenient().when(userRepository.findByEmail("fbuser@example.com")).thenReturn(Optional.of(existingUser));
        
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        JWTResponseDTO mockTokens = JWTResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
        
        // Use any() for userId to be safe
        when(jwtTokenProvider.generateTokens(any(), eq("fbuser@example.com"), eq(Role.USER)))
            .thenReturn(mockTokens);

        // Act
        AuthResponseDTO response = facebookAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Facebook login successful", response.getMessage());
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Success_NewUser() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(facebookAuthAdapter.validateTokenAndGetUserInfo(anyString())).thenReturn(mockUserInfo);
        
        lenient().when(userRepository.findByEmail("fbuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("fbuser@example.com")).thenReturn(false);
        
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User savedUser = User.builder()
                .custId("U002")
                .email("fbuser@example.com")
                .role(Role.USER)
                .authProvider(AuthProvider.FACEBOOK)
                .accountLocked(false)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        JWTResponseDTO mockTokens = JWTResponseDTO.builder().accessToken("access").build();
        when(jwtTokenProvider.generateTokens(any(), eq("fbuser@example.com"), eq(Role.USER)))
            .thenReturn(mockTokens);

        // Act
        AuthResponseDTO response = facebookAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN);

        // Assert
        assertTrue(response.getSuccess());
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Fail_EmailExistsWithDifferentProvider() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(facebookAuthAdapter.validateTokenAndGetUserInfo(anyString())).thenReturn(mockUserInfo);
        
        lenient().when(userRepository.findByEmail("fbuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("fbuser@example.com")).thenReturn(true);

        // Act & Assert
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> 
            facebookAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN)
        );
        
        assertTrue(ex.getMessage().contains("Email already exists with a different provider"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Fail_InvalidRecaptcha() {
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> 
            facebookAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN)
        );
        verify(facebookAuthAdapter, never()).validateTokenAndGetUserInfo(anyString());
    }

    @Test
    void testAuthenticate_Fail_FacebookAdapterError() {
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(facebookAuthAdapter.validateTokenAndGetUserInfo(anyString()))
                .thenThrow(new RuntimeException("Token invalid"));

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> 
            facebookAuthStrategy.authenticate(validRequest, RECAPTCHA_TOKEN)
        );
        assertTrue(ex.getMessage().contains("Facebook authentication failed"));
    }
    
    @Test
    void testGetStrategyName() {
        assertEquals("FACEBOOK_OAUTH_AUTH", facebookAuthStrategy.getStrategyName());
    }
}