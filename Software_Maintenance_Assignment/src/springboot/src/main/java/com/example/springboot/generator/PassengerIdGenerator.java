package com.example.springboot.generator;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Passenger ID Generator
 * Auto-generates passengerId based on current record count in Firebase
 */
@Component
public class PassengerIdGenerator {

    private static final String COLLECTION_NAME = "passengers";

    @Autowired
    private Firestore firestore;

    /**
     * Generate next passenger ID based on current count
     * 
     * @return Sequential passenger ID (e.g., "P00001", "P00002", "P00003")
     */
    public String generateId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        long count = future.get().size();
        return "P" + String.format("%05d", count + 1);
    }
}
