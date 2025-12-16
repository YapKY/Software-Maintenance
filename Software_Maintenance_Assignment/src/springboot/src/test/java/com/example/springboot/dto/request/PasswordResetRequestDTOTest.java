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

@DisplayName("PasswordResetRequestDTO Tests")
class PasswordResetRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid PasswordResetRequestDTO")
    void testValid() {
        PasswordResetRequestDTO dto = PasswordResetRequestDTO.builder()
                .email("user@example.com")
                .build();
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when email is blank")
    void testBlankEmail() {
        PasswordResetRequestDTO dto = PasswordResetRequestDTO.builder()
                .email("")
                .build();
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void testInvalidEmail() {
        PasswordResetRequestDTO dto = PasswordResetRequestDTO.builder()
                .email("invalid-email")
                .build();
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test constructors")
    void testConstructors() {
        PasswordResetRequestDTO dto1 = new PasswordResetRequestDTO();
        dto1.setEmail("test@example.com");
        
        PasswordResetRequestDTO dto2 = new PasswordResetRequestDTO("test@example.com");
        
        assertEquals(dto1, dto2);
        assertEquals("test@example.com", dto2.getEmail());
        assertNotNull(dto1.toString());
    }
}