package com.example.springboot.dto;

/**
 * Builder Pattern - Customer DTO
 * Data Transfer Object for Customer with Builder pattern
 * Separates API representation from entity model
 */
public class CustomerDTO {
    private String custId;
    private String custIcNo;
    private String name;
    private String email;
    private String phoneNumber;
    private String gender;

    private CustomerDTO(Builder builder) {
        this.custId = builder.custId;
        this.custIcNo = builder.custIcNo;
        this.name = builder.name;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.gender = builder.gender;
    }

    // Getters
    public String getCustId() {
        return custId;
    }

    public String getCustIcNo() {
        return custIcNo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Builder class for CustomerDTO
     */
    public static class Builder {
        private String custId;
        private String custIcNo;
        private String name;
        private String email;
        private String phoneNumber;
        private String gender;

        public Builder custId(String custId) {
            this.custId = custId;
            return this;
        }

        public Builder custIcNo(String custIcNo) {
            this.custIcNo = custIcNo;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public CustomerDTO build() {
            return new CustomerDTO(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
