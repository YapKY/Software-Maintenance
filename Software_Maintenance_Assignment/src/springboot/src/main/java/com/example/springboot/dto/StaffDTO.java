package com.example.springboot.dto;

/**
 * Builder Pattern - Staff DTO
 * Data Transfer Object for Staff with Builder pattern
 * Separates API representation from entity model
 */
public class StaffDTO {
    private String staffId;
    private String position;
    private String name;
    private String email;
    private String phoneNumber;
    private String gender;

    private StaffDTO(Builder builder) {
        this.staffId = builder.staffId;
        this.position = builder.position;
        this.name = builder.name;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.gender = builder.gender;
    }

    // Getters
    public String getStaffId() {
        return staffId;
    }

    public String getPosition() {
        return position;
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
     * Builder class for StaffDTO
     */
    public static class Builder {
        private String staffId;
        private String position;
        private String name;
        private String email;
        private String phoneNumber;
        private String gender;

        public Builder staffId(String staffId) {
            this.staffId = staffId;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
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

        public StaffDTO build() {
            return new StaffDTO(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
