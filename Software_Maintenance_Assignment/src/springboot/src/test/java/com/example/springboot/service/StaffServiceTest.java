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

    // ==================== ADDITIONAL COVERAGE TESTS ====================

    @Test
    @DisplayName("Should return empty optional when staff not found by ID")
    void testGetStaffById_NotFound() {
        // Arrange
        when(staffRepository.findByStaffId("NONEXISTENT")).thenReturn(null);

        // Act
        Optional<Staff> result = staffService.getStaffById("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should authenticate staff with valid credentials")
    void testAuthenticateStaff_ValidStringPassword() {
        // Arrange
        when(staffRepository.findByStaffId("S001")).thenReturn(testStaff);

        // Act
        Optional<Staff> result = staffService.authenticateStaff("S001", "12345");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Alice Johnson", result.get().getName());
    }

    @Test
    @DisplayName("Should fail authentication with invalid string password format")
    void testAuthenticateStaff_InvalidPasswordFormat() {
        // Arrange - Non-numeric password

        // Act
        Optional<Staff> result = staffService.authenticateStaff("S001", "notanumber");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get all managers successfully")
    void testGetAllManagers_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Staff manager = new Staff();
        manager.setStaffId("S001");
        manager.setPosition("Manager");
        manager.setName("Manager One");

        when(staffRepository.findByPosition("Manager")).thenReturn(Arrays.asList(manager));

        // Act
        List<Staff> result = staffService.getAllManagers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Manager", result.get(0).getPosition());
    }

    @Test
    @DisplayName("Should get all controllers successfully")
    void testGetAllControllers_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Staff controller = new Staff();
        controller.setStaffId("S002");
        controller.setPosition("Airline Controller");
        controller.setName("Controller One");

        when(staffRepository.findByPosition("Airline Controller")).thenReturn(Arrays.asList(controller));

        // Act
        List<Staff> result = staffService.getAllControllers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Airline Controller", result.get(0).getPosition());
    }

    @Test
    @DisplayName("Should check if staff exists")
    void testStaffExists_True() {
        // Arrange
        when(staffRepository.existsByStaffId("S001")).thenReturn(true);

        // Act
        boolean result = staffService.staffExists("S001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when staff does not exist")
    void testStaffExists_False() {
        // Arrange
        when(staffRepository.existsByStaffId("NONEXISTENT")).thenReturn(false);

        // Act
        boolean result = staffService.staffExists("NONEXISTENT");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Staff staffWithPassword = new Staff();
        staffWithPassword.setStaffId("S001");
        staffWithPassword.setStfPass("12345");
        staffWithPassword.setName("Alice Johnson");

        when(staffRepository.findByStaffId("S001")).thenReturn(staffWithPassword);
        doNothing().when(staffRepository).update(eq("S001"), any(Staff.class));

        // Act
        staffService.changePassword("S001", 12345, "54321");

        // Assert
        verify(staffRepository, times(1)).update(eq("S001"), any(Staff.class));
    }

    @Test
    @DisplayName("Should fail password change with incorrect old password")
    void testChangePassword_IncorrectOldPassword() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findByStaffId("S001")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.changePassword("S001", 99999, "54321");
        });
    }

    @Test
    @DisplayName("Should fail password change with invalid new password format")
    void testChangePassword_InvalidNewPasswordFormat() throws ExecutionException, InterruptedException {
        // Arrange
        Staff staffWithPassword = new Staff();
        staffWithPassword.setStaffId("S001");
        staffWithPassword.setStfPass("12345");

        when(staffRepository.findByStaffId("S001")).thenReturn(staffWithPassword);

        // Act & Assert - New password must be exactly 5 digits
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.changePassword("S001", 12345, "1234"); // Only 4 digits
        });
    }

    @Test
    @DisplayName("Should get staff count by position")
    void testGetStaffCountByPosition() throws ExecutionException, InterruptedException {
        // Arrange
        List<Staff> managers = Arrays.asList(testStaff, new Staff());
        when(staffRepository.findByPosition("Manager")).thenReturn(managers);

        // Act
        int count = staffService.getStaffCountByPosition("Manager");

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should return zero when no staff with position exists")
    void testGetStaffCountByPosition_Zero() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findByPosition("NonexistentPosition")).thenReturn(Arrays.asList());

        // Act
        int count = staffService.getStaffCountByPosition("NonexistentPosition");

        // Assert
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should throw error when adding staff with null staffId")
    void testAddStaff_NullStaffId() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId(null);
        newStaff.setName("John");
        newStaff.setPosition("Manager");
        newStaff.setStfPass("12345");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should throw error when adding staff with empty staffId")
    void testAddStaff_EmptyStaffId() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("");
        newStaff.setName("John");
        newStaff.setPosition("Manager");
        newStaff.setStfPass("12345");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should throw error when adding staff with null name")
    void testAddStaff_NullName() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S099");
        newStaff.setName(null);
        newStaff.setPosition("Manager");
        newStaff.setStfPass("12345");

        when(staffRepository.existsByStaffId("S099")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should throw error when adding staff with invalid password format")
    void testAddStaff_InvalidPasswordFormat() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S099");
        newStaff.setName("New Staff");
        newStaff.setPosition("Manager");
        newStaff.setStfPass("1234"); // Only 4 digits

        when(staffRepository.existsByStaffId("S099")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should throw error when adding staff with invalid email format")
    void testAddStaff_InvalidEmailFormat() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S099");
        newStaff.setName("New Staff");
        newStaff.setPosition("Manager");
        newStaff.setStfPass("12345");
        newStaff.setEmail("invalid-email-format");

        when(staffRepository.existsByStaffId("S099")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should throw error when adding staff with invalid position")
    void testAddStaff_InvalidPosition() {
        // Arrange
        Staff newStaff = new Staff();
        newStaff.setStaffId("S099");
        newStaff.setName("New Staff");
        newStaff.setPosition("InvalidPosition");
        newStaff.setStfPass("12345");

        when(staffRepository.existsByStaffId("S099")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(newStaff);
        });
    }

    @Test
    @DisplayName("Should allow updating staff with partial data")
    void testUpdateStaff_PartialUpdate() throws ExecutionException, InterruptedException {
        // Arrange
        Staff existingStaff = new Staff();
        existingStaff.setStaffId("S001");
        existingStaff.setName("Alice");
        existingStaff.setPhoneNumber("0123456789");
        existingStaff.setGender("Female");
        existingStaff.setPosition("Manager");

        Staff updatedData = new Staff();
        updatedData.setName("Alice Updated"); // Only update name
        updatedData.setPhoneNumber(null); // Don't change phone

        when(staffRepository.findByStaffId("S001")).thenReturn(existingStaff);
        doNothing().when(staffRepository).update(eq("S001"), any(Staff.class));

        // Act
        Staff result = staffService.updateStaff("S001", updatedData);

        // Assert
        assertEquals("Alice Updated", result.getName());
        verify(staffRepository, times(1)).update(eq("S001"), any(Staff.class));
    }

    @Test
    @DisplayName("Should clear phone number when updated to empty string")
    void testUpdateStaff_ClearPhoneNumber() throws ExecutionException, InterruptedException {
        // Arrange
        Staff existingStaff = new Staff();
        existingStaff.setStaffId("S001");
        existingStaff.setName("Alice");
        existingStaff.setPhoneNumber("0123456789");

        Staff updatedData = new Staff();
        updatedData.setPhoneNumber(""); // Clear phone number

        when(staffRepository.findByStaffId("S001")).thenReturn(existingStaff);
        doNothing().when(staffRepository).update(eq("S001"), any(Staff.class));

        // Act
        Staff result = staffService.updateStaff("S001", updatedData);

        // Assert
        assertNull(result.getPhoneNumber());
    }

    @Test
    @DisplayName("Should throw error when updating non-existent staff")
    void testUpdateStaff_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        Staff updatedData = new Staff();
        updatedData.setName("Updated");

        when(staffRepository.findByStaffId("NONEXISTENT")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.updateStaff("NONEXISTENT", updatedData);
        });
    }

    @Test
    @DisplayName("Should throw error when updating with invalid password format")
    void testUpdateStaff_InvalidPasswordFormat() throws ExecutionException, InterruptedException {
        // Arrange
        Staff existingStaff = new Staff();
        existingStaff.setStaffId("S001");
        existingStaff.setName("Alice");

        Staff updatedData = new Staff();
        updatedData.setStfPass("123"); // Invalid: only 3 digits

        when(staffRepository.findByStaffId("S001")).thenReturn(existingStaff);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.updateStaff("S001", updatedData);
        });
    }

    @Test
    @DisplayName("Should delete staff successfully")
    void testDeleteStaff_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findByStaffId("S001")).thenReturn(testStaff);
        doNothing().when(staffRepository).delete("S001");

        // Act
        staffService.deleteStaff("S001");

        // Assert
        verify(staffRepository, times(1)).delete("S001");
    }

    @Test
    @DisplayName("Should throw error when deleting non-existent staff")
    void testDeleteStaff_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(staffRepository.findByStaffId("NONEXISTENT")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.deleteStaff("NONEXISTENT");
        });
    }
}
