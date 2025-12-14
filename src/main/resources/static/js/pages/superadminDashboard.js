/**
 * Superadmin Dashboard Page Logic
 */
const SuperadminDashboard = {
    
    currentSuperadmin: null,
    adminsList: [],
    
    init: async function() {
        if (!Storage.isAuthenticated() || Storage.getUserRole() !== 'SUPERADMIN') {
            Helpers.redirectTo('login.html');
            return;
        }
        await this.loadSuperadminProfile();
        await this.loadAdminsList();

        // Setup Password Change Modal Logic
        const cpForm = document.getElementById('changePasswordForm');
        if (cpForm) {
            cpForm.addEventListener('submit', this.handleChangePassword.bind(this));
            if (window.Validation) {
                Validation.setupRealtimeValidation('newPassword', 'password');
                const passField = document.getElementById('newPassword');
                passField.addEventListener('input', () => this.updatePasswordStrength(passField.value));
            }
        }

        // Setup Create Staff Modal Real-time Validation
        this.setupCreateAdminValidation();
    },

    /**
     * Setup validation listeners for Create Staff form
     */
    setupCreateAdminValidation: function() {
        if (window.Validation) {
            Validation.setupRealtimeValidation('adminFullName', 'name');
            Validation.setupRealtimeValidation('adminEmail', 'email');
            Validation.setupRealtimeValidation('adminPassword', 'password');
            Validation.setupRealtimeValidation('adminPosition', 'position');
            
            // [UPDATED] Custom validation for Optional Phone
            // Only validate format if value is NOT empty
            const phoneInput = document.getElementById('adminPhone');
            if (phoneInput) {
                phoneInput.addEventListener('blur', function() {
                    const value = this.value.trim();
                    if (value !== '') {
                        // If typed, check format
                        Validation.isValidPhone(value, 'adminPhone');
                    } else {
                        // If empty, clear any previous errors (it's optional)
                        Validation.clearFieldError('adminPhone');
                    }
                });
                
                // Clear error immediately when user starts typing again
                phoneInput.addEventListener('input', function() {
                    Validation.clearFieldError('adminPhone');
                });
            }
            
            const genderSelect = document.getElementById('adminGender');
            if (genderSelect) {
                genderSelect.addEventListener('change', function() {
                    Validation.isValidGender(this.value, 'adminGender');
                });
            }
        }
    },
    
    loadSuperadminProfile: async function() {
        try {
            Helpers.showLoading('loadingState');
            const profile = await UserAPI.getSuperadminDashboard();
            if (profile) {
                this.currentSuperadmin = profile;
                this.displaySuperadminProfile(profile);
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
    
    displaySuperadminProfile: function(profile) {
        const initials = Helpers.getInitials(profile.fullName);
        document.getElementById('userAvatar').textContent = initials;
        document.getElementById('userName').textContent = profile.fullName;
        document.getElementById('profileAvatar').textContent = initials;
        document.getElementById('profileName').textContent = profile.fullName;
        document.getElementById('profileEmail').textContent = profile.email;
        document.getElementById('profileRole').textContent = profile.role;
        document.getElementById('totalAdmins').textContent = profile.totalAdminsCreated || 0;
        
        const mfaStatusEl = document.getElementById('mfaStatus');
        mfaStatusEl.innerHTML = '<span class="mfa-status mfa-enabled">Enabled (Required)</span>';
        
        document.getElementById('lastLogin').textContent = Helpers.formatDateTime(profile.lastLoginAt);
        document.getElementById('memberSince').textContent = Helpers.formatDate(profile.createdAt);
    },

    loadAdminsList: async function() {
        try {
            const data = await AdminAPI.getAdminsList();
            if (data) {
                this.adminsList = data.admins || [];
                this.displayAdminsList();
            }
        } catch (error) {
            console.error('Failed to load staff list:', error);
        }
    },
    
    displayAdminsList: function() {
        const container = document.getElementById('adminsListContainer');
        if (this.adminsList.length === 0) {
            container.innerHTML = '<p class="text-center">No staff created yet.</p>';
            return;
        }
        const html = this.adminsList.map(admin => `
            <div class="admin-card">
                <div class="admin-info">
                    <h4>${admin.name}</h4>
                    <p>${admin.email}</p>
                    <small>${admin.position} | Created: ${Helpers.formatDate(admin.createdAt)}</small>
                </div>
                <div class="admin-status">
                    ${admin.mfaEnabled ? 
                        '<span class="mfa-status mfa-enabled">MFA On</span>' : 
                        '<span class="mfa-status mfa-disabled">MFA Off</span>'
                    }
                </div>
            </div>
        `).join('');
        container.innerHTML = html;
    },
    
    showCreateAdminModal: function() {
        document.getElementById('createAdminModal').style.display = 'block';
    },
    
    hideCreateAdminModal: function() {
        document.getElementById('createAdminModal').style.display = 'none';
        document.getElementById('createAdminForm').reset();
        Validation.clearAllFieldErrors();
    },
    
    createAdmin: async function(event) {
        event.preventDefault();
        
        // 1. Clear previous errors
        Validation.clearAllFieldErrors();
        
        // 2. Get form values
        const name = document.getElementById('adminFullName').value;
        const email = document.getElementById('adminEmail').value;
        const staffPass = document.getElementById('adminPassword').value;
        const phoneNumber = document.getElementById('adminPhone').value;
        const gender = document.getElementById('adminGender').value;
        const position = document.getElementById('adminPosition').value;
        const mfaEnabled = false; 

        // 3. Validate mandatory fields
        let isValid = true;

        if (!Validation.isValidName(name, 'adminFullName').valid) isValid = false;
        if (!Validation.isValidEmail(email, 'adminEmail').valid) isValid = false;
        if (!Validation.isValidPassword(staffPass, 'adminPassword').valid) isValid = false;
        if (!Validation.isValidGender(gender, 'adminGender').valid) isValid = false;
        if (!Validation.isValidPosition(position, 'adminPosition').valid) isValid = false;
        
        // [UPDATED] Validate Phone ONLY if entered (Optional)
        if (phoneNumber && phoneNumber.trim() !== '') {
            if (!Validation.isValidPhone(phoneNumber, 'adminPhone').valid) isValid = false;
        }

        // 4. Stop if any validation failed
        if (!isValid) return;

        try {
            const data = await AdminAPI.createAdmin({ 
                name, 
                email, 
                staffPass, 
                phoneNumber, 
                gender, 
                position, 
                mfaEnabled 
            });
            
            if (data.success) {
                Helpers.showSuccess('Staff account created successfully!');
                this.hideCreateAdminModal();
                await this.loadAdminsList();
                await this.loadSuperadminProfile();
            } else {
                Helpers.showError(data.message || 'Failed to create staff');
            }
        } catch (error) {
            console.error('Create staff error:', error);
            if (error.data && error.data.fieldErrors) {
                // Map backend errors if needed
            }
            Helpers.showError(error.message || 'Failed to create staff account');
        }
    },
    
    showChangePasswordModal: function() {
        const errorEl = document.getElementById('modalAlertError');
        if (errorEl) errorEl.classList.remove('show');
        document.getElementById('changePasswordModal').style.display = 'block';
    },
    
    hideChangePasswordModal: function() {
        document.getElementById('changePasswordModal').style.display = 'none';
        document.getElementById('changePasswordForm').reset();
        Validation.clearAllFieldErrors();
        const errorEl = document.getElementById('modalAlertError');
        if (errorEl) errorEl.classList.remove('show');
    },
    
    handleChangePassword: async function(event) {
        event.preventDefault();
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        const errorEl = document.getElementById('modalAlertError');
        if (errorEl) errorEl.classList.remove('show');
        
        const current = document.getElementById('currentPassword').value;
        const newPass = document.getElementById('newPassword').value;
        const confirm = document.getElementById('confirmNewPassword').value;
        
        if (!current) { Validation.showFieldError('currentPassword', 'Current password is required'); return; }
        if (!Validation.isValidPassword(newPass, 'newPassword').valid) return;
        if (!Validation.passwordsMatch(newPass, confirm, 'confirmNewPassword').valid) return;
        
        try {
            const response = await UserAPI.changePassword(current, newPass, confirm);
            if (response.success) {
                this.hideChangePasswordModal();
                Helpers.showSuccess('Password updated successfully');
            } else {
                Helpers.showError(response.message, 'modalAlertError');
            }
        } catch (error) {
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

    viewAllUsers: function() { alert('View all users feature - Coming soon!\nThis will display all users, admins, and superadmins.'); },
    viewSystemConfig: function() { alert('System configuration - Coming soon!\nThis will show and allow editing system settings.'); },
    handleLogout: async function() { await AuthAPI.logout(); }
};

window.onload = function() { SuperadminDashboard.init(); };
function handleLogout() { SuperadminDashboard.handleLogout(); }
function showCreateAdminModal() { SuperadminDashboard.showCreateAdminModal(); }
function hideCreateAdminModal() { SuperadminDashboard.hideCreateAdminModal(); }
function createAdmin(event) { SuperadminDashboard.createAdmin(event); }
function viewAllUsers() { SuperadminDashboard.viewAllUsers(); }
function viewSystemConfig() { SuperadminDashboard.viewSystemConfig(); }
function showChangePasswordModal() { SuperadminDashboard.showChangePasswordModal(); }
function hideChangePasswordModal() { SuperadminDashboard.hideChangePasswordModal(); }