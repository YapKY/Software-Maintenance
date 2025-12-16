package com.example.springboot.service;

import com.example.springboot.model.Seat;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for SeatService
 * 
 * Tests Module: Seat Management Module
 * Coverage: Seat creation, deletion, retrieval, availability tracking
 * Target: 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Seat Service Tests")
class SeatServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private Query query;

    @Mock
    private ApiFuture<QuerySnapshot> queryFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeFuture;

    @Mock
    private ApiFuture<DocumentReference> docRefFuture;

    @InjectMocks
    private SeatService seatService;

    private String testFlightId;
    private int testTotalSeats;

    @BeforeEach
    void setUp() {
        testFlightId = "F001";
        testTotalSeats = 32;
    }

    // ==================== CREATE SEATS TESTS ====================

    @Test
    @DisplayName("Should create seats for flight successfully")
    void testCreateSeatsForFlight_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, testTotalSeats);

        // Assert
        verify(collectionReference, times(testTotalSeats)).add(anyMap());
    }

    @Test
    @DisplayName("Should create correct number of business class seats")
    void testCreateSeatsForFlight_BusinessClassCount() throws ExecutionException, InterruptedException {
        // Arrange
        ArgumentCaptor<Map<String, Object>> seatCaptor = ArgumentCaptor.forClass(Map.class);
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, testTotalSeats);

        // Assert
        verify(collectionReference, times(testTotalSeats)).add(seatCaptor.capture());
        
        List<Map<String, Object>> capturedSeats = seatCaptor.getAllValues();
        long businessSeats = capturedSeats.stream()
                .filter(seat -> "Business".equals(seat.get("typeOfSeat")))
                .count();
        
        assertEquals(4, businessSeats, "Should have 4 business class seats");
    }

    @Test
    @DisplayName("Should create correct number of economy class seats")
    void testCreateSeatsForFlight_EconomyClassCount() throws ExecutionException, InterruptedException {
        // Arrange
        ArgumentCaptor<Map<String, Object>> seatCaptor = ArgumentCaptor.forClass(Map.class);
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, testTotalSeats);

        // Assert
        verify(collectionReference, times(testTotalSeats)).add(seatCaptor.capture());
        
        List<Map<String, Object>> capturedSeats = seatCaptor.getAllValues();
        long economySeats = capturedSeats.stream()
                .filter(seat -> "Economy".equals(seat.get("typeOfSeat")))
                .count();
        
        assertEquals(28, economySeats, "Should have 28 economy class seats");
    }

    @Test
    @DisplayName("Should create seats with correct seat numbers")
    void testCreateSeatsForFlight_SeatNumbers() throws ExecutionException, InterruptedException {
        // Arrange
        ArgumentCaptor<Map<String, Object>> seatCaptor = ArgumentCaptor.forClass(Map.class);
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, testTotalSeats);

        // Assert
        verify(collectionReference, times(testTotalSeats)).add(seatCaptor.capture());
        
        List<Map<String, Object>> capturedSeats = seatCaptor.getAllValues();
        
        // Check first seat number
        assertEquals(100, capturedSeats.get(0).get("seatNumber"));
        
        // Check last seat number
        assertEquals(131, capturedSeats.get(testTotalSeats - 1).get("seatNumber"));
    }

    @Test
    @DisplayName("Should create seats with Available status")
    void testCreateSeatsForFlight_DefaultStatus() throws ExecutionException, InterruptedException {
        // Arrange
        ArgumentCaptor<Map<String, Object>> seatCaptor = ArgumentCaptor.forClass(Map.class);
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, testTotalSeats);

        // Assert
        verify(collectionReference, times(testTotalSeats)).add(seatCaptor.capture());
        
        List<Map<String, Object>> capturedSeats = seatCaptor.getAllValues();
        capturedSeats.forEach(seat -> 
            assertEquals("Available", seat.get("statusSeat"))
        );
    }

    @Test
    @DisplayName("Should associate seats with correct flight ID")
    void testCreateSeatsForFlight_FlightIdAssociation() throws ExecutionException, InterruptedException {
        // Arrange
        ArgumentCaptor<Map<String, Object>> seatCaptor = ArgumentCaptor.forClass(Map.class);
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, testTotalSeats);

        // Assert
        verify(collectionReference, times(testTotalSeats)).add(seatCaptor.capture());
        
        List<Map<String, Object>> capturedSeats = seatCaptor.getAllValues();
        capturedSeats.forEach(seat -> 
            assertEquals(testFlightId, seat.get("flightId"))
        );
    }

    @Test
    @DisplayName("Should create seats for different flight IDs")
    void testCreateSeatsForFlight_DifferentFlights() throws ExecutionException, InterruptedException {
        // Arrange
        String[] flightIds = {"F001", "F002", "F003"};
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        for (String flightId : flightIds) {
            seatService.createSeatsForFlight(flightId, 20);
        }

        // Assert
        verify(collectionReference, times(60)).add(anyMap()); // 3 flights * 20 seats
    }

    @Test
    @DisplayName("Should handle small number of seats")
    void testCreateSeatsForFlight_SmallCount() throws ExecutionException, InterruptedException {
        // Arrange
        int smallCount = 5;
        ArgumentCaptor<Map<String, Object>> seatCaptor = ArgumentCaptor.forClass(Map.class);
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, smallCount);

        // Assert
        verify(collectionReference, times(smallCount)).add(seatCaptor.capture());
        
        List<Map<String, Object>> capturedSeats = seatCaptor.getAllValues();
        // All should be business (less than 4)
        long businessSeats = capturedSeats.stream()
                .filter(seat -> "Business".equals(seat.get("typeOfSeat")))
                .count();
        
        assertEquals(4, businessSeats, "First 4 should be business");
    }

    @Test
    @DisplayName("Should handle large number of seats")
    void testCreateSeatsForFlight_LargeCount() throws ExecutionException, InterruptedException {
        // Arrange
        int largeCount = 100;
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight(testFlightId, largeCount);

        // Assert
        verify(collectionReference, times(largeCount)).add(anyMap());
    }

    @Test
    @DisplayName("Should throw exception when Firestore operation fails")
    void testCreateSeatsForFlight_FirestoreException() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenThrow(new ExecutionException(new Exception("Firestore error")));

        // Act & Assert
        assertThrows(ExecutionException.class, () -> 
            seatService.createSeatsForFlight(testFlightId, testTotalSeats)
        );
    }

    // ==================== DELETE SEATS TESTS ====================

    @Test
    @DisplayName("Should delete all seats for a flight")
    void testDeleteSeatsForFlight_Success() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createMockDocuments(10);
        setupDeleteMocks(mockDocuments);

        // Act
        seatService.deleteSeatsForFlight(testFlightId);

        // Assert
        verify(documentReference, times(10)).delete();
    }

    @Test
    @DisplayName("Should handle deletion when no seats exist")
    void testDeleteSeatsForFlight_NoSeats() throws ExecutionException, InterruptedException {
        // Arrange
        setupDeleteMocks(Collections.emptyList());

        // Act
        seatService.deleteSeatsForFlight(testFlightId);

        // Assert
        verify(documentReference, never()).delete();
    }

    @Test
    @DisplayName("Should delete seats for specific flight only")
    void testDeleteSeatsForFlight_SpecificFlight() throws ExecutionException, InterruptedException {
        // Arrange
        String specificFlightId = "F002";
        List<QueryDocumentSnapshot> mockDocuments = createMockDocuments(5);
        setupDeleteMocks(mockDocuments);

        // Act
        seatService.deleteSeatsForFlight(specificFlightId);

        // Assert
        verify(collectionReference).whereEqualTo("flightId", specificFlightId);
        verify(documentReference, times(5)).delete();
    }

    @Test
    @DisplayName("Should handle Firestore exception during deletion")
    void testDeleteSeatsForFlight_Exception() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", testFlightId)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenThrow(new ExecutionException(new Exception("Delete failed")));

        // Act & Assert
        assertThrows(ExecutionException.class, () -> 
            seatService.deleteSeatsForFlight(testFlightId)
        );
    }

    // ==================== GET SEATS TESTS ====================

    @Test
    @DisplayName("Should retrieve all seats for a flight")
    void testGetSeatsForFlight_Success() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createMockSeatDocuments(5);
        setupRetrieveMocks(mockDocuments);

        // Act
        List<Seat> seats = seatService.getSeatsForFlight(testFlightId);

        // Assert
        assertEquals(5, seats.size());
        seats.forEach(seat -> {
            assertNotNull(seat.getDocumentId());
            assertEquals(testFlightId, seat.getFlightId());
            assertNotNull(seat.getStatusSeat());
            assertNotNull(seat.getTypeOfSeat());
        });
    }

    @Test
    @DisplayName("Should return empty list when no seats found")
    void testGetSeatsForFlight_NoSeats() throws ExecutionException, InterruptedException {
        // Arrange
        setupRetrieveMocks(Collections.emptyList());

        // Act
        List<Seat> seats = seatService.getSeatsForFlight(testFlightId);

        // Assert
        assertTrue(seats.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve seats in correct order by seat number")
    void testGetSeatsForFlight_OrderedBySeatNumber() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createMockSeatDocuments(10);
        setupRetrieveMocks(mockDocuments);

        // Act
        seatService.getSeatsForFlight(testFlightId);

        // Assert
        verify(query).orderBy("seatNumber");
    }

    @Test
    @DisplayName("Should handle Firestore exception during retrieval")
    void testGetSeatsForFlight_Exception() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", testFlightId)).thenReturn(query);
        when(query.orderBy("seatNumber")).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenThrow(new ExecutionException(new Exception("Query failed")));

        // Act & Assert
        assertThrows(ExecutionException.class, () -> 
            seatService.getSeatsForFlight(testFlightId)
        );
    }

    // ==================== GET SEAT AVAILABILITY TESTS ====================

    @Test
    @DisplayName("Should calculate seat availability correctly")
    void testGetSeatAvailability_Success() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createMixedStatusSeats(10, 6); // 10 total, 6 available
        setupRetrieveMocks(mockDocuments);

        // Act
        Map<String, Integer> availability = seatService.getSeatAvailability(testFlightId);

        // Assert
        assertEquals(10, availability.get("total"));
        assertEquals(6, availability.get("available"));
        assertEquals(4, availability.get("booked"));
    }

    @Test
    @DisplayName("Should handle all seats available")
    void testGetSeatAvailability_AllAvailable() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createMixedStatusSeats(20, 20); // All available
        setupRetrieveMocks(mockDocuments);

        // Act
        Map<String, Integer> availability = seatService.getSeatAvailability(testFlightId);

        // Assert
        assertEquals(20, availability.get("total"));
        assertEquals(20, availability.get("available"));
        assertEquals(0, availability.get("booked"));
    }

    @Test
    @DisplayName("Should handle all seats booked")
    void testGetSeatAvailability_AllBooked() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createMixedStatusSeats(15, 0); // All booked
        setupRetrieveMocks(mockDocuments);

        // Act
        Map<String, Integer> availability = seatService.getSeatAvailability(testFlightId);

        // Assert
        assertEquals(15, availability.get("total"));
        assertEquals(0, availability.get("available"));
        assertEquals(15, availability.get("booked"));
    }

    @Test
    @DisplayName("Should handle no seats")
    void testGetSeatAvailability_NoSeats() throws ExecutionException, InterruptedException {
        // Arrange
        setupRetrieveMocks(Collections.emptyList());

        // Act
        Map<String, Integer> availability = seatService.getSeatAvailability(testFlightId);

        // Assert
        assertEquals(0, availability.get("total"));
        assertEquals(0, availability.get("available"));
        assertEquals(0, availability.get("booked"));
    }

    @Test
    @DisplayName("Should handle case insensitive status")
    void testGetSeatAvailability_CaseInsensitiveStatus() throws ExecutionException, InterruptedException {
        // Arrange
        List<QueryDocumentSnapshot> mockDocuments = createCaseInsensitiveStatusSeats();
        setupRetrieveMocks(mockDocuments);

        // Act
        Map<String, Integer> availability = seatService.getSeatAvailability(testFlightId);

        // Assert - Should count "available", "Available", "AVAILABLE" as available
        assertTrue(availability.get("available") > 0);
    }

    // ==================== HELPER METHODS ====================

    private List<QueryDocumentSnapshot> createMockDocuments(int count) {
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
            when(doc.getReference()).thenReturn(documentReference);
            documents.add(doc);
        }
        return documents;
    }

    private List<QueryDocumentSnapshot> createMockSeatDocuments(int count) {
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
            when(doc.getId()).thenReturn("seat_" + i);
            when(doc.getString("flightId")).thenReturn(testFlightId);
            when(doc.getLong("seatNumber")).thenReturn((long) (100 + i));
            when(doc.getString("statusSeat")).thenReturn("Available");
            when(doc.getString("typeOfSeat")).thenReturn(i < 4 ? "Business" : "Economy");
            documents.add(doc);
        }
        return documents;
    }

    private List<QueryDocumentSnapshot> createMixedStatusSeats(int total, int available) {
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
            when(doc.getId()).thenReturn("seat_" + i);
            when(doc.getString("flightId")).thenReturn(testFlightId);
            when(doc.getLong("seatNumber")).thenReturn((long) (100 + i));
            when(doc.getString("statusSeat")).thenReturn(i < available ? "Available" : "Booked");
            when(doc.getString("typeOfSeat")).thenReturn(i < 4 ? "Business" : "Economy");
            documents.add(doc);
        }
        return documents;
    }

    private List<QueryDocumentSnapshot> createCaseInsensitiveStatusSeats() {
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        String[] statuses = {"Available", "available", "AVAILABLE", "Booked", "booked", "BOOKED"};
        
        for (int i = 0; i < statuses.length; i++) {
            QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
            when(doc.getId()).thenReturn("seat_" + i);
            when(doc.getString("flightId")).thenReturn(testFlightId);
            when(doc.getLong("seatNumber")).thenReturn((long) (100 + i));
            when(doc.getString("statusSeat")).thenReturn(statuses[i]);
            when(doc.getString("typeOfSeat")).thenReturn("Economy");
            documents.add(doc);
        }
        return documents;
    }

    private void setupDeleteMocks(List<QueryDocumentSnapshot> documents) 
            throws ExecutionException, InterruptedException {
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo(eq("flightId"), anyString())).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(documents);
        
        for (QueryDocumentSnapshot doc : documents) {
            when(doc.getReference().delete()).thenReturn(writeFuture);
            when(writeFuture.get()).thenReturn(null);
        }
    }

    private void setupRetrieveMocks(List<QueryDocumentSnapshot> documents) 
            throws ExecutionException, InterruptedException {
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("flightId", testFlightId)).thenReturn(query);
        when(query.orderBy("seatNumber")).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(documents);
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle null flight ID in create")
    void testCreateSeatsForFlight_NullFlightId() {
        // Act & Assert - May throw NullPointerException or handle gracefully
        assertThrows(Exception.class, () -> 
            seatService.createSeatsForFlight(null, testTotalSeats)
        );
    }

    @Test
    @DisplayName("Should handle empty flight ID in create")
    void testCreateSeatsForFlight_EmptyFlightId() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestore.collection("seats")).thenReturn(collectionReference);
        when(collectionReference.add(anyMap())).thenReturn(docRefFuture);
        when(docRefFuture.get()).thenReturn(documentReference);

        // Act
        seatService.createSeatsForFlight("", testTotalSeats);

        // Assert - Should still create seats but with empty flightId
        verify(collectionReference, times(testTotalSeats)).add(anyMap());
    }

    // @Test
    // @DisplayName("Should handle zero seats")
    // void testCreateSeatsForFlight_ZeroSeats() throws ExecutionException, InterruptedException {
    //     // Arrange
    //     when(firestore.collection("seats")).thenReturn(collectionReference);

    //     // Act
    //     seatService.createSeatsForFlight(testFlightId, 0);

    //     // Assert
    //     verify(collectionReference, never()).add(anyMap());
    // }

    // @Test
    // @DisplayName("Should handle negative seat count")
    // void testCreateSeatsForFlight_NegativeSeats() throws ExecutionException, InterruptedException {
    //     // Arrange
    //     when(firestore.collection("seats")).thenReturn(collectionReference);

    //     // Act
    //     seatService.createSeatsForFlight(testFlightId, -5);

    //     // Assert
    //     verify(collectionReference, never()).add(anyMap());
    // }
}