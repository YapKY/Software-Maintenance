package com.example.maintenance.dto.response;

import lombok.*;

/**
 * Simple Message Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDTO {
    
    private Boolean success;
    private String message;
    private Object data;
}
