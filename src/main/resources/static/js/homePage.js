
// Function to open the drop down menu for deleteing a post
function togglePostMenu(btn) {
    const dropdown = btn.nextElementSibling;
    const isOpen = dropdown.classList.contains('open');

    // Close all other open dropdowns first
    const allDropdowns = document.querySelectorAll('.post-menu-dropdown.open');
    for (let i = 0; i < allDropdowns.length; i++) {
        allDropdowns[i].classList.remove('open');
    }

    // If it wasn't open before -> open it now
    if (!isOpen) {
        dropdown.classList.add('open');
    }
}

// Close any open dropdown when the user clicks outside of a post menu
document.addEventListener('click', function(event) {
    const clickedInsideMenu = event.target.closest('.post-menu-wrap');

    if (!clickedInsideMenu) {
        const allDropdowns = document.querySelectorAll('.post-menu-dropdown.open');
        for (let i = 0; i < allDropdowns.length; i++) {
            allDropdowns[i].classList.remove('open');
        }
    }
});