package com.example.maintenance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JwtConfig - JWT configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    
    private String secret;
    private Long accessExpiration;    // milliseconds
    private Long refreshExpiration;   // milliseconds
    private String issuer;
    
    // Default values
    public JwtConfig() {
        this.accessExpiration = 3600000L;      // 1 hour
        this.refreshExpiration = 604800000L;   // 7 days
        this.issuer = "AirlineTicketing";
    }
}
