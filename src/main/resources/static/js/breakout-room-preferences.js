// Track the current selections
let selected = [];

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Get all the possible rank circles
    let circles = document.querySelectorAll('.rank');
    console.log('Found circles:', circles.length);
    
    // Get the submit button
    let submitButton = document.getElementById('submitPreferences');
    if (submitButton) {
        submitButton.addEventListener('click', submitPreferences);
        console.log('Submit button found');
    }
    
    // Loop through all circles to add a click event to each
    for (let i = 0; i < circles.length; i++) {
        circles[i].addEventListener('click', selectCircle);
    }
    
    if (circles.length === 0) {
        console.log('No circles found! Check if .rank elements exist in the DOM');
    }
});

// Main function to handle selecting and ranking selections
function selectCircle(event) {
    event.stopPropagation();
    
    let clickedCircle = event.currentTarget;
    console.log('Circle clicked:', clickedCircle);
    
    // Check if this circle is already selected
    for (let i = 0; i < selected.length; i++) {
        if (selected[i] === clickedCircle) {
            deselectCircle(clickedCircle);
            return;
        }
    }

    // Check if we've already selected 6 sessions (max)
    if (selected.length >= 6) {
        alert('You can only select up to 6 sessions');
        return;
    }

    // Add the ranking circle to the selected list
    selected.push(clickedCircle)

    // Change the circle display and number
    clickedCircle.style.backgroundColor = '#c3ff75';
    clickedCircle.style.borderColor = '#c3ff75';
    clickedCircle.classList.add('filled');
    
    let span = clickedCircle.querySelector('span');
    if (span) {
        span.textContent = `${selected.length}`;
        span.style.color = '#000000';
    }
    
    console.log('Selected count:', selected.length);
}

// Function to deselect the unwanted session
function deselectCircle(unwanted) {
    console.log('Deselecting circle');

    let unwantedIndex = selected.findIndex(function(item) {return item === unwanted;});

    if (unwantedIndex == -1) {
        return;
    }
    selected.splice(unwantedIndex, 1);

    unwanted.style.backgroundColor = '#243023';
    unwanted.style.borderColor = '#000000';
    unwanted.classList.remove('filled');
    
    let span = unwanted.querySelector('span');
    if (span) {
        span.textContent = '';
        span.style.color = '#ffffff';
    }

    fixRanking();
    console.log('Selected count after deselect:', selected.length);
}

// Function to reorder all the selections
function fixRanking() {
    for (let i = 0; i < selected.length; i++) {
        let span = selected[i].querySelector('span');
        if (span) {
            span.textContent = `${i + 1}`;
        }
    }
}

// Function to submit preferences
function submitPreferences(event) {
    event.preventDefault();
    
    console.log('Submit clicked, selected sessions:', selected.length);
    
    if (selected.length === 0) {
        alert('Please select at least one session by clicking on the circles');
        return;
    }
    
    let sessionIds = [];
    
    for (let i = 0; i < selected.length; i++) {
        let sessionDiv = selected[i].closest('.session');
        
        if (sessionDiv) {
            let sessionId = sessionDiv.dataset.sessionId;
            if (sessionId) {
                sessionIds.push(sessionId);
                console.log('Found session ID:', sessionId);
            }
        }
    }
    
    console.log('Session IDs to submit:', sessionIds);
    
    if (!confirm(`Submit your preferences for ${sessionIds.length} sessions?`)) {
        return;
    }
    
    let submitBtn = event.target;
    let originalText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Submitting...';
    
    let formData = new URLSearchParams();
    sessionIds.forEach(id => {
        formData.append('sessionId', id);
    });
    
    let csrfToken = document.querySelector('input[name="_csrf"]');
    if (csrfToken) {
        formData.append(csrfToken.name, csrfToken.value);
    }
    
    fetch('/sessions/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.status);
        }
        return response.text();
    })
    .then(data => {
        alert(data);
        console.log('Success:', data);
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('Error registering for sessions. Please try again. ' + error.message);
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    });
}