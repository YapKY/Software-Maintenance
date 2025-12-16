package com.example.springboot.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {JwtConfig.class})
@EnableConfigurationProperties(JwtConfig.class)
@TestPropertySource(properties = {
    "jwt.secret=testSecretKey1234567890testSecretKey1234567890",
    "jwt.access-expiration=1800000",
    "jwt.refresh-expiration=3600000",
    "jwt.issuer=TestIssuer"
})
public class JwtConfigTest {

    @Autowired
    private JwtConfig jwtConfig;

    @Test
    public void testJwtPropertiesBinding() {
        // Positive Test: Verify properties are bound correctly from application.properties/yml
        Assertions.assertEquals("testSecretKey1234567890testSecretKey1234567890", jwtConfig.getSecret());
        Assertions.assertEquals(1800000L, jwtConfig.getAccessExpiration());
        Assertions.assertEquals(3600000L, jwtConfig.getRefreshExpiration());
        Assertions.assertEquals("TestIssuer", jwtConfig.getIssuer());
    }

    @Test
    public void testJwtConfigDefaultValues() {
        // Positive Test: Verify default values (by creating a manual instance)
        JwtConfig defaultConfig = new JwtConfig();
        
        Assertions.assertEquals(3600000L, defaultConfig.getAccessExpiration()); // 1 hour
        Assertions.assertEquals(604800000L, defaultConfig.getRefreshExpiration()); // 7 days
        Assertions.assertEquals("AirlineTicketing", defaultConfig.getIssuer());
    }
}