package com.example.springboot.generator;

import com.example.springboot.factory.IdGeneratorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Passenger ID Generator
 * Now uses Factory Pattern via IdGeneratorFactory
 * 
 * @deprecated Use IdGeneratorFactory directly for better maintainability
 *             This class is kept for backward compatibility
 */
@Component
public class PassengerIdGenerator {

    @Autowired
    private IdGeneratorFactory idGeneratorFactory;

    /**
     * Generate next passenger ID based on current count
     * 
     * @return Sequential passenger ID (e.g., "P00001", "P00002", "P00003")
     */
    public String generateId() throws ExecutionException, InterruptedException {
        return idGeneratorFactory.getGenerator(IdGeneratorFactory.EntityType.PASSENGER).generateId();
    }
}
