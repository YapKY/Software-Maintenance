package com.example.springboot.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;

/**
 * Custom ID generator for Passenger entity
 * Generates IDs in format: P000001, P000002, etc.
 */
public class PassengerIdGenerator implements IdentifierGenerator {

    private long counter = 0;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        counter++;
        return String.format("P%06d", counter);
    }
}
