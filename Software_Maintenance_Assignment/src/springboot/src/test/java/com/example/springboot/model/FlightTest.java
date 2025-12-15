package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Flight model
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
}