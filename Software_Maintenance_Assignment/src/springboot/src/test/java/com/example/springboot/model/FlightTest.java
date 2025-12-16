package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlightTest {

    @Test
    void testCustomConstructor() {
        // This constructor initializes status to "ACTIVE" and doesn't take documentId
        Flight flight = new Flight(
            "F001", "Malaysia", "Japan", 
            "2023-01-01", "2023-01-02", 
            1000, 1800, 900, 
            100.0, 500.0, "B777", 200
        );

        assertEquals("F001", flight.getFlightId());
        assertEquals("ACTIVE", flight.getStatus());
        assertEquals("Malaysia", flight.getDepartureCountry());
        assertEquals(200, flight.getTotalSeats());
        assertNull(flight.getDocumentId()); // Not set in custom constructor
    }

    @Test
    void testLombokAllArgsConstructor() {
        // Lombok generates a constructor for ALL fields, including status and documentId
        Flight flight = new Flight(
            "doc1", "F002", "SG", "TH", 
            "date1", "date2", 
            1000, 1200, 900, 
            50.0, 150.0, "A320", 100, "INACTIVE"
        );

        assertEquals("doc1", flight.getDocumentId());
        assertEquals("INACTIVE", flight.getStatus());
    }

    @Test
    void testStatusManagement() {
        Flight flight = new Flight();
        flight.setStatus("INACTIVE"); // Explicitly set to inactive first
        
        assertFalse(flight.isActive());
        
        flight.activate();
        assertTrue(flight.isActive());
        assertEquals("ACTIVE", flight.getStatus());

        flight.deactivate();
        assertFalse(flight.isActive());
        assertEquals("INACTIVE", flight.getStatus());
    }

    @Test
    void testSettersAndNoArgsConstructor() {
        Flight flight = new Flight();
        flight.setFlightId("F999");
        flight.setEconomyPrice(99.99);
        
        assertEquals("F999", flight.getFlightId());
        assertEquals(99.99, flight.getEconomyPrice());
        // Default status initialization in field definition
        assertEquals("ACTIVE", flight.getStatus()); 
    }

    @Test
    void testEqualsHashCodeToString() {
        Flight f1 = new Flight();
        f1.setFlightId("F1");
        Flight f2 = new Flight();
        f2.setFlightId("F1");
        
        assertEquals(f1, f2);
        assertEquals(f1.hashCode(), f2.hashCode());
        assertTrue(f1.toString().contains("F1"));
    }
}