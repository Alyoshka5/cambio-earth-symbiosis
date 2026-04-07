// Wait for the page to fully load before running any JS
document.addEventListener('DOMContentLoaded', function() {

    // Add event listeners to the tab headers
    const speakersHeading = document.getElementById("speakers-heading");
    const participantsHeading = document.getElementById("participants-heading");
    
    if (speakersHeading) {
        speakersHeading.addEventListener('click', speakerTabActive);
    }
    if (participantsHeading) {
        participantsHeading.addEventListener('click', participantsTabActive);
    }
});

function speakerTabActive(event) {
    // Change speaker header colour
    let speakerHeader = document.getElementById("speakers-heading")
    let participantHeader = document.getElementById("participants-heading")
    if (speakerHeader) speakerHeader.style.color = '#ffffff';
    if (participantHeader) participantHeader.style.color = '#c8d96e';
    
    // Hide participants list
    const participantsContainer = document.getElementById("participants-container");
    if (participantsContainer) participantsContainer.style.display = 'none';

    // Show speakers list
    const speakersContainer = document.getElementById("speakers-container");
    if (speakersContainer) speakersContainer.style.display = 'block';
}

function participantsTabActive(event) {
    // Change participants header colour
    let speakerHeader = document.getElementById("speakers-heading")
    let participantHeader = document.getElementById("participants-heading")
    if (speakerHeader) speakerHeader.style.color = '#c8d96e';
    if (participantHeader) participantHeader.style.color = '#ffffff';

    // Hide speakers list
    const speakersContainer = document.getElementById("speakers-container");
    if (speakersContainer) speakersContainer.style.display = 'none';

    // Show participants list
    const participantsContainer = document.getElementById("participants-container");
    if (participantsContainer) participantsContainer.style.display = 'block';
}

// Delete Session Modal Functions - Matching Map Delete Modal behavior
function openDeleteModal() {
    const modal = document.getElementById('deleteSessionModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeDeleteModal() {
    const modal = document.getElementById('deleteSessionModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Close modal when clicking on the backdrop
window.addEventListener('click', function(e) {
    const modal = document.getElementById('deleteSessionModal');
    if (e.target === modal) {
        closeDeleteModal();
    }
});

// Confirm remove participant (keeping browser confirm for participant removal as it's less destructive)
function confirmRemoveParticipant(event) {
    return confirm('Remove this participant from the session?');
}