/**
 * Enhanced Form Validation Utilities with Field-Level Error Display
 */
const Validation = {
    
    /**
     * Show error message under a specific field
     */
    showFieldError: function(fieldId, message) {
        const field = document.getElementById(fieldId);
        if (!field) return;
        
        // Remove any existing error
        this.clearFieldError(fieldId);
        
        // Add error class to field
        field.classList.add('error');
        
        // Create and insert error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'field-error';
        errorDiv.id = `${fieldId}-error`;
        errorDiv.textContent = message;
        
        // Insert after the field or its wrapper
        const wrapper = field.closest('.password-wrapper') || field.parentElement;
        wrapper.parentNode.insertBefore(errorDiv, wrapper.nextSibling);
    },
    
    /**
     * Clear error message from a specific field
     */
    clearFieldError: function(fieldId) {
        const field = document.getElementById(fieldId);
        if (!field) return;
        
        // Remove error class
        field.classList.remove('error');
        
        // Remove error message
        const errorDiv = document.getElementById(`${fieldId}-error`);
        if (errorDiv) {
            errorDiv.remove();
        }
    },
    
    /**
     * Clear all field errors on the page
     */
    clearAllFieldErrors: function() {
        // Remove all error classes
        document.querySelectorAll('.error').forEach(el => {
            el.classList.remove('error');
        });
        
        // Remove all error messages
        document.querySelectorAll('.field-error').forEach(el => {
            el.remove();
        });
    },
    
    /**
     * Validate email format
     */
    isValidEmail: function(email, fieldId = null) {
        if (!email || email.trim() === '') {
            const result = { valid: false, message: CONFIG.MESSAGES.EMAIL_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (!CONFIG.PATTERNS.EMAIL.test(email.trim())) {
            const result = { valid: false, message: CONFIG.MESSAGES.EMAIL_INVALID };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },
    
    /**
     * Validate password strength
     */
    isValidPassword: function(password, fieldId = null) {
        if (!password || password.length === 0) {
            const result = { valid: false, message: CONFIG.MESSAGES.PASSWORD_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (password.length < 8 || !CONFIG.PATTERNS.PASSWORD.test(password)) {
            const result = { valid: false, message: CONFIG.MESSAGES.PASSWORD_WEAK };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },
    
    /**
     * Validate phone number
     */
    isValidPhone: function(phone, fieldId = null) {
        if (!phone || phone.trim() === '') {
            const result = { valid: false, message: CONFIG.MESSAGES.PHONE_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (!CONFIG.PATTERNS.PHONE.test(phone.trim())) {
            const result = { valid: false, message: CONFIG.MESSAGES.PHONE_INVALID };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },
    
    /**
     * Validate name
     */
    isValidName: function(name, fieldId = null) {
        if (!name || name.trim() === '') {
            const result = { valid: false, message: CONFIG.MESSAGES.NAME_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (!CONFIG.PATTERNS.NAME.test(name.trim())) {
            const result = { valid: false, message: CONFIG.MESSAGES.NAME_INVALID };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },
    
    /**
     * Validate MFA code
     */
    isValidMFACode: function(code, fieldId = null) {
        if (!code || code.trim() === '') {
            const result = { valid: false, message: 'MFA code is required' };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (!CONFIG.PATTERNS.MFA_CODE.test(code.trim())) {
            const result = { valid: false, message: CONFIG.MESSAGES.MFA_CODE_INVALID };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },
    
    /**
     * Check if passwords match
     */
    passwordsMatch: function(password, confirmPassword, confirmFieldId = null) {
        if (password !== confirmPassword) {
            const result = { valid: false, message: CONFIG.MESSAGES.PASSWORD_MISMATCH };
            if (confirmFieldId) this.showFieldError(confirmFieldId, result.message);
            return result;
        }
        
        if (confirmFieldId) this.clearFieldError(confirmFieldId);
        return { valid: true };
    },
    
    /**
     * Validate Position (No digits)
     */
    isValidPosition: function(position, fieldId = null) {
        if (!position || position.trim() === '') {
            const result = { valid: false, message: CONFIG.MESSAGES.POSITION_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (!CONFIG.PATTERNS.POSITION.test(position.trim())) {
            const result = { valid: false, message: CONFIG.MESSAGES.POSITION_INVALID };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },

    /**
     * Real-time validation setup for a field
     */
    setupRealtimeValidation: function(fieldId, validationType) {
        const field = document.getElementById(fieldId);
        if (!field) {
            console.warn(`Field with id '${fieldId}' not found for validation setup`);
            return;
        }
        
        // Clear error on focus
        field.addEventListener('focus', () => {
            this.clearFieldError(fieldId);
        });

        // Validate on blur
        field.addEventListener('blur', () => {
            const value = field.value;
            
            switch(validationType) {
                case 'email':
                    this.isValidEmail(value, fieldId);
                    break;
                case 'password':
                    this.isValidPassword(value, fieldId);
                    break;
                case 'phone':
                    this.isValidPhone(value, fieldId);
                    break;
                case 'name':
                    this.isValidName(value, fieldId);
                    break;
                case 'mfa':
                    this.isValidMFACode(value, fieldId);
                    break;
                case 'ic':
                    this.isValidIcNo(value, fieldId);
                    break;
                case 'position':
                    this.isValidPosition(value, fieldId);
                    break;
            }
        });
        
        // Clear error on input
        field.addEventListener('input', () => {
            if (field.classList.contains('error')) {
                this.clearFieldError(fieldId);
            }
        });
    },

    /**
     * Validate IC Number
     */
    isValidIcNo: function(icNo, fieldId = null) {
        if (!icNo || icNo.trim() === '') {
            const result = { valid: false, message: CONFIG.MESSAGES.IC_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (!CONFIG.PATTERNS.IC_NO.test(icNo.trim())) {
            const result = { valid: false, message: CONFIG.MESSAGES.IC_INVALID };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },

    /**
     * Validate Gender
     */
    isValidGender: function(gender, fieldId = null) {
        if (!gender || gender === '') {
            const result = { valid: false, message: CONFIG.MESSAGES.GENDER_REQUIRED };
            if (fieldId) this.showFieldError(fieldId, result.message);
            return result;
        }
        
        if (fieldId) this.clearFieldError(fieldId);
        return { valid: true };
    },
};

// Make sure Validation is available globally
if (typeof window !== 'undefined') {
    window.Validation = Validation;
}