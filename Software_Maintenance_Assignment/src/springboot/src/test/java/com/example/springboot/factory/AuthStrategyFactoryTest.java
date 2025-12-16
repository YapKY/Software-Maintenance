package com.example.springboot.factory;

import com.example.springboot.enums.AuthProvider;
import com.example.springboot.strategy.authentication.AuthStrategy;
import com.example.springboot.strategy.authentication.EmailAuthStrategy;
import com.example.springboot.strategy.authentication.FacebookAuthStrategy;
import com.example.springboot.strategy.authentication.GoogleAuthStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthStrategyFactoryTest {

    @Mock private EmailAuthStrategy emailAuthStrategy;
    @Mock private GoogleAuthStrategy googleAuthStrategy;
    @Mock private FacebookAuthStrategy facebookAuthStrategy;

    private AuthStrategyFactory authStrategyFactory;

    @BeforeEach
    void setUp() {
        authStrategyFactory = new AuthStrategyFactory(
            emailAuthStrategy, 
            googleAuthStrategy, 
            facebookAuthStrategy
        );
    }

    @Test
    @DisplayName("Get Email Strategy")
    void testGetAuthStrategy_Email() {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(AuthProvider.EMAIL);
        assertEquals(emailAuthStrategy, strategy);
    }

    @Test
    @DisplayName("Get Google Strategy")
    void testGetAuthStrategy_Google() {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(AuthProvider.GOOGLE);
        assertEquals(googleAuthStrategy, strategy);
    }

    @Test
    @DisplayName("Get Facebook Strategy")
    void testGetAuthStrategy_Facebook() {
        AuthStrategy strategy = authStrategyFactory.getAuthStrategy(AuthProvider.FACEBOOK);
        assertEquals(facebookAuthStrategy, strategy);
    }
}