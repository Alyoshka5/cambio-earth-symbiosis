const form = document.getElementById("profileForm");

const currentPasswordInput = document.getElementById("currentPassword");
const newPasswordInput = document.getElementById("newPassword");
const confirmNewPasswordInput = document.getElementById("confirmNewPassword");

const currentPasswordError = document.querySelector(".client-current-password-error");
const newPasswordError = document.querySelector(".client-new-password-error");
const passwordRuleError = document.querySelector(".client-password-rule-error");
const confirmPasswordError = document.querySelector(".client-confirm-password-error");
const passwordMatchError = document.querySelector(".client-password-match-error");

const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.#_^+=\-])[A-Za-z\d@$!%*?&.#_^+=\-]{8,}$/;

function showError(element) {
    if (element) {
        element.style.display = "block";
    }
}

function hideError(element) {
    if (element) {
        element.style.display = "none";
    }
}

form.addEventListener("submit", function (event) {
    let hasError = false;

    hideError(currentPasswordError);
    hideError(newPasswordError);
    hideError(passwordRuleError);
    hideError(confirmPasswordError);
    hideError(passwordMatchError);

    const currentPassword = currentPasswordInput.value.trim();
    const newPassword = newPasswordInput.value.trim();
    const confirmNewPassword = confirmNewPasswordInput.value.trim();

    const tryingToChangePassword =
        currentPassword !== "" ||
        newPassword !== "" ||
        confirmNewPassword !== "";

    if (tryingToChangePassword) {
        if (currentPassword === "") {
            showError(currentPasswordError);
            hasError = true;
        }

        if (newPassword === "") {
            showError(newPasswordError);
            hasError = true;
        } else if (!passwordRegex.test(newPassword)) {
            showError(passwordRuleError);
            hasError = true;
        }

        if (confirmNewPassword === "") {
            showError(confirmPasswordError);
            hasError = true;
        } else if (confirmNewPassword !== newPassword) {
            showError(passwordMatchError);
            hasError = true;
        }
    }

    if (hasError) {
        event.preventDefault();
    }
});