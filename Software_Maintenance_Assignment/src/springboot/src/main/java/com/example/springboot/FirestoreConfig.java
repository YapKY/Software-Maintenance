package com.example.springboot;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirestoreConfig {

    @Bean
    public Firestore firestore() {
        try {
            // FIX: Load directly from src/main/resources using ClassPathResource
            // This works on ANY computer (Windows/Mac/Linux)
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            
            // Check if file exists to give a better error message
            if (!resource.exists()) {
                throw new IOException("File not found in resources: firebase-service-account.json");
            }

            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Initialize Firebase only if not already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase Application Initialized Successfully!");
            }

            return FirestoreClient.getFirestore();

        } catch (IOException e) {
            System.err.println("❌ CRITICAL ERROR: Could not load firebase-service-account.json");
            System.err.println("Make sure the file is located at: src/main/resources/firebase-service-account.json");
            e.printStackTrace();
            // Throwing an exception here stops the app from starting with a broken state
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}