// Date formatting function for sessions with start and end times
// Format: "Apr 5, 2026 • 5:23 pm"
function formatSessionDateTime(startDateTime, endDateTime) {
    if (!startDateTime || startDateTime === 'Time TBA') return 'Time TBA';
    
    try {
        const startDate = new Date(startDateTime);
        
        if (isNaN(startDate.getTime())) return startDateTime;
        
        // Format: "Apr 5, 2026 • 5:23 pm"
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        const month = months[startDate.getMonth()];
        const day = startDate.getDate();
        const year = startDate.getFullYear();
        
        let hours = startDate.getHours();
        const minutes = startDate.getMinutes().toString().padStart(2, '0');
        const ampm = hours >= 12 ? 'pm' : 'am';
        hours = hours % 12;
        hours = hours ? hours : 12; // Convert 0 to 12
        
        let formatted = `${month} ${day}, ${year} • ${hours}:${minutes} ${ampm}`;
        
        // Add end time if available
        if (endDateTime) {
            const endDate = new Date(endDateTime);
            if (!isNaN(endDate.getTime())) {
                let endHours = endDate.getHours();
                const endMinutes = endDate.getMinutes().toString().padStart(2, '0');
                const endAmpm = endHours >= 12 ? 'pm' : 'am';
                endHours = endHours % 12;
                endHours = endHours ? endHours : 12;
                formatted += ` – ${endHours}:${endMinutes} ${endAmpm}`;
            }
        }
        
        return formatted;
    } catch (error) {
        return startDateTime;
    }
}

// Helper function to escape HTML and prevent XSS
function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

const searchInput   = document.getElementById('searchInput');
const filterRow     = document.getElementById('filterRow');
const grid          = document.getElementById('participantsGrid');
const noResults     = document.getElementById('noResultsMsg');
let cards = [];

function updateCards() {
    cards = Array.from(grid.querySelectorAll('.participant-card:not(.empty-state)'));
}
updateCards();

let activeFilter = 'all';

if (filterRow) {
    filterRow.querySelectorAll('.filter-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            filterRow.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            activeFilter = tab.dataset.filter;
            applyFilters();
        });
    });
}

if (searchInput) {
    searchInput.addEventListener('input', applyFilters);
}

function applyFilters() {
    updateCards();
    const query = searchInput.value.trim().toLowerCase();
    let visible = 0;

    cards.forEach(card => {
        const link = card.querySelector('.card-link');
        const nameElem = link ? link.querySelector('.participant-name') : null;
        const emailElem = link ? link.querySelector('.participant-email') : null;
        const name = (nameElem ? nameElem.innerText : '').toLowerCase();
        const email = (emailElem ? emailElem.innerText : '').toLowerCase();
        const role = card.dataset.userRole || '';

        const matchesSearch = !query || name.includes(query) || email.includes(query);
        const matchesFilter = activeFilter === 'all' || role === activeFilter;

        if (matchesSearch && matchesFilter) {
            card.style.display = '';
            visible++;
        } else {
            card.style.display = 'none';
        }
    });

    if (noResults) {
        noResults.style.display = visible === 0 && cards.length > 0 ? 'block' : 'none';
    }
}

document.addEventListener('click', function() {
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        menu.classList.remove('show');
    });
});

document.querySelectorAll('.menu-dots').forEach(button => {
    button.addEventListener('click', (e) => {
        e.stopPropagation();
        const dropdown = button.nextElementSibling;
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            if (menu !== dropdown) menu.classList.remove('show');
        });
        dropdown.classList.toggle('show');
    });
});

document.querySelectorAll('.set-admin').forEach(btn => {
    btn.addEventListener('click', async (e) => {
        e.stopPropagation();
        const card = btn.closest('.participant-card');
        const userId = card.dataset.userId;
        try {
            const response = await fetch('/participants/' + userId + '/set-admin', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            if (response.ok) {
                location.reload();
            } else {
                alert('Failed to set user as admin');
            }
        } catch (error) {
            alert('Error setting user as admin');
        }
        btn.closest('.dropdown-menu').classList.remove('show');
    });
});

const modal = document.getElementById('sessionModal');
const modalBody = document.getElementById('modalBody');
const modalTitle = document.getElementById('modalTitle');
const closeModal = document.querySelector('.close-modal');

closeModal.addEventListener('click', () => {
    modal.style.display = 'none';
});

window.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});

