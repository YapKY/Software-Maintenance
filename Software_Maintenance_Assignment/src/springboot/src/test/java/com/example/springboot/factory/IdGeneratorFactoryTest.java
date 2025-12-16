package com.example.springboot.factory;

import com.example.springboot.factory.IdGeneratorFactory.IdGenerator;
import com.example.springboot.factory.IdGeneratorFactory.EntityType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdGeneratorFactoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private ApiFuture<QuerySnapshot> apiFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @InjectMocks
    private IdGeneratorFactory idGeneratorFactory;

    // Helper method to setup the Firestore mock chain
    private void setupFirestoreMock(String collectionName, int currentSize) throws ExecutionException, InterruptedException {
        // When collection(name) is called, return mock collection
        doReturn(collectionReference).when(firestore).collection(collectionName);
        
        // When get() is called on collection, return mock future
        when(collectionReference.get()).thenReturn(apiFuture);
        
        // When get() is called on future, return mock snapshot
        when(apiFuture.get()).thenReturn(querySnapshot);
        
        // When size() is called, return specific count
        when(querySnapshot.size()).thenReturn(currentSize);
    }

    @Test
    void testGetGenerator_Customer() throws ExecutionException, InterruptedException {
        // Arrange: 10 existing customers, so next ID should be C000011
        setupFirestoreMock("customers", 10);

        // Act
        IdGenerator generator = idGeneratorFactory.getGenerator(EntityType.CUSTOMER);
        String id = generator.generateId();

        // Assert
        assertNotNull(generator);
        assertEquals("C000011", id); // 6 digits padding
        verify(firestore).collection("customers");
    }

    @Test
    void testGetGenerator_Staff() throws ExecutionException, InterruptedException {
        // Arrange: 0 existing staff, so next ID should be S0001
        setupFirestoreMock("staff", 0);

        // Act
        IdGenerator generator = idGeneratorFactory.getGenerator(EntityType.STAFF);
        String id = generator.generateId();

        // Assert
        assertNotNull(generator);
        assertEquals("S0001", id); // 4 digits padding
        verify(firestore).collection("staff");
    }

    @Test
    void testGetGenerator_Passenger() throws ExecutionException, InterruptedException {
        // Arrange: 99 existing passengers, so next ID should be P000100
        setupFirestoreMock("passengers", 99);

        // Act
        IdGenerator generator = idGeneratorFactory.getGenerator(EntityType.PASSENGER);
        String id = generator.generateId();

        // Assert
        assertNotNull(generator);
        assertEquals("P000100", id); // 6 digits padding
        verify(firestore).collection("passengers");
    }

    @Test
    void testExecutionExceptionPropagation() throws ExecutionException, InterruptedException {
        // Arrange: Simulate Firestore failure
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new ExecutionException("Firestore error", new Throwable()));

        // Act & Assert
        IdGenerator generator = idGeneratorFactory.getGenerator(EntityType.CUSTOMER);
        assertThrows(ExecutionException.class, generator::generateId);
    }
}