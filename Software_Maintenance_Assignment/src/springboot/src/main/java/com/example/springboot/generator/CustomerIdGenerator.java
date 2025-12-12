package com.example.springboot.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom ID generator for Customer entity
 * Generates IDs in format: C000001, C000002, etc.
 */
public class CustomerIdGenerator implements IdentifierGenerator {

    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        long nextId = counter.incrementAndGet();
        return String.format("C%06d", nextId);
    }
}
