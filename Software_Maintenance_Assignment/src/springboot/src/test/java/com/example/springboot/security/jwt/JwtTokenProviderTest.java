package com.example.springboot.security.jwt;

import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    // Use a key long enough for HS512 (64 bytes / 512 bits)
    private final String TEST_SECRET = "TestSecretKeyThatIsVeryLongAndSecureEnoughForHS512AlgorithmToWorkCorrectly1234567890";
    private final Long ACCESS_EXPIRATION = 3600000L; // 1 hour
    private final Long REFRESH_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        // Manually inject @Value fields using ReflectionTestUtils since we aren't loading the full Spring Context
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", REFRESH_EXPIRATION);
    }

    @Test
    void testGenerateTokens_Success() {
        String userId = "user123";
        String email = "test@example.com";
        Role role = Role.USER;

        JWTResponseDTO response = jwtTokenProvider.generateTokens(userId, email, role);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(email, response.getEmail());
        assertEquals(role, response.getRole());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void testGenerateMFASessionToken_Success() {
        String userId = "user123";
        String email = "test@example.com";
        Role role = Role.ADMIN;

        String token = jwtTokenProvider.generateMFASessionToken(userId, email, role);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void testValidateToken_ValidToken() {
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens("u1", "e1", Role.USER);
        assertTrue(jwtTokenProvider.validateToken(tokens.getAccessToken()));
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Manually create an expired token
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .setSubject("user123")
                .setExpiration(new Date(System.currentTimeMillis() - 10000)) // 10 seconds ago
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }

    @Test
    void testValidateToken_InvalidSignature() {
        // Token signed with a different secret
        String diffSecret = "DifferentSecretKeyThatIsAlsoVeryLongAndSecureEnoughForHS512Algorithm1234567890";
        SecretKey key = Keys.hmacShaKeyFor(diffSecret.getBytes(StandardCharsets.UTF_8));
        String invalidToken = Jwts.builder()
                .setSubject("user123")
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_MalformedToken() {
        assertFalse(jwtTokenProvider.validateToken("Not.A.Real.Token"));
    }

    @Test
    void testGetUserIdFromToken() {
        String userId = "userXYZ";
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens(userId, "test@test.com", Role.USER);
        
        String extractedId = jwtTokenProvider.getUserIdFromToken(tokens.getAccessToken());
        assertEquals(userId, extractedId);
    }

    @Test
    void testGetEmailFromToken() {
        String email = "valid@email.com";
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens("id", email, Role.USER);
        
        String extractedEmail = jwtTokenProvider.getEmailFromToken(tokens.getAccessToken());
        assertEquals(email, extractedEmail);
    }

    @Test
    void testGetRoleFromToken_ValidRole() {
        Role role = Role.SUPERADMIN;
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens("id", "email", role);
        
        Role extractedRole = jwtTokenProvider.getRoleFromToken(tokens.getAccessToken());
        assertEquals(role, extractedRole);
    }

    @Test
    void testGetRoleFromToken_InvalidRoleFallback() {
        // Manually create token with invalid role string
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject("user123")
                .claim("role", "INVALID_ROLE_NAME")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // Should default to Role.USER per the catch block
        Role result = jwtTokenProvider.getRoleFromToken(token);
        assertEquals(Role.USER, result);
    }
}