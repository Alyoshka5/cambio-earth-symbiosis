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
    
    // Attach remove participant button listeners
    attachRemoveParticipantListeners();
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
    
    // Re-attach listeners in case content was dynamically updated
    attachRemoveParticipantListeners();
}

// Delete Session Modal Functions
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

// Remove Participant Modal Functions
let currentParticipantUserId = null;
let currentSessionId = null;
let currentParticipantCard = null;

function attachRemoveParticipantListeners() {
    const removeButtons = document.querySelectorAll('.remove-participant-btn');
    removeButtons.forEach(button => {
        // Remove existing listener to avoid duplicates
        button.removeEventListener('click', button.removeClickHandler);
        // Create new handler
        const handler = function(e) {
            e.preventDefault();
            e.stopPropagation();
            const userId = this.getAttribute('data-user-id');
            const sessionId = this.getAttribute('data-session-id');
            const participantName = this.getAttribute('data-participant-name');
            const participantCard = this.closest('.participants-card');
            openRemoveParticipantModal(userId, sessionId, participantName, participantCard);
        };
        button.removeClickHandler = handler;
        button.addEventListener('click', handler);
    });
}

function openRemoveParticipantModal(userId, sessionId, participantName, participantCard) {
    currentParticipantUserId = userId;
    currentSessionId = sessionId;
    currentParticipantCard = participantCard;
    
    const modal = document.getElementById('removeParticipantModal');
    const messageElement = document.getElementById('removeParticipantMessage');
    
    if (messageElement) {
        messageElement.innerHTML = `This will remove "<strong>${escapeHtml(participantName)}</strong>" from this session.`;
    }
    
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeRemoveParticipantModal() {
    const modal = document.getElementById('removeParticipantModal');
    if (modal) {
        modal.style.display = 'none';
    }
    currentParticipantUserId = null;
    currentSessionId = null;
    currentParticipantCard = null;
}

// Actually remove the participant via AJAX
async function confirmRemoveParticipant() {
    if (!currentParticipantUserId || !currentSessionId) {
        console.error('Missing participant or session ID');
        closeRemoveParticipantModal();
        return;
    }
    
    const confirmBtn = document.getElementById('confirmRemoveBtn');
    const originalText = confirmBtn.textContent;
    
    try {
        // Disable button and show loading state
        confirmBtn.disabled = true;
        confirmBtn.textContent = 'Removing...';
        
        // Call the backend API
        const response = await fetch(`/sessions/participants/${currentParticipantUserId}/remove/${currentSessionId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        const result = await response.json();
        
        if (result.success) {
            // Remove the participant card from the DOM
            if (currentParticipantCard && currentParticipantCard.parentElement) {
                currentParticipantCard.remove();
                
                // Check if there are no more participants
                const participantsGrid = document.querySelector('.participants-grid');
                const emptyState = document.querySelector('#participants-container .empty-state');
                
                if (participantsGrid && participantsGrid.children.length === 0) {
                    // Show empty state message
                    if (emptyState) {
                        emptyState.style.display = 'block';
                    }
                }
                
                // Update the capacity badge count if it's a breakout session
                updateParticipantCount();
            }
            
            // Show success message
            showToast('Participant removed successfully', 'success');
            
            // Close modal
            closeRemoveParticipantModal();
        } else {
            showToast(result.message || 'Failed to remove participant', 'error');
            closeRemoveParticipantModal();
        }
    } catch (error) {
        console.error('Error removing participant:', error);
        showToast('An error occurred while removing the participant', 'error');
        closeRemoveParticipantModal();
    } finally {
        // Re-enable button and restore text
        confirmBtn.disabled = false;
        confirmBtn.textContent = originalText;
    }
}

// Update the participant count in the capacity badge
function updateParticipantCount() {
    const participantsGrid = document.querySelector('.participants-grid');
    if (!participantsGrid) return;
    
    const participantCards = participantsGrid.querySelectorAll('.participants-card');
    const currentCount = participantCards.length;
    
    const capacityBadge = document.querySelector('.capacity-badge');
    if (capacityBadge) {
        const textParts = capacityBadge.innerText.match(/\d+/g);
        if (textParts && textParts.length >= 2) {
            const capacity = textParts[1];
            capacityBadge.innerHTML = `<span class="capacity-label">Attendees:</span>
                    <span>${currentCount}</span>
                    /
                    <span>${capacity}</span>`;
        }
    }
}

// Toast notification helper
function showToast(message, type = 'success') {
    // Remove existing toast if any
    const existingToast = document.querySelector('.toast-notification');
    if (existingToast) {
        existingToast.remove();
    }
    
    const toast = document.createElement('div');
    toast.className = `toast-notification ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// Close modals when clicking on the backdrop
window.addEventListener('click', function(e) {
    const deleteModal = document.getElementById('deleteSessionModal');
    const removeModal = document.getElementById('removeParticipantModal');
    
    if (e.target === deleteModal) {
        closeDeleteModal();
    }
    if (e.target === removeModal) {
        closeRemoveParticipantModal();
    }
});

// Helper function to escape HTML
function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Set up the confirm button listener
document.addEventListener('DOMContentLoaded', function() {
    const confirmBtn = document.getElementById('confirmRemoveBtn');
    if (confirmBtn) {
        confirmBtn.addEventListener('click', confirmRemoveParticipant);
    }
});