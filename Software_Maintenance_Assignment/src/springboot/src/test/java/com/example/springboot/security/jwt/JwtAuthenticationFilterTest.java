package com.example.springboot.security.jwt;

import com.example.springboot.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithValidAuthHeader() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String userId = "user123";
        Role role = Role.ADMIN;

        // Mock Request
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Mock Provider
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn(role);

        // Execute
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(filterChain).doFilter(request, response);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userId, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testDoFilterInternal_WithValidCookie() throws ServletException, IOException {
        String token = "valid.cookie.token";
        String userId = "user456";
        
        // Mock Request with no Header but Valid Cookie
        when(request.getHeader("Authorization")).thenReturn(null);
        Cookie jwtCookie = new Cookie("jwt_token", token);
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});

        // Mock Provider
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn(Role.USER);

        // Execute
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(filterChain).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userId, auth.getPrincipal());
    }

    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {
        // Mock Empty Request
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        // Execute
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidToken_ClearsCookie() throws ServletException, IOException {
        String invalidToken = "invalid.token";

        // Mock Request
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

        // Mock Provider returning false
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        // Execute
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify Cookie Deletion
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        
        Cookie deletedCookie = cookieCaptor.getValue();
        assertEquals("jwt_token", deletedCookie.getName());
        assertEquals(0, deletedCookie.getMaxAge());
        assertNull(deletedCookie.getValue());
    }

    @Test
    void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // Force an exception during extraction/validation
        when(request.getHeader("Authorization")).thenThrow(new RuntimeException("Unexpected Error"));

        // Execute
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify filter chain still continues even if auth fails
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @Test
    void testDoFilterInternal_BearerPrefixMissing() throws ServletException, IOException {
        // Header exists but missing "Bearer "
        when(request.getHeader("Authorization")).thenReturn("Basic 12345");
        when(request.getCookies()).thenReturn(null); // No fallback cookie

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void testDoFilterInternal_CookieCheckFallback_DifferentCookieName() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        Cookie otherCookie = new Cookie("session_id", "xyz");
        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie});

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }
}