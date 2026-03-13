// Speaker dynamic list 

let speakerCount = 0;

function addSpeaker(value = '') {
    speakerCount++;
    const list = document.getElementById('speakerList');
    const entry = document.createElement('div');
    entry.className = 'speaker-entry';
    entry.id = `speakerEntry${speakerCount}`;
    entry.innerHTML = `
        <input
            type="text"
            class="form-control speaker-input"
            placeholder="Speaker name"
            value="${escapeHtml(value)}"
            oninput="aggregateSpeakers()"
        >
        <button type="button" class="btn-remove-speaker" onclick="removeSpeaker(${speakerCount})" title="Remove">
            ✕
        </button>
    `;
    list.appendChild(entry);
    entry.querySelector('input').focus();
    aggregateSpeakers();
}

function removeSpeaker(id) {
    const el = document.getElementById(`speakerEntry${id}`);
    if (el) {
        el.style.opacity = '0';
        el.style.transform = 'translateY(-4px)';
        el.style.transition = 'opacity 0.15s, transform 0.15s';
        setTimeout(() => { el.remove(); aggregateSpeakers(); }, 150);
    }
}

function aggregateSpeakers() {
    const inputs = document.querySelectorAll('.speaker-input');
    const names = Array.from(inputs)
        .map(i => i.value.trim())
        .filter(Boolean)
        .join(',');
    document.getElementById('speakersHidden').value = names;
}

// Pre-populate speakers if editing an existing session
// (Thymeleaf renders this inline from the model)
/*[[${session.speakers}]]*/ 
(function seedSpeakers() {
    // Read existing speakers from a data attribute set by Thymeleaf
    const raw = document.getElementById('sessionForm').dataset.speakers || '';
    if (raw) raw.split(',').forEach(s => addSpeaker(s.trim()));
})();


function updateCounter(input, counterId, max) {
    document.getElementById(counterId).textContent = input.value.length;
}

document.addEventListener('DOMContentLoaded', () => {
    const titleInput = document.getElementById('title');
    if (titleInput) updateCounter(titleInput, 'titleCounter', 150);
});


function toggleBreakout() {
    const cb = document.getElementById('isBreakout');
    cb.checked = !cb.checked;
    syncBreakoutWrapper();
}

function syncBreakoutWrapper() {
    const cb = document.getElementById('isBreakout');
    const wrapper = document.getElementById('breakoutWrapper');
    wrapper.classList.toggle('active', cb.checked);
}

syncBreakoutWrapper();

document.getElementById('sessionForm').addEventListener('submit', () => {
    aggregateSpeakers();
});

function escapeHtml(str) {
    return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}