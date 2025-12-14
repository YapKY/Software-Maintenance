/**
 * Application Configuration
 */
const CONFIG = {
    // API Base URL - Backend running on port 8081
    API_BASE_URL: 'https://localhost:8081',
    
    // OAuth Configuration
    OAUTH: {
        GOOGLE_CLIENT_ID: '590362290466-vm70ak6cveibfmrsui742s10jvu1iv0p.apps.googleusercontent.com',
        FACEBOOK_APP_ID: '25998306979771392'
    },
    
    // reCAPTCHA Configuration
    RECAPTCHA: {
        SITE_KEY: '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI' // Test key
    },
    
    // Local Storage Keys
    STORAGE_KEYS: {
        ACCESS_TOKEN: 'accessToken',
        REFRESH_TOKEN: 'refreshToken',
        USER_ROLE: 'userRole',
        USER_EMAIL: 'userEmail'
    },
    
    // API Endpoints
    ENDPOINTS: {
        // Authentication
        LOGIN: '/api/auth/login',
        LOGIN_GOOGLE: '/api/auth/login/google',
        LOGIN_FACEBOOK: '/api/auth/login/facebook',
        VERIFY_MFA: '/api/auth/verify-mfa',
        LOGOUT: '/api/auth/logout',
        
        // Registration
        REGISTER_USER: '/api/register/user',
        REGISTER_ADMIN: '/api/register/admin',
        
        // Dashboards
        DASHBOARD_USER: '/api/dashboard/user',
        DASHBOARD_ADMIN: '/api/dashboard/admin',
        DASHBOARD_SUPERADMIN: '/api/dashboard/superadmin',
        
        // User Profile
        USER_PROFILE: '/api/user/profile',
        
        // MFA
        MFA_SETUP: '/api/mfa/setup',
        MFA_STATUS: '/api/mfa/status',
        MFA_DISABLE: '/api/mfa/disable',
        MFA_VALIDATE: '/api/mfa/validate'
    },
    
    // Role-based Dashboard Redirects
    ROLE_REDIRECTS: {
        USER: '/user-dashboard.html',
        ADMIN: '/admin-dashboard.html',
        SUPERADMIN: '/superadmin-dashboard.html'
    },
    
    // Validation Patterns
    PATTERNS: {
        EMAIL: /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
        PASSWORD: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
        PHONE: /^\d{3}-\d{7,8}$/,
        NAME: /^[a-zA-Z\s]{2,}$/,
        MFA_CODE: /^\d{6}$/,
        IC_NO: /^\d{6}-\d{2}-\d{4}$/, // New Pattern
        POSITION: /^[^0-9]+$/
    },
    
    // Validation Messages
    MESSAGES: {
        EMAIL_REQUIRED: 'Email address is required',
        EMAIL_INVALID: 'Please enter a valid email address',
        PASSWORD_REQUIRED: 'Password is required',
        PASSWORD_WEAK: 'Password must be at least 8 characters with uppercase, lowercase, number, and special character',
        PASSWORD_MISMATCH: 'Passwords do not match',
        NAME_REQUIRED: 'Full name is required',
        NAME_INVALID: 'Name must contain only letters and spaces (minimum 2 characters)',
        PHONE_REQUIRED: 'Phone number is required',
        PHONE_INVALID: 'Phone format must be XXX-XXXXXXX or XXX-XXXXXXXX',
        RECAPTCHA_REQUIRED: 'Please complete the reCAPTCHA verification',
        MFA_CODE_INVALID: 'MFA code must be 6 digits',
        IC_REQUIRED: 'IC number is required',
        IC_INVALID: 'IC format must be XXXXXX-XX-XXXX',
        GENDER_REQUIRED: 'Please select a gender',
        POSITION_REQUIRED: 'Position is required',
        POSITION_INVALID: 'Position cannot contain digits',
    },
    
    // Security Settings
    SECURITY: {
        MAX_LOGIN_ATTEMPTS: 5,
        LOCKOUT_DURATION: 1 * 60 * 1000, // 1 minutes
        TOKEN_REFRESH_INTERVAL: 50 * 60 * 1000, // 50 minutes
        SESSION_TIMEOUT: 60 * 60 * 1000 // 1 hour
    }
};

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CONFIG;
}