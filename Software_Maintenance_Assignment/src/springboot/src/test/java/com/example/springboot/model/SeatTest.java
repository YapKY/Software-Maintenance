package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Seat model
 * 
 * Coverage: Getters, Setters, Constructors, Lombok methods
 * Target: >90% coverage
 */
@DisplayName("Seat Model Tests")
class SeatTest {

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat();
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Should create Seat with no-args constructor")
    void testNoArgsConstructor() {
        // Act
        Seat newSeat = new Seat();

        // Assert
        assertNotNull(newSeat);
        assertNull(newSeat.getDocumentId());
        assertEquals(0, newSeat.getSeatNumber());
        assertNull(newSeat.getTypeOfSeat());
        assertNull(newSeat.getStatusSeat());
        assertNull(newSeat.getFlightId());
    }

    // ==================== GETTER/SETTER TESTS ====================

    @Test
    @DisplayName("Should set and get documentId")
    void testSetGetDocumentId() {
        // Arrange
        String documentId = "seat-doc-123";

        // Act
        seat.setDocumentId(documentId);

        // Assert
        assertEquals(documentId, seat.getDocumentId());
    }

    @Test
    @DisplayName("Should set and get seatNumber")
    void testSetGetSeatNumber() {
        // Arrange
        int seatNumber = 101;

        // Act
        seat.setSeatNumber(seatNumber);

        // Assert
        assertEquals(seatNumber, seat.getSeatNumber());
    }

