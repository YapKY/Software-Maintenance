// package com.example.springboot.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;

// import static org.junit.jupiter.api.Assertions.*;

// /**
//  * Unit Tests for Staff Model
//  * Tests validation methods and login functionality for Staff entities
//  */
// @DisplayName("Staff Model Validation Tests")
// class StaffValidationTest {

//     private Staff staff;

//     @BeforeEach
//     void setUp() {
//         staff = new Staff();
//     }

//     // ==================== INHERITED PERSON VALIDATION TESTS ====================

//     @Test
//     @DisplayName("Should validate name with only letters and spaces")
//     void testValidNameSuccess() {
//         // Act
//         boolean result = staff.getValidName("Alice Johnson");

//         // Assert
//         assertTrue(result);
//         assertEquals("Alice Johnson", staff.getName());
//     }

//     @Test
//     @DisplayName("Should validate single word name")
//     void testValidNameSingleWord() {
//         // Act
//         boolean result = staff.getValidName("Manager");

//         // Assert
//         assertTrue(result);
//     }

//     @Test
//     @DisplayName("Should reject name with numbers")
//     void testInvalidNameWithNumbers() {
//         // Act
//         boolean result = staff.getValidName("Alice123");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject name with special characters")
//     void testInvalidNameWithSpecialChars() {
//         // Act
//         boolean result = staff.getValidName("Alice@Johnson");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject empty name")
//     void testInvalidNameEmpty() {
//         // Act
//         boolean result = staff.getValidName("");

//         // Assert
//         assertFalse(result);
//     }

//     // ==================== EMAIL VALIDATION TESTS ====================

//     @Test
//     @DisplayName("Should validate email with correct format")
//     void testValidEmailSuccess() {
//         // Act
//         boolean result = staff.getValidEmail("alice@example.com");

//         // Assert
//         assertTrue(result);
//         assertEquals("alice@example.com", staff.getEmail());
//     }

//     @Test
//     @DisplayName("Should validate email with plus sign")
//     void testValidEmailWithPlus() {
//         // Act
//         boolean result = staff.getValidEmail("alice+test@example.com");

//         // Assert
//         assertTrue(result);
//     }

//     @Test
//     @DisplayName("Should reject email without @ symbol")
//     void testInvalidEmailNoAtSymbol() {
//         // Act
//         boolean result = staff.getValidEmail("aliceexample.com");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject email without domain")
//     void testInvalidEmailNoDomain() {
//         // Act
//         boolean result = staff.getValidEmail("alice@");

//         // Assert
//         assertFalse(result);
//     }

//     // ==================== GENDER VALIDATION TESTS ====================

//     @Test
//     @DisplayName("Should validate gender as MALE")
//     void testValidGenderMale() {
//         // Act
//         boolean result = staff.getValidGender("MALE");

//         // Assert
//         assertTrue(result);
//         assertEquals("MALE", staff.getGender());
//     }

//     @Test
//     @DisplayName("Should validate gender as FEMALE")
//     void testValidGenderFemale() {
//         // Act
//         boolean result = staff.getValidGender("FEMALE");

//         // Assert
//         assertTrue(result);
//         assertEquals("FEMALE", staff.getGender());
//     }

//     @Test
//     @DisplayName("Should validate lowercase gender male")
//     void testValidGenderLowercaseMale() {
//         // Act
//         boolean result = staff.getValidGender("male");

//         // Assert
//         assertTrue(result);
//     }

//     @Test
//     @DisplayName("Should reject invalid gender")
//     void testInvalidGender() {
//         // Act
//         boolean result = staff.getValidGender("Other");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject empty gender")
//     void testInvalidGenderEmpty() {
//         // Act
//         boolean result = staff.getValidGender("");

//         // Assert
//         assertFalse(result);
//     }

//     // ==================== PHONE NUMBER VALIDATION TESTS ====================

//     @Test
//     @DisplayName("Should validate phone number with correct format")
//     void testValidPhoneNumberSuccess() {
//         // Act
//         boolean result = staff.getValidPhoneNumber("012-34567890");

//         // Assert
//         assertTrue(result);
//         assertEquals("012-34567890", staff.getPhoneNumber());
//     }

//     @Test
//     @DisplayName("Should validate phone number with 7 digits after dash")
//     void testValidPhoneNumber7Digits() {
//         // Act
//         boolean result = staff.getValidPhoneNumber("012-1234567");

//         // Assert
//         assertTrue(result);
//     }

//     @Test
//     @DisplayName("Should reject phone number without dash")
//     void testInvalidPhoneNumberNoDash() {
//         // Act
//         boolean result = staff.getValidPhoneNumber("01234567890");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject phone number with wrong prefix format")
//     void testInvalidPhoneNumberWrongPrefix() {
//         // Act
//         boolean result = staff.getValidPhoneNumber("01-34567890");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject empty phone number")
//     void testInvalidPhoneNumberEmpty() {
//         // Act
//         boolean result = staff.getValidPhoneNumber("");

