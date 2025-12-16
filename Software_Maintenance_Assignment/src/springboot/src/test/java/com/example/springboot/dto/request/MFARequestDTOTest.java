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

@DisplayName("MFARequestDTO Tests")
class MFARequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid MFARequestDTO")
    void testValidMFARequestDTO() {
        MFARequestDTO dto = MFARequestDTO.builder()
                .email("user@example.com")
                .code("123456")
                .sessionToken("valid-session-token")
                .build();

        Set<ConstraintViolation<MFARequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation for invalid MFA code pattern")
    void testInvalidMFACode() {
        // Test non-numeric
        MFARequestDTO dto1 = MFARequestDTO.builder()
                .email("u@ex.com").sessionToken("t").code("ABCDEF").build();
        
        // Test wrong length
        MFARequestDTO dto2 = MFARequestDTO.builder()
                .email("u@ex.com").sessionToken("t").code("123").build();

        Set<ConstraintViolation<MFARequestDTO>> violations1 = validator.validate(dto1);
        Set<ConstraintViolation<MFARequestDTO>> violations2 = validator.validate(dto2);

        assertFalse(violations1.isEmpty());
        assertFalse(violations2.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getMessage().equals("MFA code must be 6 digits")));
    }

    @Test
    @DisplayName("Should check boilerplate methods")
    void testBoilerplate() {
        MFARequestDTO dto = new MFARequestDTO("test@email.com", "123456", "token");
        MFARequestDTO dto2 = new MFARequestDTO();
        dto2.setEmail("test@email.com");
        dto2.setCode("123456");
        dto2.setSessionToken("token");

        assertEquals(dto, dto2);
        assertEquals(dto.toString(), dto2.toString());
    }
}