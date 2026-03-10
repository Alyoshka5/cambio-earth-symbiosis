// Wait for the page to fully load before running any JS
document.addEventListener('DOMContentLoaded', function() {

    // Add event listeners to the tab headers
    document.getElementById("speakers-heading").addEventListener('click', speakerTabActive);
    document.getElementById("participants-heading").addEventListener('click', participantsTabActive);
});

function speakerTabActive(event) {
    // Change speaker header colour
    let speakerHeader = document.getElementById("speakers-heading")
    let participantHeader = document.getElementById("participants-heading")
    speakerHeader.style.color = '#ffffff';
    participantHeader.style.color = '#c8d96e';
    
    // Hide participants list
    document.getElementById("participants-container").style.display = 'none';

    // Show speakers list
    document.getElementById("speakers-container").style.display = 'block';
}

function participantsTabActive(event) {
    // Change participants header colour
    let speakerHeader = document.getElementById("speakers-heading")
    let participantHeader = document.getElementById("participants-heading")
    speakerHeader.style.color = '#c8d96e';
    participantHeader.style.color = '#ffffff';

    // Hide speakers list
    document.getElementById("speakers-container").style.display = 'none';

    // Show participants list
    document.getElementById("participants-container").style.display = 'block';
}