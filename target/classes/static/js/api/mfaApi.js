/**
 * MFA API Calls - Fixed for correct backend integration
 */
const MFAAPI = {
    
    /**
     * Parse error response from API
     */
    parseError: async function(response) {
        try {
            const data = await response.json();
            const error = new Error(data.message || 'Request failed');
            error.data = data;
            return error;
        } catch (e) {
            return new Error('Network error. Please check your connection.');
        }
    },
    
    /**
     * Setup MFA - Returns QR code and secret
     */
    setupMFA: async function() {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.MFA_SETUP}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            const data = await response.json();
            console.log('📥 MFA Setup response:', data);
            
            return data;
            
        } catch (error) {
            console.error('❌ MFA setup error:', error);
            throw error;
        }
    },
    
    /**
     * Get MFA status - Check if MFA is enabled
     */
    getMFAStatus: async function() {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.MFA_STATUS}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            const data = await response.json();
            console.log('📥 MFA Status response:', data);
            
            return data;
            
        } catch (error) {
            console.error('❌ MFA status error:', error);
            throw error;
        }
    },
    
    /**
     * Validate MFA code during setup
     * This enables MFA if code is valid
     */
    validateMFACode: async function(code) {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.MFA_VALIDATE}?code=${encodeURIComponent(code)}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            const data = await response.json();
            console.log('📥 MFA Validation response:', data);
            
            return data;
            
        } catch (error) {
            console.error('❌ MFA validation error:', error);
            throw error;
        }
    },
    
    /**
     * Disable MFA
     */
    disableMFA: async function(confirmationCode) {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.MFA_DISABLE}?confirmationCode=${encodeURIComponent(confirmationCode)}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            const data = await response.json();
            console.log('📥 MFA Disable response:', data);
            
            return data;
            
        } catch (error) {
            console.error('❌ MFA disable error:', error);
            throw error;
        }
    }
};