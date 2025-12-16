package com.example.springboot.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    @Test
    void testDefaultValues() {
        // Test constructor setting default values
        JwtConfig config = new JwtConfig();
        
        assertEquals(3600000L, config.getAccessExpiration());
        assertEquals(604800000L, config.getRefreshExpiration());
        assertEquals("AirlineTicketing", config.getIssuer());
    }

    @Test
    void testSettersAndGetters() {
        JwtConfig config = new JwtConfig();
        
        config.setSecret("newSecret");
        config.setAccessExpiration(1000L);
        config.setRefreshExpiration(2000L);
        config.setIssuer("NewIssuer");

        assertEquals("newSecret", config.getSecret());
        assertEquals(1000L, config.getAccessExpiration());
        assertEquals(2000L, config.getRefreshExpiration());
        assertEquals("NewIssuer", config.getIssuer());
    }

    @Test
    void testEqualsAndHashCode() {
        JwtConfig config1 = new JwtConfig();
        config1.setSecret("secret");
        
        JwtConfig config2 = new JwtConfig();
        config2.setSecret("secret");
        
        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
    }
    
    @Test
    void testToString() {
        JwtConfig config = new JwtConfig();
        config.setSecret("hidden");
        assertTrue(config.toString().contains("hidden"));
    }
}