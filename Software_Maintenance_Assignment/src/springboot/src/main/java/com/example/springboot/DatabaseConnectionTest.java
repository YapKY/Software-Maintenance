package com.example.springboot;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Firebase Connection Test
 * Tests Firebase Firestore connectivity
 */
public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String credentialsPath = "c:/Users/user/Downloads/maintenance-b96b3-firebase-adminsdk-fbsvc-342a6798db.json";

        try {
            FileInputStream serviceAccount = new FileInputStream(credentialsPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            Firestore firestore = FirestoreClient.getFirestore();

            System.out.println("✅ Firebase connection successful!");
            System.out.println("Firestore instance initialized: " + (firestore != null));

        } catch (IOException e) {
            System.out.println("❌ Firebase connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}