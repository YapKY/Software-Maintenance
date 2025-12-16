package com.example.springboot.repository;

import com.example.springboot.enums.Role;
import com.example.springboot.model.MFASecret;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MFASecretRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private Query query;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @InjectMocks
    private MFASecretRepository mfaSecretRepository;

    private MFASecret mfaSecret;

    @BeforeEach
    void setUp() {
        mfaSecret = new MFASecret();
        mfaSecret.setId("secret123");
        mfaSecret.setUserId("user123");
        mfaSecret.setUserRole(Role.USER);
        mfaSecret.setSecret("MYSECRETKEY");
        mfaSecret.setBackupCodes("code1,code2");
        mfaSecret.setVerified(true);
        mfaSecret.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSave_NewSecret() throws ExecutionException, InterruptedException {
        mfaSecret.setId(null);
        when(firestore.collection("mfa_secrets")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generatedId");
        when(collectionReference.document("generatedId")).thenReturn(documentReference);
        when(documentReference.set(anyMap())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        MFASecret saved = mfaSecretRepository.save(mfaSecret);

        assertNotNull(saved.getId());
        assertEquals("generatedId", saved.getId());
    }

    @Test
    void testSave_Exception() {
        when(firestore.collection("mfa_secrets")).thenThrow(new RuntimeException("DB Error"));
        assertThrows(RuntimeException.class, () -> mfaSecretRepository.save(mfaSecret));
    }

    @Test
    void testFindByUserIdAndUserRole_Found() throws ExecutionException, InterruptedException {
        when(firestore.collection("mfa_secrets")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("userId", "user123")).thenReturn(query);
        when(query.whereEqualTo("userRole", "USER")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        mockDocumentData(queryDocumentSnapshot);

        Optional<MFASecret> result = mfaSecretRepository.findByUserIdAndUserRole("user123", Role.USER);

        assertTrue(result.isPresent());
        assertEquals("MYSECRETKEY", result.get().getSecret());
        assertEquals(Role.USER, result.get().getUserRole());
    }

    @Test
    void testFindByUserIdAndUserRole_NotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("mfa_secrets")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("userId", "user123")).thenReturn(query);
        when(query.whereEqualTo("userRole", "USER")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        Optional<MFASecret> result = mfaSecretRepository.findByUserIdAndUserRole("user123", Role.USER);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteByUserIdAndUserRole_Found() throws ExecutionException, InterruptedException {
        // Setup find behavior first
        when(firestore.collection("mfa_secrets")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("userId", "user123")).thenReturn(query);
        when(query.whereEqualTo("userRole", "USER")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        mockDocumentData(queryDocumentSnapshot);

        // Setup delete behavior
        when(collectionReference.document("secret123")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        mfaSecretRepository.deleteByUserIdAndUserRole("user123", Role.USER);

        verify(documentReference).delete();
    }

    @Test
    void testDeleteByUserIdAndUserRole_NotFound() throws ExecutionException, InterruptedException {
        // Setup find behavior - return empty
        when(firestore.collection("mfa_secrets")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("userId", "user123")).thenReturn(query);
        when(query.whereEqualTo("userRole", "USER")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        mfaSecretRepository.deleteByUserIdAndUserRole("user123", Role.USER);

        // Should not call delete
        verify(documentReference, never()).delete();
    }

    @Test
    void testExistsByUserIdAndUserRole() throws ExecutionException, InterruptedException {
        // Reuse logic from find
        when(firestore.collection("mfa_secrets")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("userId", "user123")).thenReturn(query);
        when(query.whereEqualTo("userRole", "USER")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        mockDocumentData(queryDocumentSnapshot);

        assertTrue(mfaSecretRepository.existsByUserIdAndUserRole("user123", Role.USER));
    }

    private void mockDocumentData(DocumentSnapshot snapshot) {
        when(snapshot.getId()).thenReturn("secret123");
        when(snapshot.getString("userId")).thenReturn("user123");
        when(snapshot.getString("userRole")).thenReturn("USER");
        when(snapshot.getString("secret")).thenReturn("MYSECRETKEY");
        when(snapshot.getString("backupCodes")).thenReturn("code1,code2");
        when(snapshot.getBoolean("verified")).thenReturn(true);
        when(snapshot.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
    }
}