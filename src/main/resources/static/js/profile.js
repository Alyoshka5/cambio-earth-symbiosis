document.addEventListener("DOMContentLoaded", function () {

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
    // Append 'Z' if not present to ensure JS treats it as UTC
    const date = new Date(dateString.endsWith('Z') ? dateString : dateString + 'Z');
    
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
    return formattedDate;
}

posts.forEach(post => post.addEventListener('click', () => {
    const postData = JSON.parse(post.dataset.post);
    modalPostImg.src = postData.img;
    modalTitle.textContent = postData.title;
    modalLikes.textContent = postData.likes;
    modalCaption.textContent = postData.caption;
    modalCreatedAt.textContent = formatPostDate(postData.createdAt);
    postIdInput.value = postData.id;
    if (deleteForm) {
        deleteForm.action = `/posts/delete/${postData.id}`;
    }

    modalLikeButton.dataset.liked = !postData.liked;
    updateLikeButton(!postData.liked);
}));

modalLikeButton.addEventListener('click', async () => {
    const liked = modalLikeButton.dataset.liked === 'true' ? true : false;
    updateLikeButton(liked);
    const likes = Number(modalLikes.textContent);
    modalLikes.textContent = liked ? likes - 1 : likes + 1;

    const postId = Number(postIdInput.value);
    try {
        await fetch(`/posts/${postId}/like`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

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

}); 