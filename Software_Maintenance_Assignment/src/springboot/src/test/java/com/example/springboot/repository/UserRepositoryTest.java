package com.example.springboot.repository;

import com.example.springboot.enums.AuthProvider;
import com.example.springboot.enums.Gender;
import com.example.springboot.enums.Role;
import com.example.springboot.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserRepositoryTest {

    @Mock private Firestore firestore;
    @Mock private CollectionReference collectionReference;
    @Mock private DocumentReference documentReference;
    @Mock private ApiFuture<WriteResult> writeResultFuture;
    @Mock private ApiFuture<DocumentSnapshot> documentSnapshotFuture;
    @Mock private ApiFuture<QuerySnapshot> querySnapshotFuture;
    @Mock private DocumentSnapshot documentSnapshot;
    @Mock private QuerySnapshot querySnapshot;
    @Mock private Query query;

    @InjectMocks
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        lenient().when(firestore.collection("customers")).thenReturn(collectionReference);

        testUser = User.builder()
                .custId("cust-123")
                .email("test@example.com")
                .name("Test User")
                .role(Role.USER)
                .authProvider(AuthProvider.EMAIL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void mockUserSnapshot(DocumentSnapshot snapshot, String id, String email) {
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getId()).thenReturn(id);
        when(snapshot.getString("email")).thenReturn(email);
        when(snapshot.getString("name")).thenReturn("Test User");
        when(snapshot.getString("role")).thenReturn("USER");
        when(snapshot.getString("authProvider")).thenReturn("EMAIL");
        when(snapshot.getString("providerId")).thenReturn("provider-id");
        when(snapshot.getBoolean("mfaEnabled")).thenReturn(false);
        when(snapshot.getBoolean("emailVerified")).thenReturn(true);
        when(snapshot.getBoolean("accountLocked")).thenReturn(false);
        when(snapshot.getLong("failedLoginAttempts")).thenReturn(0L);
        when(snapshot.getString("lastLoginAt")).thenReturn(LocalDateTime.now().toString());
        when(snapshot.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
        when(snapshot.getString("updatedAt")).thenReturn(LocalDateTime.now().toString());
        when(snapshot.getString("phoneNumber")).thenReturn("012-3456789");
        when(snapshot.getString("gender")).thenReturn("MALE");
    }

    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Save - Success (New User)")
    void testSave_Success() throws ExecutionException, InterruptedException {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generated-id");
        when(collectionReference.document("generated-id")).thenReturn(documentReference);
        when(documentReference.set(any(Map.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        User result = userRepository.save(newUser);

        assertNotNull(result.getCustId());
        assertEquals("generated-id", result.getCustId());
        verify(documentReference).set(any(Map.class));
    }

    @Test
    @DisplayName("Save - Failure (Firestore Exception)")
    void testSave_Failure() throws ExecutionException, InterruptedException {
        when(collectionReference.document(anyString())).thenReturn(documentReference);
        when(documentReference.set(any(Map.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenThrow(new InterruptedException("Firestore error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userRepository.save(testUser);
        });
        
        assertTrue(exception.getMessage().contains("Failed to save user"));
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("FindById - Found")
    void testFindById_Found() throws ExecutionException, InterruptedException {
        when(collectionReference.document("cust-123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        
        mockUserSnapshot(documentSnapshot, "cust-123", "test@example.com");
        
        Optional<User> result = userRepository.findById("cust-123");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("FindById - Not Found")
    void testFindById_NotFound() throws ExecutionException, InterruptedException {
        when(collectionReference.document("cust-123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        Optional<User> result = userRepository.findById("cust-123");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("FindById - Mapping Logic (Invalid Role Fallback)")
    void testFindById_InvalidRole_Fallback() throws ExecutionException, InterruptedException {
        when(collectionReference.document("cust-123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        
        // Mock standard fields first
        mockUserSnapshot(documentSnapshot, "cust-123", "test@example.com");
        // Override role to be invalid
        when(documentSnapshot.getString("role")).thenReturn("INVALID_ROLE"); 

        Optional<User> result = userRepository.findById("cust-123");

        assertTrue(result.isPresent());
        assertEquals(Role.USER, result.get().getRole(), "Should fallback to USER on invalid role");
    }

    // ==================== QUERY TESTS ====================

    @Test
    @DisplayName("FindByEmail - Success")
    void testFindByEmail_Success() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        
        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        
        mockUserSnapshot(mockDoc, "cust-123", "test@example.com");

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("ExistsByPhoneNumber - True")
    void testExistsByPhoneNumber_True() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("phoneNumber", "0123456789")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        
        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));

        boolean exists = userRepository.existsByPhoneNumber("0123456789");

        assertTrue(exists);
    }

    @Test
    @DisplayName("FindByEmailAndAuthProvider - Found")
    void testFindByEmailAndAuthProvider() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("email", "test@gmail.com")).thenReturn(query);
        when(query.whereEqualTo("authProvider", "GOOGLE")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);

        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        
        mockUserSnapshot(mockDoc, "cust-123", "test@gmail.com");
        when(mockDoc.getString("authProvider")).thenReturn("GOOGLE");

        Optional<User> result = userRepository.findByEmailAndAuthProvider("test@gmail.com", AuthProvider.GOOGLE);
        
        assertTrue(result.isPresent());
    }

    // ==================== DELETE TEST ====================

    @Test
    @DisplayName("DeleteById - Success")
    void testDeleteById_Success() throws ExecutionException, InterruptedException {
        when(collectionReference.document("cust-123")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        assertDoesNotThrow(() -> userRepository.deleteById("cust-123"));
        verify(documentReference).delete();
    }

    // ==================== FIND ALL TEST ====================

    @Test
    @DisplayName("FindAll - Success")
    void testFindAll() throws ExecutionException, InterruptedException {
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        
        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        mockUserSnapshot(doc1, "1", "user1@test.com");
        
        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
        mockUserSnapshot(doc2, "2", "user2@test.com");

        when(querySnapshot.getDocuments()).thenReturn(List.of(doc1, doc2));

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }
}