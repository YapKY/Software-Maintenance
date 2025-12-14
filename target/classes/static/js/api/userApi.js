/**
 * User API Calls
 */
const UserAPI = {
    
    /**
     * Get user dashboard data
     */
    getUserDashboard: async function() {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.DASHBOARD_USER}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (response.status === 401 || response.status === 403) {
                Storage.clearAll();
                Helpers.redirectTo('login.html');
                return null;
            }
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error('Failed to load dashboard data');
            }
            
            return data;
            
        } catch (error) {
            console.error('Dashboard error:', error);
            throw error;
        }
    },
    
    /**
     * Get admin dashboard data
     */
    getAdminDashboard: async function() {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.DASHBOARD_ADMIN}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (response.status === 401 || response.status === 403) {
                Storage.clearAll();
                Helpers.redirectTo('login.html');
                return null;
            }
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error('Failed to load dashboard data');
            }
            
            return data;
            
        } catch (error) {
            console.error('Dashboard error:', error);
            throw error;
        }
    },
    
    /**
     * Get superadmin dashboard data
     */
    getSuperadminDashboard: async function() {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.DASHBOARD_SUPERADMIN}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (response.status === 401 || response.status === 403) {
                Storage.clearAll();
                Helpers.redirectTo('login.html');
                return null;
            }
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error('Failed to load dashboard data');
            }
            
            return data;
            
        } catch (error) {
            console.error('Dashboard error:', error);
            throw error;
        }
    },

    /**
     * Change Password
     */
    changePassword: async function(currentPassword, newPassword, confirmPassword) {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/dashboard/change-password`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    currentPassword: currentPassword,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword
                })
            });
            
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || 'Failed to change password');
            }
            
            return data;
        } catch (error) {
            console.error('Change password error:', error);
            throw error;
        }
    }
};