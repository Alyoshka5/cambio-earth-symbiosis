// Find every progress bar track element on the page
var allProgressBars = document.querySelectorAll('.progress-track');

// Loop through each progress bar for every mission
for (var i = 0; i < allProgressBars.length; i++) {

    var currProgressBar = allProgressBars[i];

    // Get the current progress and mission requirment fields
    var total = parseInt(currProgressBar.getAttribute('data-total'));
    var filled = parseInt(currProgressBar.getAttribute('data-filled'));

    // Make sure total and filled is a valid number
    if (total == 0 || isNaN(total)) {
        total = 1;
    }
    if (isNaN(filled)) {
        filled = 0;
    }

    // Cap the number of visual segments at 25
    var numberOfSegments = total;
    if (numberOfSegments > 25) {
        numberOfSegments = 25;
    }

    // Calculate how many segments should be filled
    var filledRatio = filled / total;
    var numberOfFilledSegments = Math.round(filledRatio * numberOfSegments);

    currProgressBar.innerHTML = '';

    // Build each individual segment one by one
    for (var j = 0; j < numberOfSegments; j++) {
        var segment = document.createElement('div');

        // Check if this segment should be filled in or empty
        if (j < numberOfFilledSegments) {
            segment.className = 'progress-segment filled';
        } else {
            segment.className = 'progress-segment';
        }
        currProgressBar.appendChild(segment);
    }
}
