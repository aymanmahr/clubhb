// app.js
document.addEventListener("DOMContentLoaded", () => {

  /* THEME TOGGLE */
  const themeSwitch = document.querySelector(".theme-switch");
  if (themeSwitch) {
    themeSwitch.addEventListener("click", () => {
      themeSwitch.classList.toggle("on");
      document.body.classList.toggle("light");
    });
  }

  /* SIDEBAR PANEL SWITCHING */
  const sidebarButtons = document.querySelectorAll(".sidebar-btn");
  const panels = document.querySelectorAll(".panel");
  const dashTitle = document.querySelector(".dash-title");

  if (sidebarButtons.length && panels.length) {
    sidebarButtons.forEach(btn => {
      btn.addEventListener("click", () => {
        const target = btn.getAttribute("data-panel");

        sidebarButtons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

        panels.forEach(p => {
          p.style.display = (p.id === target) ? "block" : "none";
        });

        if (dashTitle && btn.textContent.trim()) {
          dashTitle.textContent = btn.textContent.trim();
        }
      });
    });
  }

  /* LOGIN ROLE TOGGLE */
  const roleButtons = document.querySelectorAll("[data-role-toggle]");
  const loginRoleHidden = document.querySelector("#login-role");

  if (roleButtons.length && loginRoleHidden) {
    roleButtons.forEach(btn => {
      btn.addEventListener("click", () => {
        roleButtons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");
        loginRoleHidden.value = btn.dataset.roleToggle;
      });
    });
  }

  /* LOGIN FORM */
  const loginForm = document.querySelector("#login-form");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const role = loginRoleHidden.value;

      if (role === "admin") {
        window.location.href = "admin-dashboard.html";
      } else {
        window.location.href = "student-dashboard.html";
      }
    });
  }

  /* REGISTER ROLE TOGGLE */
  const regToggleButtons = document.querySelectorAll("[data-register-toggle]");
  const adminExtras = document.querySelectorAll(".admin-only");
  const registerRoleHidden = document.querySelector("#register-role");

  if (regToggleButtons.length && registerRoleHidden) {
    regToggleButtons.forEach(btn => {
      btn.addEventListener("click", () => {
        regToggleButtons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

        const mode = btn.dataset.registerToggle;
        registerRoleHidden.value = mode;

        adminExtras.forEach(el => {
          el.style.display = (mode === "admin") ? "block" : "none";
        });
      });
    });
  }

  /* REGISTER FORM */
  const registerForm = document.querySelector("#register-form");
  if (registerForm) {
    registerForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const form = e.target;

      if (form.password.value !== form.confirm_password.value) {
        alert("Passwords do not match!");
        return;
      }

      const mode = registerRoleHidden.value;
      alert(`Registration successful as ${mode}!`);
      window.location.href = "login.html";
    });
  }

  /* STUDENT BUTTONS */
  const btnSaveProfile = document.querySelector("#btn-save-profile");
  if (btnSaveProfile) btnSaveProfile.addEventListener("click", () => alert("Profile updated!"));

  const btnInterest = document.querySelector("#btn-interest");
  if (btnInterest) btnInterest.addEventListener("click", () => alert("Interest submitted!"));

  const btnRegisterEvent = document.querySelector("#btn-register-event");
  if (btnRegisterEvent) btnRegisterEvent.addEventListener("click", () => alert("Event registration successful!"));

  const btnStudentPayment = document.querySelector("#btn-stu-payment");
  if (btnStudentPayment) btnStudentPayment.addEventListener("click", () => alert("Payment recorded!"));

  /* ADMIN BUTTONS */
  const btnAdminSaveProfile = document.querySelector("#btn-admin-save-profile");
  if (btnAdminSaveProfile) btnAdminSaveProfile.addEventListener("click", () => alert("Admin profile updated!"));

  const btnAdminSaveClub = document.querySelector("#btn-admin-save-club");
  if (btnAdminSaveClub) btnAdminSaveClub.addEventListener("click", () => alert("Club saved!"));

  const btnAdminCreateEvent = document.querySelector("#btn-admin-create-event");
  if (btnAdminCreateEvent) btnAdminCreateEvent.addEventListener("click", () => alert("Event created!"));

  const btnAdminAddMembership = document.querySelector("#btn-admin-add-membership");
  if (btnAdminAddMembership) btnAdminAddMembership.addEventListener("click", () => alert("Membership added!"));

  const btnAdminPostAnnouncement = document.querySelector("#btn-admin-post-announcement");
  if (btnAdminPostAnnouncement) btnAdminPostAnnouncement.addEventListener("click", () => alert("Announcement posted!"));

});
