/**
 * Login Page Logic with Fixed reCAPTCHA & Facebook Validation
 */
const LoginPage = {
    
    mfaSessionToken: null,
    currentEmail: null,
    tokenClient: null,
    facebookInitialized: false,
    loginAttempts: 0,
    isLocked: false,
    
    init: function() {
        // 1. Check if we were redirected here due to an auth error
        const urlParams = new URLSearchParams(window.location.search);
        const isAuthError = urlParams.has('error') || urlParams.has('logout');
        
        // 2. If there is an error (e.g. ?error=unauthorized from SecurityConfig), clear bad tokens
        if (isAuthError) {
            console.log('Authentication error detected, clearing session.');
            Storage.clearAll();
            // Optional: Remove the query param from URL without refreshing to look cleaner
            window.history.replaceState({}, document.title, window.location.pathname);
            
            // Show a friendly message if needed
            const errorType = urlParams.get('error');
            if (errorType === 'unauthorized' || errorType === 'session_expired') {
                Helpers.showError('Your session has expired. Please login again.');
            }
        }

        // 3. ONLY redirect if authenticated AND no error was just detected
        if (Storage.isAuthenticated()) {
            // Double check referrer to prevent loops if SecurityConfig wasn't updated
            const referrer = document.referrer || "";
            if (referrer.includes('dashboard')) {
                // We came from a dashboard but are back at login? Token is likely bad.
                console.warn('Loop detected from dashboard. Clearing session.');
                Storage.clearAll();
            } else {
                console.log('User is authenticated, redirecting to dashboard...');
                Helpers.redirectByRole(Storage.getUserRole());
                return;
            }
        }
        
        this.checkLoginLockout();
        this.setupValidation();
        
        // Attach Listener for Step 1 (Credentials)
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', this.handleInitialLogin.bind(this));
        }

        // Attach Listener for Step 2 (MFA)
        const mfaForm = document.getElementById('mfaForm');
        if (mfaForm) {
            mfaForm.addEventListener('submit', this.handleMfaSubmit.bind(this));
        }
        
        this.initGoogleOAuth();
        this.initFacebookSDK();
        
        // Security: Clear sensitive data on unload
        window.addEventListener('beforeunload', () => {
            this.mfaSessionToken = null;
        });
    },
    
    setupValidation: function() {
        Validation.setupRealtimeValidation('loginEmail', 'email');
        Validation.setupRealtimeValidation('loginPassword', 'password');
        Validation.setupRealtimeValidation('mfaCode', 'mfa');
    },
    
    checkLoginLockout: function() {
        const lockoutTime = localStorage.getItem('loginLockoutTime');
        const attempts = parseInt(localStorage.getItem('loginAttempts') || '0');
        this.loginAttempts = attempts;
        
        if (lockoutTime) {
            const now = Date.now();
            const lockoutEnd = parseInt(lockoutTime);
            
            if (now < lockoutEnd) {
                this.isLocked = true;
                const remainingMinutes = Math.ceil((lockoutEnd - now) / 60000);
                Helpers.showError(`Account locked due to too many failed attempts. Please try again in ${remainingMinutes} minute(s).`);
                this.disableForms(true);
                
                // Auto-unlock when time expires
                setTimeout(() => {
                    this.unlockAccount();
                }, lockoutEnd - now);
            } else {
                this.unlockAccount();
            }
        }
    },
    
    unlockAccount: function() {
        localStorage.removeItem('loginLockoutTime');
        localStorage.removeItem('loginAttempts');
        this.loginAttempts = 0;
        this.isLocked = false;
        this.disableForms(false);
        Helpers.showSuccess('Account unlocked. You may login now.');
    },

    disableForms: function(disabled) {
        const inputs = document.querySelectorAll('#step1-login input, #step1-login button, #step2-mfa input, #step2-mfa button');
        inputs.forEach(el => el.disabled = disabled);
    },
    
    /**
     * STEP 1: Handle Initial Email/Password Login
     */
    handleInitialLogin: async function(event) {
        event.preventDefault();
        
        if (this.isLocked) {
            Helpers.showError('Account is locked. Please wait before trying again.');
            return;
        }
        
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;
        
        // STEP 1: Validate fields FIRST
        let hasFieldError = false;
        
        if (!email) {
            Validation.showFieldError('loginEmail', CONFIG.MESSAGES.EMAIL_REQUIRED);
            hasFieldError = true;
        } else if (!Validation.isValidEmail(email, 'loginEmail').valid) {
            hasFieldError = true;
        }
        
        if (!password) {
            Validation.showFieldError('loginPassword', CONFIG.MESSAGES.PASSWORD_REQUIRED);
            hasFieldError = true;
        }
        
        if (hasFieldError) return;
        
        // STEP 2: Check reCAPTCHA
        const recaptchaToken = grecaptcha.getResponse();
        if (!recaptchaToken) {
            Helpers.showError(CONFIG.MESSAGES.RECAPTCHA_REQUIRED);
            return;
        }
        
        const submitBtn = document.querySelector('#loginForm button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Logging in...</span>';
        
        try {
            const response = await AuthAPI.login(email, password, recaptchaToken, null);
            this.handleAuthResponse(response);
            
            if (response.requiresMfa || (response.success && response.tokens)) {
                localStorage.removeItem('loginAttempts');
                this.loginAttempts = 0;
            }
        } catch (error) {
            this.handleLoginError(error);
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    },

    /**
     * STEP 2: Handle MFA Code Submission
     */
    handleMfaSubmit: async function(event) {
        event.preventDefault();
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        const code = document.getElementById('mfaCode').value.trim();
        
        if (!code) {
            Validation.showFieldError('mfaCode', 'MFA code is required');
            return;
        }
        
        if (!Validation.isValidMFACode(code, 'mfaCode').valid) {
            return;
        }
        
        const submitBtn = document.querySelector('#mfaForm button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>Verifying...</span>';
        
        try {
            const response = await AuthAPI.verifyMFA(this.currentEmail, code, this.mfaSessionToken);
            this.handleAuthResponse(response);
        } catch (error) {
            this.handleLoginError(error);
            document.getElementById('mfaCode').value = '';
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    },

    /**
     * Unified Response Handler - UPDATED WITH COOKIE SUPPORT
     */
    handleAuthResponse: function(response) {
        if (response.requiresMfa) {
            this.mfaSessionToken = response.mfaSessionToken;
            this.currentEmail = response.email || document.getElementById('loginEmail').value;
            document.getElementById('loginPassword').value = '';
            grecaptcha.reset();
            
            document.getElementById('step1-login').style.display = 'none';
            document.getElementById('step2-mfa').style.display = 'block';
            document.getElementById('mfaEmailDisplay').textContent = this.currentEmail;
            document.getElementById('mfaCode').value = '';
            document.getElementById('mfaCode').focus();
            
            Helpers.showSuccess('Credentials verified. Please enter your 2FA code.');
            return;
        }
        
        if (response.success && response.tokens) {
            Validation.clearAllFieldErrors();
            Storage.saveAuthResponse(response);

            // [MODIFIED] Set Cookie for Browser Navigation
            const d = new Date();
            d.setTime(d.getTime() + (24*60*60*1000)); // 1 day
            document.cookie = `jwt_token=${response.tokens.accessToken};expires=${d.toUTCString()};path=/;SameSite=Strict`;

            Helpers.showSuccess('Login successful! Redirecting to dashboard...');
            setTimeout(() => {
                Helpers.redirectByRole(response.tokens.role);
            }, 1500);
        } else {
            this.handleLoginError(new Error(response.message || 'Login failed'));
        }
    },
    
    handleLoginError: function(error) {
        console.error('Auth Error:', error);
        let errorMessage = error.message || 'Authentication failed. Please try again.';
        
        if (error.data && error.data.fieldErrors) {
            const fieldMap = {
                'email': 'loginEmail',
                'password': 'loginPassword',
                'code': 'mfaCode',
                'mfaCode': 'mfaCode'
            };
            
            let hasFieldError = false;
            Object.keys(error.data.fieldErrors).forEach(field => {
                const fieldId = fieldMap[field] || field;
                const fieldElement = document.getElementById(fieldId);
                if (fieldElement) {
                    Validation.showFieldError(fieldId, error.data.fieldErrors[field]);
                    hasFieldError = true;
                }
            });
            if (!hasFieldError) Helpers.showError(errorMessage);
        } else {
            Helpers.showError(errorMessage);
        }
        
        if (document.getElementById('step1-login').style.display !== 'none') {
            grecaptcha.reset();
        }
        this.incrementFailureCount();
    },

    incrementFailureCount: function() {
        this.loginAttempts++;
        localStorage.setItem('loginAttempts', this.loginAttempts.toString());
        
        const remainingAttempts = CONFIG.SECURITY.MAX_LOGIN_ATTEMPTS - this.loginAttempts;
        
        if (this.loginAttempts >= CONFIG.SECURITY.MAX_LOGIN_ATTEMPTS) {
            const lockoutEnd = Date.now() + CONFIG.SECURITY.LOCKOUT_DURATION;
            localStorage.setItem('loginLockoutTime', lockoutEnd.toString());
            this.isLocked = true;
            this.disableForms(true);
            const lockoutMinutes = Math.ceil(CONFIG.SECURITY.LOCKOUT_DURATION / 60000);
            Helpers.showError(`Too many failed login attempts. Account locked for ${lockoutMinutes} minute(s).`);
            setTimeout(() => { this.unlockAccount(); }, CONFIG.SECURITY.LOCKOUT_DURATION);
        } else if (remainingAttempts <= 2) {
            Helpers.showError(`Invalid credentials. ${remainingAttempts} attempt(s) remaining before account lockout.`);
        }
    },

    resetView: function() {
        this.mfaSessionToken = null;
        this.currentEmail = null;
        document.getElementById('step2-mfa').style.display = 'none';
        document.getElementById('step1-login').style.display = 'block';
        document.getElementById('mfaCode').value = '';
        document.getElementById('loginPassword').value = '';
        Validation.clearAllFieldErrors();
        Helpers.hideMessages();
        grecaptcha.reset();
    },

    // --- GOOGLE LOGIN ---
    initGoogleOAuth: function() {
        const checkGoogle = setInterval(() => {
            if (window.google?.accounts?.oauth2) {
                clearInterval(checkGoogle);
                this.tokenClient = google.accounts.oauth2.initTokenClient({
                    client_id: CONFIG.OAUTH.GOOGLE_CLIENT_ID,
                    scope: 'email profile',
                    callback: (response) => {
                        if (response.access_token) {
                            this.processSocialLogin(response.access_token, 'GOOGLE');
                        } else if (response.error) {
                            Helpers.showError('Google login failed: ' + response.error);
                        }
                    }
                });
            }
        }, 100);
    },
    
    handleGoogleLogin: function() {
        if (this.isLocked) {
            Helpers.showError('Account is locked. Please wait before trying again.');
            return;
        }
        if (!this.tokenClient) {
            Helpers.showError('Google Sign-In is still loading. Please try again in a moment.');
            return;
        }
        const recaptchaToken = grecaptcha.getResponse();
        if (!recaptchaToken) {
            Helpers.showError('Please complete the reCAPTCHA verification before signing in with Google.');
            return;
        }
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        this.tokenClient.requestAccessToken();
    },

    // --- FACEBOOK LOGIN (FIXED) ---
    initFacebookSDK: function() {
        const initFB = () => {
            try {
                FB.init({
                    appId: CONFIG.OAUTH.FACEBOOK_APP_ID,
                    cookie: true,
                    xfbml: true,
                    version: 'v18.0'
                });
                this.facebookInitialized = true;
                console.log('Facebook SDK initialized');
            } catch (e) {
                console.error('Error initializing Facebook SDK:', e);
            }
        };

        // Check if FB SDK is already loaded
        if (typeof FB !== 'undefined') {
            initFB();
        } else {
            // Otherwise, wait for the async callback
            window.fbAsyncInit = initFB;
        }
    },

    handleFacebookLogin: function() {
        if (this.isLocked) {
            Helpers.showError('Account is locked. Please wait before trying again.');
            return;
        }
        
        if (!this.facebookInitialized) {
            Helpers.showError('Facebook Sign-In is still loading. Please try again in a moment.');
            return;
        }
        
        const recaptchaToken = grecaptcha.getResponse();
        if (!recaptchaToken) {
            Helpers.showError('Please complete the reCAPTCHA verification before signing in with Facebook.');
            return;
        }
        
        Helpers.hideMessages();
        Validation.clearAllFieldErrors();
        
        FB.login((response) => {
            if (response.authResponse) {
                this.processSocialLogin(response.authResponse.accessToken, 'FACEBOOK');
            } else {
                console.log('User cancelled login or did not fully authorize.');
            }
        }, { scope: 'public_profile,email' });
    },

    processSocialLogin: async function(token, provider) {
        try {
            const recaptchaToken = grecaptcha.getResponse();
            let response;
            if (provider === 'GOOGLE') {
                response = await AuthAPI.loginWithGoogle(token, recaptchaToken);
            } else {
                response = await AuthAPI.loginWithFacebook(token, recaptchaToken);
            }
            
            this.handleAuthResponse(response);
            
            if (response.success && response.tokens) {
                localStorage.removeItem('loginAttempts');
                this.loginAttempts = 0;
            }
        } catch (error) {
            const errorMessage = error.message || `${provider} login failed. Please try again.`;
            Helpers.showError(errorMessage);
            grecaptcha.reset();
        }
    }
};

document.addEventListener('DOMContentLoaded', () => LoginPage.init());