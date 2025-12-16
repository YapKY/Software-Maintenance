// package com.example.springboot.config;

// import com.example.springboot.controller.RegistrationController;
// import com.example.springboot.security.jwt.JwtAuthenticationFilter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.io.IOException;

// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doAnswer;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Import(SecurityConfigTest.TestSecurityController.class)
// class SecurityConfigTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private AuthenticationManager authenticationManager;

//     @MockBean
//     private JwtAuthenticationFilter jwtAuthenticationFilter;

//     // Mocking the controller avoids 500 errors caused by missing dependencies in the controller layer.
//     // We only care that the request reaches the controller (passing security), not what the controller does.
//     @MockBean
//     private RegistrationController registrationController;

//     @BeforeEach
//     void setUp() throws ServletException, IOException {
//         // Stub the filter to allow the request to proceed down the chain
//         doAnswer(invocation -> {
//             ServletRequest request = invocation.getArgument(0);
//             ServletResponse response = invocation.getArgument(1);
//             FilterChain chain = invocation.getArgument(2);
//             chain.doFilter(request, response);
//             return null;
//         }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
//     }

//     // ==========================================
//     // Bean Existence Tests
//     // ==========================================

//     @Test
//     void testAuthenticationManagerBeanExists() {
//         assertNotNull(authenticationManager, "AuthenticationManager bean should be created");
//     }

//     // ==========================================
//     // Public Endpoint Tests (permitAll)
//     // ==========================================

//     @Test
//     void testPublicResources_Accessible() throws Exception {
//         // Accessing CSS (Should be 404 because file doesn't exist in mock context, but NOT 401/403)
//         mockMvc.perform(get("/css/style.css").secure(true))
//                 .andExpect(status().isNotFound());
        
//         mockMvc.perform(get("/js/app.js").secure(true))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testPublicPages_Accessible() throws Exception {
//         // These templates exist, so we expect 200 OK
//         mockMvc.perform(get("/pages/login.html").secure(true))
//                 .andExpect(status().isOk());

//         mockMvc.perform(get("/pages/register.html").secure(true))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testPublicApi_Accessible() throws Exception {
//         // Ping a non-existent auth endpoint to verify access is allowed (404) rather than denied (401/403)
//         mockMvc.perform(post("/api/auth/ping").secure(true))
//                 .andExpect(status().isNotFound());
//     }

//     // ==========================================
//     // HTTPS Enforcement Tests
//     // ==========================================

//     @Test
//     void testRequiresHttps_RedirectsHttp() throws Exception {
//         mockMvc.perform(get("/pages/login.html").secure(false)) // HTTP request
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("https://localhost/pages/login.html"));
//     }

//     // ==========================================
//     // Header Configuration Tests
//     // ==========================================

//     @Test
//     void testSecurityHeaders() throws Exception {
//         mockMvc.perform(get("/pages/login.html").secure(true))
//                 .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
//                 .andExpect(header().string("Permissions-Policy", "camera=(), microphone=(), geolocation=()"))
//                 .andExpect(header().string("Cross-Origin-Opener-Policy", "same-origin-allow-popups"));
//     }

//     // ==========================================
//     // Exception Handling: AuthenticationEntryPoint (401)
//     // ==========================================

//     @Test
//     void testUnauthenticated_ApiEndpoint_Returns401Json() throws Exception {
//         // Accessing a protected API endpoint without a user
//         mockMvc.perform(get("/api/dashboard/user/profile").secure(true))
//                 .andExpect(status().isUnauthorized()) // 401
//                 .andExpect(content().contentType("application/json"))
//                 .andExpect(jsonPath("$.error").value("Unauthorized"))
//                 .andExpect(jsonPath("$.message").value("Authentication required"));
//     }

//     @Test
//     void testUnauthenticated_WebEndpoint_RedirectsToLogin() throws Exception {
//         // Accessing a protected Page endpoint without a user
//         mockMvc.perform(get("/pages/user-dashboard.html").secure(true))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/pages/login.html?error=unauthorized"));
//     }

//     // ==========================================
//     // Exception Handling: AccessDeniedHandler (403)
//     // ==========================================

//     @Test
//     @WithMockUser(roles = "USER")
//     void testAccessDenied_ApiEndpoint_Returns403Json() throws Exception {
//         // USER trying to access ADMIN API
//         mockMvc.perform(get("/api/dashboard/admin/stats").secure(true))
//                 .andExpect(status().isForbidden()) // 403
//                 .andExpect(content().contentType("application/json"))
//                 .andExpect(jsonPath("$.error").value("Forbidden"))
//                 .andExpect(jsonPath("$.message").value("Access denied"));
//     }

//     @Test
//     @WithMockUser(roles = "USER")
//     void testAccessDenied_WebEndpoint_RedirectsToLogin() throws Exception {
//         // USER trying to access a method-protected Web endpoint
//         mockMvc.perform(get("/pages/protected-view").secure(true))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/pages/login.html?error=access_denied"));
//     }

//     // ==========================================
//     // Role-Based Access Control (RBAC) Tests
//     // ==========================================

//     @Test
//     @WithMockUser(roles = "USER")
//     void testUserRole_CanAccessUserDashboard() throws Exception {
//         mockMvc.perform(get("/api/dashboard/user/overview").secure(true))
//                 .andExpect(status().isNotFound()); // 404 implies 200 OK from Security layer
//     }

//     @Test
//     @WithMockUser(roles = "ADMIN")
//     void testAdminRole_CanAccessAdminDashboard() throws Exception {
//         mockMvc.perform(get("/api/dashboard/admin/overview").secure(true))
//                 .andExpect(status().isNotFound()); // Allowed
//     }

//     @Test
//     @WithMockUser(roles = "SUPERADMIN")
//     void testSuperAdminRole_CanAccessSuperAdminEndpoints() throws Exception {
//         // 1. Dashboard access
//         mockMvc.perform(get("/api/dashboard/superadmin/users").secure(true))
//                 .andExpect(status().isNotFound()); // Allowed
        
//         // 2. Registration access
//         // Should return 200 OK because we mocked the RegistrationController to succeed
//         mockMvc.perform(post("/api/register/admin").secure(true))
//                 .andExpect(status().isOk()); 
//     }

//     @Test
//     @WithMockUser(roles = "USER")
//     void testUserRole_CannotAccessAdminEndpoints() throws Exception {
//         mockMvc.perform(get("/api/dashboard/admin/overview").secure(true))
//                 .andExpect(status().isForbidden());
//     }

//     // ==========================================
//     // Test Helper Controller
//     // ==========================================
//     /**
//      * This internal controller helps us test the "else" block of the AccessDeniedHandler.
//      */
//     @RestController
//     public static class TestSecurityController {
//         @PreAuthorize("hasRole('ADMIN')")
//         @GetMapping("/pages/protected-view")
//         public String protectedView() {
//             return "view";
//         }
//     }
// }