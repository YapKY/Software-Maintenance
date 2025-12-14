/**
 * LocalStorage Utilities
 */
const Storage = {
    
    /**
     * Save access token
     */
    saveAccessToken: function(token) {
        localStorage.setItem(CONFIG.STORAGE_KEYS.ACCESS_TOKEN, token);
    },
    
    /**
     * Get access token
     */
    getAccessToken: function() {
        return localStorage.getItem(CONFIG.STORAGE_KEYS.ACCESS_TOKEN);
    },
    
    /**
     * Save refresh token
     */
    saveRefreshToken: function(token) {
        localStorage.setItem(CONFIG.STORAGE_KEYS.REFRESH_TOKEN, token);
    },
    
    /**
     * Get refresh token
     */
    getRefreshToken: function() {
        return localStorage.getItem(CONFIG.STORAGE_KEYS.REFRESH_TOKEN);
    },
    
    /**
     * Save user role
     */
    saveUserRole: function(role) {
        localStorage.setItem(CONFIG.STORAGE_KEYS.USER_ROLE, role);
    },
    
    /**
     * Get user role
     */
    getUserRole: function() {
        return localStorage.getItem(CONFIG.STORAGE_KEYS.USER_ROLE);
    },
    
    /**
     * Save user email
     */
    saveUserEmail: function(email) {
        localStorage.setItem(CONFIG.STORAGE_KEYS.USER_EMAIL, email);
    },
    
    /**
     * Get user email
     */
    getUserEmail: function() {
        return localStorage.getItem(CONFIG.STORAGE_KEYS.USER_EMAIL);
    },
    
    /**
     * Save auth response
     */
    saveAuthResponse: function(authResponse) {
        if (authResponse.tokens) {
            this.saveAccessToken(authResponse.tokens.accessToken);
            this.saveRefreshToken(authResponse.tokens.refreshToken);
            this.saveUserRole(authResponse.tokens.role);
            this.saveUserEmail(authResponse.tokens.email);
        }
    },
    
    /**
     * Clear all stored data
     */
    clearAll: function() {
        localStorage.removeItem(CONFIG.STORAGE_KEYS.ACCESS_TOKEN);
        localStorage.removeItem(CONFIG.STORAGE_KEYS.REFRESH_TOKEN);
        localStorage.removeItem(CONFIG.STORAGE_KEYS.USER_ROLE);
        localStorage.removeItem(CONFIG.STORAGE_KEYS.USER_EMAIL);
    },
    
    /**
     * Check if user is authenticated
     */
    isAuthenticated: function() {
        return this.getAccessToken() !== null;
    }
};