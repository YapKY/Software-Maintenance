package com.example.springboot.controller;

import com.example.springboot.dto.response.AdminListResponseDTO;
import com.example.springboot.dto.response.AdminProfileDTO;
import com.example.springboot.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SuperadminController - Superadmin-specific endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/superadmin")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperadminController {
    
    private final UserManagementService userManagementService;
    
    /**
     * Get list of all admins created by current superadmin
     */
    @GetMapping("/admins")
    public ResponseEntity<AdminListResponseDTO> getAdminsList() {
        try {
            log.info("Fetching admins list for superadmin");
            
            List<AdminProfileDTO> admins = userManagementService.getAdminsCreatedBySuperadmin();
            
            AdminListResponseDTO response = AdminListResponseDTO.builder()
                .success(true)
                .message("Admins retrieved successfully")
                .totalAdmins(admins.size())
                .admins(admins)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to fetch admins list: {}", e.getMessage());
            
            AdminListResponseDTO response = AdminListResponseDTO.builder()
                .success(false)
                .message("Failed to fetch admins list")
                .totalAdmins(0)
                .build();
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
