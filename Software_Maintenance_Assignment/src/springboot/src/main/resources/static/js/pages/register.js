/**
 * Registration Page Logic with Fixed reCAPTCHA Validation
 * Features:
 * - Field validation before reCAPTCHA check
 * - No alert popups for empty fields
 * - Loading states and redirect guidance
 */
const RegisterPage = {
    
    /**
     * Initialize registration page
     */
    init: function() {
        // Check if already logged in
        if (Storage.isAuthenticated()) {
            const role = Storage.getUserRole();
            Helpers.redirectByRole(role);
            return;
        }
        
        // Setup real-time validation
        this.setupValidation();
        
        // Attach event listeners
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', this.handleRegister.bind(this));
        }
        
        // Setup password match validation
        const confirmPassword = document.getElementById('regConfirmPassword');
        if (confirmPassword) {
            confirmPassword.addEventListener('blur', () => {
                const password = document.getElementById('regPassword').value;
                const confirm = confirmPassword.value;
                if (confirm && password !== confirm) {
                    Validation.showFieldError('regConfirmPassword', CONFIG.MESSAGES.PASSWORD_MISMATCH);
                }
            });
        }
    },
    
    setupValidation: function() {
        Validation.setupRealtimeValidation('regFullName', 'name');
        Validation.setupRealtimeValidation('regIcNo', 'ic'); // New
        Validation.setupRealtimeValidation('regEmail', 'email');
        Validation.setupRealtimeValidation('regPhone', 'phone');
        Validation.setupRealtimeValidation('regPassword', 'password');
        
        document.getElementById('regGender').addEventListener('change', function() {
            Validation.isValidGender(this.value, 'regGender');
        });

        // Password strength indicator
        const passwordField = document.getElementById('regPassword');
        if (passwordField) {
            passwordField.addEventListener('input', () => {
                this.updatePasswordStrength(passwordField.value);
            });
        }
    },
    
    updatePasswordStrength: function(password) {
        const strengthDiv = document.getElementById('passwordStrength');
        if (!strengthDiv) return;
        
        if (!password) {
            strengthDiv.style.display = 'none';
            return;
        }
        
        strengthDiv.style.display = 'block';
        
        let strength = 0;
        let message = '';
        let color = '';
        
        // Check criteria
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[@$!%*?&]/.test(password)) strength++;
        
        if (strength <= 2) {
            message = 'Weak password';
            color = '#dc3545';
        } else if (strength <= 3) {
            message = 'Fair password';
            color = '#ffc107';
        } else if (strength <= 4) {
            message = 'Good password';
            color = '#17a2b8';
        } else {
            message = 'Strong password';
            color = '#28a745';
        }
        
        strengthDiv.innerHTML = `<small style="color: ${color}; font-weight: 600;">${message}</small>`;
    },
    
    /**
     * Handle registration form submission
     * FIXED: Validate fields first, only show field errors initially
     */
    handleRegister: async function(event) {
        event.preventDefault();
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        // --- [FIX START] Define variable here ---
        let hasFieldError = false;
        // --- [FIX END] ---

        const fullName = document.getElementById('regFullName').value.trim();
        const icNo = document.getElementById('regIcNo').value.trim();
        const gender = document.getElementById('regGender').value;
        const email = document.getElementById('regEmail').value.trim();
        const phoneNumber = document.getElementById('regPhone').value.trim();
        const password = document.getElementById('regPassword').value;
        const confirmPassword = document.getElementById('regConfirmPassword').value;
        
        // STEP 1: Validate all fields FIRST
        
        // Validate IC
        if (!Validation.isValidIcNo(icNo, 'regIcNo').valid) hasFieldError = true;

        // Validate Gender
        if (!Validation.isValidGender(gender, 'regGender').valid) hasFieldError = true;
        
        // Validate name
        if (!fullName) {
            Validation.showFieldError('regFullName', CONFIG.MESSAGES.NAME_REQUIRED);
            hasFieldError = true;
        } else if (!Validation.isValidName(fullName, 'regFullName').valid) {
            hasFieldError = true;
        }
        
        // Validate email
        if (!email) {
            Validation.showFieldError('regEmail', CONFIG.MESSAGES.EMAIL_REQUIRED);
            hasFieldError = true;
        } else if (!Validation.isValidEmail(email, 'regEmail').valid) {
            hasFieldError = true;
        }
        
        // Validate phone
        if (!phoneNumber) {
            Validation.showFieldError('regPhone', CONFIG.MESSAGES.PHONE_REQUIRED);
            hasFieldError = true;
        } else if (!Validation.isValidPhone(phoneNumber, 'regPhone').valid) {
            hasFieldError = true;
        }
        
        // Validate password
        if (!password) {
            Validation.showFieldError('regPassword', CONFIG.MESSAGES.PASSWORD_REQUIRED);
            hasFieldError = true;
        } else if (!Validation.isValidPassword(password, 'regPassword').valid) {
            hasFieldError = true;
        }
        
        // Validate confirm password
        if (!confirmPassword) {
            Validation.showFieldError('regConfirmPassword', 'Please confirm your password');
            hasFieldError = true;
        } else if (password !== confirmPassword) {
            Validation.showFieldError('regConfirmPassword', CONFIG.MESSAGES.PASSWORD_MISMATCH);
            hasFieldError = true;
        }
        
        // If fields have errors, stop here
        if (hasFieldError) {
            return;
        }
        
        // STEP 2: Check reCAPTCHA ONLY after all fields are valid
        const recaptchaToken = grecaptcha.getResponse();
        if (!recaptchaToken) {
            Helpers.showError(CONFIG.MESSAGES.RECAPTCHA_REQUIRED);
            return;
        }
        
        // Disable button and show loading state
        const submitBtn = document.querySelector('#registerForm button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Creating Account...</span>';
        
        // Disable all form inputs
        const formInputs = document.querySelectorAll('#registerForm input, #registerForm button');
        formInputs.forEach(input => input.disabled = true);
        
        try {
            const userData = {
                name: fullName,
                custIcNo: icNo,
                gender: gender,
                email: email,
                phoneNumber: phoneNumber,
                password: password
            };
            
            console.log('📤 Submitting registration...');
            const response = await AuthAPI.registerUser(userData, recaptchaToken);
            
            console.log('📥 Registration response:', response);
            
            if (response.success) {
                // Clear form
                document.getElementById('registerForm').reset();
                document.getElementById('passwordStrength').style.display = 'none';
                Validation.clearAllFieldErrors();
                
                Helpers.showSuccess(
                    'Registration successful! ' +
                    'A verification email has been sent to ' + email + '. ' +
                    'Please check your inbox and verify your email before logging in.'
                );
                
                setTimeout(() => {
                    Helpers.showSuccess('Redirecting to login page...');
                }, 2000);
                
                setTimeout(() => {
                    Helpers.redirectTo('login.html');
                }, 4000);
            } else {
                this.handleRegistrationError(new Error(response.message || 'Registration failed'));
                
                // Re-enable form on error
                formInputs.forEach(input => input.disabled = false);
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
            }
            
        } catch (error) {
            this.handleRegistrationError(error);
            
            // Re-enable form on error
            formInputs.forEach(input => input.disabled = false);
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        } finally {
            grecaptcha.reset();
        }
    },
    
    handleRegistrationError: function(error) {
        console.error('❌ Registration error:', error);
        
        // Handle field-specific errors from backend
        if (error.data && error.data.fieldErrors) {
            const fieldMap = {
                'name': 'regFullName',      // Backend 'name' -> HTML 'regFullName'
                'custIcNo': 'regIcNo',      // Backend 'custIcNo' -> HTML 'regIcNo'
                'gender': 'regGender',      // Backend 'gender' -> HTML 'regGender'
                'email': 'regEmail',
                'phoneNumber': 'regPhone',
                'password': 'regPassword'
            };
            
            let hasFieldError = false;
            Object.keys(error.data.fieldErrors).forEach(field => {
                const fieldId = fieldMap[field] || field;
                const fieldElement = document.getElementById(fieldId);
                if (fieldElement) {
                    Validation.showFieldError(fieldId, error.data.fieldErrors[field]);
                    hasFieldError = true;
                } else {
                    console.warn(`Could not find input field for error: ${field} (Mapped ID: ${fieldId})`);
                }
            });
            
            // Only show general error if no field-specific errors
            if (!hasFieldError) {
                Helpers.showError(error.message || 'Registration failed. Please check your input.');
            }
        } else {
            // Show general error message
            const errorMessage = error.message || 'Registration failed. Please try again.';
            Helpers.showError(errorMessage);
        }
    }
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    RegisterPage.init();
});