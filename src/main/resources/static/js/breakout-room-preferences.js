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
    registerForm.addEventListener("submit", function (e) {
        const container = document.getElementById("sessionInputs");
        container.innerHTML = "";

        if (selected.length === 0) {
            e.preventDefault();
            alert("Please select at least one session before submitting.");
            return;
        }

        selected.forEach((circle, index) => {
            const sessionDiv = circle.closest(".session");
            const sessionId = sessionDiv.dataset.sessionId;
            const rank = index + 1;

            if (sessionId) {
                const sessionInput = document.createElement("input");
                sessionInput.type = "hidden";
                sessionInput.name = "sessionIds";
                sessionInput.value = sessionId;
                container.appendChild(sessionInput);

                const rankInput = document.createElement("input");
                rankInput.type = "hidden";
                rankInput.name = "rankings";
                rankInput.value = rank;
                container.appendChild(rankInput);
            }
        });
        
        // Set flag BEFORE submitting
        sessionStorage.setItem('breakoutSubmitted', 'true');
    });
}

// Prevent back button from showing the breakout page after submission
(function() {
    // Check if we're on the breakout page
    if (window.location.pathname === '/breakout') {
        // Check sessionStorage to see if user just submitted
        const justSubmitted = sessionStorage.getItem('breakoutSubmitted');
        
        if (justSubmitted === 'true') {
            // Clear the flag and redirect to thank you page
            sessionStorage.removeItem('breakoutSubmitted');
            window.location.replace('/sessions/thankYou');
            return;
        }
        
        // Also handle when user comes back via back button after page load
        window.addEventListener('pageshow', function(event) {
            if (event.persisted || (performance.getEntriesByType && performance.getEntriesByType('navigation')[0].type === 'back_forward')) {
                const submitted = sessionStorage.getItem('breakoutSubmitted');
                if (submitted === 'true') {
                    sessionStorage.removeItem('breakoutSubmitted');
                    window.location.replace('/sessions/thankYou');
                }
            }
        });
    }
    
    // On the thank you page, set a flag that user has submitted
    if (window.location.pathname === '/sessions/thankYou') {
        sessionStorage.setItem('breakoutSubmitted', 'true');
    }
})();

// Add debug info to see what's being submitted
console.log("Breakout preferences JS loaded");