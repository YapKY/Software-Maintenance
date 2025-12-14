package com.example.maintenance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Error Response DTO - Standardized error response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    
    private Integer status;
    private String error;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String path;
}
