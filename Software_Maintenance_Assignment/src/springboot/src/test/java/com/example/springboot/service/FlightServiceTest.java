package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.example.springboot.repository.FirestoreRepository;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.api.core.ApiFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for FlightService
 * 
 * Tests Module: Customer Search Flight Module
 * Coverage: Search logic, filtering, data retrieval
 */
@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FirestoreRepository repository;

    @Mock
    private Query query;

    @Mock
    private ApiFuture<QuerySnapshot> futureSnapshot;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private QueryDocumentSnapshot documentSnapshot;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setDocumentId("doc123");
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        testFlight.setDepartureDate("11/11/2023");
        testFlight.setArrivalDate("12/11/2023");
        testFlight.setDepartureTime(1300);
        testFlight.setArrivalTime(2000);
        testFlight.setBoardingTime(1200);
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);
        testFlight.setPlaneNo("PL04");
        testFlight.setTotalSeats(32);
    }

    // ========== Search Flights Tests ==========

    @Test
    void testSearchFlights_Success() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        
        // Mock the query chain
        when(repository.getFirestore().collection("flights")
                .whereEqualTo("departureCountry", "Malaysia")).thenReturn(query);
        when(query.whereEqualTo("arrivalCountry", "Japan")).thenReturn(query);
        when(query.whereEqualTo("departureDate", "11/11/2023")).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot));
        when(documentSnapshot.toObject(Flight.class)).thenReturn(testFlight);
        when(documentSnapshot.getId()).thenReturn("doc123");

        // Act
        List<Flight> results = flightService.searchFlights("Malaysia", "Japan", "11/11/2023");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("F001", results.get(0).getFlightId());
        assertEquals("Malaysia", results.get(0).getDepartureCountry());
        assertEquals("Japan", results.get(0).getArrivalCountry());
    }

    @Test
    void testSearchFlights_NoResults() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights")
                .whereEqualTo(anyString(), any())).thenReturn(query);
        when(query.whereEqualTo(anyString(), any())).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

        // Act
        List<Flight> results = flightService.searchFlights("Malaysia", "Australia", "11/11/2023");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchFlights_ExecutionException() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights")
                .whereEqualTo(anyString(), any())).thenReturn(query);
        when(query.whereEqualTo(anyString(), any())).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenThrow(new ExecutionException("Database error", null));

        // Act & Assert
        assertThrows(ExecutionException.class, () -> {
            flightService.searchFlights("Malaysia", "Japan", "11/11/2023");
        });
    }

    // ========== Get All Flights Tests ==========

    @Test
    void testGetAllFlights_Success() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights").get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot));
        when(documentSnapshot.toObject(Flight.class)).thenReturn(testFlight);
        when(documentSnapshot.getId()).thenReturn("doc123");

        // Act
        List<Flight> results = flightService.getAllFlights();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("doc123", results.get(0).getDocumentId());
    }

    @Test
    void testGetAllFlights_Empty() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights").get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

        // Act
        List<Flight> results = flightService.getAllFlights();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // ========== Get Flight by ID Tests ==========

    @Test
    void testGetFlightById_Success() throws Exception {
        // Arrange
        when(repository.findById("flights", "doc123", Flight.class)).thenReturn(testFlight);

        // Act
        Flight result = flightService.getFlightById("doc123");

        // Assert
        assertNotNull(result);
        assertEquals("doc123", result.getDocumentId());
        assertEquals("F001", result.getFlightId());
        verify(repository).findById("flights", "doc123", Flight.class);
    }

    @Test
    void testGetFlightById_NotFound() throws Exception {
        // Arrange
        when(repository.findById("flights", "invalid", Flight.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.getFlightById("invalid");
        });
        assertTrue(exception.getMessage().contains("Flight not found"));
    }

    // ========== Get Flight by Flight ID Tests ==========

    @Test
    void testGetFlightByFlightId_Success() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights")
                .whereEqualTo("flightId", "F001")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot));
        when(documentSnapshot.toObject(Flight.class)).thenReturn(testFlight);
        when(documentSnapshot.getId()).thenReturn("doc123");

        // Act
        Flight result = flightService.getFlightByFlightId("F001");

        // Assert
        assertNotNull(result);
        assertEquals("F001", result.getFlightId());
        assertEquals("doc123", result.getDocumentId());
    }

    @Test
    void testGetFlightByFlightId_NotFound() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights")
                .whereEqualTo("flightId", "F999")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.getFlightByFlightId("F999");
        });
        assertTrue(exception.getMessage().contains("Flight not found with flightId"));
    }

    // ========== Edge Case Tests ==========

    @Test
    void testSearchFlights_NullParameters() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            flightService.searchFlights(null, null, null);
        });
    }

    @Test
    void testGetFlightById_NullId() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            flightService.getFlightById(null);
        });
    }

    @Test
    void testGetFlightByFlightId_EmptyString() throws Exception {
        // Arrange
        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights")
                .whereEqualTo("flightId", "")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            flightService.getFlightByFlightId("");
        });
    }

    @Test
    void testSearchFlights_MultipleResults() throws Exception {
        // Arrange
        Flight flight2 = new Flight();
        flight2.setDocumentId("doc456");
        flight2.setFlightId("F002");
        flight2.setDepartureCountry("Malaysia");
        flight2.setArrivalCountry("Japan");

        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
        when(doc2.toObject(Flight.class)).thenReturn(flight2);
        when(doc2.getId()).thenReturn("doc456");

        when(repository.getFirestore()).thenReturn(mock(com.google.cloud.firestore.Firestore.class));
        when(repository.getFirestore().collection("flights")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
        when(repository.getFirestore().collection("flights")
                .whereEqualTo(anyString(), any())).thenReturn(query);
        when(query.whereEqualTo(anyString(), any())).thenReturn(query);
        when(query.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot, doc2));

        // Act
        List<Flight> results = flightService.searchFlights("Malaysia", "Japan", "11/11/2023");

        // Assert
        assertEquals(2, results.size());
    }
}
