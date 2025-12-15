/**
 * Forgot Password Page Logic
 */
const ForgotPasswordPage = {
    
    init: function() {
        const form = document.getElementById('forgotPasswordForm');
        if (form) {
            form.addEventListener('submit', this.handleSubmit.bind(this));
        }
        
        Validation.setupRealtimeValidation('resetEmail', 'email');
    },
    
    handleSubmit: async function(event) {
        event.preventDefault();
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        const emailInput = document.getElementById('resetEmail');
        const email = emailInput.value.trim();
        
        // Validation
        if (!Validation.isValidEmail(email, 'resetEmail').valid) {
            return;
        }
        
        const submitBtn = document.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Sending...</span>';
        
        try {
            const response = await AuthAPI.forgotPassword(email);
            
            if (response.success) {
                Helpers.showSuccess(response.message);
                emailInput.value = '';
            } else {
                Helpers.showError(response.message);
            }
        } catch (error) {
            Helpers.showError(error.message || 'Failed to send reset link');
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    }
};

document.addEventListener('DOMContentLoaded', () => ForgotPasswordPage.init());