
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

    document.querySelectorAll('.post-date').forEach(el => {
        const utcDate = el.getAttribute('data-utc-date');
        if (utcDate) {
            // Append 'Z' if not present to ensure JS treats it as UTC
            const date = new Date(utcDate.endsWith('Z') ? utcDate : utcDate + 'Z');
            
            const options = {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
                hour: 'numeric',
                minute: '2-digit',
                hour12: true
            };

            let rawString = date.toLocaleString('en-US', options);
            const lastCommaIndex = rawString.lastIndexOf(',');

            let formattedDate = (
                rawString.substring(0, lastCommaIndex) + 
                ' •' + 
                rawString.substring(lastCommaIndex + 1)
            ).toLowerCase();

            formattedDate = formattedDate.charAt(0).toUpperCase() + formattedDate.slice(1);
            
            el.innerText = formattedDate;
        }
    });

    likeButtons.forEach(button => {
        button.addEventListener("click", async function (event) {
            event.preventDefault();
            event.stopPropagation();

            if (this.dataset.loading === "true") {
                return;
            }

            this.dataset.loading = "true";
            this.disabled = true;

            const postId = this.dataset.postId;
            const icon = this.querySelector(".post-like-icon");
            const text = this.querySelector(".post-like-text");

            const wasLiked = this.dataset.liked === "true";
            const currentLikes = parseInt(text.textContent) || 0;

            if (wasLiked) {
                icon.classList.remove("liked");
                text.textContent = `${Math.max(0, currentLikes - 1)} likes`;
                this.dataset.liked = "false";
            } else {
                icon.classList.add("liked");
                text.textContent = `${currentLikes + 1} likes`;
                this.dataset.liked = "true";
            }

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
                        this.dataset.liked = "true";
                    } else {
                        icon.classList.remove("liked");
                        this.dataset.liked = "false";
                    }
                } else {
                    if (wasLiked) {
                        icon.classList.add("liked");
                        text.textContent = `${currentLikes} likes`;
                        this.dataset.liked = "true";
                    } else {
                        icon.classList.remove("liked");
                        text.textContent = `${currentLikes} likes`;
                        this.dataset.liked = "false";
                    }
                    console.error(data.message);
                }
            } catch (error) {
                if (wasLiked) {
                    icon.classList.add("liked");
                    text.textContent = `${currentLikes} likes`;
                    this.dataset.liked = "true";
                } else {
                    icon.classList.remove("liked");
                    text.textContent = `${currentLikes} likes`;
                    this.dataset.liked = "false";
                }
                console.error("Failed to toggle like:", error);
            } finally {
                this.dataset.loading = "false";
                this.disabled = false;
            }
        });
    });
});