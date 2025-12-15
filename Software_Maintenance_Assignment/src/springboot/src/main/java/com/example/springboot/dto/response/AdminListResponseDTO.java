package com.example.springboot.dto.response;

import lombok.*;
import java.util.List;

/**
 * Admin List Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminListResponseDTO {
    
    private Boolean success;
    private String message;
    private Integer totalAdmins;
    private List<AdminProfileDTO> admins;
}
