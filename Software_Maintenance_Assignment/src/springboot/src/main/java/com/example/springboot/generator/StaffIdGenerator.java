package com.example.springboot.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom ID generator for Staff entity
 * Generates IDs in format: S0001, S0002, etc.
 */
public class StaffIdGenerator implements IdentifierGenerator {

    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        long nextId = counter.incrementAndGet();
        return String.format("S%04d", nextId);
    }
}
