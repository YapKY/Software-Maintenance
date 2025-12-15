package com.example.springboot.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Template Method Pattern - Base Service
 * Provides common CRUD operations template for all services
 * Subclasses can override specific steps while maintaining the overall
 * structure
 * 
 * @param <T>  The entity type
 * @param <ID> The ID type
 */
public abstract class BaseService<T, ID> {

    /**
     * Template method for creating an entity
     * Defines the algorithm structure, delegates validation to subclasses
     */
    public T create(T entity) throws IllegalArgumentException {
        try {
            // Step 1: Validate entity (delegated to subclass)
            validateForCreate(entity);

            // Step 2: Pre-process (hook method)
            preCreate(entity);

            // Step 3: Save entity (delegated to subclass)
            T savedEntity = saveEntity(entity);

            // Step 4: Post-process (hook method)
            postCreate(savedEntity);

            return savedEntity;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error creating entity", e);
        }
    }

    /**
     * Template method for updating an entity
     */
    public T update(ID id, T entity) throws IllegalArgumentException {
        try {
            // Step 1: Validate entity exists
            Optional<T> existingEntity = findById(id);
            if (!existingEntity.isPresent()) {
                throw new IllegalArgumentException("Entity not found with id: " + id);
            }

            // Step 2: Validate update data (delegated to subclass)
            validateForUpdate(existingEntity.get(), entity);

            // Step 3: Pre-process (hook method)
            preUpdate(existingEntity.get(), entity);

            // Step 4: Update entity (delegated to subclass)
            T updatedEntity = updateEntity(id, entity);

            // Step 5: Post-process (hook method)
            postUpdate(updatedEntity);

            return updatedEntity;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating entity", e);
        }
    }

    /**
     * Template method for deleting an entity
     */
    public void delete(ID id) throws IllegalArgumentException {
        try {
            // Step 1: Validate entity exists
            Optional<T> existingEntity = findById(id);
            if (!existingEntity.isPresent()) {
                throw new IllegalArgumentException("Entity not found with id: " + id);
            }

            // Step 2: Pre-process (hook method)
            preDelete(existingEntity.get());

            // Step 3: Delete entity (delegated to subclass)
            deleteEntity(id);

            // Step 4: Post-process (hook method)
            postDelete(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error deleting entity", e);
        }
    }

    // Abstract methods that must be implemented by subclasses
    protected abstract void validateForCreate(T entity) throws IllegalArgumentException;

    protected abstract void validateForUpdate(T existingEntity, T newEntity) throws IllegalArgumentException;

    protected abstract T saveEntity(T entity) throws ExecutionException, InterruptedException;

    protected abstract T updateEntity(ID id, T entity) throws ExecutionException, InterruptedException;

    protected abstract void deleteEntity(ID id) throws ExecutionException, InterruptedException;

    public abstract Optional<T> findById(ID id) throws ExecutionException, InterruptedException;

    public abstract List<T> findAll() throws ExecutionException, InterruptedException;

    // Hook methods - can be overridden by subclasses if needed
    protected void preCreate(T entity) {
        // Default: do nothing
    }

    protected void postCreate(T entity) {
        // Default: do nothing
    }

    protected void preUpdate(T existingEntity, T newEntity) {
        // Default: do nothing
    }

    protected void postUpdate(T entity) {
        // Default: do nothing
    }

    protected void preDelete(T entity) {
        // Default: do nothing
    }

    protected void postDelete(ID id) {
        // Default: do nothing
    }
}
