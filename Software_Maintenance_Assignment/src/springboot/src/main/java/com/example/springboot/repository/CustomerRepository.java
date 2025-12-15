package com.example.springboot.repository;

import com.example.springboot.generator.CustomerIdGenerator;
import com.example.springboot.model.Customer;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Customer Repository for Firebase Firestore
 * Provides CRUD operations and custom queries for Customer entities
 */
@Repository
public class CustomerRepository {

    private static final String COLLECTION_NAME = "customers";

    @Autowired
    private Firestore firestore;

    @Autowired
    private CustomerIdGenerator idGenerator;

    public Customer save(Customer customer) throws ExecutionException, InterruptedException {
        // Auto-generate custId using CustomerIdGenerator
        if (customer.getCustId() == null || customer.getCustId().isEmpty()) {
            customer.setCustId(idGenerator.generateId());
        }
        ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME)
                .document(customer.getCustId())
                .set(customer, SetOptions.merge());
        writeResult.get();
        return customer;
    }

    public Optional<Customer> findById(String custId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(custId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Customer customer = document.toObject(Customer.class);
            if (customer != null) {
                customer.setCustId(document.getId());
            }
            return Optional.ofNullable(customer);
        }
        return Optional.empty();
    }

    public List<Customer> findAll() throws ExecutionException, InterruptedException {
        System.out.println("DEBUG: findAll called");
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Customer> customers = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            Customer customer = document.toObject(Customer.class);
            if (customer != null) {
                System.out.println("DEBUG: Setting custId to " + document.getId());
                customer.setCustId(document.getId());
                customers.add(customer);
            }
        }
        return customers;
    }

    public Optional<Customer> findByCustIcNo(String custIcNo) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("custIcNo", custIcNo);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            Customer customer = documents.get(0).toObject(Customer.class);
            if (customer != null) {
                customer.setCustId(documents.get(0).getId());
            }
            return Optional.ofNullable(customer);
        }
        return Optional.empty();
    }

    public Optional<Customer> findByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            Customer customer = documents.get(0).toObject(Customer.class);
            if (customer != null) {
                customer.setCustId(documents.get(0).getId());
            }
            return Optional.ofNullable(customer);
        }
        return Optional.empty();
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("phoneNumber", phoneNumber);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            Customer customer = documents.get(0).toObject(Customer.class);
            if (customer != null) {
                customer.setCustId(documents.get(0).getId());
            }
            return Optional.ofNullable(customer);
        }
        return Optional.empty();
    }

    public boolean existsByCustIcNo(String custIcNo) throws ExecutionException, InterruptedException {
        return findByCustIcNo(custIcNo).isPresent();
    }

    public boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        return findByEmail(email).isPresent();
    }

    public boolean existsByPhoneNumber(String phoneNumber) throws ExecutionException, InterruptedException {
        return findByPhoneNumber(phoneNumber).isPresent();
    }

    public boolean existsByCustPassword(String custPassword) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("custPassword", custPassword);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return !querySnapshot.get().getDocuments().isEmpty();
    }

    public void deleteById(String custId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME).document(custId).delete();
        writeResult.get();
    }

    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        return future.get().size();
    }

    public boolean existsById(String custId) throws ExecutionException, InterruptedException {
        return findById(custId).isPresent();
    }
}
