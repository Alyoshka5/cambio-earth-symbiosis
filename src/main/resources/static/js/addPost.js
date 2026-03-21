const CLOUD_NAME = "dm7jfvl1p";
const UPLOAD_PRESET = "symbiosis_images";

const imageInput = document.getElementById("image");
const imgUrlField = document.getElementById("imgUrl");
const uploadArea = document.getElementById("uploadArea");
const filePreviewBox = document.getElementById("filePreviewContainer");
const previewImg = document.getElementById("previewImg");
const previewFileName = document.getElementById("fileName");
const uploadStatus = document.getElementById("imgStatus");
const spinner = document.getElementById("spinner");

function updateCounter(input, counterId, max) {
    document.getElementById(counterId).textContent = input.value.length;
}

// Function to clear image inputs for the user
function clearFile() {
    imageInput.value = "";
    imgUrlField.value = "";
    previewImg.src = "";
    previewFileName.textContent = "";
    filePreviewBox.style.display = "none";
    uploadStatus.textContent = "";
    uploadArea.style.display = "block";
}

// Listen for when the user selects a file
imageInput.addEventListener("change", function () {
    const file = this.files[0];
    if (!file) {
        return;
    }
    // Hide any previous preview while new upload is in progress
    filePreviewBox.style.display = "none";
    previewImg.src = "";
    spinner.style.display = "block";
    uploadArea.style.display = "none";

    uploadToCloudinary(file);
});

// Function to upload images to Cloudinary
async function uploadToCloudinary(file) {

    // Clear any previous URL when uploading
    uploadStatus.textContent = "Uploading...";
    imgUrlField.value = "";

    // Add image data to send to the API
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", UPLOAD_PRESET);

    try {
        const response = await fetch(
            `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`,
            { method: "POST", body: formData }
        );
        const data = await response.json();

        // Display a a preview and status if the upload was a success
        if (data.secure_url) {
            imgUrlField.value = data.secure_url;
            previewImg.src = data.secure_url;
            previewFileName.textContent = file.name;
            spinner.style.display = "none";
            filePreviewBox.style.display = "block";
            uploadStatus.textContent = "Upload Successful!";
        } 
        else { // Handle upload failing
            spinner.style.display = "none";
            uploadArea.style.display = "block";
            uploadStatus.textContent = "Upload failed: " + (data.error?.message || "unknown error");
            uploadStatus.style.color = "#ff6b6b";
        }
    } catch (err) {
        spinner.style.display = "none";
        uploadArea.style.display = "block";
        uploadStatus.textContent = "Upload failed. Check your connection.";
        uploadStatus.style.color = "#ff6b6b";
        console.error(err);
    }
}