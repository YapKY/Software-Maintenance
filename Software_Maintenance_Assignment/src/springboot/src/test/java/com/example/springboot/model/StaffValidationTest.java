// package com.example.springboot.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;

// import static org.junit.jupiter.api.Assertions.*;

// /**
// * Unit Tests for Staff Model
// * Tests Staff entity getter/setter methods and basic validations
// */
// @DisplayName("Staff Model Validation Tests")
// class StaffValidationTest {

// private Staff staff;

// @BeforeEach
// void setUp() {
// staff = new Staff();
// staff.setStaffId("S001");
// staff.setStfPass("12345");
// staff.setName("Alice Johnson");
// staff.setEmail("alice@example.com");
// staff.setPhoneNumber("0123456789");
// staff.setGender("Female");
// staff.setPosition("Manager");
// }

// // ==================== GETTER/SETTER TESTS ====================

// @Test
// @DisplayName("Should set and get staff ID correctly")
// void testSetAndGetStaffId() {
// staff.setStaffId("S123");
// assertEquals("S123", staff.getStaffId());
// }

// @Test
// @DisplayName("Should set and get password correctly")
// void testSetAndGetPassword() {
// staff.setStfPass("54321");
// assertEquals("54321", staff.getStfPass());
// }

// @Test
// @DisplayName("Should set and get name correctly")
// void testSetAndGetName() {
// staff.setName("Bob Smith");
// assertEquals("Bob Smith", staff.getName());
// }

// @Test
// @DisplayName("Should set and get email correctly")
// void testSetAndGetEmail() {
// staff.setEmail("test@example.com");
// assertEquals("test@example.com", staff.getEmail());
// }

// @Test
// @DisplayName("Should set and get phone number correctly")
// void testSetAndGetPhoneNumber() {
// staff.setPhoneNumber("0198765432");
// assertEquals("0198765432", staff.getPhoneNumber());
// }

// @Test
// @DisplayName("Should set and get gender correctly")
// void testSetAndGetGender() {
// staff.setGender("Male");
// assertEquals("Male", staff.getGender());
// }

// @Test
// @DisplayName("Should set and get position as Manager")
// void testSetPositionManager() {
// staff.setPosition("Manager");
// assertEquals("Manager", staff.getPosition());
// }

// @Test
// @DisplayName("Should set and get position as Airline Controller")
// void testSetPositionAirlineController() {
// staff.setPosition("Airline Controller");
// assertEquals("Airline Controller", staff.getPosition());
// }

// @Test
// @DisplayName("Should create staff with default constructor")
// void testStaffNoArgsConstructor() {
// Staff newStaff = new Staff();
// assertNull(newStaff.getStaffId());
// assertNull(newStaff.getPosition());
// assertNull(newStaff.getStfPass());
// }

// @Test
// @DisplayName("Should create staff with all required fields")
// void testCompleteStaffObject() {
// Staff newStaff = new Staff();
// newStaff.setStaffId("S999");
// newStaff.setStfPass("11111");
// newStaff.setName("Test User");
// newStaff.setEmail("test@example.com");
// newStaff.setPhoneNumber("0111222333");
// newStaff.setGender("Male");
// newStaff.setPosition("Manager");

// assertEquals("S999", newStaff.getStaffId());
// assertEquals("11111", newStaff.getStfPass());
// assertEquals("Test User", newStaff.getName());
// assertEquals("test@example.com", newStaff.getEmail());
// assertEquals("0111222333", newStaff.getPhoneNumber());
// assertEquals("Male", newStaff.getGender());
// assertEquals("Manager", newStaff.getPosition());
// }

// @Test
// @DisplayName("Should handle null values")
// void testNullValues() {
// Staff newStaff = new Staff();
// newStaff.setStaffId(null);
// newStaff.setEmail(null);
// newStaff.setPhoneNumber(null);

// assertNull(newStaff.getStaffId());
// assertNull(newStaff.getEmail());
// assertNull(newStaff.getPhoneNumber());
// }

// @Test
// @DisplayName("Should update staff fields correctly")
// void testUpdateStaffFields() {
// assertEquals("Alice Johnson", staff.getName());

// staff.setName("Alice Smith");
// staff.setEmail("alice.smith@example.com");
// staff.setPosition("Airline Controller");

// assertEquals("Alice Smith", staff.getName());
// assertEquals("alice.smith@example.com", staff.getEmail());
// assertEquals("Airline Controller", staff.getPosition());
// }

// @Test
// @DisplayName("Should handle toString method")
// void testToString() {
// String result = staff.toString();
// assertNotNull(result);
// assertTrue(result.contains("S001") || result.contains("Alice Johnson"));
// }

// @Test
// @DisplayName("Should maintain independent instances")
// void testIndependentInstances() {
// Staff staff1 = new Staff();
// Staff staff2 = new Staff();

// staff1.setStaffId("S100");
// staff2.setStaffId("S200");

// assertEquals("S100", staff1.getStaffId());
// assertEquals("S200", staff2.getStaffId());
// assertNotEquals(staff1.getStaffId(), staff2.getStaffId());
// }
// }
