package com.example.springboot.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Standalone setup isolates the Exception Handler and Controller
        // eliminating need for SecurityConfig or ApplicationContext loading
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- 1. Test Validation Error (400) ---
    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() throws Exception {
        TestDto invalidDto = new TestDto(); // name is null, triggers validation
        
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    // --- 2. Test Invalid Credentials (401) ---
    @Test
    void handleInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/test/invalid-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Wrong password"));
    }

    // --- 3. Test Unauthorized Exception (403) ---
    @Test
    void handleUnauthorized_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Not allowed"));
    }

    // --- 4. Test Rate Limit Exceeded (429) ---
    @Test
    void handleRateLimitExceeded_ShouldReturnTooManyRequests() throws Exception {
        mockMvc.perform(get("/test/rate-limit"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"));
    }

    // --- 5. Test Method Not Supported (405) ---
    @Test
    void handleMethodNotSupported_ShouldReturnMethodNotAllowed() throws Exception {
        // Sending POST to a GET endpoint triggers HttpRequestMethodNotSupportedException
        mockMvc.perform(post("/test/invalid-credentials"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"));
    }

    // --- 6. Test User Not Found (404) ---
    @Test
    void handleUserNotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User missing"));
    }

    // --- 7. Test No Resource Found (404) ---
    @Test
    void handleNoResourceFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/no-resource"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("The requested resource was not found"));
    }

    // --- 8. Test Email Already Exists (409) ---
    @Test
    void handleEmailAlreadyExists_ShouldReturnConflict() throws Exception {
        mockMvc.perform(get("/test/email-exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email taken"));
    }

    // --- 9. Test MFA Validation Exception (401) ---
    @Test
    void handleMFAValidation_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/test/mfa-fail"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Bad OTP"));
    }

    // --- 10. Test Invalid Token Exception (401) ---
    @Test
    void handleInvalidToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/test/invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Token expired"));
    }

    // --- 11. Test Bad Credentials (Spring Security) (401) ---
    @Test
    void handleBadCredentials_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // --- 12. Test Access Denied (Spring Security) (403) ---
    @Test
    void handleAccessDenied_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));
    }

    // --- 13. Test General Exception (500) ---
    @Test
    void handleGlobalException_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/general-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
    }

    // --- Dummy Controller and DTO for Testing ---

    @RestController
    @RequestMapping("/test")
    public static class TestController {

        @PostMapping("/validation")
        public void validation(@RequestBody @Valid TestDto dto) {
            // Validator will throw exception before this
        }

        @GetMapping("/invalid-credentials")
        public void invalidCredentials() {
            throw new InvalidCredentialsException("Wrong password");
        }

        @GetMapping("/unauthorized")
        public void unauthorized() {
            throw new UnauthorizedException("Not allowed");
        }

        @GetMapping("/rate-limit")
        public void rateLimit() {
            throw new RateLimitExceededException("Too slow");
        }

        @GetMapping("/user-not-found")
        public void userNotFound() {
            throw new UserNotFoundException("User missing");
        }

        @GetMapping("/no-resource")
        public void noResource() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/test/no-resource");
        }

        @GetMapping("/email-exists")
        public void emailExists() {
            throw new EmailAlreadyExistsException("Email taken");
        }

        @GetMapping("/mfa-fail")
        public void mfaFail() {
            throw new MFAValidationException("Bad OTP");
        }

        @GetMapping("/invalid-token")
        public void invalidToken() {
            throw new InvalidTokenException("Token expired");
        }

        @GetMapping("/bad-credentials")
        public void badCredentials() {
            throw new BadCredentialsException("Spring security error");
        }

        @GetMapping("/access-denied")
        public void accessDenied() {
            throw new AccessDeniedException("Access denied from security");
        }

        @GetMapping("/general-error")
        public void generalError() {
            throw new RuntimeException("Unexpected crash");
        }
    }

    public static class TestDto {
        @NotNull(message = "Name cannot be null")
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}