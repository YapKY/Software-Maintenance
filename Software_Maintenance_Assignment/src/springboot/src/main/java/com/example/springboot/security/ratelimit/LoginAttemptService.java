package com.example.springboot.security.ratelimit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * LoginAttemptService - Rate limiting for login attempts
 * Used by RateLimitedAuthService decorator
 */
@Slf4j
@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;
    
    private final LoadingCache<String, Integer> attemptsCache;
    
    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(BLOCK_DURATION_MINUTES, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });
    }
    
    public void loginSucceeded(String email) {
        attemptsCache.invalidate(email);
        log.info("Login succeeded, rate limit cleared for: {}", email);
    }
    
    public void loginFailed(String email) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(email);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(email, attempts);
        log.warn("Login failed for: {}, attempts: {}", email, attempts);
    }
    
    public boolean isBlocked(String email) {
        try {
            return attemptsCache.get(email) >= MAX_ATTEMPTS;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
