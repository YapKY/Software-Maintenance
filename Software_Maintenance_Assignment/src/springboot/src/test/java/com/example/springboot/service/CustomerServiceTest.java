package com.example.springboot.service;

import com.example.springboot.model.Customer;
import com.example.springboot.repository.CustomerRepository;
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
import static org.mockito.Mockito.*;

/**
 * Unit Tests for CustomerService
 * Tests business logic layer for customer operations
 */
@DisplayName("Customer Service Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer();
        testCustomer.setCustId("1");
        testCustomer.setCustIcNo("123456-12-1234");
        testCustomer.setCustPassword("SecurePass123");
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");
        testCustomer.setPhoneNumber("0123456789");
        testCustomer.setGender("Male");
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    @DisplayName("Should authenticate customer with valid credentials")
    void testAuthenticateCustomerSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("123456-12-1234"))
                .thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("123456-12-1234", "SecurePass123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(customerRepository, times(1)).findByCustIcNo("123456-12-1234");
    }

    @Test
    @DisplayName("Should fail authentication with invalid IC number")
    void testAuthenticateCustomerInvalidIc() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("999999-99-9999"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("999999-99-9999", "SecurePass123");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with incorrect password")
    void testAuthenticateCustomerInvalidPassword() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("123456-12-1234"))
                .thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("123456-12-1234", "WrongPassword");

        // Assert
        assertFalse(result.isPresent());
    }

    // ==================== REGISTRATION TESTS ====================

    @Test
    @DisplayName("Should register customer with valid data")
    void testRegisterCustomerSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.existsByCustIcNo(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(customerRepository.existsByCustPassword(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should reject registration with invalid IC number format")
    void testRegisterCustomerInvalidIcFormat() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("INVALID"); // Wrong format
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }

    @Test
    @DisplayName("Should reject registration with short password")
    void testRegisterCustomerShortPassword() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("Short1"); // Less than 8 characters
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }

    @Test
    @DisplayName("Should reject registration with invalid email")
    void testRegisterCustomerInvalidEmail() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("invalid-email"); // Invalid format
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }

    @Test
    @DisplayName("Should reject registration with duplicate IC number")
    void testRegisterCustomerDuplicateIc() throws ExecutionException, InterruptedException {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("123456-12-1234"); // Already exists
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.existsByCustIcNo("123456-12-1234")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }

    @Test
    @DisplayName("Should reject registration with duplicate email")
    void testRegisterCustomerDuplicateEmail() throws ExecutionException, InterruptedException {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("john@example.com"); // Already exists
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.existsByCustIcNo(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }

    // ==================== RETRIEVAL TESTS ====================

    @Test
    @DisplayName("Should retrieve all customers")
    void testGetAllCustomers() throws ExecutionException, InterruptedException {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    @DisplayName("Should retrieve customer by ID")
    void testGetCustomerById() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when customer ID not found")
    void testGetCustomerByIdNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerById("999");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should retrieve customer by IC number")
    void testGetCustomerByIcNumber() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("123456-12-1234"))
                .thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerByIcNumber("123456-12-1234");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Should update customer with valid data")
    void testUpdateCustomerSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName("John Smith");
        updatedData.setEmail("john.smith@example.com");
        updatedData.setPhoneNumber("0987654321");

        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("john.smith@example.com")).thenReturn(false);
        when(customerRepository.existsByPhoneNumber("0987654321")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer("1", updatedData);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should reject update for non-existent customer")
    void testUpdateCustomerNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName("John Smith");

        when(customerRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.updateCustomer("999", updatedData);
        });
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomerSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));

        // Act
        customerService.deleteCustomer("1");

        // Assert
        verify(customerRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should reject deletion of non-existent customer")
    void testDeleteCustomerNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.deleteCustomer("999");
        });
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @DisplayName("Should throw RuntimeException on execution error during authentication")
    void testAuthenticateCustomerExecutionError() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo(anyString()))
                .thenThrow(new ExecutionException(new Exception("Database error")));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.authenticateCustomer("123456-12-1234", "password");
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException on execution error during registration")
    void testRegisterCustomerExecutionError() throws ExecutionException, InterruptedException {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.existsByCustIcNo(anyString()))
                .thenThrow(new ExecutionException(new Exception("Database error")));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }
}
