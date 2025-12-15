package com.example.springboot.generator;

import com.example.springboot.factory.IdGeneratorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Customer ID Generator
 * Now uses Factory Pattern via IdGeneratorFactory
 * 
 * @deprecated Use IdGeneratorFactory directly for better maintainability
 *             This class is kept for backward compatibility
 */
@Component
public class CustomerIdGenerator {

    @Autowired
    private IdGeneratorFactory idGeneratorFactory;

    /**
     * Generate next customer ID based on current count
     * 
     * @return Sequential customer ID (e.g., "C00001", "C00002", "C00003")
     */
    public String generateId() throws ExecutionException, InterruptedException {
        return idGeneratorFactory.getGenerator(IdGeneratorFactory.EntityType.CUSTOMER).generateId();
    }
}
