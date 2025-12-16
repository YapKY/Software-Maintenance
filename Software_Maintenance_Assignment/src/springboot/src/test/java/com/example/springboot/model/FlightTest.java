package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Flight model
 * 
 * Coverage: Getters, Setters, Constructors, Business Logic
 * Target: >90% coverage
 */
@DisplayName("Flight Model Tests")
class FlightTest {

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = new Flight();
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Should create Flight with no-args constructor")
    void testNoArgsConstructor() {
        // Act
        Flight newFlight = new Flight();

        // Assert
        assertNotNull(newFlight);
        assertNull(newFlight.getFlightId());
        assertNull(newFlight.getDocumentId());
    }

    @Test
    @DisplayName("Should create Flight with all-args constructor (Lombok)")
    void testAllArgsConstructor() {
        // Act
        Flight newFlight = new Flight(
            "doc-123",
            "F001",
            "Malaysia",
            "Japan",
            "11/11/2023",
            "12/11/2023",
            1300,
            2000,
            1200,
            200.00,
            400.00,
            "PL04",
            32,
            "ACTIVE"
        );

        // Assert
        assertEquals("doc-123", newFlight.getDocumentId());
        assertEquals("F001", newFlight.getFlightId());
        assertEquals("Malaysia", newFlight.getDepartureCountry());
        assertEquals("Japan", newFlight.getArrivalCountry());
        assertEquals("11/11/2023", newFlight.getDepartureDate());
        assertEquals("12/11/2023", newFlight.getArrivalDate());
        assertEquals(1300, newFlight.getDepartureTime());
        assertEquals(2000, newFlight.getArrivalTime());
        assertEquals(1200, newFlight.getBoardingTime());
        assertEquals(200.00, newFlight.getEconomyPrice());
        assertEquals(400.00, newFlight.getBusinessPrice());
        assertEquals("PL04", newFlight.getPlaneNo());
        assertEquals(32, newFlight.getTotalSeats());
        assertEquals("ACTIVE", newFlight.getStatus());
    }

    @Test
    @DisplayName("Should create Flight with custom constructor (without documentId)")
    void testCustomConstructor() {
        // Act
        Flight newFlight = new Flight(
            "F002",
            "Singapore",
            "Thailand",
            "15/12/2023",
            "15/12/2023",
            900,
            1200,
            830,
            150.00,
            300.00,
            "PL05",
            48
        );

        // Assert
        assertNull(newFlight.getDocumentId()); // Not set by this constructor
        assertEquals("F002", newFlight.getFlightId());
        assertEquals("Singapore", newFlight.getDepartureCountry());
        assertEquals("Thailand", newFlight.getArrivalCountry());
        assertEquals("15/12/2023", newFlight.getDepartureDate());
        assertEquals("15/12/2023", newFlight.getArrivalDate());
        assertEquals(900, newFlight.getDepartureTime());
        assertEquals(1200, newFlight.getArrivalTime());
        assertEquals(830, newFlight.getBoardingTime());
        assertEquals(150.00, newFlight.getEconomyPrice());
        assertEquals(300.00, newFlight.getBusinessPrice());
        assertEquals("PL05", newFlight.getPlaneNo());
        assertEquals(48, newFlight.getTotalSeats());
        assertEquals("ACTIVE", newFlight.getStatus()); // Default value
    }

    @Test
    @DisplayName("Custom constructor should default status to ACTIVE")
    void testCustomConstructor_DefaultStatus() {
        // Act
        Flight newFlight = new Flight(
            "F003", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32
        );

        // Assert
        assertEquals("ACTIVE", newFlight.getStatus());
        assertTrue(newFlight.isActive());
    }

    // ==================== GETTER/SETTER TESTS ====================

    @Test
    @DisplayName("Should set and get documentId")
    void testSetGetDocumentId() {
        // Arrange
        String documentId = "doc-456";

        // Act
        flight.setDocumentId(documentId);

        // Assert
        assertEquals(documentId, flight.getDocumentId());
    }

    @Test
    @DisplayName("Should set and get flightId")
    void testSetGetFlightId() {
        // Arrange
        String flightId = "F100";

        // Act
        flight.setFlightId(flightId);

        // Assert
        assertEquals(flightId, flight.getFlightId());
    }

    @Test
    @DisplayName("Should set and get departureCountry")
    void testSetGetDepartureCountry() {
        // Arrange
        String country = "Indonesia";

        // Act
        flight.setDepartureCountry(country);

        // Assert
        assertEquals(country, flight.getDepartureCountry());
    }

    @Test
    @DisplayName("Should set and get arrivalCountry")
    void testSetGetArrivalCountry() {
        // Arrange
        String country = "Vietnam";

        // Act
        flight.setArrivalCountry(country);

        // Assert
        assertEquals(country, flight.getArrivalCountry());
    }

