package com.example.springboot.repository;

import com.example.springboot.enums.Gender;
import com.example.springboot.enums.Role;
import com.example.springboot.model.Admin;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Use LENIENT to handle unused stubs in helper methods
class AdminRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private Query query;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @InjectMocks
    private AdminRepository adminRepository;

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setStaffId("admin123");
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setRole(Role.ADMIN);
        admin.setGender(Gender.MALE);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        // Set other fields to ensure full object state
        admin.setStaffPass("password123"); 
        admin.setPhoneNumber("0123456789");
    }

    // --- Save Tests ---

    @Test
    void testSave_NewAdmin_GeneratesId() throws ExecutionException, InterruptedException {
        // Arrange
        admin.setStaffId(null); // Ensure ID is null
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference); // For ID generation
        when(documentReference.getId()).thenReturn("generatedId");
        when(collectionReference.document("generatedId")).thenReturn(documentReference); // For save operation
        
        // Removed the unnecessary stub: when(documentReference.set(any(Admin.class)))
        when(documentReference.set(anyMap())).thenReturn(writeResultFuture); 
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        Admin savedAdmin = adminRepository.save(admin);

        // Assert
        assertNotNull(savedAdmin.getStaffId());
        assertEquals("generatedId", savedAdmin.getStaffId());
        verify(documentReference).set(anyMap());
    }

    @Test
    void testSave_ExistingAdmin() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document(admin.getStaffId())).thenReturn(documentReference);
        when(documentReference.set(anyMap())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        // Act
        Admin savedAdmin = adminRepository.save(admin);

        // Assert
        assertEquals("admin123", savedAdmin.getStaffId());
        verify(documentReference).set(anyMap());
    }

    @Test
    void testSave_Failure() {
        // Arrange
        when(firestore.collection("staff")).thenThrow(new RuntimeException("Firestore error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> adminRepository.save(admin));
    }

    // --- Find By ID Tests ---

    @Test
    void testFindById_Found() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document("admin123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        
        mockDocumentSnapshotData(documentSnapshot);

        // Act
        Optional<Admin> result = adminRepository.findById("admin123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("admin@example.com", result.get().getEmail());
        assertEquals(Role.ADMIN, result.get().getRole());
    }

    @Test
    void testFindById_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document("admin123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        // Act
        Optional<Admin> result = adminRepository.findById("admin123");

        // Assert
        assertFalse(result.isPresent());
    }

    // --- Find By Email Tests ---

    @Test
    void testFindByEmail_Found() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "admin@example.com")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        
        mockDocumentSnapshotData(queryDocumentSnapshot);

        // Act
        Optional<Admin> result = adminRepository.findByEmail("admin@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("admin@example.com", result.get().getEmail());
    }

    // --- Find By CreatedBy Tests ---

    @Test
    void testFindByCreatedBy_Found() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("createdBy", "superId")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        
        mockDocumentSnapshotData(queryDocumentSnapshot);

        // Act
        List<Admin> results = adminRepository.findByCreatedBy("superId");

        // Assert
        assertEquals(1, results.size());
        assertEquals("admin@example.com", results.get(0).getEmail());
    }

    // --- Edge Case: Timestamp Parsing (Z suffix) ---

    @Test
    void testTimestampParsing_WithZSuffix() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document("admin123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        
        mockDocumentSnapshotData(documentSnapshot);
        // Overwrite the createdAt stub for this specific test
        when(documentSnapshot.getString("createdAt")).thenReturn("2023-10-01T10:00:00Z");
        
        // Act
        Optional<Admin> result = adminRepository.findById("admin123");

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().getCreatedAt());
    }

    @Test
    void testRoleFallback_InvalidRole() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.document("admin123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        
        mockDocumentSnapshotData(documentSnapshot);
        // Overwrite role with invalid data
        when(documentSnapshot.getString("role")).thenReturn("INVALID_ROLE_NAME");
        
        // Act
        Optional<Admin> result = adminRepository.findById("admin123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Role.ADMIN, result.get().getRole());
    }

    @Test
    void testCountAdminsCreatedBy() throws ExecutionException, InterruptedException {
        // Reuse logic from findByCreatedBy
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("createdBy", "superId")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        
        mockDocumentSnapshotData(queryDocumentSnapshot);

        int count = adminRepository.countAdminsCreatedBy("superId");
        assertEquals(1, count);
    }

    @Test
    void testExistsByEmail() throws ExecutionException, InterruptedException {
        // Reuse logic from findByEmail
        when(firestore.collection("staff")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "admin@example.com")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        
        mockDocumentSnapshotData(queryDocumentSnapshot);

        assertTrue(adminRepository.existsByEmail("admin@example.com"));
    }

    // Helper method to mock all possible fields accessed by convertToAdmin
    private void mockDocumentSnapshotData(DocumentSnapshot snapshot) {
        // Basic existence
        lenient().when(snapshot.exists()).thenReturn(true);
        lenient().when(snapshot.getId()).thenReturn("admin123");

        // Core fields
        lenient().when(snapshot.getString("email")).thenReturn("admin@example.com");
        lenient().when(snapshot.getString("name")).thenReturn("Admin User");
        lenient().when(snapshot.getString("role")).thenReturn("ADMIN");
        lenient().when(snapshot.getString("gender")).thenReturn("MALE");
        lenient().when(snapshot.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
        lenient().when(snapshot.getString("updatedAt")).thenReturn(LocalDateTime.now().toString());

        // Security fields (Critical fix: these were missing causing the crashes)
        lenient().when(snapshot.getString("staffPass")).thenReturn("encryptedPass");
        lenient().when(snapshot.getBoolean("mfaEnabled")).thenReturn(true);
        lenient().when(snapshot.getBoolean("accountLocked")).thenReturn(false);
        lenient().when(snapshot.getLong("failedLoginAttempts")).thenReturn(0L);

        // Profile fields
        lenient().when(snapshot.getString("profileImage")).thenReturn("http://image.url");
        lenient().when(snapshot.getString("phoneNumber")).thenReturn("1234567890");
        lenient().when(snapshot.getString("address")).thenReturn("123 Test St");
        lenient().when(snapshot.getString("status")).thenReturn("ACTIVE");
        lenient().when(snapshot.getString("lastLoginAt")).thenReturn(LocalDateTime.now().toString());
        lenient().when(snapshot.getString("createdBy")).thenReturn("superId");
    }
}