package com.example.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * View Controller - Handles page rendering for the Profile Module
 */
@Controller
public class ViewController {

    /**
     * Home page - Landing page with authentication check
     * URL: http://localhost:8081/
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Generic handler for pages in the 'pages' subfolder (e.g., login, dashboards)
     * Maps /pages/login.html -> templates/pages/login.html
     */
    @GetMapping("/pages/{page}")
    public String showPage(@PathVariable String page) {
        // Strip .html extension if present to match Thymeleaf template name
        if (page != null && page.endsWith(".html")) {
            page = page.substring(0, page.length() - 5);
        }
        return "pages/" + page;
    }

    /**
     * Customer profile page
     * URL: http://localhost:8081/customer-profile
     */
    @GetMapping("/customer-profile")
    public String customerProfile(
            @RequestParam(value = "id", required = false, defaultValue = "1") Integer customerId,
            Model model) {
        model.addAttribute("customerId", customerId);
        return "customer-profile";
    }

    /**
     * Staff profile page
     * URL: http://localhost:8081/staff-profile
     */
    @GetMapping("/staff-profile")
    public String staffProfile(
            @RequestParam(value = "id", required = false, defaultValue = "1") Integer staffId,
            Model model) {
        model.addAttribute("staffId", staffId);
        return "staff-profile";
    }

    @GetMapping("/customer/{id}")
    public String customerProfileById(@PathVariable Integer id, Model model) {
        model.addAttribute("customerId", id);
        return "customer-profile";
    }

    @GetMapping("/staff/{id}")
    public String staffProfileById(@PathVariable Integer id, Model model) {
        model.addAttribute("staffId", id);
        return "staff-profile";
    }

    /**
     * Customer list page - Shows all customers in a table
     * URL: http://localhost:8081/customer-list
     */
    @GetMapping("/customer-list")
    public String customerList() {
        return "customer-list";
    }

    /**
     * Staff list page - Shows all staff members in a table
     * URL: http://localhost:8081/staff-list
     */
    @GetMapping("/staff-list")
    public String staffList() {
        return "staff-list";
    }

    /**
     * API test page - For testing API connectivity
     * URL: http://localhost:8081/api-test
     */
    @GetMapping("/api-test")
    public String apiTest() {
        return "api-test";
    }

    /**
     * Customer detail page - Shows individual customer profile with view/edit
     * URL: http://localhost:8081/customer-detail
     * 
     * @param email Optional email param - if present, redirects to list page
     * @return View name "customer-detail"
     */
    @GetMapping("/customer-detail")
    public String customerDetail(@RequestParam(value = "email", required = false) String email) {
        // Security: If email is in URL, redirect to list (prevents direct URL access)
        if (email != null && !email.isEmpty()) {
            return "redirect:/customer-list";
        }
        return "customer-detail";
    }

    /**
     * Staff detail page - Shows individual staff profile with view/edit
     * URL: http://localhost:8081/staff-detail
     * 
     * @param email Optional email param - if present, redirects to list page
     * @return View name "staff-detail"
     */
    @GetMapping("/staff-detail")
    public String staffDetail(@RequestParam(value = "email", required = false) String email) {
        // Security: If email is in URL, redirect to list (prevents direct URL access)
        if (email != null && !email.isEmpty()) {
            return "redirect:/staff-list";
        }
        return "staff-detail";
    }

    @GetMapping("/booking")
    public String bookingPage(
            @RequestParam(required = false) String flightId,
            @RequestParam(required = false) String customerId,
            Model model) {
        model.addAttribute("flightId", flightId != null ? flightId : "");
        model.addAttribute("customerId", customerId != null ? customerId : "GUEST");
        return "booking";
    }

    @GetMapping("/payment")
    public String paymentPage() {
        return "payment";
    }

    @GetMapping("/confirmation")
    public String confirmationPage() {
        return "confirmation";
    }

    @GetMapping("/search-flight")
    public String searchFlightPage() {
        return "search-flight";
    }

    @GetMapping("/my-tickets")
    public String myTicketsPage(
            @RequestParam(required = false) String customerId,
            Model model) {
        model.addAttribute("customerId", customerId != null ? customerId : "cust-123");
        return "my-tickets";
    }

}
