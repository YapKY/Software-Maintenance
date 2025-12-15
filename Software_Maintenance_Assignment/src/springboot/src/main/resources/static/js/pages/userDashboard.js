/**
 * User Dashboard Page Logic
 */
const UserDashboard = {
    
    currentUser: null,
    
    init: async function() {
        if (!Storage.isAuthenticated() || Storage.getUserRole() !== 'USER') {
            Helpers.redirectTo('login.html');
            return;
        }
        
        await this.loadUserProfile();

        const cpForm = document.getElementById('changePasswordForm');
        if (cpForm) {
            cpForm.addEventListener('submit', this.handleChangePassword.bind(this));
            if (window.Validation) {
                Validation.setupRealtimeValidation('newPassword', 'password');
                const passField = document.getElementById('newPassword');
                passField.addEventListener('input', () => this.updatePasswordStrength(passField.value));
            }
        }
    },
    
    loadUserProfile: async function() {
        try {
            Helpers.showLoading('loadingState');
            const profile = await UserAPI.getUserDashboard();
            if (profile) {
                this.currentUser = profile;
                this.displayUserProfile(profile);
                Helpers.hideLoading('loadingState');
                document.getElementById('profileSection').style.display = 'block';
                document.getElementById('dashboardCards').style.display = 'grid';
            }
        } catch (error) {
            console.error('Failed to load profile:', error);
            Helpers.showError('Failed to load profile data');
            Helpers.hideLoading('loadingState');
        }
    },
    
    displayUserProfile: function(profile) {
        const initials = Helpers.getInitials(profile.fullName);
        document.getElementById('userAvatar').textContent = initials;
        document.getElementById('userName').textContent = profile.fullName;
        document.getElementById('profileAvatar').textContent = initials;
        document.getElementById('profileName').textContent = profile.fullName;
        document.getElementById('profileEmail').textContent = profile.email;
        document.getElementById('profilePhone').textContent = profile.phoneNumber || 'Not provided';
        document.getElementById('profileRole').textContent = profile.role;
        
        const emailStatusEl = document.getElementById('emailStatus');
        emailStatusEl.innerHTML = profile.emailVerified ? 
            '<span class="mfa-status mfa-enabled">Verified</span>' : 
            '<span class="mfa-status mfa-disabled">Not Verified</span>';
            
        const mfaStatusEl = document.getElementById('mfaStatus');
        mfaStatusEl.innerHTML = profile.mfaEnabled ? 
            '<span class="mfa-status mfa-enabled">Enabled</span>' : 
            '<span class="mfa-status mfa-disabled">Disabled</span>';
            
        document.getElementById('lastLogin').textContent = Helpers.formatDateTime(profile.lastLoginAt);
        document.getElementById('memberSince').textContent = Helpers.formatDate(profile.createdAt);
    },
    
    showChangePasswordModal: function() {
        // Clear previous modal errors when opening
        const errorEl = document.getElementById('modalAlertError');
        if (errorEl) errorEl.classList.remove('show');
        
        document.getElementById('changePasswordModal').style.display = 'block';
    },
    
    hideChangePasswordModal: function() {
        document.getElementById('changePasswordModal').style.display = 'none';
        document.getElementById('changePasswordForm').reset();
        Validation.clearAllFieldErrors();
        
        // Clear modal errors when closing
        const errorEl = document.getElementById('modalAlertError');
        if (errorEl) errorEl.classList.remove('show');
    },
    
    handleChangePassword: async function(event) {
        event.preventDefault();
        
        // Hide global messages just in case
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        // Hide local modal error initially
        const errorEl = document.getElementById('modalAlertError');
        if (errorEl) errorEl.classList.remove('show');
        
        const current = document.getElementById('currentPassword').value;
        const newPass = document.getElementById('newPassword').value;
        const confirm = document.getElementById('confirmNewPassword').value;
        
        // Validation with local error display targeting modalAlertError for non-field errors if any
        if (!current) { Validation.showFieldError('currentPassword', 'Current password is required'); return; }
        if (!Validation.isValidPassword(newPass, 'newPassword').valid) return;
        if (!Validation.passwordsMatch(newPass, confirm, 'confirmNewPassword').valid) return;
        
        try {
            const response = await UserAPI.changePassword(current, newPass, confirm);
            if (response.success) {
                // Success: close modal and show global success
                this.hideChangePasswordModal();
                Helpers.showSuccess('Password updated successfully');
            } else {
                // API Error (e.g. incorrect password): Show in MODAL
                Helpers.showError(response.message, 'modalAlertError');
            }
        } catch (error) {
            // Network/Exception Error: Show in MODAL
            Helpers.showError(error.message || 'Failed to update password', 'modalAlertError');
        }
    },

    updatePasswordStrength: function(password) {
        const strengthDiv = document.getElementById('passwordStrength');
        if (!password) { strengthDiv.style.display = 'none'; return; }
        strengthDiv.style.display = 'block';
        let strength = 0;
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[@$!%*?&]/.test(password)) strength++;
        
        let color = strength <= 2 ? '#dc3545' : strength <= 3 ? '#ffc107' : strength <= 4 ? '#17a2b8' : '#28a745';
        let message = strength <= 2 ? 'Weak' : strength <= 3 ? 'Fair' : strength <= 4 ? 'Good' : 'Strong';
        strengthDiv.innerHTML = `<small style="color: ${color}; font-weight: 600;">${message}</small>`;
    },
    
    handleLogout: async function() { await AuthAPI.logout(); },

    viewBookings: function() {
        if (this.currentUser && this.currentUser.id) {
            window.location.href = `/my-tickets?customerId=${this.currentUser.id}`;
        } else {
            Helpers.showError('User profile not loaded. Please try again.');
        }
    }
};

window.onload = function() { UserDashboard.init(); };
function handleLogout() { UserDashboard.handleLogout(); }
function showChangePasswordModal() { UserDashboard.showChangePasswordModal(); }
function hideChangePasswordModal() { UserDashboard.hideChangePasswordModal(); }
function viewBookings() { UserDashboard.viewBookings(); }