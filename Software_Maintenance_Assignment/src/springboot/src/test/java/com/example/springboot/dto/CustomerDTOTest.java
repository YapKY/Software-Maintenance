package com.example.springboot.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerDTOTest {

    @Test
    void testBuilderAndGetters() {
        CustomerDTO dto = CustomerDTO.builder()
                .custId("C1001")
                .custIcNo("900101-10-1234")
                .name("Alice")
                .email("alice@test.com")
                .phoneNumber("012-3456789")
                .gender("FEMALE")
                .build();

        assertEquals("C1001", dto.getCustId());
        assertEquals("900101-10-1234", dto.getCustIcNo());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@test.com", dto.getEmail());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals("FEMALE", dto.getGender());
    }

    @Test
    void testBuilderIsolation() {
        // Ensure new builder instance is created
        CustomerDTO.Builder builder1 = CustomerDTO.builder().name("Bob");
        CustomerDTO.Builder builder2 = CustomerDTO.builder().name("Charlie");

        assertNotEquals(builder1.build().getName(), builder2.build().getName());
    }
}