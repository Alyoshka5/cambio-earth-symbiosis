const codeInputs = document.querySelectorAll("input[inputmode=\"numeric\"]");

// automatically focus next or previous input on input / backspace
codeInputs.forEach(input => {
    const inputIdx = parseInt(input.dataset.idx);
    input.addEventListener("input", () => {
        if (inputIdx < 5 && input.value !== "") {
            codeInputs[inputIdx + 1].focus();
        }
    });
    input.addEventListener("keydown", (e) => {
        if (e.key === "Backspace" && input.value === "" && inputIdx > 0) {
            codeInputs[inputIdx - 1].focus();
        }
    });
});