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

@DisplayName("RefreshTokenRequestDTO Tests")
class RefreshTokenRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid RefreshTokenRequestDTO")
    void testValid() {
        RefreshTokenRequestDTO dto = RefreshTokenRequestDTO.builder()
                .refreshToken("valid-refresh-token")
                .build();
        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when refreshToken is blank")
    void testBlank() {
        RefreshTokenRequestDTO dto = RefreshTokenRequestDTO.builder()
                .refreshToken("")
                .build();
        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Refresh token is required")));
    }

    @Test
    @DisplayName("Should test constructors")
    void testConstructors() {
        RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO();
        dto1.setRefreshToken("token");
        
        RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("token");
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}