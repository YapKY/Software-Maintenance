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
 * SUPPORTS TWO DASHBOARD VERSIONS:
 * 1. Old Dashboard (/staff/dashboard) - Form-based with page reloads
 * 2. New Dashboard (/staff/dashboard-rest) - REST API with AJAX
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
    // AUTHENTICATION
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
    // OLD DASHBOARD (Form-based)
    // ========================================

    /**
     * Display Traditional Staff Dashboard (Form-based)
     * GET /staff/dashboard
     * 
     * Uses: staff-dashboard.html
     * Method: Form submissions with page reloads
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // Check if logged in
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/staff/login";
        }

        try {
            // Get all flights
            List<Flight> flights = flightService.getAllFlights();
            
            model.addAttribute("staff", staff);
            model.addAttribute("flights", flights);

            return "staff-dashboard-rest";

        } catch (ExecutionException | InterruptedException e) {
            model.addAttribute("error", "Failed to load flights: " + e.getMessage());
            model.addAttribute("staff", staff);
            return "staff-dashboard-rest";
        }
    }

    /**
     * Save Flight (Add or Update) - For OLD Dashboard
     * POST /staff/flights/save
     * 
     * Used by: staff-dashboard.html (old form-based dashboard)
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

        // Check if logged in
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/staff/login";
        }

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
    // NEW DASHBOARD (REST API-based)
    // ========================================

    /**
     * Display Modern REST API-based Staff Dashboard
     * GET /staff/dashboard-rest
     * 
     * Uses: staff-dashboard-rest.html
     * Method: AJAX calls to /api/flights endpoints
     * Benefits: No page reloads, instant updates, better UX
     */
    @GetMapping("/dashboard-rest")
    public String showRestDashboard(HttpSession session, Model model) {
        // Check if logged in
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/staff/login";
        }

        // Pass staff info to template
        // Flights will be loaded via AJAX from /api/flights
        model.addAttribute("staff", staff);
        
        return "staff-dashboard-rest";
    }

    // ========================================
    // REPORTS (Works with both dashboards)
    // ========================================

    /**
     * Display Sales Reports Page
     * GET /staff/reports
     * 
     * Access: Manager ONLY
     */
    @GetMapping("/reports")
    public String showReportsPage(HttpSession session, Model model) {
        // Check if logged in
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/staff/login";
        }

        // Check if Manager
        String position = (String) staff.get("position");
        if (!"Manager".equalsIgnoreCase(position)) {
            return "redirect:/staff/dashboard";
        }

        model.addAttribute("staff", staff);
        return "staff-reports";
    }

    /**
     * Download Sales Report PDF
     * GET /staff/reports/download
     * 
     * Access: Manager ONLY
     */
    @GetMapping("/reports/download")
    public ResponseEntity<byte[]> downloadSalesReport(HttpSession session) {
        // Check if logged in
        Map<String, Object> staff = (Map<String, Object>) session.getAttribute("staff");
        if (staff == null) {
            return ResponseEntity.status(401).build();
        }

        // Check if Manager
        String position = (String) staff.get("position");
        if (!"Manager".equalsIgnoreCase(position)) {
            return ResponseEntity.status(403).build();
        }

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
     * Redirects to dashboard if logged in, otherwise to login
     */
    @GetMapping({"", "/"})
    public String home(HttpSession session) {
        if (session.getAttribute("staff") != null) {
            return "redirect:/staff/dashboard";
        }
        return "redirect:/staff/login";
    }
}