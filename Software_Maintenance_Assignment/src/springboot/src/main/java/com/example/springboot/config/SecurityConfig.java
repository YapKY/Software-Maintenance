package com.example.springboot.config;

import com.example.springboot.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            // [FIXED] Removed STATELESS enforcement to allow Session usage in Controllers (e.g., StaffViewController)
            // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .headers(headers -> {
                // 1. Disable Frame Options
                headers.frameOptions(frame -> frame.disable());
                
                // 2. Set Referrer Policy
                headers.referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                );
                
                // 3. Set Permissions Policy
                headers.permissionsPolicy(permissions -> permissions
                    .policy("camera=(), microphone=(), geolocation=()")
                );
                
                // 4. [CRITICAL] Add Custom Header for Google Sign-In Popup
                headers.addHeaderWriter((request, response) -> {
                    response.setHeader("Cross-Origin-Opener-Policy", "same-origin-allow-popups");
                });
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/index.html", 
                    "/pages/login.html", // Ensure login page is accessible
                    "/pages/register.html",
                    "/pages/forgot-password.html",
                    "/pages/reset-password.html",
                    "/pages/verify-email.html",
                    "/pages/mfa-setup.html",
                    "/pages/**", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/favicon.ico"
                ).permitAll()
                .requestMatchers(
                    "/api/auth/**",
                    "/api/register/**",
                    "/api/email/**", 
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/api/dashboard/user/**").hasRole("USER")
                .requestMatchers("/api/dashboard/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/register/admin").hasRole("SUPERADMIN")
                .requestMatchers("/api/dashboard/superadmin/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/mfa/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                // [FIXED] Smart handling: Redirect browsers to login, return JSON to APIs
                .authenticationEntryPoint((request, response, ex) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/api/")) {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                    } else {
                        // Redirect to login page for browser navigation
                        response.sendRedirect("/pages/login.html");
                    }
                })
                .accessDeniedHandler((request, response, ex) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/api/")) {
                        response.setStatus(403);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
                    } else {
                        response.sendRedirect("/pages/login.html?error=access_denied");
                    }
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}