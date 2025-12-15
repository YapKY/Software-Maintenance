package com.example.springboot.generator;

import com.example.springboot.factory.IdGeneratorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Staff/Admin ID Generator
 * Now uses Factory Pattern via IdGeneratorFactory
 * 
 * @deprecated Use IdGeneratorFactory directly for better maintainability
 *             This class is kept for backward compatibility
 */
@Component
public class StaffIdGenerator {

    @Autowired
    private IdGeneratorFactory idGeneratorFactory;

    /**
     * Generate next staff/admin ID based on current count
     * 
     * @return Sequential staff ID with prefix (e.g., "S001", "S002", "S003")
     */
    public String generateId() throws ExecutionException, InterruptedException {
        return idGeneratorFactory.getGenerator(IdGeneratorFactory.EntityType.STAFF).generateId();
    }
}
