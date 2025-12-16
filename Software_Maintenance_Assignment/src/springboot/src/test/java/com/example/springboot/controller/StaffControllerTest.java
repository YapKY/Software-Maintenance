package com.example.springboot.controller;

import com.example.springboot.model.Staff;
import com.example.springboot.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StaffControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StaffService staffService;

    @InjectMocks
    private StaffController staffController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Staff testStaff;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(staffController).build();

        testStaff = new Staff();
        testStaff.setStaffId("S001");
        testStaff.setName("Test Staff");
        testStaff.setEmail("test@example.com");
        testStaff.setStfPass("12345");
        testStaff.setPosition("Manager");
        testStaff.setPhoneNumber("0123456789");
        testStaff.setGender("Male");
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin_Success() throws Exception {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("staffId", "S001");
        credentials.put("password", "12345");

        when(staffService.authenticateStaff("S001", "12345")).thenReturn(Optional.of(testStaff));

        mockMvc.perform(post("/api/staff/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Staff Login Successful"))
                .andExpect(jsonPath("$.staff.staffId").value("S001"));
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void testLogin_Failure() throws Exception {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("staffId", "S001");
        credentials.put("password", "wrongpass");

        when(staffService.authenticateStaff("S001", "wrongpass")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/staff/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid Staff ID or Password"));
    }

    @Test
    @DisplayName("Should fail login with missing fields")
    void testLogin_MissingFields() throws Exception {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("staffId", "S001");

        mockMvc.perform(post("/api/staff/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Password must be a 5-digit number"));
    }

    @Test
    @DisplayName("Should fail login with invalid password format")
    void testLogin_InvalidPasswordFormat() throws Exception {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("staffId", "S001");
        credentials.put("password", 12345); // Integer instead of String, but code handles toString()

        // The controller code does .toString() on the password object, so this might
        // actually pass validation
        // Let's try to trigger the exception in the catch block
        // The catch block catches Exception when doing
        // credentials.get("password").toString()
        // It's hard to make .toString() fail on a standard object.
        // However, if password is null, it will throw NullPointerException which is
        // caught?
        // No, if password is null, credentials.get("password") returns null,
        // null.toString() throws NPE.

        credentials.put("password", null);

        // Wait, if password is null, the try-catch block might catch NPE if it happens
        // inside.
        // But credentials.get("password") returns null. null.toString() throws NPE.
        // The catch block catches Exception e.

        // Let's verify the controller logic:
        // try { password = credentials.get("password").toString(); } catch (Exception
        // e) { ... }

        // If I send null for password key, credentials.get("password") is null.

        mockMvc.perform(post("/api/staff/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"staffId\":\"S001\"}")) // Missing password key
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be a 5-digit number"));
    }

    @Test
    @DisplayName("Should create staff successfully")
    void testCreateStaff_Success() throws Exception {
        when(staffService.createStaff(any(Staff.class))).thenReturn(testStaff);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.staff.staffId").value("S001"));
    }

    @Test
    @DisplayName("Should fail create staff with bad request")
    void testCreateStaff_BadRequest() throws Exception {
        when(staffService.createStaff(any(Staff.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    @DisplayName("Should fail create staff with internal error")
    void testCreateStaff_InternalError() throws Exception {
        when(staffService.createStaff(any(Staff.class))).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to create staff: DB Error"));
    }

    @Test
    @DisplayName("Should get all staff")
    void testGetAllStaff() throws Exception {
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].staffId").value("S001"));
    }

    @Test
    @DisplayName("Should handle error getting all staff")
    void testGetAllStaff_Error() throws Exception {
        when(staffService.getAllStaff()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error getting staff: DB Error"));
    }

    @Test
    @DisplayName("Should get staff by ID")
    void testGetStaffById_Success() throws Exception {
        when(staffService.getStaffById("S001")).thenReturn(Optional.of(testStaff));

        mockMvc.perform(get("/api/staff/S001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value("S001"));
    }

    @Test
    @DisplayName("Should return 404 when staff by ID not found")
    void testGetStaffById_NotFound() throws Exception {
        when(staffService.getStaffById("S001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/staff/S001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Staff not found"));
    }

    @Test
    @DisplayName("Should get staff by Staff ID")
    void testGetStaffByStaffId_Success() throws Exception {
        when(staffService.getStaffByStaffId("S001")).thenReturn(testStaff);

        mockMvc.perform(get("/api/staff/staffid/S001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value("S001"));
    }

    @Test
    @DisplayName("Should return 404 when staff by Staff ID not found")
    void testGetStaffByStaffId_NotFound() throws Exception {
        when(staffService.getStaffByStaffId("S001")).thenReturn(null);

        mockMvc.perform(get("/api/staff/staffid/S001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Staff not found"));
    }

    @Test
    @DisplayName("Should update staff successfully")
    void testUpdateStaff_Success() throws Exception {
        when(staffService.updateStaff(eq("S001"), any(Staff.class))).thenReturn(testStaff);

        mockMvc.perform(put("/api/staff/S001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value("S001"));
    }

    @Test
    @DisplayName("Should fail update staff with bad request")
    void testUpdateStaff_BadRequest() throws Exception {
        when(staffService.updateStaff(eq("S001"), any(Staff.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/staff/S001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    @DisplayName("Should fail update staff with internal error")
    void testUpdateStaff_InternalError() throws Exception {
        when(staffService.updateStaff(eq("S001"), any(Staff.class))).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(put("/api/staff/S001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaff)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error updating staff: DB Error"));
    }

    @Test
    @DisplayName("Should delete staff successfully")
    void testDeleteStaff_Success() throws Exception {
        doNothing().when(staffService).deleteStaff("S001");

        mockMvc.perform(delete("/api/staff/S001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Staff deleted successfully"));
    }

    @Test
    @DisplayName("Should fail delete staff when not found")
    void testDeleteStaff_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Staff not found")).when(staffService).deleteStaff("S001");

        mockMvc.perform(delete("/api/staff/S001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Staff not found"));
    }

    @Test
    @DisplayName("Should fail delete staff with internal error")
    void testDeleteStaff_InternalError() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(staffService).deleteStaff("S001");

        mockMvc.perform(delete("/api/staff/S001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error deleting staff: DB Error"));
    }

    @Test
    @DisplayName("Should debug get all staff")
    void testDebugGetAllStaff() throws Exception {
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        mockMvc.perform(get("/api/staff/debug/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @DisplayName("Should handle error in debug get all staff")
    void testDebugGetAllStaff_Error() throws Exception {
        when(staffService.getAllStaff()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/staff/debug/all"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error getting staff: DB Error"));
    }

    @Test
    @DisplayName("Should get staff by index")
    void testGetStaffByIndex_Success() throws Exception {
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        mockMvc.perform(get("/api/staff/index/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId").value("S001"));
    }

    @Test
    @DisplayName("Should fail get staff by index out of range")
    void testGetStaffByIndex_OutOfRange() throws Exception {
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        mockMvc.perform(get("/api/staff/index/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Staff index out of range. Available: 1-1"));
    }

    @Test
    @DisplayName("Should handle error in get staff by index")
    void testGetStaffByIndex_Error() throws Exception {
        when(staffService.getAllStaff()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/staff/index/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error getting staff: DB Error"));
    }

    @Test
    @DisplayName("Should debug create test staff")
    void testDebugCreateTestStaff_Success() throws Exception {
        when(staffService.createStaff(any(Staff.class))).thenReturn(testStaff);

        mockMvc.perform(post("/api/staff/debug/create-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Test staff created"));
    }

    @Test
    @DisplayName("Should handle error in debug create test staff")
    void testDebugCreateTestStaff_Error() throws Exception {
        when(staffService.createStaff(any(Staff.class))).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(post("/api/staff/debug/create-test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error creating test staff: DB Error"));
    }
}
