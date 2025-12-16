package com.example.springboot.generator;

import com.example.springboot.factory.IdGeneratorFactory;
import com.example.springboot.factory.IdGeneratorFactory.EntityType;
import com.example.springboot.factory.IdGeneratorFactory.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerIdGeneratorTest {

    @Mock
    private IdGeneratorFactory idGeneratorFactory;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private PassengerIdGenerator passengerIdGenerator;

    @Test
    void testGenerateId() throws ExecutionException, InterruptedException {
        // Arrange
        when(idGeneratorFactory.getGenerator(EntityType.PASSENGER)).thenReturn(idGenerator);
        when(idGenerator.generateId()).thenReturn("P00009");

        // Act
        String result = passengerIdGenerator.generateId();

        // Assert
        assertEquals("P00009", result);
        verify(idGeneratorFactory).getGenerator(EntityType.PASSENGER);
        verify(idGenerator).generateId();
    }
}