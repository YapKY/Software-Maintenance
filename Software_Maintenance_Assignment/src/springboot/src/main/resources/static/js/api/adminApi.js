/**
 * Admin Management API Calls
 */
const AdminAPI = {
    
    /**
     * Get list of all admins (Superadmin only)
     */
    getAdminsList: async function() {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/superadmin/admins`, {
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
                throw new Error('Failed to load admins list');
            }
            
            return data;
            
        } catch (error) {
            console.error('Admins list error:', error);
            throw error;
        }
    },
    
    /**
     * Create new admin account (Superadmin only)
     */
    createAdmin: async function(adminData) {
        try {
            const token = Storage.getAccessToken();
            
            const response = await fetch(`${CONFIG.API_BASE_URL}/api/register/admin`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(adminData)
            });
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'Failed to create admin');
            }
            
            return data;
            
        } catch (error) {
            console.error('Create admin error:', error);
            throw error;
        }
    }
};