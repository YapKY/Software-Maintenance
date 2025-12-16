package com.example.springboot.controller;

import com.example.springboot.model.Customer;
import com.example.springboot.service.CustomerService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        testCustomer = new Customer();
        testCustomer.setCustId("C001");
        testCustomer.setCustIcNo("123456-12-1234");
        testCustomer.setCustPassword("password");
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhoneNumber("0123456789");
        testCustomer.setGender("Male");
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin_Success() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("icNumber", "123456-12-1234");
        credentials.put("password", "password");

        when(customerService.authenticateCustomer("123456-12-1234", "password")).thenReturn(Optional.of(testCustomer));

        mockMvc.perform(post("/api/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Log In Successful"))
                .andExpect(jsonPath("$.customer.custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void testLogin_Failure() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("icNumber", "123456-12-1234");
        credentials.put("password", "wrongpass");

        when(customerService.authenticateCustomer("123456-12-1234", "wrongpass")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Log In Unsuccessful...Please input valid I/C number and Password"));
    }

    @Test
    @DisplayName("Should fail login with missing fields")
    void testLogin_MissingFields() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("icNumber", "123456-12-1234");

        mockMvc.perform(post("/api/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("I/C number and password are required"));
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegister_Success() throws Exception {
        when(customerService.registerCustomer(any(Customer.class))).thenReturn(testCustomer);

        mockMvc.perform(post("/api/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.customer.custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should fail register customer with bad request")
    void testRegister_BadRequest() throws Exception {
        when(customerService.registerCustomer(any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    @DisplayName("Should fail register customer with internal error")
    void testRegister_InternalError() throws Exception {
        when(customerService.registerCustomer(any(Customer.class))).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(post("/api/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Registration failed: DB Error"));
    }

    @Test
    @DisplayName("Should get all customers")
    void testGetAllCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should get customer by ID")
    void testGetCustomerById_Success() throws Exception {
        when(customerService.getCustomerById("C001")).thenReturn(Optional.of(testCustomer));

        mockMvc.perform(get("/api/customers/C001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should return 404 when customer by ID not found")
    void testGetCustomerById_NotFound() throws Exception {
        when(customerService.getCustomerById("C001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/C001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    @Test
    @DisplayName("Should get customer by IC number")
    void testGetCustomerByIcNumber_Success() throws Exception {
        when(customerService.getCustomerByIcNumber("123456-12-1234")).thenReturn(Optional.of(testCustomer));

        mockMvc.perform(get("/api/customers/ic/123456-12-1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should return 404 when customer by IC number not found")
    void testGetCustomerByIcNumber_NotFound() throws Exception {
        when(customerService.getCustomerByIcNumber("123456-12-1234")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/ic/123456-12-1234"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomer_Success() throws Exception {
        when(customerService.updateCustomer(eq("C001"), any(Customer.class))).thenReturn(testCustomer);

        mockMvc.perform(put("/api/customers/C001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should fail update customer with bad request")
    void testUpdateCustomer_BadRequest() throws Exception {
        when(customerService.updateCustomer(eq("C001"), any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/customers/C001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomer_Success() throws Exception {
        doNothing().when(customerService).deleteCustomer("C001");

        mockMvc.perform(delete("/api/customers/C001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));
    }

    @Test
    @DisplayName("Should fail delete customer when not found")
    void testDeleteCustomer_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Customer not found")).when(customerService).deleteCustomer("C001");

        mockMvc.perform(delete("/api/customers/C001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    @Test
    @DisplayName("Should debug get all customers")
    void testDebugGetAllCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers/debug/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @DisplayName("Should get customer by index")
    void testGetCustomerByIndex_Success() throws Exception {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers/index/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custIcNo").value("123456-12-1234"));
    }

    @Test
    @DisplayName("Should fail get customer by index out of range")
    void testGetCustomerByIndex_OutOfRange() throws Exception {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers/index/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer index out of range. Available: 1-1"));
    }

    @Test
    @DisplayName("Should debug create test customer")
    void testDebugCreateTestCustomer_Success() throws Exception {
        when(customerService.registerCustomer(any(Customer.class))).thenReturn(testCustomer);

        mockMvc.perform(post("/api/customers/debug/create-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Test customer created"));
    }

    @Test
    @DisplayName("Should handle error in debug create test customer")
    void testDebugCreateTestCustomer_Error() throws Exception {
        when(customerService.registerCustomer(any(Customer.class))).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(post("/api/customers/debug/create-test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error creating test customer: DB Error"));
    }
}
