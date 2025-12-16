package com.example.springboot.service;

import com.example.springboot.dto.request.AdminRegisterRequestDTO;
import com.example.springboot.dto.request.UserRegisterRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.factory.RegisterStrategyFactory;
import com.example.springboot.strategy.registration.RegisterStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationExecutionServiceTest {

    @Mock
    private RegisterStrategyFactory registerStrategyFactory;

    @Mock
    private RegisterStrategy registerStrategy;

    @InjectMocks
    private RegistrationExecutionService registrationService;

    @Test
    void testRegisterUser_Success() {
        UserRegisterRequestDTO request = new UserRegisterRequestDTO();
        request.setEmail("test@example.com");
        request.setRecaptchaToken("token");

        AuthResponseDTO expectedResponse = AuthResponseDTO.builder().success(true).build();

        when(registerStrategyFactory.getRegisterStrategy(Role.USER)).thenReturn(registerStrategy);
        when(registerStrategy.register(request, "token")).thenReturn(expectedResponse);

        AuthResponseDTO actualResponse = registrationService.registerUser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(registerStrategyFactory).getRegisterStrategy(Role.USER);
        verify(registerStrategy).register(request, "token");
    }

    @Test
    void testRegisterUser_Failure() {
        UserRegisterRequestDTO request = new UserRegisterRequestDTO();
        when(registerStrategyFactory.getRegisterStrategy(Role.USER)).thenReturn(registerStrategy);
        when(registerStrategy.register(any(), any())).thenThrow(new RuntimeException("Reg failed"));

        assertThrows(RuntimeException.class, () -> registrationService.registerUser(request));
    }

    @Test
    void testRegisterAdmin_Success() {
        AdminRegisterRequestDTO request = new AdminRegisterRequestDTO();
        request.setEmail("admin@example.com");

        AuthResponseDTO expectedResponse = AuthResponseDTO.builder().success(true).build();

        when(registerStrategyFactory.getRegisterStrategy(Role.ADMIN)).thenReturn(registerStrategy);
        when(registerStrategy.register(request, null)).thenReturn(expectedResponse);

        AuthResponseDTO actualResponse = registrationService.registerAdmin(request);

        assertEquals(expectedResponse, actualResponse);
        verify(registerStrategyFactory).getRegisterStrategy(Role.ADMIN);
        verify(registerStrategy).register(request, null);
    }

    @Test
    void testRegisterAdmin_Failure() {
        AdminRegisterRequestDTO request = new AdminRegisterRequestDTO();
        when(registerStrategyFactory.getRegisterStrategy(Role.ADMIN)).thenReturn(registerStrategy);
        when(registerStrategy.register(any(), any())).thenThrow(new RuntimeException("Admin Reg failed"));

        assertThrows(RuntimeException.class, () -> registrationService.registerAdmin(request));
    }
}