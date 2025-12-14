package com.example.springboot.repository;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

@Repository
public class FirestoreRepository {

    // Make this public so FlightService can access it
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // Generic Save
    public String save(String collectionName, Object data) {
        try {
            DocumentReference docRef = getFirestore().collection(collectionName).document();
            docRef.set(data).get(); // Wait for completion
            return docRef.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save to Firestore: " + e.getMessage());
        }
    }
    
    // Save with specific ID
    public void saveWithId(String collectionName, String docId, Object data) {
        try {
            getFirestore().collection(collectionName).document(docId).set(data).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save with ID: " + e.getMessage());
        }
    }

    // Generic Find By ID
    public <T> T findById(String collectionName, String docId, Class<T> type) throws Exception {
        DocumentSnapshot doc = getFirestore().collection(collectionName).document(docId).get().get();
        if (doc.exists()) {
            T object = doc.toObject(type);
            return object;
        }
        return null;
    }

    // Generic Update Field
    public void updateField(String collectionName, String docId, String fieldName, Object value) {
        try {
            getFirestore().collection(collectionName).document(docId).update(fieldName, value).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update field: " + e.getMessage());
        }
    }
    
    // Query by Field
    public Query getCollectionByField(String collectionName, String field, String value) {
        return getFirestore().collection(collectionName).whereEqualTo(field, value);
    }
}