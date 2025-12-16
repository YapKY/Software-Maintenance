package com.example.springboot.controller;

import com.example.springboot.model.Flight;
import com.example.springboot.service.FlightService;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for FlightRestController
 * 
 * Tests Module: Staff Dashboard - Flight Management REST API
 * Coverage: CRUD endpoints, authentication, authorization, validation
 */
@ExtendWith(MockitoExtension.class)
class FlightRestControllerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightRestController flightController;

    private Flight testFlight;
    private List<Flight> testFlights;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        // Create test flight data
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

        testFlights = Arrays.asList(testFlight);

        // Create mock session with staff data
        mockSession = new MockHttpSession();
        Map<String, Object> staffData = new HashMap<>();
        staffData.put("staffId", "S001");
        staffData.put("name", "Test Staff");
        staffData.put("position", "Manager");
        mockSession.setAttribute("staff", staffData);
    }

    // ========== Get All Flights Tests ==========

    @Test
    void testGetAllFlights_Success() throws Exception {
        // Arrange
        when(flightService.getAllFlights()).thenReturn(testFlights);

        // Act
        ResponseEntity<?> response = flightController.getAllFlights();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Flight> flights = (List<Flight>) body.get("flights");
        assertEquals(1, flights.size());

        verify(flightService).getAllFlights();
    }

    @Test
    void testGetAllFlights_EmptyList() throws Exception {
        // Arrange
        when(flightService.getAllFlights()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<?> response = flightController.getAllFlights();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Flight> flights = (List<Flight>) body.get("flights");
        assertTrue(flights.isEmpty());
    }

    @Test
    void testGetAllFlights_Exception() throws Exception {
        // Arrange
        when(flightService.getAllFlights())
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = flightController.getAllFlights();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
    }

    // ========== Search Flights Tests ==========

    @Test
    void testSearchFlights_Success() throws Exception {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("departureCountry", "Malaysia");
        searchParams.put("arrivalCountry", "Japan");
        searchParams.put("departureDate", "11/11/2023");

        when(flightService.searchFlights("Malaysia", "Japan", "11/11/2023"))
                .thenReturn(testFlights);

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Flight> flights = (List<Flight>) body.get("flights");
        assertEquals(1, flights.size());
        assertEquals("F001", flights.get(0).getFlightId());

        verify(flightService).searchFlights("Malaysia", "Japan", "11/11/2023");
    }

    @Test
    void testSearchFlights_NoResults() throws Exception {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("departureCountry", "Malaysia");
        searchParams.put("arrivalCountry", "Australia");
        searchParams.put("departureDate", "11/11/2023");

        when(flightService.searchFlights(anyString(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
    }

    @Test
    void testSearchFlights_MissingDepartureCountry() throws Exception {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("arrivalCountry", "Japan");
        searchParams.put("departureDate", "11/11/2023");

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("Missing required fields"));
        
        verify(flightService, never()).searchFlights(anyString(), anyString(), anyString());
    }

    @Test
    void testSearchFlights_MissingArrivalCountry() throws Exception {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("departureCountry", "Malaysia");
        searchParams.put("departureDate", "11/11/2023");

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(flightService, never()).searchFlights(anyString(), anyString(), anyString());
    }

    @Test
    void testSearchFlights_MissingDepartureDate() throws Exception {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("departureCountry", "Malaysia");
        searchParams.put("arrivalCountry", "Japan");

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(flightService, never()).searchFlights(anyString(), anyString(), anyString());
    }

    @Test
    void testSearchFlights_ServiceException() throws Exception {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("departureCountry", "Malaysia");
        searchParams.put("arrivalCountry", "Japan");
        searchParams.put("departureDate", "11/11/2023");

        when(flightService.searchFlights(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("Search failed"));
    }

    // ========== Get Flight by ID Tests ==========

    @Test
    void testGetFlightById_Success() throws Exception {
        // Arrange
        when(flightService.getFlightById("doc123")).thenReturn(testFlight);

        // Act
        ResponseEntity<?> response = flightController.getFlightById("doc123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        
        Flight flight = (Flight) body.get("flight");
        assertEquals("F001", flight.getFlightId());

        verify(flightService).getFlightById("doc123");
    }

    @Test
    void testGetFlightById_NotFound() throws Exception {
        // Arrange
        when(flightService.getFlightById("invalid"))
                .thenThrow(new IllegalArgumentException("Flight not found with ID: invalid"));

        // Act
        ResponseEntity<?> response = flightController.getFlightById("invalid");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("Flight not found"));
    }

    // ========== Get Flight by Flight ID Tests ==========

    @Test
    void testGetFlightByFlightId_Success() throws Exception {
        // Arrange
        when(flightService.getFlightByFlightId("F001")).thenReturn(testFlight);

        // Act
        ResponseEntity<?> response = flightController.getFlightByFlightId("F001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        
        Flight flight = (Flight) body.get("flight");
        assertEquals("F001", flight.getFlightId());

        verify(flightService).getFlightByFlightId("F001");
    }

    @Test
    void testGetFlightByFlightId_NotFound() throws Exception {
        // Arrange
        when(flightService.getFlightByFlightId("F999"))
                .thenThrow(new IllegalArgumentException("Flight not found with flightId: F999"));

        // Act
        ResponseEntity<?> response = flightController.getFlightByFlightId("F999");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
    }

    // // ========== Add Flight Tests ==========

// ==================== ADD FLIGHT ====================

    @Test
    void testAddFlight_Success() throws Exception {
        when(flightService.addFlight(any(Flight.class))).thenReturn(testFlight);

        ResponseEntity<?> response = flightController.addFlight(testFlight);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue((Boolean) ((Map<?, ?>) response.getBody()).get("success"));
    }

    @Test
    void testAddFlight_ValidationError() throws Exception {
        when(flightService.addFlight(any(Flight.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = flightController.addFlight(testFlight);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testAddFlight_ServerException() throws Exception {
        when(flightService.addFlight(any(Flight.class))).thenThrow(new ExecutionException(new RuntimeException("Firestore fail")));

        ResponseEntity<?> response = flightController.addFlight(testFlight);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ==================== UPDATE FLIGHT ====================

    @Test
    void testUpdateFlight_Success() throws Exception {
        when(flightService.updateFlight(eq("doc123"), any(Flight.class))).thenReturn(testFlight);

        ResponseEntity<?> response = flightController.updateFlight("doc123", testFlight);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) ((Map<?, ?>) response.getBody()).get("success"));
    }

    @Test
    void testUpdateFlight_ValidationError() throws Exception {
        when(flightService.updateFlight(anyString(), any(Flight.class))).thenThrow(new IllegalArgumentException("Invalid ID"));

        ResponseEntity<?> response = flightController.updateFlight("doc123", testFlight);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateFlight_ServerException() throws Exception {
        when(flightService.updateFlight(anyString(), any(Flight.class))).thenThrow(new ExecutionException(new RuntimeException("Fail")));

        ResponseEntity<?> response = flightController.updateFlight("doc123", testFlight);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ==================== DELETE FLIGHT ====================

    @Test
    void testDeleteFlight_Success() throws Exception {
        when(flightService.getFlightById("doc123")).thenReturn(testFlight);
        doNothing().when(flightService).deleteFlight("doc123");

        ResponseEntity<?> response = flightController.deleteFlight("doc123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) ((Map<?, ?>) response.getBody()).get("success"));
    }

    @Test
    void testDeleteFlight_NotFound() throws Exception {
        when(flightService.getFlightById("invalid")).thenThrow(new IllegalArgumentException("Not found"));

        ResponseEntity<?> response = flightController.deleteFlight("invalid");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteFlight_ServerException() throws Exception {
        when(flightService.getFlightById("doc123")).thenReturn(testFlight);
        doThrow(new ExecutionException(new RuntimeException("Fail"))).when(flightService).deleteFlight("doc123");

        ResponseEntity<?> response = flightController.deleteFlight("doc123");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ========== Get Statistics Tests ==========

    @Test
    void testGetFlightStats_Success() throws Exception {
        // Arrange
        when(flightService.getAllFlights()).thenReturn(testFlights);

        // Act
        ResponseEntity<?> response = flightController.getFlightStats();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) body.get("stats");
        assertNotNull(stats);
    }

    @Test
    void testGetFlightStats_EmptyDatabase() throws Exception {
        // Arrange
        when(flightService.getAllFlights()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<?> response = flightController.getFlightStats();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
    }

    // ========== Edge Case Tests ==========

    @Test
    void testSearchFlights_NullSearchParams() {
        // Act & Assert
        try {
            ResponseEntity<?> response = flightController.searchFlights(null);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testSearchFlights_EmptySearchParams() {
        // Arrange
        Map<String, String> emptyParams = new HashMap<>();

        // Act
        ResponseEntity<?> response = flightController.searchFlights(emptyParams);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSearchFlights_MultipleResults() throws Exception {
        // Arrange
        Flight flight2 = new Flight();
        flight2.setFlightId("F002");
        flight2.setDepartureCountry("Malaysia");
        flight2.setArrivalCountry("Japan");
        flight2.setDepartureDate("11/11/2023");

        List<Flight> multipleFlights = Arrays.asList(testFlight, flight2);

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("departureCountry", "Malaysia");
        searchParams.put("arrivalCountry", "Japan");
        searchParams.put("departureDate", "11/11/2023");

        when(flightService.searchFlights("Malaysia", "Japan", "11/11/2023"))
                .thenReturn(multipleFlights);

        // Act
        ResponseEntity<?> response = flightController.searchFlights(searchParams);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(2, body.get("count"));
    }
}