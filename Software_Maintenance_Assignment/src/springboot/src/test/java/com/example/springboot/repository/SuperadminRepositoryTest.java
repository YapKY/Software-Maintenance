package com.example.springboot.repository;

import com.example.springboot.enums.Role;
import com.example.springboot.model.Superadmin;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SuperadminRepositoryTest {

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
    private SuperadminRepository superadminRepository;

    @BeforeEach
    void setUp() {
        lenient().when(firestore.collection("superadmins")).thenReturn(collectionReference);
    }

    private void mockSuperadminSnapshot(DocumentSnapshot snapshot) {
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getId()).thenReturn("sa-1");
        when(snapshot.getString("email")).thenReturn("super@admin.com");
        when(snapshot.getString("role")).thenReturn("SUPERADMIN");
        when(snapshot.getBoolean("mfaEnabled")).thenReturn(true);
        when(snapshot.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
        when(snapshot.getString("updatedAt")).thenReturn(LocalDateTime.now().toString());
        when(snapshot.getString("lastLoginAt")).thenReturn(LocalDateTime.now().toString());
    }

    @Test
    @DisplayName("Save - Success")
    void testSave() throws ExecutionException, InterruptedException {
        Superadmin admin = new Superadmin();
        admin.setEmail("super@admin.com");

        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("sa-1");
        when(collectionReference.document("sa-1")).thenReturn(documentReference);
        when(documentReference.set(any(Map.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        Superadmin result = superadminRepository.save(admin);

        assertEquals("sa-1", result.getId());
        verify(documentReference).set(any(Map.class));
    }

    @Test
    @DisplayName("FindById - Found with Valid Data")
    void testFindById_Found() throws ExecutionException, InterruptedException {
        when(collectionReference.document("sa-1")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

        mockSuperadminSnapshot(documentSnapshot);

        Optional<Superadmin> result = superadminRepository.findById("sa-1");

        assertTrue(result.isPresent());
        assertEquals(Role.SUPERADMIN, result.get().getRole());
        assertTrue(result.get().getMfaEnabled());
    }

    @Test
    @DisplayName("FindById - Data Mapping Robustness (Nulls and Invalid Strings)")
    void testFindById_MappingEdgeCases() throws ExecutionException, InterruptedException {
        when(collectionReference.document("sa-1")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getId()).thenReturn("sa-1");

        // Edge Cases
        when(documentSnapshot.getString("role")).thenReturn("INVALID_ROLE"); 
        when(documentSnapshot.getBoolean("mfaEnabled")).thenReturn(null);
        when(documentSnapshot.getString("createdAt")).thenReturn("not-a-date"); 
        when(documentSnapshot.getString("updatedAt")).thenReturn(null);
        when(documentSnapshot.getString("lastLoginAt")).thenReturn(null);

        Optional<Superadmin> result = superadminRepository.findById("sa-1");

        assertTrue(result.isPresent());
        Superadmin sa = result.get();
        
        assertEquals(Role.SUPERADMIN, sa.getRole(), "Should default to SUPERADMIN on invalid role");
        assertTrue(sa.getMfaEnabled(), "Should default to true if mfaEnabled is null");
        assertNotNull(sa.getCreatedAt(), "Should default to now() if date parse fails");
    }

    @Test
    @DisplayName("FindByEmail - Found")
    void testFindByEmail() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("email", "super@admin.com")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);

        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        
        // We must mock the snapshot behavior on the QueryDocumentSnapshot too
        when(mockDoc.exists()).thenReturn(true);
        when(mockDoc.getId()).thenReturn("sa-1");
        when(mockDoc.getString("email")).thenReturn("super@admin.com");
        when(mockDoc.getString("role")).thenReturn("SUPERADMIN");
        when(mockDoc.getBoolean("mfaEnabled")).thenReturn(true);
        when(mockDoc.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
        when(mockDoc.getString("updatedAt")).thenReturn(LocalDateTime.now().toString());
        when(mockDoc.getString("lastLoginAt")).thenReturn(null);

        Optional<Superadmin> result = superadminRepository.findByEmail("super@admin.com");

        assertTrue(result.isPresent());
        assertEquals("super@admin.com", result.get().getEmail());
    }
    
    @Test
    @DisplayName("ExistsByEmail - False")
    void testExistsByEmail_False() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("email", "unknown@admin.com")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        boolean exists = superadminRepository.existsByEmail("unknown@admin.com");

        assertFalse(exists);
    }
}