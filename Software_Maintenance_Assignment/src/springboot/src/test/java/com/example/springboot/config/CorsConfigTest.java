package com.example.springboot.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

@SpringBootTest(classes = CorsConfig.class)
public class CorsConfigTest {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    public void testCorsConfiguration() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);

        Assertions.assertNotNull(config, "CorsConfiguration should not be null");

        // Positive Test: Verify Allowed Origins
        List<String> allowedOrigins = config.getAllowedOrigins();
        Assertions.assertTrue(allowedOrigins.contains("https://localhost:8081"), "Should allow HTTPS localhost");
        Assertions.assertTrue(allowedOrigins.contains("https://yourdomain.com"), "Should allow production HTTPS domain");
        
        // Negative Test: Ensure wildcards are not used if credentials are allowed (security best practice)
        Assertions.assertFalse(allowedOrigins.contains("*"), "Should not allow wildcard '*' when allowCredentials is true");

        // Positive Test: Verify Allowed Methods
        List<String> allowedMethods = config.getAllowedMethods();
        Assertions.assertTrue(allowedMethods.contains("GET"));
        Assertions.assertTrue(allowedMethods.contains("POST"));
        Assertions.assertTrue(allowedMethods.contains("PUT"));
        Assertions.assertTrue(allowedMethods.contains("DELETE"));

        // Positive Test: Verify Allowed Headers
        Assertions.assertTrue(config.getAllowedHeaders().contains("Authorization"));
        Assertions.assertTrue(config.getAllowedHeaders().contains("Content-Type"));

        // Positive Test: Verify Credentials
        Assertions.assertTrue(config.getAllowCredentials(), "Credentials should be allowed");
        
        // Positive Test: Verify Max Age
        Assertions.assertEquals(3600L, config.getMaxAge());
    }
}