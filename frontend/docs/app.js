// Simple theme toggle + role toggle + fake login routing

document.addEventListener("DOMContentLoaded", () => {
  // Theme switch on dashboards
  const themeSwitch = document.querySelector(".theme-switch");
  if (themeSwitch) {
    themeSwitch.addEventListener("click", () => {
      themeSwitch.classList.toggle("on");
      document.body.classList.toggle("light");
    });
  }

  // Login role toggle
  const roleButtons = document.querySelectorAll("[data-role-toggle]");
  const roleHidden = document.querySelector("#login-role");
  roleButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      roleButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      if (roleHidden) roleHidden.value = btn.dataset.roleToggle;
    });
  });

  // Login form basic handling (fake redirect)
  const loginForm = document.querySelector("#login-form");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const role = document.querySelector("#login-role").value || "student";

      // TODO: Replace this with real backend login call
      // Example: fetch('/api/login', { method: 'POST', body: new FormData(loginForm) })

      if (role === "admin") {
        window.location.href = "admin-dashboard.html";
      } else {
        window.location.href = "student-dashboard.html";
      }
    });
  }

  // Register page: toggle between admin & student layout
  const regToggleButtons = document.querySelectorAll("[data-register-toggle]");
  const adminExtras = document.querySelectorAll(".admin-only");
  const regRoleHidden = document.querySelector("#register-role");

  regToggleButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      regToggleButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      const mode = btn.dataset.registerToggle;
      if (regRoleHidden) regRoleHidden.value = mode;
      adminExtras.forEach(el => {
        el.style.display = (mode === "admin") ? "block" : "none";
      });
    });
  });

  const registerForm = document.querySelector("#register-form");
  if (registerForm) {
    registerForm.addEventListener("submit", (e) => {
      e.preventDefault();
      const mode = regRoleHidden.value || "student";

      // TODO: call backend register endpoint with form data
      // fetch('/api/register', { method: 'POST', body: new FormData(registerForm) })

      alert(`Registered as ${mode} (connect this to backend later).`);
    });
  }
});
