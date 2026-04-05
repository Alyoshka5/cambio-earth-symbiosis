const posts = document.querySelectorAll('.post');
const modalPostImg = document.querySelector('#modal-post-img');
const modalTitle = document.querySelector('#modal-title');
const modalLikeButton = document.querySelector('#modal-like-icon');
const modalLikes = document.querySelector('#modal-likes');
const modalCaption = document.querySelector('#modal-caption');
const modalCreatedAt = document.querySelector('#modal-created-at');
const postIdInput = document.querySelector('#post-id-input');
const deleteForm = document.querySelector('#delete-post-form');

function formatPostDate(dateString) {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;

    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const month = months[date.getMonth()];
    const day = date.getDate();
    const year = date.getFullYear();

    let hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    if (hours === 0) hours = 12;

    return `${month} ${day}, ${year} • ${hours}:${minutes} ${ampm}`;
}

posts.forEach(post => post.addEventListener('click', () => {
    // Add post info to modal
    const postData = JSON.parse(post.dataset.post);
    modalPostImg.src = postData.img;
    modalTitle.textContent = postData.title;
    modalLikes.textContent = postData.likes;
    modalCaption.textContent = postData.caption;
    // Use formatted date
    modalCreatedAt.textContent = formatPostDate(postData.createdAt);
    postIdInput.value = postData.id;
    if (deleteForm) {
        deleteForm.action = `/posts/delete/${postData.id}`;
    }

    // Set like button
    modalLikeButton.dataset.liked = !postData.liked;
    updateLikeButton(!postData.liked);
}));

modalLikeButton.addEventListener('click', async () => {
    const liked = modalLikeButton.dataset.liked === 'true' ? true : false;
    updateLikeButton(liked);
    const likes = Number(modalLikes.textContent);
    modalLikes.textContent = liked ? likes - 1 : likes + 1;

    // Update like on server
    const postId = Number(postIdInput.value);
    try {
        await fetch(`/posts/${postId}/like`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Update post data on data-post attribute
        posts.forEach(post => {
            const postData = JSON.parse(post.dataset.post);
            if (postData.id === postId) {
                const postLikes = Number(postData.likes);
                postData.likes = liked ? postLikes - 1 : postLikes + 1;
                postData.liked = !liked;
                post.dataset.post = JSON.stringify(postData);
            }
        })
    } catch(error) {
        console.log('ERROR: ' + error);
    }
});

function updateLikeButton(liked) {
    modalLikeButton.dataset.liked = modalLikeButton.dataset.liked === 'true' ? 'false' : 'true';
    modalLikeButton.src = liked ? '/images/heart-outline-icon.svg' : '/images/heart-filled-icon.svg';
    if (liked) {
        modalLikeButton.classList.add('white-filter');
    } else {
        modalLikeButton.classList.remove('white-filter');
    }
}