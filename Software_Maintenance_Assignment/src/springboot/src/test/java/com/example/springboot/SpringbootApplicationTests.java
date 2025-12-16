package com.example.springboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for SpringbootApplication
 * 
 * Coverage: Main method, Spring Boot application startup
 * Target: 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Springboot Application Tests")
class SpringbootApplicationTest {

    @Test
    @DisplayName("Should call SpringApplication.run with correct parameters")
    void testMain_CallsSpringApplicationRun() {
        // Arrange
        String[] args = new String[]{"--spring.main.web-environment=false"};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        // Act & Assert
        try (MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {
            springAppMock.when(() -> SpringApplication.run(SpringbootApplication.class, args))
                .thenReturn(mockContext);

            SpringbootApplication.main(args);

            springAppMock.verify(() -> SpringApplication.run(SpringbootApplication.class, args), times(1));
        }
    }

    @Test
    @DisplayName("Should call SpringApplication.run with empty args")
    void testMain_WithEmptyArgs() {
        // Arrange
        String[] emptyArgs = new String[]{};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        // Act & Assert
        try (MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {
            springAppMock.when(() -> SpringApplication.run(SpringbootApplication.class, emptyArgs))
                .thenReturn(mockContext);

            SpringbootApplication.main(emptyArgs);

            springAppMock.verify(() -> SpringApplication.run(SpringbootApplication.class, emptyArgs), times(1));
        }
    }

    @Test
    @DisplayName("Should call SpringApplication.run with null args")
    void testMain_WithNullArgs() {
        // Arrange
        String[] nullArgs = null;
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        // Act & Assert
        try (MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {
            springAppMock.when(() -> SpringApplication.run(eq(SpringbootApplication.class), any(String[].class)))
                .thenReturn(mockContext);

            SpringbootApplication.main(nullArgs);

            springAppMock.verify(() -> SpringApplication.run(eq(SpringbootApplication.class), any()), times(1));
        }
    }

    @Test
    @DisplayName("Should create SpringbootApplication instance")
    void testApplicationInstantiation() {
        // Act
        SpringbootApplication app = new SpringbootApplication();

        // Assert
        assertNotNull(app);
    }

    @Test
    @DisplayName("Main method should not throw exception with valid args")
    void testMain_NoExceptionWithValidArgs() {
        // Arrange
        String[] args = new String[]{"--server.port=8081"};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        // Act & Assert
        try (MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {
            springAppMock.when(() -> SpringApplication.run(SpringbootApplication.class, args))
                .thenReturn(mockContext);

            assertDoesNotThrow(() -> SpringbootApplication.main(args));
        }
    }

    @Test
    @DisplayName("Should be annotated with @SpringBootApplication")
    void testSpringBootApplicationAnnotation() {
        // Act
        boolean hasAnnotation = SpringbootApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class
        );

        // Assert
        assertTrue(hasAnnotation, "SpringbootApplication should be annotated with @SpringBootApplication");
    }

    @Test
    @DisplayName("Should have public main method")
    void testMainMethodExists() throws NoSuchMethodException {
        // Act
        var mainMethod = SpringbootApplication.class.getMethod("main", String[].class);

        // Assert
        assertNotNull(mainMethod);
        assertEquals("main", mainMethod.getName());
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
    }

    @Test
    @DisplayName("Main method should have void return type")
    void testMainMethodReturnType() throws NoSuchMethodException {
        // Act
        var mainMethod = SpringbootApplication.class.getMethod("main", String[].class);

        // Assert
        assertEquals(void.class, mainMethod.getReturnType());
    }

    // @Test
    // @DisplayName("Should call SpringApplication.run with application class")
    // void testMain_PassesCorrectClass() {
    //     // Arrange
    //     String[] args = new String[]{};
    //     ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

    //     // Act & Assert
    //     try (MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {
    //         springAppMock.when(() -> SpringApplication.run(SpringbootApplication.class, args))
    //             .thenReturn(mockContext);

    //         SpringbootApplication.main(args);

    //         // Verify the exact class is passed
    //         springAppMock.verify(() -> SpringApplication.run(
    //             argThat(clazz -> clazz.equals(SpringbootApplication.class)), 
    //             eq(args)
    //         ), times(1));
    //     }
    // }

    @Test
    @DisplayName("Should handle multiple invocations of main")
    void testMain_MultipleInvocations() {
        // Arrange
        String[] args1 = new String[]{"arg1"};
        String[] args2 = new String[]{"arg2"};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        // Act & Assert
        try (MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {
            springAppMock.when(() -> SpringApplication.run(eq(SpringbootApplication.class), any(String[].class)))
                .thenReturn(mockContext);

            SpringbootApplication.main(args1);
            SpringbootApplication.main(args2);

            springAppMock.verify(() -> SpringApplication.run(eq(SpringbootApplication.class), any(String[].class)), 
                times(2));
        }
    }
}