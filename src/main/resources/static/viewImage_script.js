document.addEventListener('DOMContentLoaded', function() {
    const commentForm = document.querySelector('.comment-form');
    const commentsContainer = document.querySelector('.comments-container');

    commentForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const nameInput = commentForm.querySelector('input[type="text"][placeholder="Your name"]');
        const commentInput = commentForm.querySelector('input[type="text"][placeholder="Add a comment"]');
        const name = nameInput.value.trim();
        const comment = commentInput.value.trim();

        if (name !== '' && comment !== '') {
            addComment(name, comment);
            nameInput.value = '';
            commentInput.value = '';
        } else {
            alert('Please enter your name and comment');
        }
    });

    function addComment(name, comment) {
        const commentElement = document.createElement('div');
        commentElement.classList.add('comment');
        commentElement.innerHTML = `<strong>${name}</strong>: ${comment}`;
        commentsContainer.appendChild(commentElement);
    }
});