    @Test
    @DisplayName("Should set and get departureDate")
    void testSetGetDepartureDate() {
        // Arrange
        String date = "20/12/2023";

        // Act
        flight.setDepartureDate(date);

        // Assert
        assertEquals(date, flight.getDepartureDate());
    }

    @Test
    @DisplayName("Should set and get arrivalDate")
    void testSetGetArrivalDate() {
        // Arrange
        String date = "21/12/2023";

        // Act
        flight.setArrivalDate(date);

        // Assert
        assertEquals(date, flight.getArrivalDate());
    }

    @Test
    @DisplayName("Should set and get departureTime")
    void testSetGetDepartureTime() {
        // Arrange
        int time = 1430; // 2:30 PM

        // Act
        flight.setDepartureTime(time);

        // Assert
        assertEquals(time, flight.getDepartureTime());
    }

    @Test
    @DisplayName("Should set and get arrivalTime")
    void testSetGetArrivalTime() {
        // Arrange
        int time = 1830; // 6:30 PM

        // Act
        flight.setArrivalTime(time);

        // Assert
        assertEquals(time, flight.getArrivalTime());
    }

    @Test
    @DisplayName("Should set and get boardingTime")
    void testSetGetBoardingTime() {
        // Arrange
        int time = 1400; // 2:00 PM

        // Act
        flight.setBoardingTime(time);

        // Assert
        assertEquals(time, flight.getBoardingTime());
    }

    @Test
    @DisplayName("Should set and get economyPrice")
    void testSetGetEconomyPrice() {
        // Arrange
        double price = 250.50;

        // Act
        flight.setEconomyPrice(price);

        // Assert
        assertEquals(price, flight.getEconomyPrice());
    }

    @Test
    @DisplayName("Should set and get businessPrice")
    void testSetGetBusinessPrice() {
        // Arrange
        double price = 500.75;

        // Act
        flight.setBusinessPrice(price);

        // Assert
        assertEquals(price, flight.getBusinessPrice());
    }

    @Test
    @DisplayName("Should set and get planeNo")
    void testSetGetPlaneNo() {
        // Arrange
        String planeNo = "PL99";

        // Act
        flight.setPlaneNo(planeNo);

        // Assert
        assertEquals(planeNo, flight.getPlaneNo());
    }

    @Test
    @DisplayName("Should set and get totalSeats")
    void testSetGetTotalSeats() {
        // Arrange
        int seats = 64;

        // Act
        flight.setTotalSeats(seats);

        // Assert
        assertEquals(seats, flight.getTotalSeats());
    }

    @Test
    @DisplayName("Should set and get status")
    void testSetGetStatus() {
        // Arrange
        String status = "INACTIVE";

        // Act
        flight.setStatus(status);

        // Assert
        assertEquals(status, flight.getStatus());
    }

    // ==================== BUSINESS LOGIC TESTS ====================

    @Test
    @DisplayName("isActive should return true when status is ACTIVE")
    void testIsActive_WhenActive() {
        // Arrange
        flight.setStatus("ACTIVE");

        // Act & Assert
        assertTrue(flight.isActive());
    }

    @Test
    @DisplayName("isActive should return false when status is INACTIVE")
    void testIsActive_WhenInactive() {
        // Arrange
        flight.setStatus("INACTIVE");

        // Act & Assert
        assertFalse(flight.isActive());
    }

    @Test
    @DisplayName("isActive should be case-insensitive")
    void testIsActive_CaseInsensitive() {
        // Test various cases
        flight.setStatus("active");
        assertTrue(flight.isActive());

        flight.setStatus("Active");
        assertTrue(flight.isActive());

        flight.setStatus("ACTIVE");
        assertTrue(flight.isActive());

        flight.setStatus("inactive");
        assertFalse(flight.isActive());

        flight.setStatus("InAcTiVe");
        assertFalse(flight.isActive());
    }

    @Test
    @DisplayName("isActive should return false for null status")
    void testIsActive_NullStatus() {
        // Arrange
        flight.setStatus(null);

        // Act & Assert
        assertFalse(flight.isActive());
    }

    @Test
    @DisplayName("isActive should return false for empty status")
    void testIsActive_EmptyStatus() {
        // Arrange
        flight.setStatus("");

        // Act & Assert
        assertFalse(flight.isActive());
    }

    @Test
    @DisplayName("deactivate should set status to INACTIVE")
    void testDeactivate() {
        // Arrange
        flight.setStatus("ACTIVE");

        // Act
        flight.deactivate();

        // Assert
        assertEquals("INACTIVE", flight.getStatus());
        assertFalse(flight.isActive());
    }

    @Test
    @DisplayName("activate should set status to ACTIVE")
    void testActivate() {
        // Arrange
        flight.setStatus("INACTIVE");

        // Act
        flight.activate();

        // Assert
        assertEquals("ACTIVE", flight.getStatus());
        assertTrue(flight.isActive());
    }

