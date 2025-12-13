package com.example.springboot.generator;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Staff/Admin ID Generator
 * Auto-generates staffId (adminId) based on current record count in Firebase
 */
@Component
public class StaffIdGenerator {

    private static final String COLLECTION_NAME = "staff";

    @Autowired
    private Firestore firestore;

    /**
     * Generate next staff/admin ID based on current count
     * 
     * @return Sequential staff ID with prefix (e.g., "S0001", "S0002", "S0003")
     */
    public String generateId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        long count = future.get().size();
        return "S" + String.format("%04d", count + 1);
    }
}
