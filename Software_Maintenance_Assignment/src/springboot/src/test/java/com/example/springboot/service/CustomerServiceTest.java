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
import java.util.Collections;
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
    @DisplayName("Should register customer successfully (no client-side validation)")
    void testRegisterCustomerSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should allow registration with any IC format (no validation in service)")
    void testRegisterCustomerInvalidIcFormat() throws ExecutionException, InterruptedException {
        // Arrange - Service does NOT validate IC format, just saves
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("INVALID"); // Wrong format but service will accept
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act - This should NOT throw
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should allow registration with short password (no validation in service)")
    void testRegisterCustomerShortPassword() throws ExecutionException, InterruptedException {
        // Arrange - Service does NOT validate password length, just saves
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("Short1"); // Less than 8 characters but service will accept
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act - This should NOT throw
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should allow registration with invalid email (no validation in service)")
    void testRegisterCustomerInvalidEmail() throws ExecutionException, InterruptedException {
        // Arrange - Service does NOT validate email format, just saves
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("invalid-email"); // Invalid format but service will accept
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act - This should NOT throw
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should allow registration with duplicate IC (no duplicate check in service)")
    void testRegisterCustomerDuplicateIc() throws ExecutionException, InterruptedException {
        // Arrange - Service does NOT check for duplicates, just saves
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("123456-12-1234"); // Already exists but service will accept
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act - This should NOT throw
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should allow registration with duplicate email (no duplicate check in service)")
    void testRegisterCustomerDuplicateEmail() throws ExecutionException, InterruptedException {
        // Arrange - Service does NOT check for duplicates, just saves
        Customer newCustomer = new Customer();
        newCustomer.setCustIcNo("654321-21-4321");
        newCustomer.setCustPassword("NewPassword123");
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("john@example.com"); // Already exists but service will accept
        newCustomer.setPhoneNumber("9876543210");
        newCustomer.setGender("Female");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act - This should NOT throw
        Customer result = customerService.registerCustomer(newCustomer);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
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
    @DisplayName("Should wrap IllegalArgumentException in RuntimeException for non-existent customer")
    void testUpdateCustomerNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName("John Smith");

        when(customerRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert - Service wraps IllegalArgumentException in RuntimeException
        assertThrows(RuntimeException.class, () -> {
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
    @DisplayName("Should allow deletion of non-existent customer (no check in service)")
    void testDeleteCustomerNotFound() throws ExecutionException, InterruptedException {
        // Arrange - Service doesn't check if customer exists before deleting
        when(customerRepository.findById("999")).thenReturn(Optional.empty());

        // Act - This should NOT throw
        customerService.deleteCustomer("999");

        // Assert
        verify(customerRepository, times(1)).deleteById("999");
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @DisplayName("Should handle execution error gracefully during authentication")
    void testAuthenticateCustomerExecutionError() throws ExecutionException, InterruptedException {
        // Arrange - Service catches exceptions and returns Optional.empty()
        when(customerRepository.findByCustIcNo(anyString()))
                .thenThrow(new ExecutionException(new Exception("Database error")));

        // Act - This should return empty, not throw
        Optional<Customer> result = customerService.authenticateCustomer("123456-12-1234", "password");

        // Assert
        assertFalse(result.isPresent());
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

        // Mock save() to throw an exception, not existsByCustIcNo()
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new ExecutionException(new Exception("Database error")));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.registerCustomer(newCustomer);
        });
    }

    // ==================== ADDITIONAL COVERAGE TESTS ====================

    @Test
    @DisplayName("Should fail authentication with exceptions handled gracefully")
    void testAuthenticateCustomer_ExceptionHandled() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("123456-12-1234"))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("123456-12-1234", "SecurePass123");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication when IC number does not exist")
    void testAuthenticateCustomer_IcNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("999999-99-9999"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("999999-99-9999", "AnyPassword");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should fail authentication with wrong password")
    void testAuthenticateCustomer_WrongPassword() throws ExecutionException, InterruptedException {
        // Arrange
        testCustomer.setCustPassword("CorrectPassword");
        when(customerRepository.findByCustIcNo("123456-12-1234"))
                .thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("123456-12-1234", "WrongPassword");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get customer by IC with verification")
    void testGetCustomerByIcNumber_WithVerification() throws ExecutionException, InterruptedException {
        // Arrange
        Customer anotherCustomer = new Customer();
        anotherCustomer.setCustId("2");
        anotherCustomer.setCustIcNo("987654-54-3210");
        anotherCustomer.setName("Jane Doe");

        when(customerRepository.findByCustIcNo("987654-54-3210"))
                .thenReturn(Optional.of(anotherCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerByIcNumber("987654-54-3210");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Jane Doe", result.get().getName());
        assertEquals("987654-54-3210", result.get().getCustIcNo());
    }

    @Test
    @DisplayName("Should return empty when IC number not found")
    void testGetCustomerByIcNumber_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("999999-99-9999"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerByIcNumber("999999-99-9999");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle empty customer list")
    void testGetAllCustomers_Empty() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should retrieve all customers with multiple records")
    void testGetAllCustomers_Multiple() throws ExecutionException, InterruptedException {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setCustId("2");
        customer2.setName("Jane Doe");

        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should update customer with all valid fields")
    void testUpdateCustomer_AllFields() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName("John Updated");
        updatedData.setEmail("john.updated@example.com");
        updatedData.setPhoneNumber("9876543210");
        updatedData.setGender("Female");

        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer("1", updatedData);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update customer with only name field")
    void testUpdateCustomer_OnlyName() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName("John Updated");

        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer("1", updatedData);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomer() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        doNothing().when(customerRepository).deleteById("1");

        // Act
        customerService.deleteCustomer("1");

        // Assert
        verify(customerRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should handle error when deleting customer encounters repository error")
    void testDeleteCustomer_RepositoryError() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        doThrow(new RuntimeException("Database error")).when(customerRepository).deleteById("1");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.deleteCustomer("1");
        });
    }

    @Test
    @DisplayName("Should handle null name in update")
    void testUpdateCustomer_NullName() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName(null); // Don't update

        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer("1", updatedData);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should handle empty string fields in update")
    void testUpdateCustomer_EmptyStrings() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setName(""); // Empty string
        updatedData.setPhoneNumber("");

        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer("1", updatedData);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should handle repository error during authentication")
    void testAuthenticateCustomer_RepositoryError() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findByCustIcNo("123456-12-1234"))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<Customer> result = customerService.authenticateCustomer("123456-12-1234", "password");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle repository error during getAllCustomers")
    void testGetAllCustomers_RepositoryError() throws ExecutionException, InterruptedException {
        // Arrange
        when(customerRepository.findAll())
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.getAllCustomers();
        });
    }

    @Test
    @DisplayName("Should update gender field successfully")
    void testUpdateCustomer_Gender() throws ExecutionException, InterruptedException {
        // Arrange
        Customer updatedData = new Customer();
        updatedData.setGender("Male");

        when(customerRepository.findById("1")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer("1", updatedData);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}