    @Test
    @DisplayName("Should toggle status between active and inactive")
    void testToggleStatus() {
        // Start as active
        flight.setStatus("ACTIVE");
        assertTrue(flight.isActive());

        // Deactivate
        flight.deactivate();
        assertFalse(flight.isActive());
        assertEquals("INACTIVE", flight.getStatus());

        // Activate again
        flight.activate();
        assertTrue(flight.isActive());
        assertEquals("ACTIVE", flight.getStatus());
    }

    // ==================== EQUALS AND HASHCODE TESTS ====================

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void testEquals_SameFields() {
        // Arrange
        Flight flight1 = new Flight("F001", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32);

        Flight flight2 = new Flight("F001", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32);

        // Act & Assert
        assertEquals(flight1, flight2);
        assertEquals(flight1.hashCode(), flight2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields differ")
    void testEquals_DifferentFields() {
        // Arrange
        Flight flight1 = new Flight("F001", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32);

        Flight flight2 = new Flight("F002", "Singapore", "Thailand",
            "15/12/2023", "15/12/2023", 900, 1200, 830,
            150.00, 300.00, "PL05", 48);

        // Act & Assert
        assertNotEquals(flight1, flight2);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEquals_SameObject() {
        // Arrange
        Flight flight1 = new Flight("F001", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32);

        // Act & Assert
        assertEquals(flight1, flight1);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEquals_Null() {
        // Arrange
        Flight flight1 = new Flight("F001", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32);

        // Act & Assert
        assertNotEquals(flight1, null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void testEquals_DifferentClass() {
        // Arrange
        Flight flight1 = new Flight("F001", "Malaysia", "Japan",
            "11/11/2023", "12/11/2023", 1300, 2000, 1200,
            200.00, 400.00, "PL04", 32);

        String differentObject = "Not a flight";

        // Act & Assert
        assertNotEquals(flight1, differentObject);
    }

    // ==================== TOSTRING TEST ====================

    @Test
    @DisplayName("toString should contain key information")
    void testToString() {
        // Arrange
        flight.setFlightId("F001");
        flight.setDepartureCountry("Malaysia");
        flight.setArrivalCountry("Japan");
        flight.setStatus("ACTIVE");

        // Act
        String result = flight.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("F001"));
        assertTrue(result.contains("Malaysia"));
        assertTrue(result.contains("Japan"));
        assertTrue(result.contains("ACTIVE"));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle zero prices")
    void testZeroPrices() {
        // Arrange & Act
        flight.setEconomyPrice(0.0);
        flight.setBusinessPrice(0.0);

        // Assert
        assertEquals(0.0, flight.getEconomyPrice());
        assertEquals(0.0, flight.getBusinessPrice());
    }

    @Test
    @DisplayName("Should handle negative prices")
    void testNegativePrices() {
        // Arrange & Act
        flight.setEconomyPrice(-100.0);
        flight.setBusinessPrice(-200.0);

        // Assert
        assertEquals(-100.0, flight.getEconomyPrice());
        assertEquals(-200.0, flight.getBusinessPrice());
    }

    @Test
    @DisplayName("Should handle zero seats")
    void testZeroSeats() {
        // Arrange & Act
        flight.setTotalSeats(0);

        // Assert
        assertEquals(0, flight.getTotalSeats());
    }

    @Test
    @DisplayName("Should handle large seat numbers")
    void testLargeSeats() {
        // Arrange & Act
        flight.setTotalSeats(500);

        // Assert
        assertEquals(500, flight.getTotalSeats());
    }

    @Test
    @DisplayName("Should handle 24-hour time format")
    void testTimeFormats() {
        // Arrange & Act
        flight.setDepartureTime(0); // Midnight
        flight.setArrivalTime(2359); // 11:59 PM
        flight.setBoardingTime(1200); // Noon

        // Assert
        assertEquals(0, flight.getDepartureTime());
        assertEquals(2359, flight.getArrivalTime());
        assertEquals(1200, flight.getBoardingTime());
    }

    @Test
    @DisplayName("Should handle null string fields")
    void testNullStringFields() {
        // Arrange & Act
        flight.setFlightId(null);
        flight.setDepartureCountry(null);
        flight.setArrivalCountry(null);
        flight.setDepartureDate(null);
        flight.setArrivalDate(null);
        flight.setPlaneNo(null);
        flight.setStatus(null);

        // Assert
        assertNull(flight.getFlightId());
        assertNull(flight.getDepartureCountry());
        assertNull(flight.getArrivalCountry());
        assertNull(flight.getDepartureDate());
        assertNull(flight.getArrivalDate());
        assertNull(flight.getPlaneNo());
        assertNull(flight.getStatus());
    }
}