package com.example.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PreDestroy; // Import PreDestroy
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class AppConfig {
    
    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${firebase.database.url:https://maintenance-b96b3.firebaseio.com}")
    private String databaseUrl;
    
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Prevent double initialization
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        InputStream serviceAccount;
        try {
            String classpathPath = firebaseConfigPath.replace("src/main/resources/", "");
            serviceAccount = new ClassPathResource(classpathPath).getInputStream();
        } catch (Exception e) {
            log.warn("Could not load from classpath, trying file system: {}", firebaseConfigPath);
            serviceAccount = new java.io.FileInputStream(firebaseConfigPath);
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(databaseUrl)
            .build();
        
        log.info("Firebase Admin SDK initialized successfully");
        return FirebaseApp.initializeApp(options);
    }
    
    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
    
    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
    
    // --- ADD THIS METHOD TO FIX "CLIENT ALREADY CLOSED" ERROR ---
    @PreDestroy
    public void destroy() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                log.info("⚠️ Closing Firebase Application to prevent stale clients...");
                FirebaseApp.getInstance().delete();
            }
        } catch (Exception e) {
            log.error("Error closing Firebase: {}", e.getMessage());
        }
    }
    // ------------------------------------------------------------
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}