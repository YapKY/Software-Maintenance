package com.example.springboot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ViewControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ViewController viewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(viewController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("Should show index page")
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Should show generic page")
    void testShowPage() throws Exception {
        mockMvc.perform(get("/pages/login.html"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/login"));

        mockMvc.perform(get("/pages/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/dashboard"));
    }

    @Test
    @DisplayName("Should show customer profile page")
    void testCustomerProfile() throws Exception {
        mockMvc.perform(get("/customer-profile").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-profile"))
                .andExpect(model().attribute("customerId", 1));
    }

    @Test
    @DisplayName("Should show staff profile page")
    void testStaffProfile() throws Exception {
        mockMvc.perform(get("/staff-profile").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-profile"))
                .andExpect(model().attribute("staffId", 1));
    }

    @Test
    @DisplayName("Should show customer profile by ID")
    void testCustomerProfileById() throws Exception {
        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-profile"))
                .andExpect(model().attribute("customerId", 1));
    }

    @Test
    @DisplayName("Should show staff profile by ID")
    void testStaffProfileById() throws Exception {
        mockMvc.perform(get("/staff/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-profile"))
                .andExpect(model().attribute("staffId", 1));
    }

    @Test
    @DisplayName("Should show customer list page")
    void testCustomerList() throws Exception {
        mockMvc.perform(get("/customer-list"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-list"));
    }

    @Test
    @DisplayName("Should show staff list page")
    void testStaffList() throws Exception {
        mockMvc.perform(get("/staff-list"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-list"));
    }

    @Test
    @DisplayName("Should show API test page")
    void testApiTest() throws Exception {
        mockMvc.perform(get("/api-test"))
                .andExpect(status().isOk())
                .andExpect(view().name("api-test"));
    }

    @Test
    @DisplayName("Should show customer detail page")
    void testCustomerDetail() throws Exception {
        mockMvc.perform(get("/customer-detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-detail"));
    }

    @Test
    @DisplayName("Should redirect customer detail page if email present")
    void testCustomerDetail_Redirect() throws Exception {
        mockMvc.perform(get("/customer-detail").param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer-list"));
    }

    @Test
    @DisplayName("Should show staff detail page")
    void testStaffDetail() throws Exception {
        mockMvc.perform(get("/staff-detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff-detail"));
    }

    @Test
    @DisplayName("Should redirect staff detail page if email present")
    void testStaffDetail_Redirect() throws Exception {
        mockMvc.perform(get("/staff-detail").param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff-list"));
    }

    @Test
    @DisplayName("Should show booking page")
    void testBookingPage() throws Exception {
        mockMvc.perform(get("/booking")
                .param("flightId", "F001")
                .param("customerId", "C001"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking"))
                .andExpect(model().attribute("flightId", "F001"))
                .andExpect(model().attribute("customerId", "C001"));
    }

    @Test
    @DisplayName("Should show payment page")
    void testPaymentPage() throws Exception {
        mockMvc.perform(get("/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"));
    }

    @Test
    @DisplayName("Should show confirmation page")
    void testConfirmationPage() throws Exception {
        mockMvc.perform(get("/confirmation"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmation"));
    }

    @Test
    @DisplayName("Should show search flight page")
    void testSearchFlightPage() throws Exception {
        mockMvc.perform(get("/search-flight"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-flight"));
    }

    @Test
    @DisplayName("Should show my tickets page")
    void testMyTicketsPage() throws Exception {
        mockMvc.perform(get("/my-tickets").param("customerId", "C001"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-tickets"))
                .andExpect(model().attribute("customerId", "C001"));
    }
}
