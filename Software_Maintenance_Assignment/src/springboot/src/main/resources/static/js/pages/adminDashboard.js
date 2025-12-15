/**
 * Admin Dashboard Page Logic
 */
const AdminDashboard = {
  currentAdmin: null,

  init: async function () {
    if (!Storage.isAuthenticated() || Storage.getUserRole() !== "ADMIN") {
      Helpers.redirectTo("login.html");
      return;
    }
    await this.loadAdminProfile();

    const cpForm = document.getElementById("changePasswordForm");
    if (cpForm) {
      cpForm.addEventListener("submit", this.handleChangePassword.bind(this));
      if (window.Validation) {
        Validation.setupRealtimeValidation("newPassword", "password");
        const passField = document.getElementById("newPassword");
        passField.addEventListener("input", () =>
          this.updatePasswordStrength(passField.value)
        );
      }
    }
  },

  loadAdminProfile: async function () {
    try {
      Helpers.showLoading("loadingState");
      const profile = await UserAPI.getAdminDashboard();
      if (profile) {
        this.currentAdmin = profile;
        this.displayAdminProfile(profile);
        Helpers.hideLoading("loadingState");
        document.getElementById("profileSection").style.display = "block";
        document.getElementById("dashboardCards").style.display = "grid";
      }
    } catch (error) {
      console.error("Failed to load profile:", error);
      Helpers.showError("Failed to load profile data");
      Helpers.hideLoading("loadingState");
    }
  },

  displayAdminProfile: function (profile) {
    const initials = Helpers.getInitials(profile.name);
    document.getElementById("userAvatar").textContent = initials;
    document.getElementById("userName").textContent = profile.name;
    document.getElementById("profileAvatar").textContent = initials;
    document.getElementById("profileName").textContent = profile.name;
    document.getElementById("profileEmail").textContent = profile.email;
    document.getElementById("profilePhone").textContent =
      profile.phoneNumber || "Not provided";
    document.getElementById("profileRole").textContent = profile.role;

    const mfaStatusEl = document.getElementById("mfaStatus");
    mfaStatusEl.innerHTML = profile.mfaEnabled
      ? '<span class="mfa-status mfa-enabled">Enabled</span>'
      : '<span class="mfa-status mfa-disabled">Disabled</span>';

    document.getElementById("lastLogin").textContent = Helpers.formatDateTime(
      profile.lastLoginAt
    );
    document.getElementById("memberSince").textContent = Helpers.formatDate(
      profile.createdAt
    );
  },

  showChangePasswordModal: function () {
    const errorEl = document.getElementById("modalAlertError");
    if (errorEl) errorEl.classList.remove("show");

    document.getElementById("changePasswordModal").style.display = "block";
  },

  hideChangePasswordModal: function () {
    document.getElementById("changePasswordModal").style.display = "none";
    document.getElementById("changePasswordForm").reset();
    Validation.clearAllFieldErrors();

    const errorEl = document.getElementById("modalAlertError");
    if (errorEl) errorEl.classList.remove("show");
  },

  handleChangePassword: async function (event) {
    event.preventDefault();
    Helpers.hideMessages();
    Validation.clearAllFieldErrors();

    // Hide local modal error
    const errorEl = document.getElementById("modalAlertError");
    if (errorEl) errorEl.classList.remove("show");

    const current = document.getElementById("currentPassword").value;
    const newPass = document.getElementById("newPassword").value;
    const confirm = document.getElementById("confirmNewPassword").value;

    if (!current) {
      Validation.showFieldError(
        "currentPassword",
        "Current password is required"
      );
      return;
    }
    if (!Validation.isValidPassword(newPass, "newPassword").valid) return;
    if (
      !Validation.passwordsMatch(newPass, confirm, "confirmNewPassword").valid
    )
      return;

    try {
      const response = await UserAPI.changePassword(current, newPass, confirm);
      if (response.success) {
        this.hideChangePasswordModal();
        Helpers.showSuccess("Password updated successfully");
      } else {
        // Show API error in modal
        Helpers.showError(response.message, "modalAlertError");
      }
    } catch (error) {
      // Show exception error in modal
      Helpers.showError(
        error.message || "Failed to update password",
        "modalAlertError"
      );
    }
  },

  updatePasswordStrength: function (password) {
    const strengthDiv = document.getElementById("passwordStrength");
    if (!password) {
      strengthDiv.style.display = "none";
      return;
    }
    strengthDiv.style.display = "block";
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[@$!%*?&]/.test(password)) strength++;

    let color =
      strength <= 2
        ? "#dc3545"
        : strength <= 3
        ? "#ffc107"
        : strength <= 4
        ? "#17a2b8"
        : "#28a745";
    let message =
      strength <= 2
        ? "Weak"
        : strength <= 3
        ? "Fair"
        : strength <= 4
        ? "Good"
        : "Strong";
    strengthDiv.innerHTML = `<small style="color: ${color}; font-weight: 600;">${message}</small>`;
  },

  viewAllUsers: function () {
    window.location.href = "/customer-list";
  },


  manageBookings: function () {
    // Navigate to the staff dashboard
    window.location.href = "/staff/dashboard";
  },
  handleLogout: async function () {
    await AuthAPI.logout();
  },
};

window.onload = function () {
  AdminDashboard.init();
};
function handleLogout() {
  AdminDashboard.handleLogout();
}
function viewAllUsers() {
  AdminDashboard.viewAllUsers();
}
function viewReports() {
  AdminDashboard.viewReports();
}
function manageBookings() {
  AdminDashboard.manageBookings();
}
function showChangePasswordModal() {
  AdminDashboard.showChangePasswordModal();
}
function hideChangePasswordModal() {
  AdminDashboard.hideChangePasswordModal();
}
