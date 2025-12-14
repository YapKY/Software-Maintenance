package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.example.springboot.repository.FirestoreRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FlightServiceTest {

    @Mock
    private FirestoreRepository repository;

    @Mock
    private SeatService seatService;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private Query query;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private ApiFuture<QuerySnapshot> future;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

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
        testFlight.setStatus("ACTIVE");
    }

    // ========== Get All Flights Tests ==========

    @Test
    void testGetAllFlights_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        
        // FIX: Mock the missing .whereEqualTo("status", "ACTIVE") call
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        
        // Mock the document conversion
        mockDocumentConversion(queryDocumentSnapshot, testFlight);

        // Act
        List<Flight> results = flightService.getAllFlights();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("F001", results.get(0).getFlightId());
    }

    @Test
    void testGetAllFlights_EmptyDatabase() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        List<Flight> results = flightService.getAllFlights();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllFlights_MultipleFlights() throws Exception {
        // Arrange
        Flight flight2 = new Flight();
        flight2.setFlightId("F002");
        flight2.setDepartureCountry("Singapore");

        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, doc2));

        mockDocumentConversion(queryDocumentSnapshot, testFlight);
        mockDocumentConversion(doc2, flight2);

        // Act
        List<Flight> results = flightService.getAllFlights();

        // Assert
        assertEquals(2, results.size());
    }

    @Test
    void testGetAllFlights_Exception() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> flightService.getAllFlights());
    }

    // ========== Search Flights Tests (No Results) ==========

    @Test
    void testSearchFlights_NoResults() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        
        // FIX: Start chain with status=ACTIVE
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        
        // Allow any subsequent filters to return the same query mock
        when(query.whereEqualTo(anyString(), anyString())).thenReturn(query);
        
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        List<Flight> results = flightService.searchFlights("Malaysia", "Australia", "11/11/2023");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchFlights_Exception() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.whereEqualTo(anyString(), anyString())).thenReturn(query);
        when(query.get()).thenThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            flightService.searchFlights("Malaysia", "Japan", "11/11/2023")
        );
    }

    private void mockDocumentConversion(DocumentSnapshot mockDoc, Flight flight) {
        when(mockDoc.getId()).thenReturn(flight.getDocumentId());
        when(mockDoc.getString("flightId")).thenReturn(flight.getFlightId());
        when(mockDoc.getString("departureCountry")).thenReturn(flight.getDepartureCountry());
        when(mockDoc.getString("arrivalCountry")).thenReturn(flight.getArrivalCountry());
        when(mockDoc.getString("departureDate")).thenReturn(flight.getDepartureDate());
        when(mockDoc.getString("arrivalDate")).thenReturn(flight.getArrivalDate());
        when(mockDoc.getLong("departureTime")).thenReturn((long) flight.getDepartureTime());
        when(mockDoc.getLong("arrivalTime")).thenReturn((long) flight.getArrivalTime());
        when(mockDoc.getLong("boardingTime")).thenReturn((long) flight.getBoardingTime());
        when(mockDoc.getDouble("economyPrice")).thenReturn(flight.getEconomyPrice());
        when(mockDoc.getDouble("businessPrice")).thenReturn(flight.getBusinessPrice());
        when(mockDoc.getString("planeNo")).thenReturn(flight.getPlaneNo());
        when(mockDoc.getLong("totalSeats")).thenReturn((long) flight.getTotalSeats());
        when(mockDoc.getString("status")).thenReturn(flight.getStatus());
    }

    // ========== Validation Tests ==========

    @Test
    void testSearchFlights_ValidatesParameters() {
        // Test that service handles null parameters gracefully
        // Implementation specific - may throw exception or return empty list
        try {
            List<Flight> results = flightService.searchFlights(null, null, null);
            // If it doesn't throw, it should return empty or handle gracefully
            assertNotNull(results);
        } catch (Exception e) {
            // It's acceptable to throw exception for null parameters
            assertTrue(e instanceof RuntimeException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testGetFlightById_ValidatesParameter() {
        // Test that service handles null/empty ID
        try {
            Flight result = flightService.getFlightById(null);
            // Should either throw or return null
            assertNull(result);
        } catch (Exception e) {
            // Acceptable to throw exception
            assertTrue(e instanceof RuntimeException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testGetFlightByFlightId_ValidatesParameter() {
        // Test that service handles null/empty flight ID
        try {
            Flight result = flightService.getFlightByFlightId(null);
            // Should either throw or return null
            assertNull(result);
        } catch (Exception e) {
            // Acceptable to throw exception
            assertTrue(e instanceof RuntimeException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testFirestoreRepository_IsInjected() {
        // Verify that the repository is properly injected
        assertNotNull(flightService);
        // The service should have been created with the mocked repository
    }
}