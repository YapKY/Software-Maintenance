package com.example.springboot.config;

import com.example.springboot.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    public void setup() throws Exception {
        // CRITICAL FIX: Ensure the mocked filter continues the chain.
        // Without this, the chain stops, and Spring Security authorization is never checked (resulting in 200 OK).
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    // --- POSITIVE TESTS ---

    @Test
    public void testPublicEndpointsAccess() throws Exception {
        // Access a permitted page (Login)
        mockMvc.perform(get("/pages/login.html").secure(true))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser // Simulates an authenticated user
    public void testAuthenticatedAccessToApi() throws Exception {
        // As an authenticated user, accessing a non-existent protected endpoint
        // should pass security (200 OK or 404 Not Found), NOT 401/403.
        // Since we don't have a controller for /api/test/protected, Spring Boot returns 404.
        mockMvc.perform(get("/api/test/protected").secure(true))
               .andExpect(status().isNotFound());
    }

    // --- NEGATIVE TESTS ---

    @Test
    public void testUnauthenticatedAccessToApi() throws Exception {
        // Scenario: User tries to access API without token
        // Expected: 401 Unauthorized with JSON response
        mockMvc.perform(get("/api/user/profile").secure(true))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.error").value("Unauthorized"))
               .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    public void testUnauthenticatedAccessToWebPage() throws Exception {
        // Scenario: User tries to access a protected HTML page (e.g., dashboard)
        // Expected: 302 Redirect to /pages/login.html
        
        // Note: Ensure /pages/user-dashboard.html is NOT in permitAll()
        mockMvc.perform(get("/pages/user-dashboard.html").secure(true))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/pages/login.html"));
    }
}