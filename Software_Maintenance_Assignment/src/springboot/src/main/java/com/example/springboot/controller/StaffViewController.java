package com.example.springboot.controller;
import org.springframework.security.core.Authentication;

import com.example.springboot.model.Flight;
import com.example.springboot.model.Staff;
import com.example.springboot.service.FlightService;
import com.example.springboot.service.PdfReportService;
import com.example.springboot.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;

/**
 * Spring MVC Controller for Staff Web Pages (Thymeleaf)
 * Handles traditional server-side rendering for staff dashboard
 * 
 * MODIFIED VERSION: Login checks removed to allow direct access from Admin panel
 * Dashboard and Reports can now be accessed without staff login session
 */
@Controller
@RequestMapping("/staff")
public class StaffViewController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private StaffService staffService;

    // ========================================
    // AUTHENTICATION (Optional - kept for backward compatibility)
    // ========================================

    /**
     * Display Login Page
     * GET /staff/login
     */
    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        // If already logged in, redirect to dashboard
        if (session.getAttribute("staff") != null) {
            return "redirect:/staff/dashboard";
        }
        return "staff";
    }

    /**
     * Handle Login Form Submission
     * POST /staff/login
     * 
     * Updated to use StaffService for cleaner authentication
     */
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String staffId,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Convert password to integer
            int passwordInt = Integer.parseInt(password);

            // Authenticate using StaffService
            Staff staff = staffService.authenticate(staffId, passwordInt);

            if (staff == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid Staff ID or Password");
                return "redirect:/staff/login";
            }

            // Create staff object for session
            Map<String, Object> staffData = new HashMap<>();
            staffData.put("staffId", staff.getStaffId());
            staffData.put("name", staff.getName());
            staffData.put("position", staff.getPosition());
            staffData.put("email", staff.getEmail());
            staffData.put("phoneNumber", staff.getPhoneNumber());
            staffData.put("gender", staff.getGender());

            // Store in session
            session.setAttribute("staff", staffData);

            // Redirect to dashboard
            return "redirect:/staff/dashboard";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Password must be a 5-digit number");
            return "redirect:/staff/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed. Please try again.");
            return "redirect:/staff/login";
        }
    }

    /**
     * Logout
     * GET /staff/logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/staff/login";
    }

    // ========================================
    // DASHBOARD - NO LOGIN REQUIRED
    // ========================================

    /**
     * Display Staff Dashboard
     * GET /staff/dashboard
     * 
     * MODIFIED: Login check removed - accessible from Admin panel
     * Staff info is optional now
     */
    @GetMapping("/dashboard")
public String showDashboard(Model model, Authentication authentication) {
    
    // Initialize default staff object to prevent null errors
    Map<String, Object> defaultStaff = new HashMap<>();
    defaultStaff.put("name", "Admin User");
    defaultStaff.put("position", "Administrator");
    defaultStaff.put("role", "ADMIN");
    
    // 1. Check if user is authenticated via Spring Security
    if (authentication != null && authentication.isAuthenticated()) {
        
        // Get the username (or staffId, depending on your UserDetails implementation)
        String username = authentication.getName();
        
        // 2. Extract Roles
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.joining(","));

        // 3. Update staff object with actual authentication info
        defaultStaff.put("name", username);
        defaultStaff.put("role", role);
        if (role.contains("SUPERADMIN")) {
            defaultStaff.put("position", "Super Administrator");
        } else if (role.contains("ADMIN")) {
            defaultStaff.put("position", "Administrator");
        }
        
        // 4. Add to Model for Thymeleaf/HTML access
        model.addAttribute("username", username);
        model.addAttribute("userRole", role);
        
        // Helper booleans for easier "th:if" checks
        model.addAttribute("isSuperAdmin", role.contains("SUPERADMIN"));
        model.addAttribute("isAdmin", role.contains("ADMIN"));
        
        // Debug log
        System.out.println("User: " + username + " | Role: " + role);
    } else {
        // Not authenticated - set default values
        model.addAttribute("username", "Guest");
        model.addAttribute("userRole", "GUEST");
        model.addAttribute("isSuperAdmin", false);
        model.addAttribute("isAdmin", false);
    }
    
    // CRITICAL: Always add staff object to prevent null errors
    model.addAttribute("staff", defaultStaff);

    // Return the name of your HTML file
    return "staff-dashboard-rest"; 
}

    /**
     * Save Flight (Add or Update)
     * POST /staff/flights/save
     * 
     * MODIFIED: Login check removed
     */
    @PostMapping("/flights/save")
    public String saveFlight(
            @RequestParam(required = false) String documentId,
            @RequestParam String flightId,
            @RequestParam String departureCountry,
            @RequestParam String arrivalCountry,
            @RequestParam String departureDate,
            @RequestParam String arrivalDate,
            @RequestParam int departureTime,
            @RequestParam int arrivalTime,
            @RequestParam int boardingTime,
            @RequestParam double economyPrice,
            @RequestParam double businessPrice,
            @RequestParam String planeNo,
            @RequestParam int totalSeats,
            @RequestParam(defaultValue = "false") boolean isEdit,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Create flight object
            Flight flight = new Flight();
            flight.setFlightId(flightId);
            flight.setDepartureCountry(departureCountry);
            flight.setArrivalCountry(arrivalCountry);
            flight.setDepartureDate(departureDate);
            flight.setArrivalDate(arrivalDate);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);
            flight.setBoardingTime(boardingTime);
            flight.setEconomyPrice(economyPrice);
            flight.setBusinessPrice(businessPrice);
            flight.setPlaneNo(planeNo);
            flight.setTotalSeats(totalSeats);

            if (isEdit && documentId != null && !documentId.isEmpty()) {
                // Update existing flight
                flightService.updateFlight(documentId, flight);
                redirectAttributes.addFlashAttribute("message", 
                    "Flight " + flightId + " updated successfully!");
            } else {
                // Add new flight
                flightService.addFlight(flight);
                redirectAttributes.addFlashAttribute("message", 
                    "Flight " + flightId + " added successfully!");
            }

            return "redirect:/staff/dashboard";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/dashboard";
        } catch (ExecutionException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save flight: " + e.getMessage());
            return "redirect:/staff/dashboard";
        }
    }

    // ========================================
    // REST DASHBOARD - NO LOGIN REQUIRED
    // ========================================

    /**
     * Display Modern REST API-based Staff Dashboard
     * GET /staff/dashboard-rest
     * 
     * MODIFIED: Login check removed - accessible from Admin panel
     */
    @GetMapping("/dashboard-rest")
