package com.example.springboot.security.jwt;

import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private final String SECRET = "mySecretKeyMustBeLongEnoughForHS512Algorithm_AtLeast64BytesLength!!";
    private final long ACCESS_EXP = 3600000L;
    private final long REFRESH_EXP = 7200000L;

    @BeforeEach
    void setUp() {
        // Inject @Value properties manually
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", ACCESS_EXP);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", REFRESH_EXP);
    }

    @Test
    @DisplayName("Generate Tokens - Structure Check")
    void testGenerateTokens() {
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens("u1", "test@mail.com", Role.USER);

        assertNotNull(tokens.getAccessToken());
        assertNotNull(tokens.getRefreshToken());
        assertEquals("Bearer", tokens.getTokenType());
        assertEquals(Role.USER, tokens.getRole());
    }

    @Test
    @DisplayName("Generate MFA Session Token")
    void testGenerateMFASessionToken() {
        String token = jwtTokenProvider.generateMFASessionToken("u1", "test@mail.com", Role.ADMIN);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Validate Token - Valid")
    void testValidateToken_Valid() {
        String token = jwtTokenProvider.generateTokens("u1", "test@mail.com", Role.USER).getAccessToken();
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Validate Token - Invalid Signature")
    void testValidateToken_Invalid() {
        String token = jwtTokenProvider.generateTokens("u1", "test@mail.com", Role.USER).getAccessToken();
        String tampered = token.substring(0, token.length() - 5) + "xxxxx";
        assertFalse(jwtTokenProvider.validateToken(tampered));
    }

    @Test
    @DisplayName("Extract Claims")
    void testExtractClaims() {
        String token = jwtTokenProvider.generateTokens("user-123", "a@b.com", Role.SUPERADMIN).getAccessToken();

        assertEquals("user-123", jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(Role.SUPERADMIN, jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Get Role - Fallback on Invalid/Null")
    void testGetRole_Fallback() {
        // Create token manually with invalid role string
        Date now = new Date();
        String token = Jwts.builder()
                .setSubject("u1")
                .claim("role", "INVALID_ROLE_ENUM")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 10000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();

        Role role = jwtTokenProvider.getRoleFromToken(token);
        assertEquals(Role.USER, role, "Should default to USER for invalid role strings");
    }
}