//         // Assert
//         assertFalse(result);
//     }

//     // ==================== LOGIN METHOD TESTS ====================

//     @Test
//     @DisplayName("Should authenticate staff with valid credentials using login method")
//     void testLoginSuccess() {
//         // Arrange
//         staff.setStaffId("S001");
//         staff.setStfPass("12345");

//         // Act
//         boolean result = staff.login("S001", "12345");

//         // Assert
//         assertTrue(result);
//     }

//     @Test
//     @DisplayName("Should reject login with invalid staff ID")
//     void testLoginInvalidStaffId() {
//         // Arrange
//         staff.setStaffId("S001");
//         staff.setStfPass("12345");

//         // Act
//         boolean result = staff.login("S999", "12345");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject login with invalid password")
//     void testLoginInvalidPassword() {
//         // Arrange
//         staff.setStaffId("S001");
//         staff.setStfPass("12345");

//         // Act
//         boolean result = staff.login("S001", "54321");

//         // Assert
//         assertFalse(result);
//     }

//     @Test
//     @DisplayName("Should reject login with both ID and password wrong")
//     void testLoginBothInvalid() {
//         // Arrange
//         staff.setStaffId("S001");
//         staff.setStfPass("12345");

//         // Act
//         boolean result = staff.login("S999", "54321");

//         // Assert
//         assertFalse(result);
//     }

//     // ==================== OBJECT EQUALITY TESTS ====================

//     @Test
//     @DisplayName("Should determine two staff with same data are equal")
//     void testStaffEqualitySuccess() {
//         // Arrange
//         Staff staff1 = new Staff();
//         staff1.setName("Alice Johnson");
//         staff1.setEmail("alice@example.com");
//         staff1.setPhoneNumber("012-12345678");
//         staff1.setGender("Female");

//         Staff staff2 = new Staff();
//         staff2.setName("Alice Johnson");
//         staff2.setEmail("alice@example.com");
//         staff2.setPhoneNumber("012-12345678");
//         staff2.setGender("Female");

//         // Act & Assert
//         assertEquals(staff1, staff2);
//     }

//     @Test
//     @DisplayName("Should determine staff with different emails are not equal")
//     void testStaffInequalityDifferentEmail() {
//         // Arrange
//         Staff staff1 = new Staff();
//         staff1.setName("Alice Johnson");
//         staff1.setEmail("alice@example.com");
//         staff1.setPhoneNumber("012-12345678");
//         staff1.setGender("Female");

//         Staff staff2 = new Staff();
//         staff2.setName("Alice Johnson");
//         staff2.setEmail("bob@example.com");
//         staff2.setPhoneNumber("012-12345678");
//         staff2.setGender("Female");

//         // Act & Assert
//         assertNotEquals(staff1, staff2);
//     }

//     // ==================== CONSTRUCTOR AND GETTERS/SETTERS TESTS
//     // ====================

//     @Test
//     @DisplayName("Should create staff with all-args constructor")
//     void testStaffConstructor() {
//         // Act
//         Staff newStaff = new Staff("Manager", "12345", "Bob Smith", "012-12345678", "Male", "bob@example.com");

//         // Assert
//         assertEquals("Manager", newStaff.getPosition());
//         assertEquals("12345", newStaff.getStfPass());
//         assertEquals("Bob Smith", newStaff.getName());
//         assertEquals("012-12345678", newStaff.getPhoneNumber());
//         assertEquals("Male", newStaff.getGender());
//         assertEquals("bob@example.com", newStaff.getEmail());
//     }

//     @Test
//     @DisplayName("Should create staff with no-args constructor")
//     void testStaffNoArgsConstructor() {
//         // Act
//         Staff newStaff = new Staff();

//         // Assert
//         assertNull(newStaff.getStaffId());
//         assertNull(newStaff.getPosition());
//         assertNull(newStaff.getStfPass());
//     }

//     @Test
//     @DisplayName("Should set and get all staff attributes")
//     void testStaffGettersSetters() {
//         // Arrange
//         staff.setStaffId("S001");
//         staff.setPosition("Manager");
//         staff.setStfPass("12345");
//         staff.setName("Charlie Davis");
//         staff.setPhoneNumber("012-87654321");
//         staff.setGender("Male");
//         staff.setEmail("charlie@example.com");

//         // Act & Assert
//         assertEquals("S001", staff.getStaffId());
//         assertEquals("Manager", staff.getPosition());
//         assertEquals("12345", staff.getStfPass());
//         assertEquals("Charlie Davis", staff.getName());
//         assertEquals("012-87654321", staff.getPhoneNumber());
//         assertEquals("Male", staff.getGender());
//         assertEquals("charlie@example.com", staff.getEmail());
//     }
// }
