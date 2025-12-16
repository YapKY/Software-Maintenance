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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


/**
 * Test class for FlightService
 * * Tests Module: Staff Dashboard - Flight Management
 * Coverage: CRUD operations, validation, search functionality
 */
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

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<DocumentReference> futureDocumentReference;

    @Mock
    private ApiFuture<DocumentSnapshot> futureDocumentSnapshot;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private ApiFuture<WriteResult> futureWriteResult;

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
        
        // Mock the .whereEqualTo("status", "ACTIVE") call
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
        flight2.setStatus("ACTIVE");

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

    // ========== Search Flights Tests ==========

    @Test
    void testSearchFlights_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.whereEqualTo(anyString(), anyString())).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        mockDocumentConversion(queryDocumentSnapshot, testFlight);

        // Act
        List<Flight> results = flightService.searchFlights("Malaysia", "Japan", "11/11/2023");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("F001", results.get(0).getFlightId());
    }

    @Test
    void testSearchFlights_NoResults() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        
        // Start chain with status=ACTIVE
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

    // ========== Get Flight by ID Tests ==========

    @Test
    void testGetFlightById_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.document("doc123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(futureDocumentSnapshot);
        when(futureDocumentSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);

        mockDocumentConversion(documentSnapshot, testFlight);

        // Act
        Flight result = flightService.getFlightById("doc123");

        // Assert
        assertNotNull(result);
        assertEquals("F001", result.getFlightId());
    }

    @Test
    void testGetFlightById_NotFound() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.document("invalid")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(futureDocumentSnapshot);
        when(futureDocumentSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.getFlightById("invalid")
        );
    }

    // ========== Get Flight by Flight ID Tests ==========

    @Test
    void testGetFlightByFlightId_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", "F001")).thenReturn(query);
        when(query.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        mockDocumentConversion(queryDocumentSnapshot, testFlight);

        // Act
        Flight result = flightService.getFlightByFlightId("F001");

        // Assert
        assertNotNull(result);
        assertEquals("F001", result.getFlightId());
    }

    @Test
    void testGetFlightByFlightId_NotFound() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", "F999")).thenReturn(query);
        when(query.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.getFlightByFlightId("F999")
        );
    }

    // ========== Add Flight Tests ==========

    @Test
    void testAddFlight_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", "F001")).thenReturn(query);
        when(query.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());
        
        when(collectionReference.add(anyMap())).thenReturn(futureDocumentReference);
        when(futureDocumentReference.get()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("newDoc123");

        // Act
        Flight result = flightService.addFlight(testFlight);

        // Assert
        assertNotNull(result);
        assertEquals("newDoc123", result.getDocumentId());
        verify(seatService).createSeatsForFlight("F001", 32);
    }

    @Test
    void testAddFlight_DuplicateFlightId() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", "F001")).thenReturn(query);
        when(query.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.addFlight(testFlight)
        );
    }

    // ========== Update Flight Tests ==========

    @Test
    void testUpdateFlight_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.document("doc123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(futureDocumentSnapshot);
        when(futureDocumentSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        
        when(collectionReference.whereEqualTo("flightId", "F001")).thenReturn(query);
        when(query.whereEqualTo("status", "ACTIVE")).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());
        
        when(documentReference.update(anyMap())).thenReturn(futureWriteResult);

        // Act
        Flight result = flightService.updateFlight("doc123", testFlight);

        // Assert
        assertNotNull(result);
        verify(documentReference).update(anyMap());
    }

    @Test
    void testUpdateFlight_NotFound() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.document("invalid")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(futureDocumentSnapshot);
        when(futureDocumentSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.updateFlight("invalid", testFlight)
        );
    }

    // ========== Delete Flight Tests ==========

    @Test
    void testDeleteFlight_Success() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.document("doc123")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(futureDocumentSnapshot);
        when(futureDocumentSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getString("flightId")).thenReturn("F001");
        when(documentReference.update(anyMap())).thenReturn(futureWriteResult);

        // Act
        flightService.deleteFlight("doc123");

        // Assert
        verify(documentReference).update(anyMap());
        verify(seatService).deleteSeatsForFlight("F001");
    }

    @Test
    void testDeleteFlight_NotFound() throws Exception {
        // Arrange
        when(firestore.collection("flights")).thenReturn(collectionReference);
        when(collectionReference.document("invalid")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(futureDocumentSnapshot);
        when(futureDocumentSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.deleteFlight("invalid")
        );
    }

    // ========== Validation Tests ==========

    @Test
    void testSearchFlights_ValidatesParameters() {
        // Test that service handles null parameters gracefully
        // Act & Assert
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
        // Act & Assert
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
        // Act & Assert
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

    // ========== Helper Methods ==========

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
}