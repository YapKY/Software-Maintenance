package com.example.springboot;

import com.example.springboot.controller.FlightRestController;
import com.example.springboot.model.Flight;
import com.example.springboot.service.FlightService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test cases for FlightRestController
 * Tests all REST API endpoints for flight management
 */
@ExtendWith(MockitoExtension.class)
class FlightRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightService flightService;

    @Mock
    private Firestore firestore;

    @InjectMocks
    private FlightRestController flightRestController;

    private MockHttpSession session;
    private Map<String, Object> staffData;
    private Flight testFlight;
    private List<Flight> flightList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightRestController).build();

        // Create test session
        session = new MockHttpSession();
        staffData = new HashMap<>();
        staffData.put("staffId", "S001");
        staffData.put("name", "Apple Doe");
        staffData.put("position", "Manager");
        session.setAttribute("staff", staffData);

        // Create test flight
        testFlight = new Flight();
        testFlight.setDocumentId("doc123");
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        testFlight.setDepartureDate("15/12/2024");
        testFlight.setArrivalDate("15/12/2024");
        testFlight.setDepartureTime(1400);
        testFlight.setArrivalTime(2000);
        testFlight.setBoardingTime(1300);
        testFlight.setEconomyPrice(200.0);
        testFlight.setBusinessPrice(400.0);
        testFlight.setPlaneNo("PL01");
        testFlight.setTotalSeats(32);
        testFlight.setStatus("ACTIVE");

        flightList = Arrays.asList(testFlight);
    }

    // ==================== GET ALL FLIGHTS TESTS ====================

    @Test
    void testGetAllFlights_ShouldReturnFlightList() throws Exception {
        when(flightService.getAllFlights()).thenReturn(flightList);

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.flights[0].flightId").value("F001"));

        verify(flightService).getAllFlights();
    }

    @Test
    void testGetAllFlights_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(flightService.getAllFlights()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(0));
    }

    // ==================== GET FLIGHT BY ID TESTS ====================

    @Test
    void testGetFlightById_WithValidId_ShouldReturnFlight() throws Exception {
        when(flightService.getFlightById("doc123")).thenReturn(testFlight);

        mockMvc.perform(get("/api/flights/doc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.flight.flightId").value("F001"));

        verify(flightService).getFlightById("doc123");
    }

    @Test
    void testGetFlightById_WithInvalidId_ShouldReturn404() throws Exception {
        when(flightService.getFlightById("invalid"))
                .thenThrow(new IllegalArgumentException("Flight not found"));

        mockMvc.perform(get("/api/flights/invalid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== SEARCH FLIGHTS TESTS ====================

    @Test
    void testSearchFlights_WithValidCriteria_ShouldReturnResults() throws Exception {
        when(flightService.searchFlights("Malaysia", "Japan", "15/12/2024"))
                .thenReturn(flightList);

        String requestBody = "{\"departureCountry\":\"Malaysia\",\"arrivalCountry\":\"Japan\",\"departureDate\":\"15/12/2024\"}";

        mockMvc.perform(post("/api/flights/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));

        verify(flightService).searchFlights("Malaysia", "Japan", "15/12/2024");
    }

    // ==================== ADD FLIGHT TESTS ====================

    @Test
    void testAddFlight_WithValidData_ShouldReturnCreated() throws Exception {
        when(flightService.addFlight(any(Flight.class))).thenReturn(testFlight);

        String requestBody = """
            {
                "flightId": "F001",
                "departureCountry": "Malaysia",
                "arrivalCountry": "Japan",
                "departureDate": "15/12/2024",
                "arrivalDate": "15/12/2024",
                "departureTime": 1400,
                "arrivalTime": 2000,
                "boardingTime": 1300,
                "economyPrice": 200.0,
                "businessPrice": 400.0,
                "planeNo": "PL01",
                "totalSeats": 32
            }
            """;

        mockMvc.perform(post("/api/flights")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flight F001 added successfully!"));

        verify(flightService).addFlight(any(Flight.class));
    }

    @Test
    void testAddFlight_WithoutAuthentication_ShouldReturn401() throws Exception {
        String requestBody = "{\"flightId\":\"F001\"}";

        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAddFlight_WithInvalidData_ShouldReturn400() throws Exception {
        when(flightService.addFlight(any(Flight.class)))
                .thenThrow(new IllegalArgumentException("Invalid flight data"));

        String requestBody = "{\"flightId\":\"INVALID\"}";

        mockMvc.perform(post("/api/flights")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== UPDATE FLIGHT TESTS ====================

    @Test
    void testUpdateFlight_WithValidData_ShouldReturnSuccess() throws Exception {
        when(flightService.updateFlight(eq("doc123"), any(Flight.class)))
                .thenReturn(testFlight);

        String requestBody = """
            {
                "flightId": "F001",
                "departureCountry": "Malaysia",
                "arrivalCountry": "Singapore",
                "departureDate": "16/12/2024",
                "arrivalDate": "16/12/2024",
                "departureTime": 1500,
                "arrivalTime": 1700,
                "boardingTime": 1400,
                "economyPrice": 250.0,
                "businessPrice": 450.0,
                "planeNo": "PL01",
                "totalSeats": 32
            }
            """;

        mockMvc.perform(put("/api/flights/doc123")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(flightService).updateFlight(eq("doc123"), any(Flight.class));
    }

    @Test
    void testUpdateFlight_WithoutAuthentication_ShouldReturn401() throws Exception {
        String requestBody = "{\"flightId\":\"F001\"}";

        mockMvc.perform(put("/api/flights/doc123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    // ==================== DELETE FLIGHT TESTS ====================

    @Test
    void testDeleteFlight_AsManager_ShouldReturnSuccess() throws Exception {
        when(flightService.getFlightById("doc123")).thenReturn(testFlight);
        doNothing().when(flightService).deleteFlight("doc123");

        mockMvc.perform(delete("/api/flights/doc123").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flight F001 deactivated successfully (data preserved)"));

        verify(flightService).deleteFlight("doc123");
    }

    @Test
    void testDeleteFlight_AsController_ShouldReturn403() throws Exception {
        staffData.put("position", "Airline Controller");
        session.setAttribute("staff", staffData);

        mockMvc.perform(delete("/api/flights/doc123").session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        verify(flightService, never()).deleteFlight(anyString());
    }

    @Test
    void testDeleteFlight_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/flights/doc123"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET FLIGHT BY FLIGHT ID TESTS ====================

    @Test
    void testGetFlightByFlightId_WithValidId_ShouldReturnFlight() throws Exception {
        when(flightService.getFlightByFlightId("F001")).thenReturn(testFlight);

        mockMvc.perform(get("/api/flights/by-id/F001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.flight.flightId").value("F001"));

        verify(flightService).getFlightByFlightId("F001");
    }

    @Test
    void testGetFlightByFlightId_WithInvalidId_ShouldReturn404() throws Exception {
        when(flightService.getFlightByFlightId("F999"))
                .thenThrow(new IllegalArgumentException("Flight not found: F999"));

        mockMvc.perform(get("/api/flights/by-id/F999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== GET FLIGHT STATS TESTS ====================

    @Test
    void testGetFlightStats_ShouldReturnStatistics() throws Exception {
        when(flightService.getAllFlights()).thenReturn(flightList);

        mockMvc.perform(get("/api/flights/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.stats.totalFlights").value(1))
                .andExpect(jsonPath("$.stats.totalSeats").value(32));

        verify(flightService).getAllFlights();
    }

    // ==================== GET SEAT STATS TESTS ====================

    @Test
    void testGetSeatStats_WithValidFlightId_ShouldReturnStats() throws Exception {
        // Mock Firestore query
        QueryDocumentSnapshot mockSeat1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot mockSeat2 = mock(QueryDocumentSnapshot.class);
        when(mockSeat1.getString("statusSeat")).thenReturn("Booked");
        when(mockSeat2.getString("statusSeat")).thenReturn("Available");

        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList(mockSeat1, mockSeat2));

        ApiFuture<QuerySnapshot> mockFuture = mock(ApiFuture.class);
        when(mockFuture.get()).thenReturn(mockQuerySnapshot);

        com.google.cloud.firestore.Query mockQuery = mock(com.google.cloud.firestore.Query.class);
        when(mockQuery.get()).thenReturn(mockFuture);

        com.google.cloud.firestore.CollectionReference mockCollection = mock(com.google.cloud.firestore.CollectionReference.class);
        when(mockCollection.whereEqualTo("flightId", "F001")).thenReturn(mockQuery);

        when(firestore.collection("seats")).thenReturn(mockCollection);

        mockMvc.perform(get("/api/flights/F001/seats/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.flightId").value("F001"))
                .andExpect(jsonPath("$.totalSeats").value(2))
                .andExpect(jsonPath("$.ticketsSold").value(1))
                .andExpect(jsonPath("$.availableSeats").value(1));
    }
}