package com.example.springboot.controller;

import com.example.springboot.dto.response.MFAStatusDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.model.User;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.MFAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MFAControllerTest {

    private MockMvc mockMvc;

    @Mock private MFAService mfaService;
    @Mock private UserRepository userRepository;

    @InjectMocks private MFAController mfaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mfaController).build();
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("user-id", null, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );
    }

    @Test
    void testSetupMFA_Success() throws Exception {
        // FIX: Removed .secretKey() if it doesn't exist. 
        // If your DTO has a secret field, ensure you use the right name (e.g. .secret() or .key())
        MFAStatusDTO statusDTO = MFAStatusDTO.builder()
                .mfaEnabled(false)
                .build();
                
        when(mfaService.setupMFA(anyString(), eq(Role.USER))).thenReturn(statusDTO);

        mockMvc.perform(post("/api/mfa/setup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testValidateMFA_Success() throws Exception {
        when(mfaService.verifyAndEnableMFA(anyString(), eq(Role.USER), eq("123456"))).thenReturn(true);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/mfa/validate").param("code", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void testDisableMFA_Success() throws Exception {
        when(mfaService.validateMFACode(anyString(), eq(Role.USER), eq("123456"))).thenReturn(true);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/mfa/disable").param("confirmationCode", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}