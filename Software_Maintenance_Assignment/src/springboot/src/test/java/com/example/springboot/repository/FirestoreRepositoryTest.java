package com.example.springboot.repository;

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

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FirestoreRepositoryTest {

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
    private Query query;

    @InjectMocks
    private FirestoreRepository firestoreRepository;

    private static final String COLLECTION_NAME = "testCollection";
    private static final String DOC_ID = "testId";

    @BeforeEach
    void setUp() {
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    void testGetFirestore() {
        assertEquals(firestore, firestoreRepository.getFirestore());
    }

    @Test
    void testSave_GeneratesId() throws ExecutionException, InterruptedException {
        Object data = new Object();
        when(documentReference.getId()).thenReturn(DOC_ID);
        when(documentReference.set(data)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        String resultId = firestoreRepository.save(COLLECTION_NAME, data);

        assertEquals(DOC_ID, resultId);
        verify(firestore).collection(COLLECTION_NAME);
        verify(collectionReference).document();
        verify(documentReference).set(data);
    }

    @Test
    void testSave_Exception() {
        Object data = new Object();
        when(documentReference.set(data)).thenThrow(new RuntimeException("Firestore error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            firestoreRepository.save(COLLECTION_NAME, data);
        });

        assertTrue(exception.getMessage().contains("Failed to save to Firestore"));
    }

    @Test
    void testSaveWithId() throws ExecutionException, InterruptedException {
        Object data = new Object();
        when(documentReference.set(data)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        firestoreRepository.saveWithId(COLLECTION_NAME, DOC_ID, data);

        verify(firestore).collection(COLLECTION_NAME);
        verify(collectionReference).document(DOC_ID);
        verify(documentReference).set(data);
    }

    @Test
    void testSaveWithId_Exception() {
        Object data = new Object();
        when(documentReference.set(data)).thenThrow(new RuntimeException("Firestore error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            firestoreRepository.saveWithId(COLLECTION_NAME, DOC_ID, data);
        });

        assertTrue(exception.getMessage().contains("Failed to save with ID"));
    }

    @Test
    void testFindById_Found() throws Exception {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(String.class)).thenReturn("FoundData");

        String result = firestoreRepository.findById(COLLECTION_NAME, DOC_ID, String.class);

        assertEquals("FoundData", result);
    }

    @Test
    void testFindById_NotFound() throws Exception {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        String result = firestoreRepository.findById(COLLECTION_NAME, DOC_ID, String.class);

        assertNull(result);
    }

    @Test
    void testUpdateField() throws ExecutionException, InterruptedException {
        when(documentReference.update(anyString(), any())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        firestoreRepository.updateField(COLLECTION_NAME, DOC_ID, "fieldName", "value");

        verify(documentReference).update("fieldName", "value");
    }

    @Test
    void testUpdateField_Exception() {
        when(documentReference.update(anyString(), any())).thenThrow(new RuntimeException("Firestore error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            firestoreRepository.updateField(COLLECTION_NAME, DOC_ID, "fieldName", "value");
        });

        assertTrue(exception.getMessage().contains("Failed to update field"));
    }

    @Test
    void testGetCollectionByField() {
        when(collectionReference.whereEqualTo(anyString(), any())).thenReturn(query);

        Query result = firestoreRepository.getCollectionByField(COLLECTION_NAME, "field", "value");

        assertEquals(query, result);
        verify(collectionReference).whereEqualTo("field", "value");
    }
}
