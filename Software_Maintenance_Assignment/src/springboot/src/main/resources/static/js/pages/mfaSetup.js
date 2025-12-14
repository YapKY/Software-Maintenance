/**
 * MFA Setup Page Logic
 */
const MFASetupPage = {
    
    mfaData: null,
    
    /**
     * Initialize MFA setup page
     */
    init: async function() {
        // Check authentication
        if (!Storage.isAuthenticated()) {
            Helpers.redirectTo('login.html');
            return;
        }
        
        // Load MFA status
        await this.loadMFAStatus();
    },
    
    /**
     * Load current MFA status
     */
    loadMFAStatus: async function() {
        try {
            const response = await MFAAPI.getMFAStatus();
            
            if (response.success) {
                const status = response.data;
                
                if (status.mfaEnabled) {
                    // MFA already enabled
                    document.getElementById('setupSection').style.display = 'none';
                    document.getElementById('enabledSection').style.display = 'block';
                } else {
                    // MFA not enabled, show setup
                    document.getElementById('setupSection').style.display = 'block';
                    document.getElementById('enabledSection').style.display = 'none';
                }
            }
            
        } catch (error) {
            console.error('Failed to load MFA status:', error);
        }
    },
    
    /**
     * Setup MFA
     */
    setupMFA: async function() {
        try {
            Helpers.showLoading('setupLoading');
            
            const response = await MFAAPI.setupMFA();
            
            if (response.success) {
                this.mfaData = response.data;
                this.displayQRCode(response.data);
                
                document.getElementById('setupForm').style.display = 'none';
                document.getElementById('qrSection').style.display = 'block';
            } else {
                Helpers.showError(response.message);
            }
            
            Helpers.hideLoading('setupLoading');
            
        } catch (error) {
            console.error('MFA setup error:', error);
            Helpers.showError(error.message || 'Failed to setup MFA');
            Helpers.hideLoading('setupLoading');
        }
    },
    
    /**
     * Display QR code and backup codes
     */
    displayQRCode: function(mfaData) {
        // Display QR code
        const qrImg = document.getElementById('qrCode');
        qrImg.src = mfaData.qrCodeUrl;
        
        // Display secret (for manual entry)
        document.getElementById('secretKey').textContent = mfaData.secret;
        
        // Display backup codes
        const backupCodesEl = document.getElementById('backupCodes');
        backupCodesEl.innerHTML = mfaData.backupCodes
            .map(code => `<div class="backup-code">${code}</div>`)
            .join('');
    },
    
    /**
     * Verify MFA code
     */
    verifyMFA: async function() {
        const code = document.getElementById('verifyCode').value;
        
        const validation = Validation.isValidMFACode(code);
        if (!validation.valid) {
            Helpers.showError(validation.message);
            return;
        }
        
        try {
            const response = await MFAAPI.validateMFACode(code);
            
            if (response.success && response.valid) {
                Helpers.showSuccess('MFA enabled successfully!');
                setTimeout(() => {
                    Helpers.redirectTo('user-dashboard.html');
                }, 2000);
            } else {
                Helpers.showError('Invalid MFA code. Please try again.');
            }
            
        } catch (error) {
            console.error('MFA verification error:', error);
            Helpers.showError('Failed to verify MFA code');
        }
    },
    
    /**
     * Disable MFA
     */
    disableMFA: async function() {
        const code = prompt('Enter your current MFA code to disable:');
        
        if (!code) return;
        
        try {
            const response = await MFAAPI.disableMFA(code);
            
            if (response.success) {
                Helpers.showSuccess('MFA disabled successfully');
                await this.loadMFAStatus();
            } else {
                Helpers.showError(response.message);
            }
            
        } catch (error) {
            console.error('Failed to disable MFA:', error);
            Helpers.showError('Failed to disable MFA');
        }
    }
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    MFASetupPage.init();
});