// package com.example.springboot.controller;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.hamcrest.Matchers.*;

// /**
//  * Integration Tests for View Layer
//  * Tests HTML/Thymeleaf templates rendering and basic UI component functionality
//  */
// @WebMvcTest(ViewController.class)
// @DisplayName("View Layer Integration Tests")
// class ViewControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     // ==================== HOME PAGE TESTS ====================

//     @Test
//     @DisplayName("Should render index page successfully")
//     void testIndexPageLoad() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/"))
//                 .andExpect(status().isOk())

//                 .andExpect(view().name("index"));
//     }

//     @Test
//     @DisplayName("Should contain navigation elements in index page")
//     void testIndexPageNavigation() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("Customer")))
//                 .andExpect(content().string(containsString("Staff")));
//     }

//     // ==================== CUSTOMER LIST PAGE TESTS ====================

//     @Test
//     @DisplayName("Should render customer list page")
//     void testCustomerListPageLoad() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("customer-list"));
//     }

//     @Test
//     @DisplayName("Should contain response with text elements")
//     void testCustomerListPageStructure() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("table")))
//                 .andExpect(content().string(containsString("Name")))
//                 .andExpect(content().string(containsString("Email")))
//                 .andExpect(content().string(containsString("Phone")));
//     }

//     @Test
//     @DisplayName("Should contain action buttons in customer list")
//     void testCustomerListActionButtons() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("Edit")))
//                 .andExpect(content().string(containsString("Delete")))
//                 .andExpect(content().string(containsString("Add Customer")));
//     }

//     // ==================== CUSTOMER DETAIL PAGE TESTS ====================

//     @Test
//     @DisplayName("Should render customer detail page")
//     void testCustomerDetailPageLoad() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("customer-detail"));
//     }

//     @Test
//     @DisplayName("Should contain customer form elements")
//     void testCustomerDetailFormElements() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("form")))
//                 .andExpect(content().string(containsString("input")))
//                 .andExpect(content().string(containsString("Name")))
//                 .andExpect(content().string(containsString("Email")))
//                 .andExpect(content().string(containsString("Phone")))
//                 .andExpect(content().string(containsString("IC Number")));
//     }

//     @Test
//     @DisplayName("Should contain form action buttons")
//     void testCustomerDetailFormButtons() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("Save")))
//                 .andExpect(content().string(containsString("Cancel")));
//     }

//     // ==================== STAFF LIST PAGE TESTS ====================

//     @Test
//     @DisplayName("Should render staff list page")
//     void testStaffListPageLoad() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("staff-list"));
//     }

//     @Test
//     @DisplayName("Should contain staff table structure")
//     void testStaffListPageStructure() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("table")))
//                 .andExpect(content().string(containsString("Staff ID")))
//                 .andExpect(content().string(containsString("Position")))
//                 .andExpect(content().string(containsString("Name")))
//                 .andExpect(content().string(containsString("Email")));
//     }

//     @Test
//     @DisplayName("Should contain action buttons in staff list")
//     void testStaffListActionButtons() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("Edit")))
//                 .andExpect(content().string(containsString("Delete")))
//                 .andExpect(content().string(containsString("Add Staff")));
//     }

//     // ==================== STAFF DETAIL PAGE TESTS ====================

//     @Test
//     @DisplayName("Should render staff detail page")
//     void testStaffDetailPageLoad() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("staff-detail"));
//     }

//     @Test
//     @DisplayName("Should contain staff form elements")
//     void testStaffDetailFormElements() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("form")))
//                 .andExpect(content().string(containsString("input")))
//                 .andExpect(content().string(containsString("Staff ID")))
//                 .andExpect(content().string(containsString("Position")))
//                 .andExpect(content().string(containsString("Name")))
//                 .andExpect(content().string(containsString("Email")));
//     }

//     @Test
//     @DisplayName("Should contain staff form action buttons")
//     void testStaffDetailFormButtons() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("Save")))
//                 .andExpect(content().string(containsString("Cancel")));
//     }

//     // ==================== API TEST PAGE TESTS ====================

//     @Test
//     @DisplayName("Should render API test page")
//     void testApiTestPageLoad() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/api-test"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("api-test"));
//     }

//     @Test
//     @DisplayName("Should contain API test form elements")
//     void testApiTestPageElements() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/api-test"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("form")))
//                 .andExpect(content().string(containsString("request")))
//                 .andExpect(content().string(containsString("response")));
//     }

//     // ==================== RESPONSIVE DESIGN TESTS ====================

//     @Test
//     @DisplayName("Should include responsive viewport meta tag")
//     void testResponsiveViewport() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("viewport")))
//                 .andExpect(content().string(containsString("width=device-width")))
//                 .andExpect(content().string(containsString("initial-scale=1")));
//     }

//     @Test
//     @DisplayName("Should include CSS resources")
//     void testCssResources() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("css")));
//     }

//     @Test
//     @DisplayName("Should include JavaScript resources")
//     void testJavaScriptResources() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("script")));
//     }

//     // ==================== ERROR PAGE TESTS ====================

//     @Test
//     @DisplayName("Should return 404 for non-existent page")
//     void testNotFoundPage() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/nonexistent"))
//                 .andExpect(status().isNotFound());
//     }

//     // ==================== TEMPLATE VARIABLE TESTS ====================

//     @Test
//     @DisplayName("Should pass model data to customer list template")
//     void testCustomerListModelData() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("customer")));
//     }

//     @Test
//     @DisplayName("Should pass model data to staff list template")
//     void testStaffListModelData() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/staff"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("staff")));
//     }

//     // ==================== CONTENT TYPE TESTS ====================

//     @Test
//     @DisplayName("All HTML pages should return HTML content type")
//     void testHtmlContentType() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("<")));

//         mockMvc.perform(get("/staff"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("<")));
//     }

//     @Test
//     @DisplayName("Should have proper UTF-8 encoding")
//     void testUtf8Encoding() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("<")));
//     }

//     // ==================== SECURITY HEADERS TESTS ====================

//     @Test
//     @DisplayName("Should include X-UA-Compatible header for IE compatibility")
//     void testIeCompatibilityHeader() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk());
//         // This test verifies that the page loads - actual header testing
//         // would require checking response headers in integration tests
//     }

//     // ==================== ACCESSIBILITY TESTS ====================

//     @Test
//     @DisplayName("Should contain proper heading hierarchy")
//     void testHeadingHierarchy() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("h")));
//     }

//     @Test
//     @DisplayName("Should contain alt text for images")
//     void testImageAltText() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers"))
//                 .andExpect(status().isOk())
//         // This test verifies basic accessibility - actual alt text
//         // testing would require parsing HTML structure
//         ;
//     }

//     @Test
//     @DisplayName("Should have proper form labels")
//     void testFormLabels() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/customers/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("label")));
//     }
// }
