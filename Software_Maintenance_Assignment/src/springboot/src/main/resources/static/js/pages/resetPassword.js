/**
 * Reset Password Page Logic
 */
const ResetPasswordPage = {
    
    token: null,
    
    init: function() {
        // Get token from URL
        const urlParams = new URLSearchParams(window.location.search);
        this.token = urlParams.get('token');
        
        if (!this.token) {
            Helpers.showError('Invalid or missing reset token.');
            document.querySelector('button[type="submit"]').disabled = true;
            return;
        }
        
        const form = document.getElementById('resetPasswordForm');
        if (form) {
            form.addEventListener('submit', this.handleSubmit.bind(this));
        }
        
        // Validation Setup
        Validation.setupRealtimeValidation('newPassword', 'password');
        
        // Password Strength
        const passwordField = document.getElementById('newPassword');
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
        
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[@$!%*?&]/.test(password)) strength++;
        
        if (strength <= 2) { message = 'Weak'; color = '#dc3545'; }
        else if (strength <= 3) { message = 'Fair'; color = '#ffc107'; }
        else if (strength <= 4) { message = 'Good'; color = '#17a2b8'; }
        else { message = 'Strong'; color = '#28a745'; }
        
        strengthDiv.innerHTML = `<small style="color: ${color}; font-weight: 600;">${message}</small>`;
    },
    
    handleSubmit: async function(event) {
        event.preventDefault();
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        // Validation
        if (!Validation.isValidPassword(newPassword, 'newPassword').valid) return;
        if (!Validation.passwordsMatch(newPassword, confirmPassword, 'confirmPassword').valid) return;
        
        const submitBtn = document.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Resetting...</span>';
        
        try {
            // Pass confirmPassword as the 3rd argument
            const response = await AuthAPI.resetPassword(this.token, newPassword, confirmPassword);
            
            if (response.success) {
                Helpers.showSuccess('Password reset successful! Redirecting to login...');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 2000);
            } else {
                Helpers.showError(response.message);
            }
        } catch (error) {
            Helpers.showError(error.message || 'Failed to reset password');
        } finally {
            if (submitBtn.innerText !== 'Resetting...') {
                 submitBtn.disabled = false;
                 submitBtn.innerHTML = originalText;
            }
        }
    }
};

document.addEventListener('DOMContentLoaded', () => ResetPasswordPage.init());