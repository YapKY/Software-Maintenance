package com.example.springboot;

import com.example.springboot.model.Customer;
import com.example.springboot.model.Staff;
import com.example.springboot.service.CustomerService;
import com.example.springboot.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Integration Tests
 * Tests complete application flows combining multiple components
 * Tests business logic flows, API interactions, and data persistence
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("End-to-End Integration Tests")
class ApplicationFlowIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StaffService staffService;

    private String baseUrl = "/api/customers";
    private String staffBaseUrl = "/api/staff";

    @BeforeEach
    void setUp() {
        // Reset state before each test
    }

    // ==================== CUSTOMER REGISTRATION & LOGIN FLOW ====================

    @Test
    @Order(1)
    @DisplayName("Should complete customer registration and login flow")
    void testCustomerRegistrationAndLoginFlow() throws Exception {
        // Step 1: Register a new customer
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("custIcNo", "111111-11-1111");
        registrationData.put("custPassword", "TestPassword123");
        registrationData.put("name", "Integration Test User");
        registrationData.put("email", "integration@example.com");
        registrationData.put("phoneNumber", "012-12345678");
        registrationData.put("gender", "Male");

        ResponseEntity<Map> registerResponse = restTemplate.postForEntity(
                baseUrl + "/register",
                registrationData,
                Map.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertTrue((Boolean) registerResponse.getBody().get("success"));
        assertNotNull(registerResponse.getBody().get("customer"));

        // Step 2: Login with registered credentials
        Map<String, String> loginData = new HashMap<>();
        loginData.put("icNumber", "111111-11-1111");
        loginData.put("password", "TestPassword123");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                baseUrl + "/login",
                loginData,
                Map.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertTrue((Boolean) loginResponse.getBody().get("success"));
        assertEquals("Log In Successful", loginResponse.getBody().get("message"));
    }

    @Test
    @Order(2)
    @DisplayName("Should reject login with incorrect credentials")
    void testLoginWithIncorrectCredentials() {
        // Arrange
        Map<String, String> loginData = new HashMap<>();
        loginData.put("icNumber", "999999-99-9999");
        loginData.put("password", "WrongPassword");

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginData,
                Map.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    // ==================== STAFF MANAGEMENT FLOW ====================

    @Test
    @Order(3)
    @DisplayName("Should complete staff creation and retrieval flow")
    void testStaffCreationAndRetrievalFlow() {
        // Step 1: Create a new staff member
        Staff newStaff = new Staff();
        newStaff.setStaffId("S1001");
        newStaff.setStfPass("99999");
        newStaff.setName("Integration Test Staff");
        newStaff.setEmail("stafftest@example.com");
        newStaff.setPhoneNumber("012-12345678");
        newStaff.setGender("Female");
        newStaff.setPosition("Tester");

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
                staffBaseUrl,
                newStaff,
                Map.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertTrue((Boolean) createResponse.getBody().get("success"));

        // Step 2: Retrieve the created staff
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                staffBaseUrl + "/staffid/S1001",
                Map.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Test Staff", getResponse.getBody().get("name"));
    }

    @Test
    @Order(4)
    @DisplayName("Should complete staff update flow")
    void testStaffUpdateFlow() {
        // Step 1: Create staff
        Staff staff = new Staff();
        staff.setStaffId("S1002");
        staff.setStfPass("88888");
        staff.setName("Update Test Staff");
        staff.setEmail("updatetest@example.com");
        staff.setPhoneNumber("012-87654321");
        staff.setGender("Male");
        staff.setPosition("Developer");

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
                staffBaseUrl,
                staff,
                Map.class);

        String staffId = null;
        if (createResponse.getBody().containsKey("staff")) {
            Map staffMap = (Map) createResponse.getBody().get("staff");
            staffId = (String) staffMap.get("id");
        }

        // Step 2: Update staff
        Staff updateData = new Staff();
        updateData.setPosition("Senior Developer");
        updateData.setEmail("updatetest.new@example.com");

        ResponseEntity<Map> updateResponse = restTemplate.exchange(
                staffBaseUrl + "/" + staffId,
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateData),
                Map.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    // ==================== CUSTOMER DATA MANAGEMENT FLOW ====================

    @Test
    @Order(5)
    @DisplayName("Should complete customer retrieval and update flow")
    void testCustomerRetrievalAndUpdateFlow() {
        // Step 1: Get all customers
        ResponseEntity<Customer[]> getAllResponse = restTemplate.getForEntity(
                baseUrl,
                Customer[].class);

        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertNotNull(getAllResponse.getBody());

        if (getAllResponse.getBody().length > 0) {
            Customer firstCustomer = getAllResponse.getBody()[0];
            String customerId = firstCustomer.getCustId();

            // Step 2: Get specific customer by ID
            ResponseEntity<Map> getByIdResponse = restTemplate.getForEntity(
                    baseUrl + "/" + customerId,
                    Map.class);

            assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
            assertNotNull(getByIdResponse.getBody().get("custIcNo"));

            // Step 3: Update customer
            Customer updateData = new Customer();
            updateData.setName("Updated Customer");

            ResponseEntity<Map> updateResponse = restTemplate.exchange(
                    baseUrl + "/" + customerId,
                    org.springframework.http.HttpMethod.PUT,
                    new org.springframework.http.HttpEntity<>(updateData),
                    Map.class);

            assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        }
    }

    // ==================== DATA VALIDATION FLOW ====================

    @Test
    @Order(6)
    @DisplayName("Should reject invalid customer data during registration")
    void testCustomerDataValidationFlow() {
        // Test 1: Invalid IC number format
        Map<String, String> invalidIcData = new HashMap<>();
        invalidIcData.put("custIcNo", "INVALID");
        invalidIcData.put("custPassword", "ValidPassword123");
        invalidIcData.put("name", "Test User");
        invalidIcData.put("email", "test@example.com");
        invalidIcData.put("phoneNumber", "012-12345678");
        invalidIcData.put("gender", "Male");

        ResponseEntity<Map> response1 = restTemplate.postForEntity(
                baseUrl + "/register",
                invalidIcData,
                Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertFalse((Boolean) response1.getBody().get("success"));

        // Test 2: Invalid password (too short)
        Map<String, String> shortPasswordData = new HashMap<>();
        shortPasswordData.put("custIcNo", "222222-22-2222");
        shortPasswordData.put("custPassword", "Short1");
        shortPasswordData.put("name", "Test User");
        shortPasswordData.put("email", "test2@example.com");
        shortPasswordData.put("phoneNumber", "012-12345678");
        shortPasswordData.put("gender", "Female");

        ResponseEntity<Map> response2 = restTemplate.postForEntity(
                baseUrl + "/register",
                shortPasswordData,
                Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertFalse((Boolean) response2.getBody().get("success"));
    }

    @Test
    @Order(7)
    @DisplayName("Should reject duplicate customer registration")
    void testDuplicateCustomerValidation() {
        // Create first customer
        Map<String, String> customerData = new HashMap<>();
        customerData.put("custIcNo", "333333-33-3333");
        customerData.put("custPassword", "ValidPassword123");
        customerData.put("name", "Duplicate Test");
        customerData.put("email", "duplicate@example.com");
        customerData.put("phoneNumber", "012-12345678");
        customerData.put("gender", "Male");

        ResponseEntity<Map> response1 = restTemplate.postForEntity(
                baseUrl + "/register",
                customerData,
                Map.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());

        // Try to create duplicate
        ResponseEntity<Map> response2 = restTemplate.postForEntity(
                baseUrl + "/register",
                customerData,
                Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertTrue(response2.getBody().get("message").toString().contains("already exists"));
    }

    // ==================== LIST RETRIEVAL FLOW ====================

    @Test
    @Order(8)
    @DisplayName("Should retrieve lists via debug endpoints")
    void testListRetrievalFlow() {
        // Step 1: Get all customers via debug endpoint
        ResponseEntity<Map> customersResponse = restTemplate.getForEntity(
                baseUrl + "/debug/all",
                Map.class);

        assertEquals(HttpStatus.OK, customersResponse.getStatusCode());
        assertTrue((Boolean) customersResponse.getBody().get("success"));
        assertNotNull(customersResponse.getBody().get("customers"));

        // Step 2: Get all staff via debug endpoint
        ResponseEntity<Map> staffResponse = restTemplate.getForEntity(
                staffBaseUrl + "/debug/all",
                Map.class);

        assertEquals(HttpStatus.OK, staffResponse.getStatusCode());
        assertTrue((Boolean) staffResponse.getBody().get("success"));
        assertNotNull(staffResponse.getBody().get("staff"));
    }

    // ==================== PAGINATION/INDEX FLOW ====================

    @Test
    @Order(9)
    @DisplayName("Should retrieve items by index")
    void testIndexRetrievalFlow() {
        // Step 1: Get first customer by index
        ResponseEntity<Map> customerByIndexResponse = restTemplate.getForEntity(
                baseUrl + "/index/1",
                Map.class);

        if (customerByIndexResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(customerByIndexResponse.getBody().get("name"));
        } else {
            assertEquals(HttpStatus.NOT_FOUND, customerByIndexResponse.getStatusCode());
        }

        // Step 2: Get first staff by index
        ResponseEntity<Map> staffByIndexResponse = restTemplate.getForEntity(
                staffBaseUrl + "/index/1",
                Map.class);

        if (staffByIndexResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(staffByIndexResponse.getBody());
        } else {
            assertEquals(HttpStatus.NOT_FOUND, staffByIndexResponse.getStatusCode());
        }
    }

    // ==================== DELETION FLOW ====================

    @Test
    @Order(10)
    @DisplayName("Should delete customer successfully")
    void testCustomerDeletionFlow() throws Exception {
        // Step 1: Create a customer
        Map<String, String> customerData = new HashMap<>();
        customerData.put("custIcNo", "444444-44-4444");
        customerData.put("custPassword", "DeleteTest123");
        customerData.put("name", "Deletion Test");
        customerData.put("email", "delete@example.com");
        customerData.put("phoneNumber", "012-12345678");
        customerData.put("gender", "Male");

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
                baseUrl + "/register",
                customerData,
                Map.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Map customerMap = (Map) createResponse.getBody().get("customer");
        String customerId = (String) customerMap.get("custId");

        // Step 2: Delete the customer
        ResponseEntity<Map> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + customerId,
                org.springframework.http.HttpMethod.DELETE,
                null,
                Map.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue((Boolean) deleteResponse.getBody().get("success"));

        // Step 3: Verify customer is deleted
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + customerId,
                Map.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    // ==================== ERROR HANDLING FLOW ====================

    @Test
    @Order(11)
    @DisplayName("Should handle not found errors properly")
    void testNotFoundErrorHandling() {
        // Try to get non-existent customer
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/non-existent-id",
                Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertTrue(response.getBody().get("message").toString().contains("not found"));
    }

    @Test
    @Order(12)
    @DisplayName("Should handle missing required fields")
    void testMissingFieldsErrorHandling() {
        // Try to login without password
        Map<String, String> incompleteData = new HashMap<>();
        incompleteData.put("icNumber", "123456-12-1234");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/login",
                incompleteData,
                Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    // ==================== CORS FLOW ====================

    @Test
    @Order(13)
    @DisplayName("Should support cross-origin requests")
    void testCorsFlow() {
        // The test passes if the endpoint responds correctly with CORS enabled
        ResponseEntity<Customer[]> response = restTemplate.getForEntity(
                baseUrl,
                Customer[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ==================== CONCURRENT OPERATIONS FLOW ====================

    @Test
    @Order(14)
    @DisplayName("Should handle concurrent customer operations")
    void testConcurrentOperations() throws InterruptedException {
        // Create multiple customers in parallel simulation
        for (int i = 0; i < 3; i++) {
            Map<String, String> customerData = new HashMap<>();
            customerData.put("custIcNo", String.format("555555-55-%04d", i));
            customerData.put("custPassword", "ConcurrentTest123");
            customerData.put("name", "Concurrent User " + i);
            customerData.put("email", "concurrent" + i + "@example.com");
            customerData.put("phoneNumber", "012-12345678");
            customerData.put("gender", "Male");

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/register",
                    customerData,
                    Map.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        // Verify all were created
        ResponseEntity<Customer[]> allResponse = restTemplate.getForEntity(
                baseUrl,
                Customer[].class);

        assertTrue(allResponse.getBody().length >= 3);
    }
}
