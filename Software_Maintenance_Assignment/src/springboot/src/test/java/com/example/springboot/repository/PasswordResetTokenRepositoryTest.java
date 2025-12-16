package com.example.springboot.repository;

import com.example.springboot.enums.Role;
import com.example.springboot.model.PasswordResetToken;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenRepositoryTest {

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
    private PasswordResetTokenRepository repository;

    private PasswordResetToken token;

    @BeforeEach
    void setUp() {
        token = new PasswordResetToken();
        token.setId("tokenDocId");
        token.setToken("reset-uuid");
        token.setUserId("user123");
        token.setUserRole(Role.USER);
        token.setEmail("test@test.com");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(false);
    }

    @Test
    void testSave_NewToken() throws ExecutionException, InterruptedException {
        token.setId(null);
        when(firestore.collection("password_reset_tokens")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generatedId");
        when(collectionReference.document("generatedId")).thenReturn(documentReference);
        when(documentReference.set(anyMap())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        PasswordResetToken saved = repository.save(token);

        assertEquals("generatedId", saved.getId());
    }

    @Test
    void testSave_Failure() {
        when(firestore.collection("password_reset_tokens")).thenThrow(new RuntimeException("Error"));
        assertThrows(RuntimeException.class, () -> repository.save(token));
    }

    @Test
    void testFindByToken_Found() throws ExecutionException, InterruptedException {
        when(firestore.collection("password_reset_tokens")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("token", "reset-uuid")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        mockDocumentData(queryDocumentSnapshot);

        Optional<PasswordResetToken> result = repository.findByToken("reset-uuid");

        assertTrue(result.isPresent());
        assertEquals("reset-uuid", result.get().getToken());
    }

    @Test
    void testFindByToken_NotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("password_reset_tokens")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("token", "invalid-token")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        Optional<PasswordResetToken> result = repository.findByToken("invalid-token");

        assertFalse(result.isPresent());
    }

    @Test
    void testRoleFallback_InvalidRole() throws ExecutionException, InterruptedException {
        when(firestore.collection("password_reset_tokens")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("token", "reset-uuid")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        // Mock invalid role
        mockDocumentData(queryDocumentSnapshot);
        when(queryDocumentSnapshot.getString("userRole")).thenReturn("BAD_ROLE");

        Optional<PasswordResetToken> result = repository.findByToken("reset-uuid");

        assertTrue(result.isPresent());
        assertEquals(Role.USER, result.get().getUserRole()); // Default behavior
    }

    private void mockDocumentData(DocumentSnapshot snapshot) {
        when(snapshot.getId()).thenReturn("tokenDocId");
        when(snapshot.getString("token")).thenReturn("reset-uuid");
        when(snapshot.getString("userId")).thenReturn("user123");
        when(snapshot.getString("userRole")).thenReturn("USER");
        when(snapshot.getString("email")).thenReturn("test@test.com");
        when(snapshot.getString("expiryDate")).thenReturn(LocalDateTime.now().plusHours(1).toString());
        when(snapshot.getBoolean("used")).thenReturn(false);
        when(snapshot.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
    }
}