/**
 * Authentication API Calls with Enhanced Error Handling
 */
const AuthAPI = {
    
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
     * Login with email and password
     */
    login: async function(email, password, recaptchaToken, mfaCode = null) {
        try {
            console.log('📤 Sending login request to:', `${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN}`);
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: email,
                    password: password,
                    recaptchaToken: recaptchaToken,
                    mfaCode: mfaCode
                })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ Login error:', error);
            throw error;
        }
    },

    /**
     * Request Password Reset Email
     */
    forgotPassword: async function(email) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/auth/forgot-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ Forgot password error:', error);
            throw error;
        }
    },

    /**
     * Confirm Password Reset
     */
    resetPassword: async function(token, newPassword) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/auth/reset-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: token,
                    newPassword: newPassword
                })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ Reset password error:', error);
            throw error;
        }
    },
    
    /**
     * Login with Google
     */
    loginWithGoogle: async function(accessToken, recaptchaToken) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN_GOOGLE}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ provider: 'GOOGLE', accessToken, recaptchaToken })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ Google login error:', error);
            throw error;
        }
    },
    
    /**
     * Login with Facebook
     */
    loginWithFacebook: async function(accessToken, recaptchaToken) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN_FACEBOOK}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ provider: 'FACEBOOK', accessToken, recaptchaToken })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ Facebook login error:', error);
            throw error;
        }
    },
    
    /**
     * Register user
     */
    registerUser: async function(userData, recaptchaToken) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.REGISTER_USER}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    name: userData.name,           // Updated from fullName
                    custIcNo: userData.custIcNo,   // New Field
                    gender: userData.gender,       // New Field
                    email: userData.email,
                    phoneNumber: userData.phoneNumber,
                    password: userData.password,
                    recaptchaToken: recaptchaToken
                })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ Registration error:', error);
            throw error;
        }
    },
    
    /**
     * Verify MFA code
     */
    verifyMFA: async function(email, code, sessionToken) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.VERIFY_MFA}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: email,
                    code: code,
                    sessionToken: sessionToken
                })
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('❌ MFA verification error:', error);
            throw error;
        }
    },
    
    /**
     * Logout - UPDATED TO CLEAR COOKIE
     */
    logout: async function() {
        try {
            const token = Storage.getAccessToken();
            
            if (token) {
                await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGOUT}`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
            }
            
            // [MODIFIED] Clear the cookie
            document.cookie = "jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            
            Storage.clearAll();
            Helpers.redirectTo('login.html');
            
        } catch (error) {
            console.error('❌ Logout error:', error);
            document.cookie = "jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            Storage.clearAll();
            Helpers.redirectTo('login.html');
        }
    },

    /**
     * Verify Email Token
     */
    verifyEmail: async function(token) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/email/verify?token=${token}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('Verification error:', error);
            throw error;
        }
    },

    /**
     * Resend Verification Email
     */
    resendVerification: async function(email) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/email/resend-verification?email=${email}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            
            if (!response.ok) {
                throw await this.parseError(response);
            }
            
            return await response.json();
        } catch (error) {
            console.error('Resend error:', error);
            throw error;
        }
    }
};