package com.example.springboot.repository;

import com.example.springboot.generator.PassengerIdGenerator;
import com.example.springboot.model.Passenger;
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

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PassengerRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private PassengerIdGenerator idGenerator;

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

    @InjectMocks
    private PassengerRepository passengerRepository;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setName("Test Passenger");
        passenger.setPassportNo("A12345678");

        when(firestore.collection("passengers")).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    void testSave_NewPassenger_GeneratesId() throws ExecutionException, InterruptedException {
        passenger.setPassengerId(null);
        when(idGenerator.generateId()).thenReturn("P001");
        when(documentReference.set(any(Passenger.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        Passenger savedPassenger = passengerRepository.save(passenger);

        assertEquals("P001", savedPassenger.getPassengerId());
        verify(idGenerator).generateId();
        verify(documentReference).set(passenger);
    }

    @Test
    void testSave_ExistingPassenger_UsesId() throws ExecutionException, InterruptedException {
        passenger.setPassengerId("P002");
        when(documentReference.set(any(Passenger.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        Passenger savedPassenger = passengerRepository.save(passenger);

        assertEquals("P002", savedPassenger.getPassengerId());
        verify(idGenerator, never()).generateId();
        verify(documentReference).set(passenger);
    }

    @Test
    void testFindById_Found() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Passenger.class)).thenReturn(passenger);

        Optional<Passenger> result = passengerRepository.findById("P001");

        assertTrue(result.isPresent());
        assertEquals(passenger, result.get());
    }

    @Test
    void testFindById_NotFound() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        Optional<Passenger> result = passengerRepository.findById("P001");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("passengers")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Passenger.class)).thenReturn(passenger);

        java.util.List<Passenger> result = passengerRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindByPassportNo() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("passengers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("passportNo", "A12345678")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Passenger.class)).thenReturn(passenger);

        Optional<Passenger> result = passengerRepository.findByPassportNo("A12345678");

        assertTrue(result.isPresent());
        assertEquals("A12345678", result.get().getPassportNo());
    }

    @Test
    void testFindByEmail() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("passengers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Passenger.class)).thenReturn(passenger);

        Optional<Passenger> result = passengerRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void testExistsByPassportNo() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("passengers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("passportNo", "A12345678")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Passenger.class)).thenReturn(passenger);

        boolean exists = passengerRepository.existsByPassportNo("A12345678");

        assertTrue(exists);
    }

    @Test
    void testDeleteById() throws ExecutionException, InterruptedException {
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        passengerRepository.deleteById("P001");

        verify(documentReference).delete();
    }

    @Test
    void testCount() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);

        when(firestore.collection("passengers")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.size()).thenReturn(5);

        long count = passengerRepository.count();

        assertEquals(5, count);
    }

    @Test
    void testExistsById() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Passenger.class)).thenReturn(passenger);

        boolean exists = passengerRepository.existsById("P001");

        assertTrue(exists);
    }
}
