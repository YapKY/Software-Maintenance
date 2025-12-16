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
class StaffIdGeneratorTest {

    @Mock
    private IdGeneratorFactory idGeneratorFactory;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private StaffIdGenerator staffIdGenerator;

    @Test
    void testGenerateId() throws ExecutionException, InterruptedException {
        // Arrange
        when(idGeneratorFactory.getGenerator(EntityType.STAFF)).thenReturn(idGenerator);
        when(idGenerator.generateId()).thenReturn("S001");

        // Act
        String result = staffIdGenerator.generateId();

        // Assert
        assertEquals("S001", result);
        verify(idGeneratorFactory).getGenerator(EntityType.STAFF);
        verify(idGenerator).generateId();
    }
}