document.getElementById('launch-btn').addEventListener('click', function () {
    document.getElementById('launch-confirm-text').style.display = 'none';
    document.getElementById('launch-footer').style.display = 'none';
    document.getElementById('launch-loading').style.display = 'block';
 
    const form = document.createElement('form');
    form.method = 'post';
    form.action = '/launch';
    document.body.appendChild(form);
    form.submit();
});