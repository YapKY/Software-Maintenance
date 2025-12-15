/**
 * Helper Utilities
 */
const Helpers = {
    
    /**
     * Show loading spinner
     */
    showLoading: function(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'block';
        }
    },
    
    /**
     * Hide loading spinner
     */
    hideLoading: function(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'none';
        }
    },
    
    /**
     * Show success message
     */
    showSuccess: function(message, elementId = 'alertSuccess') {
        const alertEl = document.getElementById(elementId);
        if (alertEl) {
            alertEl.textContent = message;
            alertEl.classList.add('show');
            setTimeout(() => alertEl.classList.remove('show'), 5000);
        }
    },
    
    /**
     * Show error message
     */
    showError: function(message, elementId = 'alertError') {
        const alertEl = document.getElementById(elementId);
        if (alertEl) {
            alertEl.textContent = message;
            alertEl.classList.add('show');
            setTimeout(() => alertEl.classList.remove('show'), 5000);
        }
    },
    
    /**
     * Hide all messages
     */
    hideMessages: function() {
        const successEl = document.getElementById('alertSuccess');
        const errorEl = document.getElementById('alertError');
        if (successEl) successEl.classList.remove('show');
        if (errorEl) errorEl.classList.remove('show');
    },
    
    /**
     * Get initials from name
     */
    getInitials: function(name) {
        if (!name) return 'U';
        return name.split(' ')
            .map(n => n[0])
            .join('')
            .toUpperCase()
            .substring(0, 2);
    },
    
    /**
     * Format date
     */
    formatDate: function(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString();
    },
    
    /**
     * Format datetime
     */
    formatDateTime: function(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleString();
    },
    
    /**
     * Redirect to page
     */
    redirectTo: function(page) {
        window.location.href = page;
    },
    
    /**
     * Redirect based on role
     */
    redirectByRole: function(role) {
        switch(role) {
            case 'USER':
                this.redirectTo('user-dashboard.html');
                break;
            case 'ADMIN':
                this.redirectTo('admin-dashboard.html');
                break;
            case 'SUPERADMIN':
                this.redirectTo('superadmin-dashboard.html');
                break;
            default:
                this.redirectTo('login.html');
        }
    }
};