public String showRestDashboard(HttpSession session, Model model, Authentication authentication) {
    
    // Initialize default staff object
    Map<String, Object> defaultStaff = new HashMap<>();
    defaultStaff.put("name", "Admin User");
    defaultStaff.put("position", "Administrator");
    defaultStaff.put("role", "ADMIN");
    
    // Try to get staff from session first (legacy login)
    Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
    
    if (staff != null) {
        // Session-based staff info exists
        model.addAttribute("staff", staff);
    } else if (authentication != null && authentication.isAuthenticated()) {
        // Spring Security authentication exists
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.joining(","));
        
        defaultStaff.put("name", username);
        defaultStaff.put("role", role);
        if (role.contains("SUPERADMIN")) {
            defaultStaff.put("position", "Super Administrator");
        } else if (role.contains("ADMIN")) {
            defaultStaff.put("position", "Administrator");
        }
        
        model.addAttribute("staff", defaultStaff);
    } else {
        // No authentication - use defaults
        model.addAttribute("staff", defaultStaff);
    }
    
    return "staff-dashboard-rest";
}

    // ========================================
    // REPORTS - NO LOGIN REQUIRED
    // ========================================

    /**
     * Display Sales Reports Page
     * GET /staff/reports
     * 
     * MODIFIED: Login and Manager checks removed - accessible from Admin panel
     */
    @GetMapping("/reports")
    public String showReportsPage(HttpSession session, Model model) {
        // Get staff info if exists (optional)
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        
        if (staff != null) {
            model.addAttribute("staff", staff);
        } else {
            // Provide default staff info for UI display
            Map<String, Object> defaultStaff = new HashMap<>();
            defaultStaff.put("name", "Admin User");
            defaultStaff.put("position", "Manager");
            defaultStaff.put("role", "SUPERADMIN");
            model.addAttribute("staff", defaultStaff);
        }
        
        return "staff-reports";
    }

    /**
     * Download Sales Report PDF
     * GET /staff/reports/download
     * 
     * MODIFIED: Login and Manager checks removed - accessible from Admin panel
     */
    @GetMapping("/reports/download")
    public ResponseEntity<byte[]> downloadSalesReport(HttpSession session) {
        try {
            byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "Sales_Report_" + System.currentTimeMillis() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ========================================
    // UTILITY ROUTES
    // ========================================

    /**
     * Home/Root redirect
     * GET /staff or GET /staff/
     * 
     * MODIFIED: Always redirects to dashboard (no login required)
     */
    @GetMapping({"", "/"})
    public String home(HttpSession session) {
        return "redirect:/staff/dashboard";
    }
}