package com.example.maintenance.security.ratelimit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * RateLimiter - Prevents brute force attacks
 * Uses Guava cache to track login attempts by IP/email
 */
@Slf4j
@Component
public class RateLimiter {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;
    
    private final LoadingCache<String, Integer> attemptsCache;
    private final LoadingCache<String, Long> blockCache;
    
    public RateLimiter() {
        // Cache for tracking attempts
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(BLOCK_DURATION_MINUTES, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });
        
        // Cache for tracking blocked users
        blockCache = CacheBuilder.newBuilder()
            .expireAfterWrite(BLOCK_DURATION_MINUTES, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Long>() {
                @Override
                public Long load(String key) {
                    return 0L;
                }
            });
    }
    
    /**
     * Check if identifier is currently blocked
     */
    public boolean isBlocked(String identifier) {
        try {
            int attempts = attemptsCache.get(identifier);
            if (attempts >= MAX_ATTEMPTS) {
                Long blockTime = blockCache.get(identifier);
                if (blockTime == 0L) {
                    blockCache.put(identifier, System.currentTimeMillis());
                    log.warn("Identifier blocked due to {} failed attempts: {}", attempts, identifier);
                }
                return true;
            }
            return false;
        } catch (ExecutionException e) {
            log.error("Error checking rate limit: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Record a failed attempt
     */
    public void recordFailedAttempt(String identifier) {
        try {
            int attempts = attemptsCache.get(identifier);
            attempts++;
            attemptsCache.put(identifier, attempts);
            log.warn("Failed attempt #{} for: {}", attempts, identifier);
            
            if (attempts >= MAX_ATTEMPTS) {
                blockCache.put(identifier, System.currentTimeMillis());
                log.error("Identifier blocked after {} attempts: {}", attempts, identifier);
            }
        } catch (ExecutionException e) {
            log.error("Error recording failed attempt: {}", e.getMessage());
        }
    }
    
    /**
     * Clear attempts for identifier (after successful login)
     */
    public void clearAttempts(String identifier) {
        attemptsCache.invalidate(identifier);
        blockCache.invalidate(identifier);
        log.info("Rate limit cleared for: {}", identifier);
    }
    
    /**
     * Get remaining attempts
     */
    public int getRemainingAttempts(String identifier) {
        try {
            int attempts = attemptsCache.get(identifier);
            return Math.max(0, MAX_ATTEMPTS - attempts);
        } catch (ExecutionException e) {
            return MAX_ATTEMPTS;
        }
    }
    
    /**
     * Get block duration in minutes
     */
    public int getBlockDurationMinutes() {
        return BLOCK_DURATION_MINUTES;
    }
    
    /**
     * Get time remaining in block (in seconds)
     */
    public long getBlockTimeRemaining(String identifier) {
        try {
            Long blockTime = blockCache.get(identifier);
            if (blockTime == 0L) {
                return 0L;
            }
            
            long elapsed = System.currentTimeMillis() - blockTime;
            long blockDurationMs = BLOCK_DURATION_MINUTES * 60 * 1000;
            long remaining = blockDurationMs - elapsed;
            
            return Math.max(0, remaining / 1000); // Return seconds
        } catch (ExecutionException e) {
            return 0L;
        }
    }
    
    /**
     * Manually unblock an identifier (for admin purposes)
     */
    public void unblock(String identifier) {
        attemptsCache.invalidate(identifier);
        blockCache.invalidate(identifier);
        log.info("Identifier manually unblocked: {}", identifier);
    }
    
    /**
     * Get current attempt count
     */
    public int getAttemptCount(String identifier) {
        try {
            return attemptsCache.get(identifier);
        } catch (ExecutionException e) {
            return 0;
        }
    }
}
