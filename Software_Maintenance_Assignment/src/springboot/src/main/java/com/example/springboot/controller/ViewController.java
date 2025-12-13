package com.example.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * View Controller - Handles page rendering for the Profile Module
 * Following MVC Architecture:
 * - Controller Layer: Routes requests to appropriate views
 * - View Layer: Thymeleaf templates (HTML files)
 * - Model Layer: Data passed to views (entity objects)
 * 
 * This controller serves the HTML pages for the frontend UI.
 * REST controllers handle the API endpoints for data operations.
 */
@Controller
public class ViewController {

    /**
     * Home page - Landing page with navigation to customer and staff profiles
     * URL: http://localhost:8081/
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Customer profile page
     * URL: http://localhost:8081/customer-profile
     * 
     * @param customerId Optional customer ID parameter (temporary - will be
     *                   replaced with session)
     * @param model      Spring Model object to pass data to view
     * @return View name "customer-profile"
     */
    @GetMapping("/customer-profile")
    public String customerProfile(
            @RequestParam(value = "id", required = false, defaultValue = "1") Integer customerId,
            Model model) {

        // Pass the customer ID to the view
        // In future, this will be retrieved from logged-in user session
        model.addAttribute("customerId", customerId);

        return "customer-profile";
    }

    /**
     * Staff profile page
     * URL: http://localhost:8081/staff-profile
     * 
     * @param staffId Optional staff ID parameter (temporary - will be replaced with
     *                session)
     * @param model   Spring Model object to pass data to view
     * @return View name "staff-profile"
     */
    @GetMapping("/staff-profile")
    public String staffProfile(
            @RequestParam(value = "id", required = false, defaultValue = "1") Integer staffId,
            Model model) {

        // Pass the staff ID to the view
        // In future, this will be retrieved from logged-in user session
        model.addAttribute("staffId", staffId);

        return "staff-profile";
    }

    /**
     * Alternative URL pattern for customer profile with path variable
     * URL: http://localhost:8081/customer/{id}
     */
    @GetMapping("/customer/{id}")
    public String customerProfileById(@PathVariable Integer id, Model model) {
        model.addAttribute("customerId", id);
        return "customer-profile";
    }

    /**
     * Alternative URL pattern for staff profile with path variable
     * URL: http://localhost:8081/staff/{id}
     */
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
}
