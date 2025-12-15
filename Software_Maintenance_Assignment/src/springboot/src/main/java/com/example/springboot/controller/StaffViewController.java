package com.example.springboot.controller;

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
    public String showDashboard(HttpSession session, Model model) {
        try {
            // Get staff info if exists (optional)
            Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
            
            // Get all flights
            List<Flight> flights = flightService.getAllFlights();
            
            // Add attributes to model
            if (staff != null) {
                model.addAttribute("staff", staff);
            } else {
                // Provide default staff info for UI display
                Map<String, Object> defaultStaff = new HashMap<>();
                defaultStaff.put("name", "Admin User");
                defaultStaff.put("position", "Administrator");
                model.addAttribute("staff", defaultStaff);
            }
            
            model.addAttribute("flights", flights);

            return "staff-dashboard-rest";

        } catch (ExecutionException | InterruptedException e) {
            model.addAttribute("error", "Failed to load flights: " + e.getMessage());
            // Provide default staff info even on error
            Map<String, Object> defaultStaff = new HashMap<>();
            defaultStaff.put("name", "Admin User");
            defaultStaff.put("position", "Administrator");
            model.addAttribute("staff", defaultStaff);
            return "staff-dashboard-rest";
        }
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
    public String showRestDashboard(HttpSession session, Model model) {
        // Get staff info if exists (optional)
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        
        if (staff != null) {
            model.addAttribute("staff", staff);
        } else {
            // Provide default staff info for UI display
            Map<String, Object> defaultStaff = new HashMap<>();
            defaultStaff.put("name", "Admin User");
            defaultStaff.put("position", "Administrator");
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