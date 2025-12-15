package com.example.springboot.controller;

import com.example.springboot.dto.response.MFAStatusDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.model.Admin;
import com.example.springboot.model.Superadmin;
import com.example.springboot.model.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.SuperadminRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.MFAService;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MFAControllerTest {

    private MockMvc mockMvc;

    @Mock private MFAService mfaService;
    @Mock private UserRepository userRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private SuperadminRepository superadminRepository;

    @InjectMocks private MFAController mfaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mfaController).build();
        setSecurityContext("user-id", "ROLE_USER");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(String userId, String role) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userId, null, 
            Collections.singletonList(new SimpleGrantedAuthority(role)))
        );
    }

    // ==========================================
    // Setup MFA Tests
    // ==========================================

    @Test
    void testSetupMFA_Success() throws Exception {
        MFAStatusDTO statusDTO = MFAStatusDTO.builder()
                .mfaEnabled(false)
                .secret("SECRET")
                .qrCodeUrl("url")
                .build();
                
        when(mfaService.setupMFA(anyString(), eq(Role.USER))).thenReturn(statusDTO);

        mockMvc.perform(post("/api/mfa/setup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.secret").value("SECRET"));
    }

    @Test
    void testSetupMFA_Exception() throws Exception {
        when(mfaService.setupMFA(anyString(), any())).thenThrow(new RuntimeException("Setup failed"));

        mockMvc.perform(post("/api/mfa/setup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Setup failed"));
    }

    // Coverage for parseRoleFromAuthority fallback
    @Test
    void testSetupMFA_InvalidRole_DefaultsToUser() throws Exception {
        setSecurityContext("user-id", "ROLE_INVALID_THING");
        
        MFAStatusDTO statusDTO = MFAStatusDTO.builder().build();
        // Should default to Role.USER
        when(mfaService.setupMFA(anyString(), eq(Role.USER))).thenReturn(statusDTO);

        mockMvc.perform(post("/api/mfa/setup"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // Status Tests
    // ==========================================

    @Test
    void testGetMFAStatus_Success() throws Exception {
        MFAStatusDTO statusDTO = MFAStatusDTO.builder().mfaEnabled(true).build();
        when(mfaService.getMFAStatus(anyString(), eq(Role.USER))).thenReturn(statusDTO);

        mockMvc.perform(get("/api/mfa/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mfaEnabled").value(true));
    }

    @Test
    void testGetMFAStatus_Exception() throws Exception {
        when(mfaService.getMFAStatus(anyString(), any())).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/mfa/status"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==========================================
    // Validate MFA & Entity Update Tests
    // ==========================================

    @Test
    void testValidateMFA_Success_User() throws Exception {
        when(mfaService.verifyAndEnableMFA(anyString(), eq(Role.USER), eq("123456"))).thenReturn(true);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/mfa/validate").param("code", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.valid").value(true));
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testValidateMFA_Success_Admin() throws Exception {
        setSecurityContext("admin-id", "ROLE_ADMIN");
        
        when(mfaService.verifyAndEnableMFA(anyString(), eq(Role.ADMIN), eq("123456"))).thenReturn(true);
        when(adminRepository.findById(anyString())).thenReturn(Optional.of(new Admin()));

        mockMvc.perform(post("/api/mfa/validate").param("code", "123456"))
                .andExpect(status().isOk());
        
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testValidateMFA_Success_Superadmin() throws Exception {
        setSecurityContext("super-id", "ROLE_SUPERADMIN");
        
        when(mfaService.verifyAndEnableMFA(anyString(), eq(Role.SUPERADMIN), eq("123456"))).thenReturn(true);
        when(superadminRepository.findById(anyString())).thenReturn(Optional.of(new Superadmin()));

        mockMvc.perform(post("/api/mfa/validate").param("code", "123456"))
                .andExpect(status().isOk());
        
        verify(superadminRepository).save(any(Superadmin.class));
    }

    @Test
    void testValidateMFA_Failure_InvalidCode() throws Exception {
        when(mfaService.verifyAndEnableMFA(anyString(), any(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/mfa/validate").param("code", "000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
        
        // Entity status should NOT be updated
        verify(userRepository, never()).save(any());
    }

    @Test
    void testValidateMFA_Exception() throws Exception {
        when(mfaService.verifyAndEnableMFA(anyString(), any(), anyString())).thenThrow(new RuntimeException("Validation Error"));

        mockMvc.perform(post("/api/mfa/validate").param("code", "123456"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Validation Error"));
    }

    @Test
    void testValidateMFA_EntityUpdateException() throws Exception {
        // This covers the catch block inside private method updateUserMfaStatus
        when(mfaService.verifyAndEnableMFA(anyString(), eq(Role.USER), anyString())).thenReturn(true);
        when(userRepository.findById(anyString())).thenThrow(new RuntimeException("DB Connection Failed"));

        // Should still return 200 OK because the MFA service succeeded, the entity update failure is logged but suppressed
        mockMvc.perform(post("/api/mfa/validate").param("code", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==========================================
    // Disable MFA Tests
    // ==========================================

    @Test
    void testDisableMFA_Success_User() throws Exception {
        when(mfaService.validateMFACode(anyString(), eq(Role.USER), eq("123456"))).thenReturn(true);
        doNothing().when(mfaService).disableMFA(anyString(), eq(Role.USER));
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/mfa/disable").param("confirmationCode", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDisableMFA_InvalidCode() throws Exception {
        when(mfaService.validateMFACode(anyString(), any(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/mfa/disable").param("confirmationCode", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid MFA code. Cannot disable MFA."));
    }

    @Test
    void testDisableMFA_Exception() throws Exception {
        when(mfaService.validateMFACode(anyString(), any(), anyString())).thenThrow(new RuntimeException("Service Error"));

        mockMvc.perform(post("/api/mfa/disable").param("confirmationCode", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Service Error"));
    }

    // ==========================================
    // Regenerate Backup Codes Tests
    // ==========================================

    @Test
    void testRegenerateBackupCodes_Success() throws Exception {
        String[] codes = {"A", "B"};
        when(mfaService.validateMFACode(anyString(), eq(Role.USER), eq("123456"))).thenReturn(true);
        when(mfaService.regenerateBackupCodes(anyString(), eq(Role.USER))).thenReturn(codes);

        mockMvc.perform(post("/api/mfa/regenerate-backup-codes").param("confirmationCode", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegenerateBackupCodes_InvalidCode() throws Exception {
        when(mfaService.validateMFACode(anyString(), any(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/mfa/regenerate-backup-codes").param("confirmationCode", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegenerateBackupCodes_Exception() throws Exception {
        when(mfaService.validateMFACode(anyString(), any(), anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/mfa/regenerate-backup-codes").param("confirmationCode", "123456"))
                .andExpect(status().isBadRequest());
    }
}