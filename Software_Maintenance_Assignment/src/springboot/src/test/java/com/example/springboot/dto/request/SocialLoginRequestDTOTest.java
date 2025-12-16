package com.example.springboot.dto.request;

import com.example.springboot.enums.AuthProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SocialLoginRequestDTO Additional Tests")
class SocialLoginRequestDTOAdditionalTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should test with long access token")
    void testLongAccessToken() {
        String longToken = "a".repeat(500);
        SocialLoginRequestDTO dto = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken(longToken)
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test with very long recaptcha token")
    void testLongRecaptchaToken() {
        String longToken = "a".repeat(1000);
        SocialLoginRequestDTO dto = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.FACEBOOK)
                .accessToken("access-token")
                .recaptchaToken(longToken)
                .build();
        
        Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test equals with same values")
    void testEqualsWithSameValues() {
        SocialLoginRequestDTO dto1 = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken("token123")
                .recaptchaToken("recaptcha")
                .build();
        
        SocialLoginRequestDTO dto2 = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken("token123")
                .recaptchaToken("recaptcha")
                .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should test equals with different providers")
    void testEqualsWithDifferentProviders() {
        SocialLoginRequestDTO dto1 = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken("token")
                .recaptchaToken("recaptcha")
                .build();
        
        SocialLoginRequestDTO dto2 = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.FACEBOOK)
                .accessToken("token")
                .recaptchaToken("recaptcha")
                .build();
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should test with EMAIL provider")
    void testWithEmailProvider() {
        SocialLoginRequestDTO dto = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.EMAIL)
                .accessToken("token")
                .recaptchaToken("recaptcha")
                .build();
        
        Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals(AuthProvider.EMAIL, dto.getProvider());
    }

    @Test
    @DisplayName("Should fail with null access token")
    void testNullAccessToken() {
        SocialLoginRequestDTO dto = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken(null)
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail with null recaptcha token")
    void testNullRecaptchaToken() {
        SocialLoginRequestDTO dto = SocialLoginRequestDTO.builder()
                .provider(AuthProvider.GOOGLE)
                .accessToken("access-token")
                .recaptchaToken(null)
                .build();
        
        Set<ConstraintViolation<SocialLoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}