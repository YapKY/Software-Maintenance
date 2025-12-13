package com.example.springboot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Firebase Configuration
 * Initializes Firebase Admin SDK and provides Firestore instance
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private String firebaseCredentialsPath;

    @Bean
    public Firestore firestore() throws IOException {
        // Initialize Firebase if not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}
