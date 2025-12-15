package com.example.springboot.factory;

import com.example.springboot.enums.Role;
import com.example.springboot.exception.UnauthorizedException;
import com.example.springboot.strategy.registration.AdminRegisterStrategy;
import com.example.springboot.strategy.registration.EmailRegisterStrategy;
import com.example.springboot.strategy.registration.RegisterStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RegisterStrategyFactoryTest {

    @Mock private EmailRegisterStrategy emailRegisterStrategy;
    @Mock private AdminRegisterStrategy adminRegisterStrategy;

    @InjectMocks
    private RegisterStrategyFactory registerStrategyFactory;

    @Test
    @DisplayName("Should return EmailRegisterStrategy for USER role")
    void testGetRegisterStrategy_User() {
        RegisterStrategy strategy = registerStrategyFactory.getRegisterStrategy(Role.USER);
        assertEquals(emailRegisterStrategy, strategy);
    }

    @Test
    @DisplayName("Should return AdminRegisterStrategy for ADMIN role")
    void testGetRegisterStrategy_Admin() {
        RegisterStrategy strategy = registerStrategyFactory.getRegisterStrategy(Role.ADMIN);
        assertEquals(adminRegisterStrategy, strategy);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException for SUPERADMIN role")
    void testGetRegisterStrategy_Superadmin() {
        assertThrows(UnauthorizedException.class, () -> 
            registerStrategyFactory.getRegisterStrategy(Role.SUPERADMIN)
        );
    }
}