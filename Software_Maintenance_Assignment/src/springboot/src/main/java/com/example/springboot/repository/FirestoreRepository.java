package com.example.springboot.repository;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ExecutionException;

@Repository
public class FirestoreRepository {

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // Generic Save
    public String save(String collectionName, Object data) {
        DocumentReference docRef = getFirestore().collection(collectionName).document();
        docRef.set(data);
        return docRef.getId();
    }
    
    // Save with specific ID
    public void saveWithId(String collectionName, String docId, Object data) {
        getFirestore().collection(collectionName).document(docId).set(data);
    }

    // Generic Find By ID
    public <T> T findById(String collectionName, String docId, Class<T> type) throws Exception {
        DocumentSnapshot doc = getFirestore().collection(collectionName).document(docId).get().get();
        if (doc.exists()) {
            T object = doc.toObject(type);
            // Assuming the model has a setDocumentId method
            // You might need reflection or an interface here for strict design, 
            // but for simplicity we rely on Lombok getters/setters matching.
            return object;
        }
        return null;
    }

    // Generic Update Field
    public void updateField(String collectionName, String docId, String fieldName, Object value) {
        getFirestore().collection(collectionName).document(docId).update(fieldName, value);
    }
    
    // Query by Field
    public Query getCollectionByField(String collectionName, String field, String value) {
        return getFirestore().collection(collectionName).whereEqualTo(field, value);
    }
}