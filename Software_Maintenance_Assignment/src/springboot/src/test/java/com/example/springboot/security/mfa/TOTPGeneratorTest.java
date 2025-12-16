package com.example.springboot.security.mfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TOTPGeneratorTest {

    @InjectMocks
    private TOTPGenerator totpGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Generate Secret - Returns valid Base32 string")
    void testGenerateSecret() {
        String secret = totpGenerator.generateSecret();
        assertNotNull(secret);
        // 20 bytes encoded in Base32 (5 bits per char) -> 160 bits / 5 = 32 chars
        assertEquals(32, secret.length()); 
        assertTrue(secret.matches("^[A-Z2-7]+$")); // Base32 alphabet
    }

    @Test
    @DisplayName("Generate QR Code URL - Format Check")
    void testGenerateQRCodeUrl() {
        String secret = "JBSWY3DPEHPK3PXP"; // Test Base32 string
        String account = "test@example.com";
        
        String url = totpGenerator.generateQRCodeUrl(secret, account);
        
        assertNotNull(url);
        assertTrue(url.startsWith("https://api.qrserver.com/v1/create-qr-code/"));
        assertTrue(url.contains("data="));
        // Encoded "AirlineTicketing"
        assertTrue(url.contains("AirlineTicketing"));
    }

    @Test
    @DisplayName("Validate Code - Logic Test (Reflection/Mocking Time)")
    void testValidateCode_Logic() throws Exception {
        // Since validateCode relies on System.currentTimeMillis, we can't easily mock time 
        // without PowerMock or changing source to use a Clock. 
        // Instead, we will generate a valid code for "NOW" using the private method and ensure it passes validation.
        
        String secret = totpGenerator.generateSecret();
        
        // Access private method generateCode via reflection
        Method generateCodeMethod = TOTPGenerator.class.getDeclaredMethod("generateCode", String.class, long.class);
        generateCodeMethod.setAccessible(true);
        
        long currentTime = System.currentTimeMillis() / 1000L;
        long timeStep = currentTime / 30; // 30s step
        
        String validCode = (String) generateCodeMethod.invoke(totpGenerator, secret, timeStep);
        
        // Test 1: Current code
        assertTrue(totpGenerator.validateCode(secret, validCode), "Should validate current code");
        
        // Test 2: Invalid code
        assertFalse(totpGenerator.validateCode(secret, "000000"), "Should reject invalid code");
        
        // Test 3: Exception handling (invalid secret format)
        assertFalse(totpGenerator.validateCode("NOT_BASE_32_!!!", "123456"), "Should handle crypto exceptions gracefully");
    }
}