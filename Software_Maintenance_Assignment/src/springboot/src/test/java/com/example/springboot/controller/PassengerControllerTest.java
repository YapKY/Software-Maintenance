package com.example.springboot.controller;

import com.example.springboot.model.Passenger;
import com.example.springboot.repository.PassengerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PassengerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerController passengerController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Passenger testPassenger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(passengerController).build();

        testPassenger = new Passenger();
        testPassenger.setPassengerId("P001");
        testPassenger.setName("Test Passenger");
        testPassenger.setFullName("Test Passenger");
        testPassenger.setEmail("test@example.com");
        testPassenger.setPassportNo("A12345678");
        testPassenger.setPhoneNumber("0123456789");
        testPassenger.setGender("Male");
    }

    @Test
    @DisplayName("Should create passenger successfully")
    void testCreatePassenger_Success() throws Exception {
        when(passengerRepository.existsByPassportNo("A12345678")).thenReturn(false);
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.passenger.passengerId").value("P001"));
    }

    @Test
    @DisplayName("Should fail create passenger with invalid passport")
    void testCreatePassenger_InvalidPassport() throws Exception {
        testPassenger.setPassportNo("invalid");

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid passport number format"));
    }

    @Test
    @DisplayName("Should fail create passenger with invalid name")
    void testCreatePassenger_InvalidName() throws Exception {
        testPassenger.setName("Invalid123");

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid name format"));
    }

    @Test
    @DisplayName("Should fail create passenger with invalid email")
    void testCreatePassenger_InvalidEmail() throws Exception {
        testPassenger.setEmail("invalid-email");

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail create passenger when passport exists")
    void testCreatePassenger_PassportExists() throws Exception {
        when(passengerRepository.existsByPassportNo("A12345678")).thenReturn(true);

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passport number already exists"));
    }

    @Test
    @DisplayName("Should fail create passenger with internal error")
    void testCreatePassenger_InternalError() throws Exception {
        when(passengerRepository.existsByPassportNo(anyString())).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(post("/api/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to create passenger: DB Error"));
    }

    @Test
    @DisplayName("Should get all passengers")
    void testGetAllPassengers() throws Exception {
        List<Passenger> passengers = Arrays.asList(testPassenger);
        when(passengerRepository.findAll()).thenReturn(passengers);

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passengerId").value("P001"));
    }

    @Test
    @DisplayName("Should handle error getting all passengers")
    void testGetAllPassengers_Error() throws Exception {
        when(passengerRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error fetching passengers: DB Error"));
    }

    @Test
    @DisplayName("Should get passenger by ID")
    void testGetPassengerById_Success() throws Exception {
        when(passengerRepository.findById("P001")).thenReturn(Optional.of(testPassenger));

        mockMvc.perform(get("/api/passengers/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("P001"));
    }

    @Test
    @DisplayName("Should return 404 when passenger by ID not found")
    void testGetPassengerById_NotFound() throws Exception {
        when(passengerRepository.findById("P001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/passengers/P001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger not found"));
    }

    @Test
    @DisplayName("Should handle error getting passenger by ID")
    void testGetPassengerById_Error() throws Exception {
        when(passengerRepository.findById("P001")).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/passengers/P001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error fetching passenger: DB Error"));
    }

    @Test
    @DisplayName("Should get passenger by passport number")
    void testGetPassengerByPassportNo_Success() throws Exception {
        when(passengerRepository.findByPassportNo("A12345678")).thenReturn(Optional.of(testPassenger));

        mockMvc.perform(get("/api/passengers/passport/A12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("P001"));
    }

    @Test
    @DisplayName("Should return 404 when passenger by passport number not found")
    void testGetPassengerByPassportNo_NotFound() throws Exception {
        when(passengerRepository.findByPassportNo("A12345678")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/passengers/passport/A12345678"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger not found"));
    }

    @Test
    @DisplayName("Should handle error getting passenger by passport number")
    void testGetPassengerByPassportNo_Error() throws Exception {
        when(passengerRepository.findByPassportNo("A12345678")).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/passengers/passport/A12345678"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error fetching passenger: DB Error"));
    }

    @Test
    @DisplayName("Should update passenger successfully")
    void testUpdatePassenger_Success() throws Exception {
        when(passengerRepository.findById("P001")).thenReturn(Optional.of(testPassenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(testPassenger);

        Passenger updateData = new Passenger();
        updateData.setName("Updated Name");
        updateData.setEmail("updated@example.com");
        updateData.setPhoneNumber("0987654321");

        mockMvc.perform(put("/api/passengers/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("P001"));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent passenger")
    void testUpdatePassenger_NotFound() throws Exception {
        when(passengerRepository.findById("P001")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/passengers/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger not found"));
    }

    @Test
    @DisplayName("Should handle error updating passenger")
    void testUpdatePassenger_Error() throws Exception {
        when(passengerRepository.findById("P001")).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(put("/api/passengers/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPassenger)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error updating passenger: DB Error"));
    }

    @Test
    @DisplayName("Should delete passenger successfully")
    void testDeletePassenger_Success() throws Exception {
        when(passengerRepository.existsById("P001")).thenReturn(true);
        doNothing().when(passengerRepository).deleteById("P001");

        mockMvc.perform(delete("/api/passengers/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Passenger deleted successfully"));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent passenger")
    void testDeletePassenger_NotFound() throws Exception {
        when(passengerRepository.existsById("P001")).thenReturn(false);

        mockMvc.perform(delete("/api/passengers/P001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Passenger not found"));
    }

    @Test
    @DisplayName("Should handle error deleting passenger")
    void testDeletePassenger_Error() throws Exception {
        when(passengerRepository.existsById("P001")).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(delete("/api/passengers/P001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error deleting passenger: DB Error"));
    }
}
