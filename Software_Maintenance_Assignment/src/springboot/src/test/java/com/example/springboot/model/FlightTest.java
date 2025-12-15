package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Flight model
 * * Tests Module: Flight Model
 * Coverage: Getters/Setters, validation, constructors
 */
class FlightTest {

    @Test
    void testFlightGettersAndSetters() {
        // Arrange & Act
        Flight flight = new Flight();
        flight.setDocumentId("doc123");
        flight.setFlightId("F001");
        flight.setDepartureCountry("Malaysia");
        flight.setArrivalCountry("Japan");
        flight.setDepartureDate("11/11/2023");
        flight.setArrivalDate("12/11/2023");
        flight.setDepartureTime(1300);
        flight.setArrivalTime(2000);
        flight.setBoardingTime(1200);
        flight.setEconomyPrice(200.00);
        flight.setBusinessPrice(400.00);
        flight.setPlaneNo("PL04");
        flight.setTotalSeats(32);
        flight.setStatus("ACTIVE");

        // Assert
        assertEquals("doc123", flight.getDocumentId());
        assertEquals("F001", flight.getFlightId());
        assertEquals("Malaysia", flight.getDepartureCountry());
        assertEquals("Japan", flight.getArrivalCountry());
        assertEquals("11/11/2023", flight.getDepartureDate());
        assertEquals("12/11/2023", flight.getArrivalDate());
        assertEquals(1300, flight.getDepartureTime());
        assertEquals(2000, flight.getArrivalTime());
        assertEquals(1200, flight.getBoardingTime());
        assertEquals(200.00, flight.getEconomyPrice());
        assertEquals(400.00, flight.getBusinessPrice());
        assertEquals("PL04", flight.getPlaneNo());
        assertEquals(32, flight.getTotalSeats());
        assertEquals("ACTIVE", flight.getStatus());
    }

    @Test
    void testFlightDefaultConstructor() {
        // Act
        Flight flight = new Flight();

        // Assert
        assertNull(flight.getDocumentId());
        assertNull(flight.getFlightId());
        assertEquals(0, flight.getDepartureTime());
        assertEquals(0.0, flight.getEconomyPrice());
        assertNull(flight.getStatus());
    }

    @Test
    void testFlightPriceValidation() {
        // Arrange
        Flight flight = new Flight();
        flight.setEconomyPrice(200.00);
        flight.setBusinessPrice(400.00);

        // Assert
        assertTrue(flight.getBusinessPrice() > flight.getEconomyPrice());
    }

    @Test
    void testFlightTimeValidation() {
        // Arrange
        Flight flight = new Flight();
        flight.setBoardingTime(1200);
        flight.setDepartureTime(1300);
        flight.setArrivalTime(2000);

        // Assert
        assertTrue(flight.getDepartureTime() > flight.getBoardingTime());
        assertTrue(flight.getArrivalTime() > flight.getDepartureTime());
    }

    @Test
    void testFlightStatus() {
        // Arrange & Act
        Flight flight = new Flight();
        flight.setStatus("ACTIVE");

        // Assert
        assertEquals("ACTIVE", flight.getStatus());
    }

    @Test
    void testFlightWithAllFields() {
        // Arrange & Act
        Flight flight = new Flight();
        flight.setFlightId("F002");
        flight.setDepartureCountry("Singapore");
        flight.setArrivalCountry("Thailand");
        flight.setDepartureDate("15/12/2024");
        flight.setArrivalDate("15/12/2024");
        flight.setDepartureTime(1500);
        flight.setArrivalTime(1800);
        flight.setBoardingTime(1400);
        flight.setEconomyPrice(150.00);
        flight.setBusinessPrice(300.00);
        flight.setPlaneNo("PL05");
        flight.setTotalSeats(28);
        flight.setStatus("ACTIVE");

        // Assert
        assertNotNull(flight.getFlightId());
        assertNotNull(flight.getDepartureCountry());
        assertNotNull(flight.getArrivalCountry());
    }

    @Test
    void testFlightNullValues() {
        // Arrange & Act
        Flight flight = new Flight();
        flight.setFlightId(null);
        flight.setDepartureCountry(null);
        flight.setStatus(null);

        // Assert
        assertNull(flight.getFlightId());
        assertNull(flight.getDepartureCountry());
        assertNull(flight.getStatus());
    }

    @Test
    void testFlightZeroValues() {
        // Arrange & Act
        Flight flight = new Flight();
        flight.setDepartureTime(0);
        flight.setArrivalTime(0);
        flight.setEconomyPrice(0.0);
        flight.setTotalSeats(0);

        // Assert
        assertEquals(0, flight.getDepartureTime());
        assertEquals(0, flight.getArrivalTime());
        assertEquals(0.0, flight.getEconomyPrice());
        assertEquals(0, flight.getTotalSeats());
    }
}