package com.example.springboot.controller;

import com.example.springboot.model.Staff;
import com.example.springboot.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for StaffController
 * Tests HTTP endpoints and request/response flows for staff operations
 */
@WebMvcTest(StaffController.class)
@DisplayName("Staff Controller Integration Tests")
class StaffControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private StaffService staffService;

        @Autowired
        private ObjectMapper objectMapper;

        private Staff testStaff;

        @BeforeEach
        void setUp() {
                testStaff = new Staff();
                testStaff.setStaffId("S001");
                testStaff.setStfPass("12345");
                testStaff.setName("Alice Johnson");
                testStaff.setEmail("alice@example.com");
                testStaff.setPhoneNumber("0123456789");
                testStaff.setGender("Female");
                testStaff.setPosition("Manager");
        }

        // ==================== LOGIN ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should login staff successfully")
        void testLoginSuccess() throws Exception {
                // Arrange
                when(staffService.authenticateStaff("S001", "12345"))
                                .thenReturn(Optional.of(testStaff));

                Map<String, Object> loginRequest = new HashMap<>();
                loginRequest.put("staffId", "S001");
                loginRequest.put("password", "12345");

                String requestBody = objectMapper.writeValueAsString(loginRequest);

                // Act & Assert
                mockMvc.perform(post("/api/staff/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Staff Login Successful"))
                                .andExpect(jsonPath("$.staff.staffId").value("S001"))
                                .andExpect(jsonPath("$.staff.name").value("Alice Johnson"));

                verify(staffService, times(1)).authenticateStaff("S001", "12345");
        }

        @Test
        @DisplayName("Should reject login with invalid credentials")
        void testLoginFailure() throws Exception {
                // Arrange
                when(staffService.authenticateStaff("S001", "54321"))
                                .thenReturn(Optional.empty());

                Map<String, Object> loginRequest = new HashMap<>();
                loginRequest.put("staffId", "S001");
                loginRequest.put("password", "54321");

                String requestBody = objectMapper.writeValueAsString(loginRequest);

                // Act & Assert
                mockMvc.perform(post("/api/staff/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value(containsString("Invalid")));
        }

        @Test
        @DisplayName("Should reject login with missing staff ID")
        void testLoginMissingStaffId() throws Exception {
                // Arrange
                Map<String, Object> loginRequest = new HashMap<>();
                loginRequest.put("password", "12345");

                String requestBody = objectMapper.writeValueAsString(loginRequest);

                // Act & Assert
                mockMvc.perform(post("/api/staff/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should reject login with missing password")
        void testLoginMissingPassword() throws Exception {
                // Arrange
                Map<String, Object> loginRequest = new HashMap<>();
                loginRequest.put("staffId", "S001");

                String requestBody = objectMapper.writeValueAsString(loginRequest);

                // Act & Assert
                mockMvc.perform(post("/api/staff/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false));
        }

        // ==================== CREATION ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should create staff successfully")
        void testCreateStaffSuccess() throws Exception {
                // Arrange
                Staff newStaff = new Staff();
                newStaff.setStaffId("S002");
                newStaff.setStfPass("54321");
                newStaff.setName("Bob Smith");
                newStaff.setEmail("bob@example.com");
                newStaff.setPhoneNumber("9876543210");
                newStaff.setGender("Male");
                newStaff.setPosition("Developer");

                Staff savedStaff = new Staff();
                savedStaff.setStaffId("S002");
                savedStaff.setStfPass("54321");
                savedStaff.setName("Bob Smith");
                savedStaff.setEmail("bob@example.com");
                savedStaff.setPhoneNumber("9876543210");
                savedStaff.setGender("Male");
                savedStaff.setPosition("Developer");

                when(staffService.createStaff(any(Staff.class)))
                                .thenReturn(savedStaff);

                String requestBody = objectMapper.writeValueAsString(newStaff);

                // Act & Assert
                mockMvc.perform(post("/api/staff")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Staff created successfully"))
                                .andExpect(jsonPath("$.staff.name").value("Bob Smith"));

                verify(staffService, times(1)).createStaff(any(Staff.class));
        }

        @Test
        @DisplayName("Should reject staff creation with duplicate staff ID")
        void testCreateStaffDuplicateId() throws Exception {
                // Arrange
                Staff newStaff = new Staff();
                newStaff.setStaffId("S001"); // Already exists
                newStaff.setStfPass("54321");
                newStaff.setName("Bob Smith");
                newStaff.setEmail("bob@example.com");
                newStaff.setPhoneNumber("9876543210");
                newStaff.setGender("Male");

                when(staffService.createStaff(any(Staff.class)))
                                .thenThrow(new IllegalArgumentException("Staff ID already exists"));

                String requestBody = objectMapper.writeValueAsString(newStaff);

                // Act & Assert
                mockMvc.perform(post("/api/staff")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }

        @Test
        @DisplayName("Should reject staff creation with invalid password")
        void testCreateStaffInvalidPassword() throws Exception {
                // Arrange
                Staff newStaff = new Staff();
                newStaff.setStaffId("S002");
                newStaff.setStfPass("ABC"); // Not 5 digits
                newStaff.setName("Bob Smith");
                newStaff.setEmail("bob@example.com");
                newStaff.setPhoneNumber("9876543210");
                newStaff.setGender("Male");

                when(staffService.createStaff(any(Staff.class)))
                                .thenThrow(new IllegalArgumentException("Password must be a 5-digit number string"));

                String requestBody = objectMapper.writeValueAsString(newStaff);

                // Act & Assert
                mockMvc.perform(post("/api/staff")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("5-digit")));
        }

        // ==================== RETRIEVAL ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should retrieve all staff members")
        void testGetAllStaff() throws Exception {
                // Arrange
                List<Staff> staffList = Arrays.asList(testStaff);
                when(staffService.getAllStaff()).thenReturn(staffList);

                // Act & Assert
                mockMvc.perform(get("/api/staff")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name").value("Alice Johnson"));

                verify(staffService, times(1)).getAllStaff();
        }

        @Test
        @DisplayName("Should retrieve staff by ID")
        void testGetStaffById() throws Exception {
                // Arrange
                when(staffService.getStaffById("staff-1")).thenReturn(Optional.of(testStaff));

                // Act & Assert
                mockMvc.perform(get("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.staffId").value("S001"))
                                .andExpect(jsonPath("$.name").value("Alice Johnson"));

                verify(staffService, times(1)).getStaffById("staff-1");
        }

        @Test
        @DisplayName("Should return 404 when staff ID not found")
        void testGetStaffByIdNotFound() throws Exception {
                // Arrange
                when(staffService.getStaffById("staff-999")).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(get("/api/staff/staff-999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        @DisplayName("Should retrieve staff by staff ID")
        void testGetStaffByStaffId() throws Exception {
                // Arrange
                when(staffService.getStaffByStaffId("S001")).thenReturn(testStaff);

                // Act & Assert
                mockMvc.perform(get("/api/staff/staffid/S001")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.staffId").value("S001"))
                                .andExpect(jsonPath("$.position").value("Manager"));
        }

        @Test
        @DisplayName("Should return 404 when staff by staff ID not found")
        void testGetStaffByStaffIdNotFound() throws Exception {
                // Arrange
                when(staffService.getStaffByStaffId("S999")).thenReturn(null);

                // Act & Assert
                mockMvc.perform(get("/api/staff/staffid/S999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        // ==================== UPDATE ENDPOINT TESTS (PROFILE) ====================

        @Test
        @DisplayName("Should update staff profile successfully")
        void testUpdateStaffSuccess() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setName("Alice Smith");
                updatedData.setPosition("Senior Manager");

                Staff updatedStaff = testStaff;
                updatedStaff.setName("Alice Smith");
                updatedStaff.setPosition("Senior Manager");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenReturn(updatedStaff);

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Alice Smith"))
                                .andExpect(jsonPath("$.position").value("Senior Manager"));

                verify(staffService, times(1)).updateStaff("staff-1", updatedData);
        }

        @Test
        @DisplayName("Should update staff profile with all fields")
        void testUpdateStaffProfile_AllFields() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setStaffId("S001");
                updatedData.setName("Alice Smith Updated");
                updatedData.setEmail("alice.updated@example.com");
                updatedData.setPhoneNumber("0198765432");
                updatedData.setGender("Female");
                updatedData.setPosition("Director");

                Staff updatedStaff = new Staff();
                updatedStaff.setStaffId("S001");
                updatedStaff.setName("Alice Smith Updated");
                updatedStaff.setEmail("alice.updated@example.com");
                updatedStaff.setPhoneNumber("0198765432");
                updatedStaff.setGender("Female");
                updatedStaff.setPosition("Director");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenReturn(updatedStaff);

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Alice Smith Updated"))
                                .andExpect(jsonPath("$.email").value("alice.updated@example.com"))
                                .andExpect(jsonPath("$.phoneNumber").value("0198765432"))
                                .andExpect(jsonPath("$.gender").value("Female"))
                                .andExpect(jsonPath("$.position").value("Director"));

                verify(staffService, times(1)).updateStaff("staff-1", updatedData);
        }

        @Test
        @DisplayName("Should update staff profile with partial fields")
        void testUpdateStaffProfile_PartialFields() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setPhoneNumber("0187654321");

                Staff updatedStaff = testStaff;
                updatedStaff.setPhoneNumber("0187654321");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenReturn(updatedStaff);

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.phoneNumber").value("0187654321"))
                                .andExpect(jsonPath("$.name").value("Alice Johnson")); // Other fields remain

                verify(staffService, times(1)).updateStaff("staff-1", updatedData);
        }

        @Test
        @DisplayName("Should update staff email in profile")
        void testUpdateStaffProfile_EmailOnly() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setEmail("newemail@example.com");

                Staff updatedStaff = testStaff;
                updatedStaff.setEmail("newemail@example.com");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenReturn(updatedStaff);

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("newemail@example.com"));

                verify(staffService, times(1)).updateStaff("staff-1", updatedData);
        }

        @Test
        @DisplayName("Should update staff position in profile")
        void testUpdateStaffProfile_PositionOnly() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setPosition("Team Lead");

                Staff updatedStaff = testStaff;
                updatedStaff.setPosition("Team Lead");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenReturn(updatedStaff);

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.position").value("Team Lead"));

                verify(staffService, times(1)).updateStaff("staff-1", updatedData);
        }

        @Test
        @DisplayName("Should reject update for non-existent staff")
        void testUpdateStaffNotFound() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setName("Bob Smith");

                when(staffService.updateStaff("staff-999", updatedData))
                                .thenThrow(new IllegalArgumentException("Staff not found"));

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        @DisplayName("Should reject profile update with duplicate email")
        void testUpdateStaffDuplicateEmail() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setEmail("existing@example.com");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenThrow(new IllegalArgumentException("Email already exists"));

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }

        @Test
        @DisplayName("Should reject profile update with invalid email format")
        void testUpdateStaffProfile_InvalidEmail() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setEmail("invalid-email");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenThrow(new IllegalArgumentException("Invalid email format"));

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("Invalid email")));
        }

        @Test
        @DisplayName("Should reject profile update with invalid phone number")
        void testUpdateStaffProfile_InvalidPhone() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setPhoneNumber("123");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenThrow(new IllegalArgumentException("Invalid phone number"));

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("Invalid phone")));
        }

        @Test
        @DisplayName("Should update staff name and phone together")
        void testUpdateStaffProfile_NameAndPhone() throws Exception {
                // Arrange
                Staff updatedData = new Staff();
                updatedData.setName("Alice Johnson-Smith");
                updatedData.setPhoneNumber("0199887766");

                Staff updatedStaff = testStaff;
                updatedStaff.setName("Alice Johnson-Smith");
                updatedStaff.setPhoneNumber("0199887766");

                when(staffService.updateStaff("staff-1", updatedData))
                                .thenReturn(updatedStaff);

                String requestBody = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Alice Johnson-Smith"))
                                .andExpect(jsonPath("$.phoneNumber").value("0199887766"));

                verify(staffService, times(1)).updateStaff("staff-1", updatedData);
        }

        // ==================== DELETE ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should delete staff successfully")
        void testDeleteStaffSuccess() throws Exception {
                // Arrange
                doNothing().when(staffService).deleteStaff("staff-1");

                // Act & Assert
                mockMvc.perform(delete("/api/staff/staff-1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Staff deleted successfully"));

                verify(staffService, times(1)).deleteStaff("staff-1");
        }

        @Test
        @DisplayName("Should reject deletion of non-existent staff")
        void testDeleteStaffNotFound() throws Exception {
                // Arrange
                doThrow(new IllegalArgumentException("Staff not found"))
                                .when(staffService).deleteStaff("staff-999");

                // Act & Assert
                mockMvc.perform(delete("/api/staff/staff-999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(containsString("not found")));

                verify(staffService, times(1)).deleteStaff("staff-999");
        }

        // ==================== DEBUG ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should get all staff via debug endpoint")
        void testDebugGetAllStaff() throws Exception {
                // Arrange
                List<Staff> staffList = Arrays.asList(testStaff);
                when(staffService.getAllStaff()).thenReturn(staffList);

                // Act & Assert
                mockMvc.perform(get("/api/staff/debug/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.total").value(1))
                                .andExpect(jsonPath("$.staff", hasSize(1)));
        }

        @Test
        @DisplayName("Should get staff by index")
        void testGetStaffByIndex() throws Exception {
                // Arrange
                List<Staff> staffList = Arrays.asList(testStaff);
                when(staffService.getAllStaff()).thenReturn(staffList);

                // Act & Assert
                mockMvc.perform(get("/api/staff/index/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Alice Johnson"));
        }

        @Test
        @DisplayName("Should return error for invalid staff index")
        void testGetStaffByInvalidIndex() throws Exception {
                // Arrange
                List<Staff> staffList = Arrays.asList(testStaff);
                when(staffService.getAllStaff()).thenReturn(staffList);

                // Act & Assert
                mockMvc.perform(get("/api/staff/index/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(containsString("out of range")));
        }

        @Test
        @DisplayName("Should create test staff via debug endpoint")
        void testDebugCreateTestStaff() throws Exception {
                // Arrange
                Staff testStaff = new Staff();
                testStaff.setStaffId("S999");
                testStaff.setName("Test Staff");

                when(staffService.createStaff(any(Staff.class)))
                                .thenReturn(testStaff);

                // Act & Assert
                mockMvc.perform(post("/api/staff/debug/create-test")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Test staff created"));
        }

        // ==================== CORS TESTS ====================

        @Test
        @DisplayName("Should allow cross-origin requests")
        void testCorsSupport() throws Exception {
                // Arrange
                List<Staff> staffList = Arrays.asList(testStaff);
                when(staffService.getAllStaff()).thenReturn(staffList);

                // Act & Assert
                mockMvc.perform(get("/api/staff")
                                .header("Origin", "http://localhost:3000"))
                                .andExpect(status().isOk());
        }

        // ==================== ERROR HANDLING TESTS ====================

        @Test
        @DisplayName("Should handle internal server error during staff creation")
        void testCreateStaffServerError() throws Exception {
                // Arrange
                Staff newStaff = new Staff();
                newStaff.setStaffId("S002");
                newStaff.setStfPass("54321");
                newStaff.setName("Bob Smith");

                when(staffService.createStaff(any(Staff.class)))
                                .thenThrow(new RuntimeException("Database connection error"));

                String requestBody = objectMapper.writeValueAsString(newStaff);

                // Act & Assert
                mockMvc.perform(post("/api/staff")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value(containsString("Failed to create staff")));
        }
}
