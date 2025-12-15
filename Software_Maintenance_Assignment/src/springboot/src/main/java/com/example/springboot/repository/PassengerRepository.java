package com.example.springboot.repository;

import com.example.springboot.generator.PassengerIdGenerator;
import com.example.springboot.model.Passenger;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Passenger Repository for Firebase Firestore
 * Provides CRUD operations and custom queries for Passenger entities
 */
@Repository
public class PassengerRepository {

    private static final String COLLECTION_NAME = "passengers";

    @Autowired
    private Firestore firestore;

    @Autowired
    private PassengerIdGenerator idGenerator;

    public Passenger save(Passenger passenger) throws ExecutionException, InterruptedException {
        // Auto-generate passengerId using PassengerIdGenerator
        if (passenger.getPassengerId() == null || passenger.getPassengerId().isEmpty()) {
            passenger.setPassengerId(idGenerator.generateId());
        }
        ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME)
                .document(passenger.getPassengerId())
                .set(passenger);
        writeResult.get();
        return passenger;
    }

    public Optional<Passenger> findById(String passengerId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(passengerId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Passenger passenger = document.toObject(Passenger.class);
            return Optional.ofNullable(passenger);
        }
        return Optional.empty();
    }

    public List<Passenger> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Passenger> passengers = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            passengers.add(document.toObject(Passenger.class));
        }
        return passengers;
    }

    public Optional<Passenger> findByPassportNo(String passportNo) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("passportNo", passportNo);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            return Optional.ofNullable(documents.get(0).toObject(Passenger.class));
        }
        return Optional.empty();
    }

    public Optional<Passenger> findByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            return Optional.ofNullable(documents.get(0).toObject(Passenger.class));
        }
        return Optional.empty();
    }

    public boolean existsByPassportNo(String passportNo) throws ExecutionException, InterruptedException {
        return findByPassportNo(passportNo).isPresent();
    }

    public void deleteById(String passengerId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME).document(passengerId).delete();
        writeResult.get();
    }

    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        return future.get().size();
    }

    public boolean existsById(String passengerId) throws ExecutionException, InterruptedException {
        return findById(passengerId).isPresent();
    }
}
