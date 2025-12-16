package com.example.springboot.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RecaptchaRequestDTO Tests")
class RecaptchaRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid RecaptchaRequestDTO")
    void testValid() {
        RecaptchaRequestDTO dto = RecaptchaRequestDTO.builder()
                .recaptchaToken("valid-token")
                .build();
        Set<ConstraintViolation<RecaptchaRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when token is blank")
    void testBlankToken() {
        RecaptchaRequestDTO dto = RecaptchaRequestDTO.builder()
                .recaptchaToken("")
                .build();
        Set<ConstraintViolation<RecaptchaRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("reCAPTCHA token is required")));
    }

    @Test
    @DisplayName("Should fail when token is null")
    void testNullToken() {
        RecaptchaRequestDTO dto = RecaptchaRequestDTO.builder()
                .recaptchaToken(null)
                .build();
        Set<ConstraintViolation<RecaptchaRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test constructors")
    void testConstructors() {
        RecaptchaRequestDTO dto1 = new RecaptchaRequestDTO();
        dto1.setRecaptchaToken("token123");
        
        RecaptchaRequestDTO dto2 = new RecaptchaRequestDTO("token123");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}