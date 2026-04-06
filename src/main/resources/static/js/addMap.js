function updateFileName(input) {
    const display = document.getElementById('fileNameDisplay');
    const label = document.getElementById('fileLabel');
    if (input.files && input.files[0]) {
        display.textContent = '✓ ' + input.files[0].name;
        display.style.display = 'block';
        label.style.borderColor = 'rgba(200,217,110,0.5)';
    }
}

const CLOUD_NAME = "dm7jfvl1p";
const UPLOAD_PRESET = "symbiosis_images";

function handleFileSelected(input) {
    const file = input.files[0];
    if (!file) return;

    document.getElementById('fileNameDisplay').style.display = 'none';
    document.getElementById('uploadStatus').textContent = 'Uploading...';
    document.getElementById('uploadStatus').style.color = 'var(--sage)';

    uploadToCloudinary(file);
}

async function uploadToCloudinary(file) {
    const status = document.getElementById('uploadStatus');
    const imgUrlField = document.getElementById('imgUrlField');
    const fileTypeField = document.getElementById('fileType');
    const display = document.getElementById('fileNameDisplay');
    const label = document.getElementById('fileLabel');

    const resourceType = file.type === 'application/pdf' ? 'raw' : 'image';

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', UPLOAD_PRESET);

    try {
        const response = await fetch(
            `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/${resourceType}/upload`,
            { method: 'POST', body: formData }
        );
        const data = await response.json();

        if (data.secure_url) {
            imgUrlField.value = data.secure_url;
            fileTypeField.value = file.type;
            display.textContent = '✓ ' + file.name;
            display.style.display = 'block';
            label.style.borderColor = 'rgba(200,217,110,0.5)';
            status.textContent = 'Upload successful!';
            status.style.color = 'var(--accent)';
        } else {
            status.textContent = 'Upload failed: ' + (data.error?.message || 'unknown error');
            status.style.color = 'var(--danger)';
        }
    } catch (err) {
        status.textContent = 'Upload failed. Check your connection.';
        status.style.color = 'var(--danger)';
        console.error(err);
    }
}