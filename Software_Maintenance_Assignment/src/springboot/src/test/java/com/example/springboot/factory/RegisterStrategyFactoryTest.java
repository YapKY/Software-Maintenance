package com.example.springboot.factory;

import com.example.springboot.enums.Role;
import com.example.springboot.exception.UnauthorizedException;
import com.example.springboot.strategy.registration.AdminRegisterStrategy;
import com.example.springboot.strategy.registration.EmailRegisterStrategy;
import com.example.springboot.strategy.registration.RegisterStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisterStrategyFactoryTest {

    @Mock private EmailRegisterStrategy emailRegisterStrategy;
    @Mock private AdminRegisterStrategy adminRegisterStrategy;

    private RegisterStrategyFactory registerStrategyFactory;

    @BeforeEach
    void setUp() {
        registerStrategyFactory = new RegisterStrategyFactory(
            emailRegisterStrategy, 
            adminRegisterStrategy
        );
    }

    @Test
    @DisplayName("Get User Registration Strategy")
    void testGetRegisterStrategy_User() {
        RegisterStrategy strategy = registerStrategyFactory.getRegisterStrategy(Role.USER);
        assertEquals(emailRegisterStrategy, strategy);
    }

    @Test
    @DisplayName("Get Admin Registration Strategy")
    void testGetRegisterStrategy_Admin() {
        RegisterStrategy strategy = registerStrategyFactory.getRegisterStrategy(Role.ADMIN);
        assertEquals(adminRegisterStrategy, strategy);
    }

    @Test
    @DisplayName("Get Superadmin Strategy - Should Fail")
    void testGetRegisterStrategy_Superadmin() {
        assertThrows(UnauthorizedException.class, () -> {
            registerStrategyFactory.getRegisterStrategy(Role.SUPERADMIN);
        });
    }
}