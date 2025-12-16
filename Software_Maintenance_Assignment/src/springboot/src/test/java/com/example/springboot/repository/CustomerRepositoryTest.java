package com.example.springboot.repository;

import com.example.springboot.generator.CustomerIdGenerator;
import com.example.springboot.model.Customer;
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
class CustomerRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CustomerIdGenerator idGenerator;

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
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    void testSave_NewCustomer_GeneratesId() throws ExecutionException, InterruptedException {
        customer.setCustId(null);
        when(idGenerator.generateId()).thenReturn("C001");
        when(documentReference.set(any(Customer.class), any(SetOptions.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        Customer savedCustomer = customerRepository.save(customer);

        assertEquals("C001", savedCustomer.getCustId());
        verify(idGenerator).generateId();
        verify(documentReference).set(customer, SetOptions.merge());
    }

    @Test
    void testSave_ExistingCustomer_UsesId() throws ExecutionException, InterruptedException {
        customer.setCustId("C002");
        when(documentReference.set(any(Customer.class), any(SetOptions.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        Customer savedCustomer = customerRepository.save(customer);

        assertEquals("C002", savedCustomer.getCustId());
        verify(idGenerator, never()).generateId();
        verify(documentReference).set(customer, SetOptions.merge());
    }

    @Test
    void testFindById_Found() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Customer.class)).thenReturn(customer);
        when(documentSnapshot.getId()).thenReturn("C001");

        Optional<Customer> result = customerRepository.findById("C001");

        assertTrue(result.isPresent());
        assertEquals("C001", result.get().getCustId());
    }

    @Test
    void testFindById_NotFound() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        Optional<Customer> result = customerRepository.findById("C001");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);
        when(queryDocumentSnapshot.getId()).thenReturn("C001");

        java.util.List<Customer> result = customerRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("C001", result.get(0).getCustId());
    }

    @Test
    void testFindByCustIcNo() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("custIcNo", "123456-78-9012")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);
        when(queryDocumentSnapshot.getId()).thenReturn("C001");

        Optional<Customer> result = customerRepository.findByCustIcNo("123456-78-9012");

        assertTrue(result.isPresent());
        assertEquals("C001", result.get().getCustId());
    }

    @Test
    void testFindByEmail() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);
        when(queryDocumentSnapshot.getId()).thenReturn("C001");

        Optional<Customer> result = customerRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("C001", result.get().getCustId());
    }

    @Test
    void testFindByPhoneNumber() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("phoneNumber", "0123456789")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);
        when(queryDocumentSnapshot.getId()).thenReturn("C001");

        Optional<Customer> result = customerRepository.findByPhoneNumber("0123456789");

        assertTrue(result.isPresent());
        assertEquals("C001", result.get().getCustId());
    }

    @Test
    void testExistsByCustIcNo() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("custIcNo", "123456-78-9012")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);

        boolean exists = customerRepository.existsByCustIcNo("123456-78-9012");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("email", "test@example.com")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);

        boolean exists = customerRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByPhoneNumber() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("phoneNumber", "0123456789")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(Customer.class)).thenReturn(customer);

        boolean exists = customerRepository.existsByPhoneNumber("0123456789");

        assertTrue(exists);
    }

    @Test
    void testExistsByCustPassword() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("custPassword", "password123")).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(java.util.Collections.singletonList(queryDocumentSnapshot));

        boolean exists = customerRepository.existsByCustPassword("password123");

        assertTrue(exists);
    }

    @Test
    void testDeleteById() throws ExecutionException, InterruptedException {
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        customerRepository.deleteById("C001");

        verify(documentReference).delete();
    }

    @Test
    void testCount() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshotFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);

        when(firestore.collection("customers")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.size()).thenReturn(5);

        long count = customerRepository.count();

        assertEquals(5, count);
    }

    @Test
    void testExistsById() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Customer.class)).thenReturn(customer);

        boolean exists = customerRepository.existsById("C001");

        assertTrue(exists);
    }
}
