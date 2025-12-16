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
class CustomerIdGeneratorTest {

    @Mock
    private IdGeneratorFactory idGeneratorFactory;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CustomerIdGenerator customerIdGenerator;

    @Test
    void testGenerateId() throws ExecutionException, InterruptedException {
        // Arrange
        when(idGeneratorFactory.getGenerator(EntityType.CUSTOMER)).thenReturn(idGenerator);
        when(idGenerator.generateId()).thenReturn("C00005");

        // Act
        String result = customerIdGenerator.generateId();

        // Assert
        assertEquals("C00005", result);
        verify(idGeneratorFactory).getGenerator(EntityType.CUSTOMER);
        verify(idGenerator).generateId();
    }
}