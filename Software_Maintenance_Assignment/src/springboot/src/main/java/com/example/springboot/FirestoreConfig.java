package com.example.springboot;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirestoreConfig {

    @Bean
    public Firestore firestore() {
        try {
            // 1. Point to your specific file path
            // NOTE: Double backslashes (\\) are required for Windows paths in Java
            // NOTE: Check if you need to add ".json" at the end of this string!
            String keyPath = "C:\\Users\\WENYEE\\flutter_workplace\\Software-Maintenance\\Software_Maintenance_Assignment\\src\\springboot\\src\\main\\resources\\firebase-service-account.json"; // Add .json here if needed

            FileInputStream serviceAccount = new FileInputStream(keyPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 2. Initialize Firebase (only if it hasn't been done yet)
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase Application Initialized Successfully!");
            }

            // 3. Return the Firestore connection
            return FirestoreClient.getFirestore();

        } catch (IOException e) {
            // This will print the exact reason if the file is not found
            System.err.println("❌ CRITICAL ERROR: Could not find firebase key file!");
            System.err.println("Checked path: " + "C:\\Users\\WENYEE... (check code for full path)");
            e.printStackTrace();
            return null; // This will stop the app from crashing immediately, but API calls will fail
        }
    }
}