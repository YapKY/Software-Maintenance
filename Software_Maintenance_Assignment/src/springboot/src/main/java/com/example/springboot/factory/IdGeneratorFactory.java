package com.example.springboot.factory;

import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Factory Pattern - ID Generator Factory
 * Creates ID generators for different entity types
 * Eliminates code duplication across multiple generator classes
 */
@Component
public class IdGeneratorFactory {

    @Autowired
    private Firestore firestore;

    /**
     * Get ID generator for specific entity type
     * 
     * @param entityType The type of entity (e.g., "customer", "staff", "passenger")
     * @return Appropriate ID generator
     */
    public IdGenerator getGenerator(EntityType entityType) {
        switch (entityType) {
            case CUSTOMER:
                return new EntityIdGenerator(firestore, "customers", "C", 6);
            case STAFF:
                return new EntityIdGenerator(firestore, "staff", "S", 4);
            case PASSENGER:
                return new EntityIdGenerator(firestore, "passengers", "P", 6);
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    /**
     * Template Method Pattern - Base ID Generator
     * Provides common ID generation logic
     */
    public interface IdGenerator {
        String generateId() throws ExecutionException, InterruptedException;
    }

    /**
     * Concrete implementation of ID generator
     * Uses template method pattern for consistent ID generation
     */
    private static class EntityIdGenerator implements IdGenerator {
        private final Firestore firestore;
        private final String collectionName;
        private final String prefix;
        private final int digitCount;

        public EntityIdGenerator(Firestore firestore, String collectionName, String prefix, int digitCount) {
            this.firestore = firestore;
            this.collectionName = collectionName;
            this.prefix = prefix;
            this.digitCount = digitCount;
        }

        @Override
        public String generateId() throws ExecutionException, InterruptedException {
            long count = firestore.collection(collectionName).get().get().size();
            String formatString = "%0" + digitCount + "d";
            return prefix + String.format(formatString, count + 1);
        }
    }

    /**
     * Enum for entity types
     * Makes factory usage type-safe
     */
    public enum EntityType {
        CUSTOMER,
        STAFF,
        PASSENGER
    }
}
