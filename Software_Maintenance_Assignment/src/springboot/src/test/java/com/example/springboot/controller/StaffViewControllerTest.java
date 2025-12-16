package com.example.springboot.controller;

import com.example.springboot.model.Flight;
import com.example.springboot.model.Staff;
import com.example.springboot.service.FlightService;
import com.example.springboot.service.PdfReportService;
import com.example.springboot.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StaffViewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightService flightService;

    @Mock
    private PdfReportService pdfReportService;

    @Mock
    private StaffService staffService;

        @InjectMocks
    private StaffViewController staffViewController;

    private Staff testStaff;
    private Map<String, Object> staffSessionData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(staffViewController).build();

        testStaff = new Staff();
        testStaff.setStaffId("S001");
        testStaff.setName("Test Staff");
        testStaff.setEmail("test@example.com");
        testStaff.setStfPass("12345");
        testStaff.setPosition("Manager");
        testStaff.setPhoneNumber("0123456789");
        testStaff.setGender("Male");

        staffSessionData = new HashMap<>();
        staffSessionData.put("staffId", "S001");
        staffSessionData.put("name", "Test Staff");
        staffSessionData.put("position", "Manager");
    }

    @Test
    @DisplayName("Should show login page when not logged in")
    void testShowLoginPage_NotLoggedIn() throws Exception {
        mockMvc.perform(get("/staff/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff"));
    }

    @Test
    @DisplayName("Should redirect to dashboard when already logged in")
    void testShowLoginPage_LoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("staff", staffSessionData);

        mockMvc.perform(get("/staff/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    @DisplayName("Should handle login success")
    void testHandleLogin_Success() throws Exception {
        when(staffService.authenticate("S001", 12345)).thenReturn(testStaff);

        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    @DisplayName("Should handle login failure - invalid credentials")
    void testHandleLogin_Failure() throws Exception {
        when(staffService.authenticate("S001", 12345)).thenReturn(null);

        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attribute("error", "Invalid Staff ID or Password"));
    }

    @Test
    @DisplayName("Should handle login failure - invalid password format")
    void testHandleLogin_InvalidPasswordFormat() throws Exception {
        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "notanumber"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attribute("error", "Password must be a 5-digit number"));
    }

    @Test
    @DisplayName("Should handle login failure - exception")
    void testHandleLogin_Exception() throws Exception {
        when(staffService.authenticate(anyString(), anyInt())).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attribute("error", "Login failed. Please try again."));
    }

    @Test
    @DisplayName("Should logout successfully")
    void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("staff", staffSessionData);

        mockMvc.perform(get("/staff/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attribute("message", "You have been logged out successfully"));

        // Verify session is invalidated (MockHttpSession doesn't really invalidate but
        // we can check logic)
    }

    @Test
    @DisplayName("Should save new flight successfully")
    void testSaveFlight_New() throws Exception {
        mockMvc.perform(post("/staff/flights/save")
                .param("flightId", "F001")
                .param("departureCountry", "USA")
                .param("arrivalCountry", "UK")
                .param("departureDate", "2023-01-01")
                .param("arrivalDate", "2023-01-02")
                .param("departureTime", "1000")
                .param("arrivalTime", "1800")
                .param("boardingTime", "0900")
                .param("economyPrice", "500.0")
                .param("businessPrice", "1000.0")
                .param("planeNo", "P123")
                .param("totalSeats", "200")
                .param("isEdit", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"))
                .andExpect(flash().attribute("message", "Flight F001 added successfully!"));

        verify(flightService).addFlight(any(Flight.class));
    }

    @Test
    @DisplayName("Should update existing flight successfully")
    void testSaveFlight_Update() throws Exception {
        mockMvc.perform(post("/staff/flights/save")
                .param("documentId", "doc123")
                .param("flightId", "F001")
                .param("departureCountry", "USA")
                .param("arrivalCountry", "UK")
                .param("departureDate", "2023-01-01")
                .param("arrivalDate", "2023-01-02")
                .param("departureTime", "1000")
                .param("arrivalTime", "1800")
                .param("boardingTime", "0900")
                .param("economyPrice", "500.0")
                .param("businessPrice", "1000.0")
                .param("planeNo", "P123")
                .param("totalSeats", "200")
                .param("isEdit", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"))
                .andExpect(flash().attribute("message", "Flight F001 updated successfully!"));

        verify(flightService).updateFlight(eq("doc123"), any(Flight.class));
    }

    @Test
    @DisplayName("Should handle save flight error - IllegalArgumentException")
    void testSaveFlight_IllegalArgumentException() throws Exception {
        // Simulate error by passing invalid params or mocking service to throw
        // Since we can't easily mock the constructor of Flight to throw, we'll mock the
        // service call
        doThrow(new IllegalArgumentException("Invalid flight data")).when(flightService).addFlight(any(Flight.class));

        mockMvc.perform(post("/staff/flights/save")
                .param("flightId", "F001")
                .param("departureCountry", "USA")
                .param("arrivalCountry", "UK")
                .param("departureDate", "2023-01-01")
                .param("arrivalDate", "2023-01-02")
                .param("departureTime", "1000")
                .param("arrivalTime", "1800")
                .param("boardingTime", "0900")
                .param("economyPrice", "500.0")
                .param("businessPrice", "1000.0")
                .param("planeNo", "P123")
                .param("totalSeats", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"))
                .andExpect(flash().attribute("error", "Invalid flight data"));
    }

    @Test
    @DisplayName("Should handle save flight error - ExecutionException")
    void testSaveFlight_ExecutionException() throws Exception {
        doThrow(new ExecutionException(new RuntimeException("DB Error"))).when(flightService)
                .addFlight(any(Flight.class));

        mockMvc.perform(post("/staff/flights/save")
                .param("flightId", "F001")
                .param("departureCountry", "USA")
                .param("arrivalCountry", "UK")
                .param("departureDate", "2023-01-01")
                .param("arrivalDate", "2023-01-02")
                .param("departureTime", "1000")
                .param("arrivalTime", "1800")
                .param("boardingTime", "0900")
                .param("economyPrice", "500.0")
                .param("businessPrice", "1000.0")
                .param("planeNo", "P123")
                .param("totalSeats", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"))
                .andExpect(flash().attribute("error", "Failed to save flight: java.lang.RuntimeException: DB Error"));
    }

    @Test
    @DisplayName("Should show REST dashboard")
    void testShowRestDashboard() throws Exception {
        mockMvc.perform(get("/staff/dashboard-rest"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-dashboard-rest"))
                .andExpect(model().attributeExists("staff"));
    }

    @Test
    @DisplayName("Should show reports page")
    void testShowReportsPage() throws Exception {
        mockMvc.perform(get("/staff/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-reports"))
                .andExpect(model().attributeExists("staff"));
    }

    @Test
    @DisplayName("Should download sales report")
    void testDownloadSalesReport() throws Exception {
        byte[] pdfContent = "PDF Content".getBytes();
        when(pdfReportService.generateSalesReportPdf()).thenReturn(pdfContent);

        mockMvc.perform(get("/staff/reports/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfContent));
    }

    @Test
    @DisplayName("Should handle download report error")
    void testDownloadSalesReport_Error() throws Exception {
        when(pdfReportService.generateSalesReportPdf())
                .thenThrow(new ExecutionException(new RuntimeException("Error")));

        mockMvc.perform(get("/staff/reports/download"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should redirect home to dashboard")
    void testHome() throws Exception {
        mockMvc.perform(get("/staff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));

        mockMvc.perform(get("/staff/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }
}