    @Test
    @DisplayName("Should set and get typeOfSeat")
    void testSetGetTypeOfSeat() {
        // Arrange
        String type = "Business";

        // Act
        seat.setTypeOfSeat(type);

        // Assert
        assertEquals(type, seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should set and get statusSeat")
    void testSetGetStatusSeat() {
        // Arrange
        String status = "Booked";

        // Act
        seat.setStatusSeat(status);

        // Assert
        assertEquals(status, seat.getStatusSeat());
    }

    @Test
    @DisplayName("Should set and get flightId")
    void testSetGetFlightId() {
        // Arrange
        String flightId = "F001";

        // Act
        seat.setFlightId(flightId);

        // Assert
        assertEquals(flightId, seat.getFlightId());
    }

    // ==================== COMPLETE SEAT OBJECT TEST ====================

    @Test
    @DisplayName("Should create complete Economy seat")
    void testCompleteEconomySeat() {
        // Arrange & Act
        seat.setDocumentId("seat-economy-1");
        seat.setSeatNumber(201);
        seat.setTypeOfSeat("Economy");
        seat.setStatusSeat("Available");
        seat.setFlightId("F001");

        // Assert
        assertEquals("seat-economy-1", seat.getDocumentId());
        assertEquals(201, seat.getSeatNumber());
        assertEquals("Economy", seat.getTypeOfSeat());
        assertEquals("Available", seat.getStatusSeat());
        assertEquals("F001", seat.getFlightId());
    }

    @Test
    @DisplayName("Should create complete Business seat")
    void testCompleteBusinessSeat() {
        // Arrange & Act
        seat.setDocumentId("seat-business-1");
        seat.setSeatNumber(101);
        seat.setTypeOfSeat("Business");
        seat.setStatusSeat("Booked");
        seat.setFlightId("F002");

        // Assert
        assertEquals("seat-business-1", seat.getDocumentId());
        assertEquals(101, seat.getSeatNumber());
        assertEquals("Business", seat.getTypeOfSeat());
        assertEquals("Booked", seat.getStatusSeat());
        assertEquals("F002", seat.getFlightId());
    }

    // ==================== EQUALS AND HASHCODE TESTS ====================

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void testEquals_SameFields() {
        // Arrange
        Seat seat1 = new Seat();
        seat1.setDocumentId("seat-1");
        seat1.setSeatNumber(101);
        seat1.setTypeOfSeat("Economy");
        seat1.setStatusSeat("Available");
        seat1.setFlightId("F001");

        Seat seat2 = new Seat();
        seat2.setDocumentId("seat-1");
        seat2.setSeatNumber(101);
        seat2.setTypeOfSeat("Economy");
        seat2.setStatusSeat("Available");
        seat2.setFlightId("F001");

        // Act & Assert
        assertEquals(seat1, seat2);
        assertEquals(seat1.hashCode(), seat2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields differ")
    void testEquals_DifferentFields() {
        // Arrange
        Seat seat1 = new Seat();
        seat1.setDocumentId("seat-1");
        seat1.setSeatNumber(101);

        Seat seat2 = new Seat();
        seat2.setDocumentId("seat-2");
        seat2.setSeatNumber(102);

        // Act & Assert
        assertNotEquals(seat1, seat2);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEquals_SameObject() {
        // Arrange
        seat.setDocumentId("seat-1");

        // Act & Assert
        assertEquals(seat, seat);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEquals_Null() {
        // Arrange
        seat.setDocumentId("seat-1");

        // Act & Assert
        assertNotEquals(seat, null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void testEquals_DifferentClass() {
        // Arrange
        seat.setDocumentId("seat-1");
        String differentObject = "Not a seat";

        // Act & Assert
        assertNotEquals(seat, differentObject);
    }

    // ==================== TOSTRING TEST ====================

    @Test
    @DisplayName("toString should contain key information")
    void testToString() {
        // Arrange
        seat.setDocumentId("seat-123");
        seat.setSeatNumber(105);
        seat.setTypeOfSeat("Business");
        seat.setStatusSeat("Available");
        seat.setFlightId("F003");

        // Act
        String result = seat.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("seat-123"));
        assertTrue(result.contains("105"));
        assertTrue(result.contains("Business"));
        assertTrue(result.contains("Available"));
        assertTrue(result.contains("F003"));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle zero seat number")
    void testZeroSeatNumber() {
        // Arrange & Act
        seat.setSeatNumber(0);

        // Assert
        assertEquals(0, seat.getSeatNumber());
    }

    @Test
    @DisplayName("Should handle negative seat number")
    void testNegativeSeatNumber() {
        // Arrange & Act
        seat.setSeatNumber(-1);

        // Assert
        assertEquals(-1, seat.getSeatNumber());
    }

    @Test
    @DisplayName("Should handle large seat number")
    void testLargeSeatNumber() {
        // Arrange & Act
        seat.setSeatNumber(9999);

        // Assert
        assertEquals(9999, seat.getSeatNumber());
    }

    @Test
    @DisplayName("Should handle null typeOfSeat")
    void testNullTypeOfSeat() {
        // Arrange & Act
        seat.setTypeOfSeat(null);

        // Assert
        assertNull(seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should handle empty typeOfSeat")
    void testEmptyTypeOfSeat() {
        // Arrange & Act
        seat.setTypeOfSeat("");

        // Assert
        assertEquals("", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should handle null statusSeat")
    void testNullStatusSeat() {
        // Arrange & Act
        seat.setStatusSeat(null);

        // Assert
        assertNull(seat.getStatusSeat());
    }

    @Test
    @DisplayName("Should handle empty statusSeat")
    void testEmptyStatusSeat() {
        // Arrange & Act
        seat.setStatusSeat("");

        // Assert
        assertEquals("", seat.getStatusSeat());
    }

    @Test
    @DisplayName("Should handle null flightId")
    void testNullFlightId() {
        // Arrange & Act
        seat.setFlightId(null);

        // Assert
        assertNull(seat.getFlightId());
    }

    @Test
    @DisplayName("Should handle empty flightId")
    void testEmptyFlightId() {
        // Arrange & Act
        seat.setFlightId("");

        // Assert
        assertEquals("", seat.getFlightId());
    }

    // ==================== SEAT TYPE TESTS ====================

    @Test
    @DisplayName("Should handle Economy seat type")
    void testEconomySeatType() {
        // Arrange & Act
        seat.setTypeOfSeat("Economy");

        // Assert
        assertEquals("Economy", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should handle Business seat type")
    void testBusinessSeatType() {
        // Arrange & Act
        seat.setTypeOfSeat("Business");

        // Assert
        assertEquals("Business", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should handle Premium Economy seat type")
    void testPremiumEconomySeatType() {
        // Arrange & Act
        seat.setTypeOfSeat("Premium Economy");

        // Assert
        assertEquals("Premium Economy", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should handle First Class seat type")
    void testFirstClassSeatType() {
        // Arrange & Act
        seat.setTypeOfSeat("First Class");

        // Assert
        assertEquals("First Class", seat.getTypeOfSeat());
    }

    // ==================== SEAT STATUS TESTS ====================

    @Test
    @DisplayName("Should handle Available status")
    void testAvailableStatus() {
        // Arrange & Act
        seat.setStatusSeat("Available");

        // Assert
        assertEquals("Available", seat.getStatusSeat());
    }

    @Test
    @DisplayName("Should handle Booked status")
    void testBookedStatus() {
        // Arrange & Act
        seat.setStatusSeat("Booked");

        // Assert
        assertEquals("Booked", seat.getStatusSeat());
    }

    @Test
    @DisplayName("Should handle Empty status")
    void testEmptyStatus() {
        // Arrange & Act
        seat.setStatusSeat("Empty");

        // Assert
        assertEquals("Empty", seat.getStatusSeat());
    }

    @Test
    @DisplayName("Should handle Reserved status")
    void testReservedStatus() {
        // Arrange & Act
        seat.setStatusSeat("Reserved");

        // Assert
        assertEquals("Reserved", seat.getStatusSeat());
    }

    // ==================== SCENARIO TESTS ====================

    @Test
    @DisplayName("Should represent available economy seat")
    void testAvailableEconomySeat() {
        // Arrange & Act
        seat.setDocumentId("seat-eco-available");
        seat.setSeatNumber(301);
        seat.setTypeOfSeat("Economy");
        seat.setStatusSeat("Available");
        seat.setFlightId("F100");

        // Assert
        assertEquals("Available", seat.getStatusSeat());
        assertEquals("Economy", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should represent booked business seat")
    void testBookedBusinessSeat() {
        // Arrange & Act
        seat.setDocumentId("seat-bus-booked");
        seat.setSeatNumber(101);
        seat.setTypeOfSeat("Business");
        seat.setStatusSeat("Booked");
        seat.setFlightId("F200");

        // Assert
        assertEquals("Booked", seat.getStatusSeat());
        assertEquals("Business", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should change seat status from Available to Booked")
    void testSeatStatusChange() {
        // Arrange
        seat.setSeatNumber(150);
        seat.setTypeOfSeat("Economy");
        seat.setStatusSeat("Available");
        seat.setFlightId("F300");

        // Assert initial state
        assertEquals("Available", seat.getStatusSeat());

        // Act - Book the seat
        seat.setStatusSeat("Booked");

        // Assert final state
        assertEquals("Booked", seat.getStatusSeat());
        assertEquals(150, seat.getSeatNumber());
        assertEquals("Economy", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should represent seat with all null fields except seat number")
    void testMinimalSeat() {
        // Arrange & Act
        seat.setSeatNumber(999);

        // Assert
        assertNull(seat.getDocumentId());
        assertEquals(999, seat.getSeatNumber());
        assertNull(seat.getTypeOfSeat());
        assertNull(seat.getStatusSeat());
        assertNull(seat.getFlightId());
    }

    @Test
    @DisplayName("Should handle seat number ranges")
    void testSeatNumberRanges() {
        // Business class (1-100)
        seat.setSeatNumber(50);
        seat.setTypeOfSeat("Business");
        assertEquals(50, seat.getSeatNumber());

        // Economy class (101-999)
        seat.setSeatNumber(250);
        seat.setTypeOfSeat("Economy");
        assertEquals(250, seat.getSeatNumber());
    }

    @Test
    @DisplayName("Should handle multiple seat number updates")
    void testMultipleSeatNumberUpdates() {
        // Initial
        seat.setSeatNumber(100);
        assertEquals(100, seat.getSeatNumber());

        // Update 1
        seat.setSeatNumber(200);
        assertEquals(200, seat.getSeatNumber());

        // Update 2
        seat.setSeatNumber(300);
        assertEquals(300, seat.getSeatNumber());
    }

    @Test
    @DisplayName("Should handle case-sensitive seat types")
    void testCaseSensitiveSeatTypes() {
        seat.setTypeOfSeat("economy");
        assertEquals("economy", seat.getTypeOfSeat());

        seat.setTypeOfSeat("ECONOMY");
        assertEquals("ECONOMY", seat.getTypeOfSeat());

        seat.setTypeOfSeat("Economy");
        assertEquals("Economy", seat.getTypeOfSeat());
    }

    @Test
    @DisplayName("Should handle case-sensitive seat status")
    void testCaseSensitiveSeatStatus() {
        seat.setStatusSeat("available");
        assertEquals("available", seat.getStatusSeat());

        seat.setStatusSeat("AVAILABLE");
        assertEquals("AVAILABLE", seat.getStatusSeat());

        seat.setStatusSeat("Available");
        assertEquals("Available", seat.getStatusSeat());
    }
}