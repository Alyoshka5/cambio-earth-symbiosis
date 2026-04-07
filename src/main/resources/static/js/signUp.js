const form = document.querySelector("form");

const firstNameInput = document.querySelector(".first-name");
const lastNameInput = document.querySelector(".last-name");
const emailInput = document.querySelector(".input-email");
const passwordInput = document.querySelector(".input-pass");
const confirmPasswordInput = document.querySelector(".input-confirm-pass");

const firstNameError = document.querySelector(".first-name-error");
const lastNameError = document.querySelector(".last-name-error");
const emailBlankError = document.querySelector(".email-blank-error");
const emailInvalidError = document.querySelector(".email-invalid-error");
const passwordError = document.querySelector(".password-error");
const confirmPasswordError = document.querySelector(".confirm-password-error");
const passwordRuleError = document.querySelector(".password-rule-error");
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.#_^+=\-])[A-Za-z\d@$!%*?&.#_^+=\-]{8,}$/;

function showError(element) {
    element.style.display = "block";
}

function hideError(element) {
    element.style.display = "none";
}

form.addEventListener("submit", function (event) {
    let hasError = false;

    hideError(firstNameError);
    hideError(lastNameError);
    hideError(emailBlankError);
    hideError(emailInvalidError);
    hideError(passwordError);
    hideError(confirmPasswordError);
    hideError(passwordRuleError);

    if (firstNameInput.value.trim() === "") {
        showError(firstNameError);
        hasError = true;
    }

    if (lastNameInput.value.trim() === "") {
        showError(lastNameError);
        hasError = true;
    }

    if (emailInput.value.trim() === "") {
        showError(emailBlankError);
        hasError = true;
    } else if (!/^[A-Za-z0-9._%+-]+@cambioearth\.com$/.test(emailInput.value.trim())) {
        showError(emailInvalidError);
        hasError = true;
    }

    if (passwordInput.value.trim() === "") {
        showError(passwordError);
        hasError = true;
    } else if (!passwordRegex.test(passwordInput.value)) {
        showError(passwordRuleError);
        hasError = true;
    }

    if (confirmPasswordInput.value !== passwordInput.value) {
        showError(confirmPasswordError);
        hasError = true;
    }

    if (hasError) {
        event.preventDefault();
    }
});