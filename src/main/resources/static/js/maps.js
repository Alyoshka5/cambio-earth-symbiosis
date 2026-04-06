/* ── Delete modal ── */
function openDeleteModal(id, title) {
    document.getElementById('deleteModalMsg').textContent = `This will permanently remove "${title}".`;
    document.getElementById('deleteForm').action = `/maps/${id}/delete`;
    document.getElementById('deleteModal').classList.add('open');
}

function closeDeleteModal() {
    document.getElementById('deleteModal').classList.remove('open');
}

/* ── Lightbox ── */
function openLightbox(imgEl, floor, title) {
    document.getElementById('lightboxImg').src = imgEl.src;
    document.getElementById('lightboxFloor').textContent = floor;
    document.getElementById('lightboxTitle').textContent = title;
    document.getElementById('lightbox').classList.add('open');
    document.body.style.overflow = 'hidden';
}

function closeLightbox() {
    document.getElementById('lightbox').classList.remove('open');
    document.body.style.overflow = '';
}

function closeLightboxOnBackdrop(event) {
    if (event.target === document.getElementById('lightbox')) {
        closeLightbox();
    }
}

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeLightbox();
});