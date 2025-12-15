package com.example.springboot.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder Pattern - API Response Builder
 * Provides a fluent interface for building consistent API responses
 */
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private Map<String, Object> metadata;

    private ApiResponse(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.data = builder.data;
        this.metadata = builder.metadata;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Builder class implementing Builder Pattern
     */
    public static class Builder {
        private boolean success;
        private String message;
        private Object data;
        private Map<String, Object> metadata;

        public Builder() {
            this.metadata = new HashMap<>();
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this);
        }
    }

    // Convenience methods for common responses
    public static ApiResponse success(String message, Object data) {
        return new Builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse success(String message) {
        return new Builder()
                .success(true)
                .message(message)
                .build();
    }

    public static ApiResponse error(String message) {
        return new Builder()
                .success(false)
                .message(message)
                .build();
    }

    public static ApiResponse error(String message, Object data) {
        return new Builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
