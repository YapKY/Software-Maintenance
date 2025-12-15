package com.example.springboot.controller;

import com.example.springboot.model.Customer;
import com.example.springboot.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for CustomerController
 * Tests HTTP endpoints and request/response flows for customer operations
 */
@WebMvcTest(CustomerController.class)
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CustomerService customerService;

        @Autowired
        private ObjectMapper objectMapper;

        private Customer testCustomer;

        @BeforeEach
        void setUp() {
                testCustomer = new Customer();
                testCustomer.setCustId("1");
                testCustomer.setCustIcNo("123456-12-1234");
                testCustomer.setCustPassword("SecurePass123");
                testCustomer.setName("John Doe");
                testCustomer.setEmail("john@example.com");
                testCustomer.setPhoneNumber("0123456789");
                testCustomer.setGender("Male");
        }

        // ==================== LOGIN ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should login customer successfully")
        void testLoginSuccess() throws Exception {
                // Arrange
                when(customerService.authenticateCustomer("123456-12-1234", "SecurePass123"))
                                .thenReturn(Optional.of(testCustomer));

                String loginRequest = "{\"icNumber\": \"123456-12-1234\", \"password\": \"SecurePass123\"}";

                // Act & Assert
                mockMvc.perform(post("/api/customers/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Log In Successful"))
                                .andExpect(jsonPath("$.customer.custIcNo").value("123456-12-1234"))
                                .andExpect(jsonPath("$.customer.name").value("John Doe"));

                verify(customerService, times(1)).authenticateCustomer("123456-12-1234", "SecurePass123");
        }

        @Test
        @DisplayName("Should reject login with invalid credentials")
        void testLoginFailure() throws Exception {
                // Arrange
                when(customerService.authenticateCustomer("123456-12-1234", "WrongPassword"))
                                .thenReturn(Optional.empty());

                String loginRequest = "{\"icNumber\": \"123456-12-1234\", \"password\": \"WrongPassword\"}";

                // Act & Assert
                mockMvc.perform(post("/api/customers/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value(containsString("Unsuccessful")));
        }

        @Test
        @DisplayName("Should reject login with missing credentials")
        void testLoginMissingCredentials() throws Exception {
                // Arrange
                String loginRequest = "{\"icNumber\": \"123456-12-1234\"}";

                // Act & Assert
                mockMvc.perform(post("/api/customers/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false));
        }

        // ==================== REGISTRATION ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should register customer successfully")
        void testRegisterSuccess() throws Exception {
                // Arrange
                Customer newCustomer = new Customer();
                newCustomer.setCustIcNo("654321-21-4321");
                newCustomer.setCustPassword("NewPassword123");
                newCustomer.setName("Jane Smith");
                newCustomer.setEmail("jane@example.com");
                newCustomer.setPhoneNumber("9876543210");
                newCustomer.setGender("Female");

                Customer savedCustomer = new Customer();
                savedCustomer.setCustId("2");
                savedCustomer.setCustIcNo("654321-21-4321");
                savedCustomer.setCustPassword("NewPassword123");
                savedCustomer.setName("Jane Smith");
                savedCustomer.setEmail("jane@example.com");
                savedCustomer.setPhoneNumber("9876543210");
                savedCustomer.setGender("Female");

                when(customerService.registerCustomer(any(Customer.class)))
                                .thenReturn(savedCustomer);

                String registerRequest = objectMapper.writeValueAsString(newCustomer);

                // Act & Assert
                mockMvc.perform(post("/api/customers/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerRequest))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Registration Successful"))
                                .andExpect(jsonPath("$.customer.name").value("Jane Smith"));

                verify(customerService, times(1)).registerCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("Should reject registration with invalid data")
        void testRegisterInvalidData() throws Exception {
                // Arrange
                Customer invalidCustomer = new Customer();
                invalidCustomer.setCustIcNo("INVALID");
                invalidCustomer.setCustPassword("short");
                invalidCustomer.setName("Jane Smith");
                invalidCustomer.setEmail("jane@example.com");
                invalidCustomer.setPhoneNumber("9876543210");
                invalidCustomer.setGender("Female");

                when(customerService.registerCustomer(any(Customer.class)))
                                .thenThrow(new IllegalArgumentException("Invalid I/C number format"));

                String registerRequest = objectMapper.writeValueAsString(invalidCustomer);

                // Act & Assert
                mockMvc.perform(post("/api/customers/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should reject registration with duplicate IC number")
        void testRegisterDuplicateIc() throws Exception {
                // Arrange
                Customer newCustomer = new Customer();
                newCustomer.setCustIcNo("123456-12-1234");
                newCustomer.setCustPassword("NewPassword123");
                newCustomer.setName("Jane Smith");
                newCustomer.setEmail("jane@example.com");
                newCustomer.setPhoneNumber("9876543210");
                newCustomer.setGender("Female");

                when(customerService.registerCustomer(any(Customer.class)))
                                .thenThrow(new IllegalArgumentException("I/C number already exists"));

                String registerRequest = objectMapper.writeValueAsString(newCustomer);

                // Act & Assert
                mockMvc.perform(post("/api/customers/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }

        // ==================== RETRIEVAL ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should retrieve all customers")
        void testGetAllCustomers() throws Exception {
                // Arrange
                List<Customer> customers = Arrays.asList(testCustomer);
                when(customerService.getAllCustomers()).thenReturn(customers);

                // Act & Assert
                mockMvc.perform(get("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name").value("John Doe"));

                verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        @DisplayName("Should retrieve customer by ID")
        void testGetCustomerById() throws Exception {
                // Arrange
                when(customerService.getCustomerById("1")).thenReturn(Optional.of(testCustomer));

                // Act & Assert
                mockMvc.perform(get("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"))
                                .andExpect(jsonPath("$.name").value("John Doe"));

                verify(customerService, times(1)).getCustomerById("1");
        }

        @Test
        @DisplayName("Should return 404 when customer ID not found")
        void testGetCustomerByIdNotFound() throws Exception {
                // Arrange
                when(customerService.getCustomerById("999")).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(get("/api/customers/999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        @DisplayName("Should retrieve customer by IC number")
        void testGetCustomerByIcNumber() throws Exception {
                // Arrange
                when(customerService.getCustomerByIcNumber("123456-12-1234"))
                                .thenReturn(Optional.of(testCustomer));

                // Act & Assert
                mockMvc.perform(get("/api/customers/ic/123456-12-1234")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"))
                                .andExpect(jsonPath("$.name").value("John Doe"));
        }

        // ==================== UPDATE ENDPOINT TESTS (PROFILE) ====================

        @Test
        @DisplayName("Should update customer profile successfully")
        void testUpdateCustomerSuccess() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setName("John Smith");
                updatedData.setEmail("john.smith@example.com");

                Customer updatedCustomer = testCustomer;
                updatedCustomer.setName("John Smith");
                updatedCustomer.setEmail("john.smith@example.com");

                when(customerService.updateCustomer("1", updatedData))
                                .thenReturn(updatedCustomer);

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("John Smith"))
                                .andExpect(jsonPath("$.email").value("john.smith@example.com"));

                verify(customerService, times(1)).updateCustomer("1", updatedData);
        }

        @Test
        @DisplayName("Should update customer profile with all fields")
        void testUpdateCustomerProfile_AllFields() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setCustId("1");
                updatedData.setName("John Smith Updated");
                updatedData.setEmail("john.updated@example.com");
                updatedData.setPhoneNumber("0198765432");
                updatedData.setGender("Male");

                Customer updatedCustomer = new Customer();
                updatedCustomer.setCustId("1");
                updatedCustomer.setCustIcNo("123456-12-1234");
                updatedCustomer.setName("John Smith Updated");
                updatedCustomer.setEmail("john.updated@example.com");
                updatedCustomer.setPhoneNumber("0198765432");
                updatedCustomer.setGender("Male");

                when(customerService.updateCustomer("1", updatedData))
                                .thenReturn(updatedCustomer);

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("John Smith Updated"))
                                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                                .andExpect(jsonPath("$.phoneNumber").value("0198765432"))
                                .andExpect(jsonPath("$.gender").value("Male"));

                verify(customerService, times(1)).updateCustomer("1", updatedData);
        }

        @Test
        @DisplayName("Should update customer profile with partial fields")
        void testUpdateCustomerProfile_PartialFields() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setPhoneNumber("0187654321");

                Customer updatedCustomer = testCustomer;
                updatedCustomer.setPhoneNumber("0187654321");

                when(customerService.updateCustomer("1", updatedData))
                                .thenReturn(updatedCustomer);

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.phoneNumber").value("0187654321"))
                                .andExpect(jsonPath("$.name").value("John Doe")); // Other fields remain

                verify(customerService, times(1)).updateCustomer("1", updatedData);
        }

        @Test
        @DisplayName("Should update customer email in profile")
        void testUpdateCustomerProfile_EmailOnly() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setEmail("newemail@example.com");

                Customer updatedCustomer = testCustomer;
                updatedCustomer.setEmail("newemail@example.com");

                when(customerService.updateCustomer("1", updatedData))
                                .thenReturn(updatedCustomer);

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("newemail@example.com"));

                verify(customerService, times(1)).updateCustomer("1", updatedData);
        }

        @Test
        @DisplayName("Should reject update for non-existent customer")
        void testUpdateCustomerNotFound() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setName("John Smith");

                when(customerService.updateCustomer("999", updatedData))
                                .thenThrow(new IllegalArgumentException("Customer not found"));

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        @DisplayName("Should reject profile update with invalid email format")
        void testUpdateCustomerProfile_InvalidEmail() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setEmail("invalid-email");

                when(customerService.updateCustomer("1", updatedData))
                                .thenThrow(new IllegalArgumentException("Invalid email format"));

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("Invalid email")));
        }

        @Test
        @DisplayName("Should reject profile update with invalid phone number")
        void testUpdateCustomerProfile_InvalidPhone() throws Exception {
                // Arrange
                Customer updatedData = new Customer();
                updatedData.setPhoneNumber("123");

                when(customerService.updateCustomer("1", updatedData))
                                .thenThrow(new IllegalArgumentException("Invalid phone number"));

                String updateRequest = objectMapper.writeValueAsString(updatedData);

                // Act & Assert
                mockMvc.perform(put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(containsString("Invalid phone")));
        }

        // ==================== DELETE ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should delete customer successfully")
        void testDeleteCustomerSuccess() throws Exception {
                // Arrange
                doNothing().when(customerService).deleteCustomer("1");

                // Act & Assert
                mockMvc.perform(delete("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));

                verify(customerService, times(1)).deleteCustomer("1");
        }

        @Test
        @DisplayName("Should reject deletion of non-existent customer")
        void testDeleteCustomerNotFound() throws Exception {
                // Arrange
                doThrow(new IllegalArgumentException("Customer not found"))
                                .when(customerService).deleteCustomer("999");

                // Act & Assert
                mockMvc.perform(delete("/api/customers/999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(containsString("not found")));

                verify(customerService, times(1)).deleteCustomer("999");
        }

        // ==================== DEBUG ENDPOINT TESTS ====================

        @Test
        @DisplayName("Should get all customers via debug endpoint")
        void testDebugGetAllCustomers() throws Exception {
                // Arrange
                List<Customer> customers = Arrays.asList(testCustomer);
                when(customerService.getAllCustomers()).thenReturn(customers);

                // Act & Assert
                mockMvc.perform(get("/api/customers/debug/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.total").value(1))
                                .andExpect(jsonPath("$.customers", hasSize(1)));
        }

        @Test
        @DisplayName("Should get customer by index")
        void testGetCustomerByIndex() throws Exception {
                // Arrange
                List<Customer> customers = Arrays.asList(testCustomer);
                when(customerService.getAllCustomers()).thenReturn(customers);

                // Act & Assert
                mockMvc.perform(get("/api/customers/index/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("John Doe"));
        }

        @Test
        @DisplayName("Should return error for invalid customer index")
        void testGetCustomerByInvalidIndex() throws Exception {
                // Arrange
                List<Customer> customers = Arrays.asList(testCustomer);
                when(customerService.getAllCustomers()).thenReturn(customers);

                // Act & Assert
                mockMvc.perform(get("/api/customers/index/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(containsString("out of range")));
        }

        @Test
        @DisplayName("Should create test customer via debug endpoint")
        void testDebugCreateTestCustomer() throws Exception {
                // Arrange
                Customer testCustomer = new Customer();
                testCustomer.setCustId("test-1");
                testCustomer.setCustIcNo("040225-14-1143");

                when(customerService.registerCustomer(any(Customer.class)))
                                .thenReturn(testCustomer);

                // Act & Assert
                mockMvc.perform(post("/api/customers/debug/create-test")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Test customer created"));
        }

        // ==================== CORS TESTS ====================

        @Test
        @DisplayName("Should allow cross-origin requests")
        void testCorsSupport() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/api/customers")
                                .header("Origin", "http://localhost:3000"))
                                .andExpect(status().isOk());
        }
}
