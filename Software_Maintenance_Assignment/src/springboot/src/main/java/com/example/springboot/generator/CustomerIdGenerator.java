package com.example.springboot.generator;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Customer ID Generator
 * Auto-generates custId based on current record count in Firebase
 */
@Component
public class CustomerIdGenerator {

    private static final String COLLECTION_NAME = "customers";

    @Autowired
    private Firestore firestore;

    /**
     * Generate next customer ID based on current count
     * 
     * @return Sequential customer ID (e.g., "C00001", "C00002", "C00003")
     */
    public String generateId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        long count = future.get().size();
        return "C" + String.format("%05d", count + 1);
    }
}
