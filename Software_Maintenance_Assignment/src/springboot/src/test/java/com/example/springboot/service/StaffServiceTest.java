package com.example.springboot.service;

import com.example.springboot.model.Staff;
import com.example.springboot.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for StaffService
 * Tests business logic layer for staff operations
 */
@DisplayName("Staff Service Unit Tests")
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testStaff = new Staff();
        testStaff.setStaffId("S001");
        testStaff.setStfPass("12345");
        testStaff.setName("Alice Johnson");
        testStaff.setEmail("alice@example.com");
        testStaff.setPhoneNumber("0123456789");
        testStaff.setGender("Female");
        testStaff.setPosition("Manager");
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    @DisplayName("Should authenticate staff with valid credentials")
    void testAuthenticateStaffSuccess() {
        // Arrange
        when(staffRepository.findByStaffId("S001")).thenReturn(testStaff);

        // Act
        Optional<Staff> result = staffService.authenticateStaff("S001", "12345");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Alice Johnson", result.get().getName());
        verify(staffRepository, times(1)).findByStaffId("S001");
    }

    @Test
    @DisplayName("Should fail authentication with invalid staff ID")
    void testAuthenticateStaffInvalidId() {
        // Arrange
        when(staffRepository.findByStaffId("S999")).thenReturn(null);

        // Act
        Optional<Staff> result = staffService.authenticateStaff("S999", "12345");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with incorrect password")
    void testAuthenticateStaffInvalidPassword() {
        // Arrange
        Staff wrongPasswordStaff = new Staff();
        wrongPasswordStaff.setStaffId("S001");
        wrongPasswordStaff.setStfPass("99999");
        when(staffRepository.findByStaffId("S001")).thenReturn(wrongPasswordStaff);

        // Act
        Optional<Staff> result = staffService.authenticateStaff("S001", "54321");

        // Assert
        assertFalse(result.isPresent());
    }

    // ==================== CREATION TESTS ====================

    @Test
    @DisplayName("Should create staff with valid data")
    void testCreateStaffSuccess() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S002");
        newStaff.setStfPass("54321");
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");
        newStaff.setPosition("Manager");

        when(staffRepository.existsByStaffId("S002")).thenReturn(false);
        when(staffRepository.save(any(Staff.class))).thenReturn(newStaff);

        // Act
        Staff result = staffService.createStaff(newStaff);

        // Assert
        assertNotNull(result);
        assertEquals("Bob Smith", result.getName());
        assertEquals("S002", result.getStaffId());
        verify(staffRepository, times(1)).save(any(Staff.class));
    }

    @Test
    @DisplayName("Should reject staff creation with null staff ID")
    void testCreateStaffNullId() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId(null);
        newStaff.setStfPass("54321");
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.createStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should reject staff creation with empty staff ID")
    void testCreateStaffEmptyId() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("");
        newStaff.setStfPass("54321");
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.createStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should reject staff creation with invalid password format")
    void testCreateStaffInvalidPassword() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S002");
        newStaff.setStfPass("ABC12"); // Not all digits
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.createStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should reject staff creation with short password")
    void testCreateStaffShortPassword() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S002");
        newStaff.setStfPass("1234"); // Less than 5 digits
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.createStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should reject staff creation with duplicate staff ID")
    void testCreateStaffDuplicateId() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S001"); // Already exists
        newStaff.setStfPass("54321");
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");

        when(staffRepository.existsByStaffId("S001")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.createStaff(newStaff);
        });
    }

    // ==================== RETRIEVAL TESTS ====================

    @Test
    @DisplayName("Should retrieve all staff members")
    void testGetAllStaff() throws ExecutionException, InterruptedException {
        // Arrange
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepository.findAll()).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getAllStaff();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list when no staff members exist")
    void testGetAllStaffEmpty() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Staff> result = staffService.getAllStaff();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve staff by ID")
    void testGetStaffById() {
        // Arrange
        when(staffRepository.findByStaffId("staff-1")).thenReturn(testStaff);

        // Act
        Optional<Staff> result = staffService.getStaffById("staff-1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Alice Johnson", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when staff ID not found")
    void testGetStaffByIdNotFound() {
        // Arrange
        when(staffRepository.findByStaffId("staff-999")).thenReturn(null);

        // Act
        Optional<Staff> result = staffService.getStaffById("staff-999");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should retrieve staff by staff ID")
    void testGetStaffByStaffId() {
        // Arrange
        when(staffRepository.findByStaffId("S001")).thenReturn(testStaff);

        // Act
        Staff result = staffService.getStaffByStaffId("S001");

        // Assert
        assertNotNull(result);
        assertEquals("Alice Johnson", result.getName());
    }

    @Test
    @DisplayName("Should return null when staff ID not found")
    void testGetStaffByStaffIdNotFound() {
        // Arrange
        when(staffRepository.findByStaffId("S999")).thenReturn(null);

        // Act
        Staff result = staffService.getStaffByStaffId("S999");

        // Assert
        assertNull(result);
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Should update staff with valid data")
    void testUpdateStaffSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        Staff updatedData = new Staff();
        updatedData.setName("Alice Johnson Updated");
        updatedData.setEmail("alice.updated@example.com");
        updatedData.setPosition("Manager");

        when(staffRepository.findByStaffId("staff-1")).thenReturn(testStaff);
        doNothing().when(staffRepository).update(eq("staff-1"), any(Staff.class));

        // Act
        Staff result = staffService.updateStaff("staff-1", updatedData);

        // Assert
        assertNotNull(result);
        verify(staffRepository, times(1)).update(eq("staff-1"), any(Staff.class));
    }

    @Test
    @DisplayName("Should reject update for non-existent staff")
    void testUpdateStaffNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        Staff updatedData = new Staff();
        updatedData.setName("New Name");

        when(staffRepository.findByStaffId("staff-999")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.updateStaff("staff-999", updatedData);
        });
    }

    @Test
    @DisplayName("Should call repository update when updating staff")
    void testUpdateStaffRepositoryCall() throws ExecutionException, InterruptedException {
        // Arrange
        Staff updatedData = new Staff();
        updatedData.setName("New Name");
        updatedData.setPosition("Manager");

        when(staffRepository.findByStaffId("staff-1")).thenReturn(testStaff);
        doNothing().when(staffRepository).update(eq("staff-1"), any(Staff.class));

        // Act
        Staff result = staffService.updateStaff("staff-1", updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Manager", result.getPosition());
        verify(staffRepository, times(1)).update(eq("staff-1"), any(Staff.class));
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Should delete staff successfully")
    void testDeleteStaffSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findByStaffId("staff-1")).thenReturn(testStaff);
        doNothing().when(staffRepository).delete("staff-1");

        // Act
        staffService.deleteStaff("staff-1");

        // Assert
        verify(staffRepository, times(1)).delete("staff-1");
    }

    @Test
    @DisplayName("Should reject deletion of non-existent staff")
    void testDeleteStaffNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findByStaffId("staff-999")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.deleteStaff("staff-999");
        });
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @DisplayName("Should throw RuntimeException on execution error during authentication")
    void testAuthenticateStaffExecutionError() {
        // Arrange
        when(staffRepository.findByStaffId(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            staffService.authenticateStaff("S001", "12345");
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException on execution error during creation")
    void testCreateStaffExecutionError() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S002");
        newStaff.setStfPass("54321");
        newStaff.setName("Bob Smith");
        newStaff.setEmail("bob@example.com");
        newStaff.setPhoneNumber("9876543210");
        newStaff.setGender("Male");

        when(staffRepository.existsByStaffId("S002"))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            staffService.createStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should throw ExecutionException on execution error during retrieval")
    void testGetAllStaffExecutionError() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findAll())
                .thenThrow(new ExecutionException(new Exception("Database error")));

        // Act & Assert
        assertThrows(ExecutionException.class, () -> {
            staffService.getAllStaff();
        });
    }
}
