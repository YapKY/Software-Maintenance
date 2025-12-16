package com.example.springboot.adapter.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.AuthErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirebaseAdapterTest {

    @Mock
    private FirebaseAuth firebaseAuth;

    @InjectMocks
    private FirebaseAdapter firebaseAdapter;

    @Test
    void testCreateUser_Success() throws FirebaseAuthException {
        // Mock the UserRecord result
        UserRecord mockRecord = mock(UserRecord.class);
        when(mockRecord.getUid()).thenReturn("firebase-uid-123");
        
        // Mock createUser call
        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class))).thenReturn(mockRecord);

        // Fix: Use a valid password (>6 chars) to pass Firebase SDK internal validation
        String uid = firebaseAdapter.createUser("test@test.com", "password123", "Name");
        assertEquals("firebase-uid-123", uid);
    }

    @Test
    void testCreateUser_Failure() throws FirebaseAuthException {
        // Simulate Firebase exception
        // Fix: Use valid inputs so we actually reach the mock
        when(firebaseAuth.createUser(any())).thenThrow(new RuntimeException("Firebase Error"));
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            firebaseAdapter.createUser("error@test.com", "password123", "Name"));
        
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("Firebase Error"));
    }

    @Test
    void testSendPasswordResetEmail_Success() throws FirebaseAuthException {
        // 1. User exists
        when(firebaseAuth.getUserByEmail("test@test.com")).thenReturn(mock(UserRecord.class));
        // 2. Link generation works
        when(firebaseAuth.generatePasswordResetLink("test@test.com")).thenReturn("http://reset-link");

        assertDoesNotThrow(() -> firebaseAdapter.sendPasswordResetEmail("test@test.com"));
        
        verify(firebaseAuth).generatePasswordResetLink("test@test.com");
    }

    @Test
    void testSendPasswordResetEmail_UserNotFound() throws FirebaseAuthException {
        // Mock FirebaseAuthException with "USER_NOT_FOUND"
        FirebaseAuthException mockEx = mock(FirebaseAuthException.class);
        when(mockEx.getAuthErrorCode()).thenReturn(AuthErrorCode.USER_NOT_FOUND);
        
        when(firebaseAuth.getUserByEmail("unknown@test.com")).thenThrow(mockEx);

        // Should return silently (log warning)
        assertDoesNotThrow(() -> firebaseAdapter.sendPasswordResetEmail("unknown@test.com"));
        
        // Should NOT try to generate link
        verify(firebaseAuth, never()).generatePasswordResetLink(any());
    }

    @Test
    void testSendPasswordResetEmail_OtherFirebaseError() throws FirebaseAuthException {
        // Use a generic error code (not USER_NOT_FOUND) to trigger the catch block
        FirebaseAuthException mockEx = mock(FirebaseAuthException.class);
        when(mockEx.getAuthErrorCode()).thenReturn(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        when(mockEx.getMessage()).thenReturn("Simulated Firebase Error");

        when(firebaseAuth.getUserByEmail("error@test.com")).thenThrow(mockEx);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            firebaseAdapter.sendPasswordResetEmail("error@test.com"));
        
        assertTrue(ex.getMessage().contains("Failed to send password reset email"));
    }
}