package com.example.springboot.repository;

import com.example.springboot.enums.Role;
import com.example.springboot.model.RefreshToken;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@MockitoSettings(strictness = Strictness.LENIENT) // Use lenient to avoid unnecessary stubbing errors on partial mocks
class RefreshTokenRepositoryTest {

    @Mock private Firestore firestore;
    @Mock private CollectionReference collectionReference;
    @Mock private DocumentReference documentReference;
    @Mock private ApiFuture<WriteResult> writeResultFuture;
    @Mock private ApiFuture<QuerySnapshot> querySnapshotFuture;
    @Mock private QuerySnapshot querySnapshot;
    @Mock private Query query;

    @InjectMocks
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        lenient().when(firestore.collection("refresh_tokens")).thenReturn(collectionReference);
    }

    private void mockRefreshTokenSnapshot(QueryDocumentSnapshot mockDoc, String tokenStr) {
        when(mockDoc.getId()).thenReturn("doc-1");
        when(mockDoc.getString("token")).thenReturn(tokenStr);
        when(mockDoc.getString("userId")).thenReturn("u1");
        when(mockDoc.getString("userRole")).thenReturn("USER");
        when(mockDoc.getString("expiryDate")).thenReturn(LocalDateTime.now().plusDays(1).toString());
        when(mockDoc.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
        when(mockDoc.getBoolean("revoked")).thenReturn(false);
    }

    @Test
    @DisplayName("Save - Success")
    void testSave() throws ExecutionException, InterruptedException {
        RefreshToken token = RefreshToken.builder()
                .token("xyz-token")
                .userId("u1")
                .userRole(Role.USER)
                .expiryDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now()) // Ensure createdAt is set
                .revoked(false)
                .build();

        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("doc-1");
        when(collectionReference.document("doc-1")).thenReturn(documentReference);
        when(documentReference.set(any(Map.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        RefreshToken result = refreshTokenRepository.save(token);

        assertEquals("doc-1", result.getId());
        verify(documentReference).set(any(Map.class));
    }

    @Test
    @DisplayName("FindByToken - Success")
    void testFindByToken() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("token", "xyz-token")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);

        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        
        mockRefreshTokenSnapshot(mockDoc, "xyz-token");

        Optional<RefreshToken> result = refreshTokenRepository.findByToken("xyz-token");

        assertTrue(result.isPresent());
        assertEquals("u1", result.get().getUserId());
    }

    @Test
    @DisplayName("RevokeToken - Success")
    void testRevokeToken_Success() throws ExecutionException, InterruptedException {
        // Step 1: Mock Finding the token
        when(collectionReference.whereEqualTo("token", "active-token")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);

        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        
        // Use helper to mock ALL fields required by convertToRefreshToken
        mockRefreshTokenSnapshot(mockDoc, "active-token");

        // Step 2: Mock Saving the token (the update part)
        // Note: Repository likely calls firestore.collection(...).document(ID).set(...)
        when(collectionReference.document("doc-1")).thenReturn(documentReference);
        when(documentReference.set(any(Map.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        // Act
        refreshTokenRepository.revokeToken("active-token");

        // Assert
        ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(documentReference).set(mapCaptor.capture());
        
        Map<String, Object> savedData = mapCaptor.getValue();
        assertEquals(true, savedData.get("revoked"), "Revoked status should be true");
    }

    @Test
    @DisplayName("RevokeToken - Token Not Found")
    void testRevokeToken_NotFound() throws ExecutionException, InterruptedException {
        when(collectionReference.whereEqualTo("token", "missing-token")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        refreshTokenRepository.revokeToken("missing-token");

        // Assert: Save should NEVER be called if token not found
        verify(documentReference, never()).set(any());
    }
    
    @Test
    @DisplayName("Exception Handling in Save")
    void testSave_Exception() {
        when(collectionReference.document()).thenThrow(new RuntimeException("Firestore Down"));
        
        assertThrows(RuntimeException.class, () -> {
            refreshTokenRepository.save(new RefreshToken());
        });
    }
}