package com.example.springboot.repository;

import com.example.springboot.model.Staff;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Staff Repository
 * Handles all Firestore database operations for Staff
 * Uses the generic FirestoreRepository for common operations
 */
@Repository
public class StaffRepository {

    private static final String COLLECTION_NAME = "staff";

    @Autowired
    private FirestoreRepository firestoreRepository;

    /**
     * Find staff by Staff ID (e.g., "S001")
     * Used for login authentication
     * Uses staffId as document ID for direct lookup (fast!)
     * 
     * @param staffId The staff ID to search for
     * @return Staff object if found, null otherwise
     */
    public Staff findByStaffId(String staffId) {
        try {
            // Use Firestore document ID = staffId for direct lookup
            return firestoreRepository.findById(COLLECTION_NAME, staffId, Staff.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get all staff members
     * 
     * @return List of all staff
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Staff> findAll() throws ExecutionException, InterruptedException {
        List<Staff> staffList = new ArrayList<>();

        Firestore firestore = firestoreRepository.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = future.get();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Staff staff = documentToStaff(document);
            if (staff != null) {
                staffList.add(staff);
            }
        }

        return staffList;
    }

    /**
     * Save a new staff member
     * Uses staffId as the document ID for easy lookup
     * 
     * @param staff The staff object to save
     * @return The saved staff with documentId populated
     */
    public Staff save(Staff staff) {
        try {
            // Use staffId as the document ID for fast lookup
            String docId = staff.getStaffId();

            // Save to Firestore using FirestoreRepository
            firestoreRepository.saveWithId(COLLECTION_NAME, docId, staff);

            // Set the documentId
            staff.setDocumentId(docId);

            return staff;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save staff: " + e.getMessage(), e);
        }
    }

    /**
     * Update staff information
     * 
     * @param staffId The staff ID to update
     * @param staff   The updated staff object
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void update(String staffId, Staff staff) throws ExecutionException, InterruptedException {
        try {
            Firestore firestore = firestoreRepository.getFirestore();
            firestore.collection(COLLECTION_NAME)
                    .document(staffId)
                    .set(staff)
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update staff: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a staff member
     * 
     * @param staffId The staff ID to delete
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void delete(String staffId) throws ExecutionException, InterruptedException {
        try {
            Firestore firestore = firestoreRepository.getFirestore();
            firestore.collection(COLLECTION_NAME)
                    .document(staffId)
                    .delete()
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete staff: " + e.getMessage(), e);
        }
    }

    /**
     * Check if staff ID exists
     * 
     * @param staffId The staff ID to check
     * @return true if exists, false otherwise
     */
    public boolean existsByStaffId(String staffId) {
        try {
            Staff staff = findByStaffId(staffId);
            return staff != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Find staff by position (e.g., "Manager", "Airline Controller")
     * 
     * @param position The position to filter by
     * @return List of staff with the given position
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Staff> findByPosition(String position) throws ExecutionException, InterruptedException {
        List<Staff> staffList = new ArrayList<>();

        Query query = firestoreRepository.getCollectionByField(COLLECTION_NAME, "position", position);
        QuerySnapshot querySnapshot = query.get().get();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Staff staff = documentToStaff(document);
            if (staff != null) {
                staffList.add(staff);
            }
        }

        return staffList;
    }

    /**
     * Helper method to convert Firestore DocumentSnapshot to Staff object
     * 
     * @param document The Firestore document
     * @return Staff object
     */
    private Staff documentToStaff(DocumentSnapshot document) {
        try {
            Staff staff = new Staff();
            staff.setDocumentId(document.getId());
            staff.setStaffId(document.getString("staffId"));
            staff.setStfPass(document.getString("stfPass"));
            staff.setPosition(document.getString("position"));
            staff.setName(document.getString("name"));
            staff.setPhoneNumber(document.getString("phoneNumber"));
            staff.setGender(document.getString("gender"));
            staff.setEmail(document.getString("email"));
            return staff;
        } catch (Exception e) {
            System.err.println("Error converting document to Staff: " + e.getMessage());
            return null;
        }
    }
}
