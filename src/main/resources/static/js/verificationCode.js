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


for (let i = 0; i < codeInputs.length; i++) {
    codeInputs[i].addEventListener('paste', function(e) {

        // Prevent pasting everything to the first box or only the first box
        e.preventDefault();
        codeInputs.forEach(input => input.removeAttribute('maxlength'));

        // Grab pasted content and remove any non-digit values
        const pasted = (e.clipboardData || window.clipboardData)
            .getData('text')

        // Fill each box starting from the one that was pasted into
        for (let j = 0; j < pasted.length; j++) {
            let currentBox = codeInputs[i + j];

            if (currentBox) {
                currentBox.value = pasted[j];
            }
        }

        let lastFilledIndex = i + pasted.length - 1;

        // Don't paste anything past input box 5
        if (lastFilledIndex > codeInputs.length - 1) {
            lastFilledIndex = codeInputs.length - 1;
        }

        // Move the cursor to input box 5 after filling the boxes
        codeInputs[lastFilledIndex].focus();

        // Add back the maxlength attribute for input boxes
        codeInputs.forEach(input => input.setAttribute('maxlength', '1'));
    });
}