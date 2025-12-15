package com.example.springboot.strategy.registration;

import com.example.springboot.adapter.firebase.FirebaseAdapter;
import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.dto.request.UserRegisterRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.enums.Gender;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.model.User;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.EmailService;
import com.example.springboot.service.EmailVerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailRegisterStrategyTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RecaptchaAdapter recaptchaAdapter;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailVerificationTokenService tokenService;
    @Mock
    private FirebaseAdapter firebaseAdapter;

    @InjectMocks
    private EmailRegisterStrategy emailRegisterStrategy;

    private UserRegisterRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password@123")
                .name("John Doe")
                .custIcNo("900101-14-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-1234567")
                .recaptchaToken("valid-token")
                .build();
    }

    // --- Positive Tests ---

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(firebaseAdapter.createUser(anyString(), anyString(), anyString())).thenReturn("firebase-uid-123");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        
        User savedUser = User.builder()
                .custId("firebase-uid-123")
                .email(validRequest.getEmail())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.createVerificationToken(anyString(), anyString())).thenReturn("verify-token");

        // Act
        AuthResponseDTO response = emailRegisterStrategy.register(validRequest, "valid-token");

        // Assert
        assertTrue(response.getSuccess());
        verify(emailService).sendVerificationEmail(any(User.class), eq("verify-token"));
        verify(firebaseAdapter).createUser(validRequest.getEmail(), validRequest.getPassword(), validRequest.getName());
    }

    // --- Negative Tests ---

    @Test
    void testRegister_Fail_InvalidRecaptcha() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> 
            emailRegisterStrategy.register(validRequest, "invalid-token")
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegister_Fail_DuplicateEmail() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // Act & Assert
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () ->
            emailRegisterStrategy.register(validRequest, "valid-token")
        );
        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void testRegister_Fail_DuplicatePhoneNumber() {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).thenReturn(true);

        // Act & Assert
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () ->
            emailRegisterStrategy.register(validRequest, "valid-token")
        );
        assertEquals("Phone number already registered", ex.getMessage());
    }

    @Test
    void testRegister_Fail_FirebaseError() throws Exception {
        // Arrange
        when(recaptchaAdapter.validateRecaptcha(anyString())).thenReturn(true);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(firebaseAdapter.createUser(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Weak password"));

        // Act & Assert
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () ->
            emailRegisterStrategy.register(validRequest, "valid-token")
        );
        assertTrue(ex.getMessage().contains("Registration failed: Weak password"));
    }
}