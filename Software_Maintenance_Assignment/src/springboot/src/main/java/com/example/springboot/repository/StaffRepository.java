package com.example.springboot.repository;

import com.example.springboot.generator.StaffIdGenerator;
import com.example.springboot.model.Staff;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Staff Repository for Firebase Firestore
 * Provides CRUD operations and custom queries for Staff entities
 */
@Repository
public class StaffRepository {

    private static final String COLLECTION_NAME = "staff";

    @Autowired
    private Firestore firestore;

    @Autowired
    private StaffIdGenerator idGenerator;

    public Staff save(Staff staff) throws ExecutionException, InterruptedException {
        // Auto-generate staffId using StaffIdGenerator
        if (staff.getStaffId() == null || staff.getStaffId().isEmpty()) {
            staff.setStaffId(idGenerator.generateId());
        }
        ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME)
                .document(staff.getStaffId())
                .set(staff);
        writeResult.get();
        return staff;
    }

    public Optional<Staff> findById(String staffId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(staffId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Staff staff = document.toObject(Staff.class);
            if (staff != null) {
                staff.setStaffId(document.getId());
            }
            return Optional.ofNullable(staff);
        }
        return Optional.empty();
    }

    public List<Staff> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Staff> staffList = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            Staff staff = document.toObject(Staff.class);
            if (staff != null) {
                staff.setStaffId(document.getId());
                staffList.add(staff);
            }
        }
        return staffList;
    }

    public Optional<Staff> findByStaffId(String staffId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("staffId", staffId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            Staff staff = documents.get(0).toObject(Staff.class);
            if (staff != null) {
                staff.setStaffId(documents.get(0).getId());
            }
            return Optional.ofNullable(staff);
        }
        return Optional.empty();
    }

    public Optional<Staff> findByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            Staff staff = documents.get(0).toObject(Staff.class);
            if (staff != null) {
                staff.setStaffId(documents.get(0).getId());
            }
            return Optional.ofNullable(staff);
        }
        return Optional.empty();
    }

    public boolean existsByStaffId(String staffId) throws ExecutionException, InterruptedException {
        return findByStaffId(staffId).isPresent();
    }

    public boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        return findByEmail(email).isPresent();
    }

    public boolean existsByPhoneNumber(String phoneNumber) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("phoneNumber", phoneNumber);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        return !documents.isEmpty();
    }

    public Optional<Staff> findByStaffIdAndStfPass(String staffId, String stfPass)
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("staffId", staffId)
                .whereEqualTo("stfPass", stfPass);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            Staff staff = documents.get(0).toObject(Staff.class);
            if (staff != null) {
                staff.setStaffId(documents.get(0).getId());
            }
            return Optional.ofNullable(staff);
        }
        return Optional.empty();
    }

    public void deleteById(String staffId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME).document(staffId).delete();
        writeResult.get();
    }

    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        return future.get().size();
    }

    public boolean existsById(String staffId) throws ExecutionException, InterruptedException {
        return findById(staffId).isPresent();
    }
}
