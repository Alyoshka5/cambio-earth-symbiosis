let selected = [];

// get all rank circles
const circles = document.querySelectorAll(".rank");

// Initialize selected array with any pre-selected sessions from the server
document.addEventListener('DOMContentLoaded', function() {
    circles.forEach(circle => {
        const span = circle.querySelector("span");
        if (span && span.textContent.trim() !== "") {
            // This circle was pre-selected (has a rank number)
            selected.push(circle);
            circle.classList.add("filled");
            circle.style.backgroundColor = "#c3f775";
            circle.style.borderColor = "#c3f775";
        } else {
            // Clear any default styling
            circle.classList.remove("filled");
            circle.style.backgroundColor = "";
            circle.style.borderColor = "";
            if (span) {
                span.textContent = "";
            }
        }
    });
    
    // Fix ranking numbers to ensure they're in order
    fixRanking();
    
    // Add debug logging
    console.log("Initialized with selected sessions:", selected.length);
});

// add click event to each circle
circles.forEach(circle => {
    circle.addEventListener("click", selectCircle);
});

function selectCircle(event) {
    const clickedCircle = event.currentTarget;

    // if already selected, deselect it
    if (selected.includes(clickedCircle)) {
        deselectCircle(clickedCircle);
        return;
    }

    // add to selected list
    selected.push(clickedCircle);

    // style selected circle
    clickedCircle.classList.add("filled");
    clickedCircle.style.backgroundColor = "#c3f775";
    clickedCircle.style.borderColor = "#c3f775";

    // update ranking numbers
    fixRanking();
    
    console.log("Selected session. Total selected:", selected.length);
}

function deselectCircle(circle) {
    const index = selected.indexOf(circle);
    if (index === -1) return;

    selected.splice(index, 1);

    circle.classList.remove("filled");
    circle.style.backgroundColor = "";
    circle.style.borderColor = "";

    const span = circle.querySelector("span");
    if (span) {
        span.textContent = "";
    }

    fixRanking();
    
    console.log("Deselected session. Total selected:", selected.length);
}

function fixRanking() {
    // first clear all displayed numbers
    circles.forEach(circle => {
        const span = circle.querySelector("span");
        if (span) {
            span.textContent = "";
        }
    });

    // then number only selected ones in order
    selected.forEach((circle, index) => {
        const span = circle.querySelector("span");
        if (span) {
            span.textContent = index + 1;
        }
    });
}

// Handle form submission
const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", function(e) {
        const container = document.getElementById("sessionInputs");
        container.innerHTML = "";

        if (selected.length === 0) {
            e.preventDefault();
            alert("Please select at least one session before submitting.");
            return;
        }

        // Sort selected circles by their rank number
        const sortedSelected = [...selected].sort((a, b) => {
            const aSpan = a.querySelector("span");
            const bSpan = b.querySelector("span");
            return parseInt(aSpan.textContent) - parseInt(bSpan.textContent);
        });

        sortedSelected.forEach(circle => {
            const sessionDiv = circle.closest(".session");
            const sessionId = sessionDiv.dataset.sessionId;
            
            if (sessionId) {
                // Create hidden input for session ID
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "sessionIds";
                input.value = sessionId;
                container.appendChild(input);
                console.log("Adding session ID to form:", sessionId);
            } else {
                console.error("No session ID found for circle:", circle);
            }
        });
        
        console.log("Submitting form with", selected.length, "sessions");
    });
}

// Add debug info to see what's being submitted
console.log("Breakout preferences JS loaded");