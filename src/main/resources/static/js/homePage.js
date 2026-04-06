
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

document.addEventListener("DOMContentLoaded", function () {
    const likeButtons = document.querySelectorAll(".post-like-btn");

    likeButtons.forEach(button => {
        button.addEventListener("click", async function () {
            const postId = this.dataset.postId;
            const icon = this.querySelector(".post-like-icon");
            const text = this.querySelector(".post-like-text");

            try {
                const response = await fetch(`/posts/${postId}/like`, {
                    method: "POST",
                    headers: {
                        "X-Requested-With": "XMLHttpRequest"
                    }
                });

                const data = await response.json();

                if (data.success) {
                    text.textContent = `${data.likes} likes`;

                    if (data.liked) {
                        icon.classList.add("liked");
                    } else {
                        icon.classList.remove("liked");
                    }
                } else {
                    console.error(data.message);
                }
            } catch (error) {
                console.error("Failed to toggle like:", error);
            }
        });
    });
});