package com.example.springboot.repository;

import com.example.springboot.model.EmailVerificationToken;
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
class EmailVerificationTokenRepositoryTest {

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
    private EmailVerificationTokenRepository repository;

    private EmailVerificationToken token;

    @BeforeEach
    void setUp() {
        token = new EmailVerificationToken();
        token.setId("verifyDocId");
        token.setToken("verify-uuid");
        token.setUserId("user123");
        token.setEmail("test@test.com");
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        token.setUsed(false);
    }

    @Test
    void testSave_NewToken() throws ExecutionException, InterruptedException {
        token.setId(null);
        when(firestore.collection("email_verification_tokens")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generatedId");
        when(collectionReference.document("generatedId")).thenReturn(documentReference);
        when(documentReference.set(anyMap())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        EmailVerificationToken saved = repository.save(token);

        assertEquals("generatedId", saved.getId());
        verify(documentReference).set(anyMap());
    }

    @Test
    void testSave_Exception() {
        when(firestore.collection("email_verification_tokens")).thenThrow(new RuntimeException("DB Error"));
        assertThrows(RuntimeException.class, () -> repository.save(token));
    }

    @Test
    void testFindByToken_Found() throws ExecutionException, InterruptedException {
        when(firestore.collection("email_verification_tokens")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("token", "verify-uuid")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        mockDocumentData(queryDocumentSnapshot);

        Optional<EmailVerificationToken> result = repository.findByToken("verify-uuid");

        assertTrue(result.isPresent());
        assertEquals("verify-uuid", result.get().getToken());
        assertEquals("user123", result.get().getUserId());
    }

    @Test
    void testFindByToken_NotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("email_verification_tokens")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("token", "invalid")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        Optional<EmailVerificationToken> result = repository.findByToken("invalid");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByToken_Exception() {
        when(firestore.collection("email_verification_tokens")).thenThrow(new RuntimeException("Connection Failed"));
        
        Optional<EmailVerificationToken> result = repository.findByToken("token");
        
        assertFalse(result.isPresent()); // The code explicitly catches Exception and returns Optional.empty()
    }

    private void mockDocumentData(DocumentSnapshot snapshot) {
        when(snapshot.getId()).thenReturn("verifyDocId");
        when(snapshot.getString("token")).thenReturn("verify-uuid");
        when(snapshot.getString("userId")).thenReturn("user123");
        when(snapshot.getString("email")).thenReturn("test@test.com");
        when(snapshot.getString("expiryDate")).thenReturn(LocalDateTime.now().plusHours(24).toString());
        when(snapshot.getBoolean("used")).thenReturn(false);
        when(snapshot.getString("createdAt")).thenReturn(LocalDateTime.now().toString());
    }
}