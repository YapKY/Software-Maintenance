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

@DisplayName("PasswordChangeRequestDTO Tests")
class PasswordChangeRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid PasswordChangeRequestDTO")
    void testValidPasswordChangeRequestDTO() {
        PasswordChangeRequestDTO dto = PasswordChangeRequestDTO.builder()
                .currentPassword("OldPass123!")
                .newPassword("NewPass123!")
                .confirmPassword("NewPass123!")
                .build();
        Set<ConstraintViolation<PasswordChangeRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when required fields are blank")
    void testBlankFields() {
        PasswordChangeRequestDTO dto = PasswordChangeRequestDTO.builder()
                .currentPassword("")
                .newPassword("")
                .confirmPassword("")
                .build();
        
        Set<ConstraintViolation<PasswordChangeRequestDTO>> violations = validator.validate(dto);
        assertEquals(3, violations.size(), "Should have violations for all 3 empty fields");
    }

    @Test
    @DisplayName("Should test Lombok methods")
    void testLombokMethods() {
        PasswordChangeRequestDTO dto1 = new PasswordChangeRequestDTO("Old!", "New!", "New!");
        PasswordChangeRequestDTO dto2 = new PasswordChangeRequestDTO();
        dto2.setCurrentPassword("Old!");
        dto2.setNewPassword("New!");
        dto2.setConfirmPassword("New!");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}