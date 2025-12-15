package com.example.springboot;

import com.example.springboot.controller.StaffViewController;
import com.example.springboot.model.Staff;
import com.example.springboot.service.FlightService;
import com.example.springboot.service.PdfReportService;
import com.example.springboot.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test cases for StaffViewController
 * Tests authentication, dashboard access, and staff operations
 */
@ExtendWith(MockitoExtension.class)
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

    private MockHttpSession session;
    private Staff testStaff;
    private Map<String, Object> staffData;

    @BeforeEach
    void setUp() {
        // Setup view resolver for Thymeleaf
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(staffViewController)
                .setViewResolvers(viewResolver)
                .build();

        // Create test staff
        testStaff = new Staff();
        testStaff.setStaffId("S001");
        testStaff.setName("Apple Doe");
        testStaff.setPosition("Manager");
        testStaff.setEmail("apple@example.com");
        testStaff.setStfPass("11111");

        // Create staff session data
        staffData = new HashMap<>();
        staffData.put("staffId", "S001");
        staffData.put("name", "Apple Doe");
        staffData.put("position", "Manager");

        // Create mock session
        session = new MockHttpSession();
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void testShowLoginPage_WhenNotLoggedIn_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/staff/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff"));
    }

    @Test
    void testShowLoginPage_WhenAlreadyLoggedIn_ShouldRedirectToDashboard() throws Exception {
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    void testHandleLogin_WithValidCredentials_ShouldRedirectToDashboard() throws Exception {
        when(staffService.authenticate("S001", 11111)).thenReturn(testStaff);

        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "11111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));

        verify(staffService).authenticate("S001", 11111);
    }

    @Test
    void testHandleLogin_WithInvalidCredentials_ShouldRedirectBackToLogin() throws Exception {
        when(staffService.authenticate("S001", 99999)).thenReturn(null);

        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "99999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attributeExists("error"));

        verify(staffService).authenticate("S001", 99999);
    }

    @Test
    void testHandleLogin_WithInvalidPasswordFormat_ShouldRedirectWithError() throws Exception {
        mockMvc.perform(post("/staff/login")
                .param("staffId", "S001")
                .param("password", "abc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attributeExists("error"));
    }

    // ==================== LOGOUT TESTS ====================

    @Test
    void testLogout_ShouldInvalidateSessionAndRedirect() throws Exception {
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"))
                .andExpect(flash().attributeExists("message"));
    }

    // ==================== DASHBOARD TESTS ====================

    @Test
    void testShowDashboard_WhenLoggedIn_ShouldReturnDashboardView() throws Exception {
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-dashboard-rest"))
                .andExpect(model().attributeExists("staff"));
    }

    @Test
    void testShowDashboard_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/staff/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"));
    }

    // ==================== REPORTS TESTS ====================

    @Test
    void testShowReportsPage_AsManager_ShouldReturnReportsView() throws Exception {
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff/reports").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-reports"))
                .andExpect(model().attributeExists("staff"));
    }

    @Test
    void testShowReportsPage_AsController_ShouldRedirectToDashboard() throws Exception {
        staffData.put("position", "Airline Controller");
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff/reports").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    void testShowReportsPage_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/staff/reports"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"));
    }

    @Test
    void testDownloadSalesReport_AsManager_ShouldReturnPDF() throws Exception {
        session.setAttribute("staff", staffData);
        byte[] pdfBytes = "PDF Content".getBytes();

        when(pdfReportService.generateSalesReportPdf()).thenReturn(pdfBytes);

        mockMvc.perform(get("/staff/reports/download").session(session))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));

        verify(pdfReportService).generateSalesReportPdf();
    }

    @Test
    void testDownloadSalesReport_AsController_ShouldReturnForbidden() throws Exception {
        staffData.put("position", "Airline Controller");
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff/reports/download").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDownloadSalesReport_WhenNotLoggedIn_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/staff/reports/download"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== HOME REDIRECT TESTS ====================

    @Test
    void testHomeRedirect_WhenLoggedIn_ShouldRedirectToDashboard() throws Exception {
        session.setAttribute("staff", staffData);

        mockMvc.perform(get("/staff").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    void testHomeRedirect_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/staff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/login"));
    }
}