async function loadUserAddSessions(userId, userName) {
    modalTitle.innerText = 'Add ' + userName + ' to Session';
    modalBody.innerHTML = '<div class="no-sessions">Loading available sessions...</div>';
    modal.style.display = 'block';
    
    try {
        const response = await fetch('/sessions/user/' + userId + '/available-sessions');
        const sessions = await response.json();
        
        if (sessions.length === 0) {
            modalBody.innerHTML = '<div class="no-sessions">No available sessions to add</div>';
            return;
        }
        
        let html = '';
        sessions.forEach(session => {
            const timeDisplay = formatSessionDateTime(session.startDateTime, session.endDateTime);
            html += `
                <div class="session-list-item" data-session-id="${session.id}">
                    <div class="session-info">
                        <div class="session-title">${escapeHtml(session.title)}</div>
                        <div class="session-time">${escapeHtml(timeDisplay)}</div>
                    </div>
                    <button class="add-session-btn" onclick="addUserToSession(${userId}, ${session.id}, '${escapeHtml(session.title).replace(/'/g, "\\'")}')">Add</button>
                </div>
            `;
        });
        modalBody.innerHTML = html;
    } catch (error) {
        modalBody.innerHTML = '<div class="no-sessions">Error loading sessions</div>';
    }
}

async function loadUserRemoveSessions(userId, userName) {
    modalTitle.innerText = 'Remove ' + userName + ' from Session';
    modalBody.innerHTML = '<div class="no-sessions">Loading registered sessions...</div>';
    modal.style.display = 'block';
    
    try {
        const response = await fetch('/sessions/user/' + userId + '/sessions');
        const sessions = await response.json();
        
        if (sessions.length === 0) {
            modalBody.innerHTML = '<div class="no-sessions">No sessions registered</div>';
            return;
        }
        
        let html = '';
        sessions.forEach(session => {
            const timeDisplay = formatSessionDateTime(session.startDateTime, session.endDateTime);
            html += `
                <div class="session-list-item" data-session-id="${session.id}">
                    <div class="session-info">
                        <div class="session-title">${escapeHtml(session.title)}</div>
                        <div class="session-time">${escapeHtml(timeDisplay)}</div>
                    </div>
                    <button class="remove-session-btn" onclick="removeUserFromSession(${userId}, ${session.id}, '${escapeHtml(session.title).replace(/'/g, "\\'")}')">Remove</button>
                </div>
            `;
        });
        modalBody.innerHTML = html;
    } catch (error) {
        modalBody.innerHTML = '<div class="no-sessions">Error loading sessions</div>';
    }
}

window.addUserToSession = async function(userId, sessionId, sessionTitle) {
    try {
        const response = await fetch('/sessions/participants/' + userId + '/add/' + sessionId, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        if (response.ok) {
            loadUserAddSessions(userId, document.getElementById('modalTitle').innerText.replace('Add ', '').replace(' to Session', ''));
        } else {
            const data = await response.json();
            alert(data.message || 'Failed to add user to session');
        }
    } catch (error) {
        alert('Error adding user to session');
    }
};

window.removeUserFromSession = async function(userId, sessionId, sessionTitle) {
    try {
        const response = await fetch('/sessions/participants/' + userId + '/remove/' + sessionId, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        if (response.ok) {
            loadUserRemoveSessions(userId, document.getElementById('modalTitle').innerText.replace('Remove ', '').replace(' from Session', ''));
        } else {
            const data = await response.json();
            alert(data.message || 'Failed to remove user from session');
        }
    } catch (error) {
        alert('Error removing user from session');
    }
};

document.querySelectorAll('.add-session').forEach(btn => {
    btn.addEventListener('click', async (e) => {
        e.stopPropagation();
        const card = btn.closest('.participant-card');
        const userId = card.dataset.userId;
        const userName = card.dataset.userName;
        loadUserAddSessions(userId, userName);
        btn.closest('.dropdown-menu').classList.remove('show');
    });
});

document.querySelectorAll('.remove-session').forEach(btn => {
    btn.addEventListener('click', async (e) => {
        e.stopPropagation();
        const card = btn.closest('.participant-card');
        const userId = card.dataset.userId;
        const userName = card.dataset.userName;
        loadUserRemoveSessions(userId, userName);
        btn.closest('.dropdown-menu').classList.remove('show');
    });
});
