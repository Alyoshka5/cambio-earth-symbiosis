const searchInput   = document.getElementById('searchInput');
const filterRow     = document.getElementById('filterRow');
const grid          = document.getElementById('participantsGrid');
const noResults     = document.getElementById('noResultsMsg');
const cards         = Array.from(grid.querySelectorAll('.participant-card'));

let activeFilter = 'all';

// ── Filter tabs ──────────────────────────────────────────
filterRow.querySelectorAll('.filter-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        filterRow.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        activeFilter = tab.dataset.filter;
        applyFilters();
    });
});

// ── Search ───────────────────────────────────────────────
searchInput.addEventListener('input', applyFilters);

// ── Combined filter + search ─────────────────────────────
function applyFilters() {
    const query = searchInput.value.trim().toLowerCase();
    let visible = 0;

    cards.forEach(card => {
        const name  = (card.dataset.name  || '').toLowerCase();
        const email = (card.dataset.email || '').toLowerCase();
        const role  = (card.dataset.role  || '').toLowerCase();

        const matchesSearch = !query || name.includes(query) || email.includes(query);
        const matchesFilter = activeFilter === 'all' || role === activeFilter;

        if (matchesSearch && matchesFilter) {
            card.style.display = '';
            visible++;
        } else {
            card.style.display = 'none';
        }
    });

    noResults.style.display = visible === 0 && cards.length > 0 ? 'block' : 'none';
}