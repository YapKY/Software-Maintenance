package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWTResponseDTO Tests")
class JWTResponseDTOTest {

    @Test
    @DisplayName("Should create valid JWTResponseDTO")
    void testValid() {
        JWTResponseDTO dto = JWTResponseDTO.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .expiresIn(3600L)
                .role(Role.USER)
                .email("user@example.com")
                .fullName("John Doe")
                .build();
        
        assertEquals("access-token-123", dto.getAccessToken());
        assertEquals("refresh-token-456", dto.getRefreshToken());
        assertEquals("Bearer", dto.getTokenType());
        assertEquals(3600L, dto.getExpiresIn());
        assertEquals(Role.USER, dto.getRole());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getFullName());
    }

    @Test
    @DisplayName("Should test default tokenType")
    void testDefaultTokenType() {
        JWTResponseDTO dto = JWTResponseDTO.builder()
                .accessToken("token")
                .refreshToken("refresh")
                .build();
        
        assertEquals("Bearer", dto.getTokenType());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        JWTResponseDTO dto1 = new JWTResponseDTO();
        dto1.setAccessToken("access");
        dto1.setRefreshToken("refresh");
        dto1.setTokenType("Bearer");
        dto1.setExpiresIn(3600L);
        dto1.setRole(Role.ADMIN);
        dto1.setEmail("admin@example.com");
        dto1.setFullName("Admin User");
        
        JWTResponseDTO dto2 = new JWTResponseDTO("access", "refresh", "Bearer", 
                3600L, Role.ADMIN, "admin@example.com", "Admin User");
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }
}