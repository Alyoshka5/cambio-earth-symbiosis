// Track the current selections
let selected = [];

// Get all the possible rank circles
let circles = document.querySelectorAll('.rank');

// Loop through all circles to add a click event to each
for (let i = 0; i < circles.length; i++) {
    circles[i].addEventListener('click', selectCircle);
}

// Main function to handle selecting and ranking selections
function selectCircle(event) {

    // Go through all selected circles to see if its being deselected
    let clickedCircle = event.currentTarget;
    for (let i = 0; i < selected.length; i++) {
        if (selected[i] === clickedCircle) {
            deselectCircle(clickedCircle);
            return;
        }
    }

    // Add the ranking circle to the selected list
    selected.push(clickedCircle)

    // Change the circle display and number
    clickedCircle.style.backgroundColor = '#c3ff75';
    clickedCircle.style.borderColor = '#c3ff75';
    clickedCircle.classList.add('filled');
    clickedCircle.querySelector('span').textContent = `${selected.length}`;
}

// Function to deselect the unwanted session in selections
function deselectCircle(unwanted) {

    // Remove the unwanted selection from the selected array
    let unwantedIndex = selected.findIndex(function(item) {return item === unwanted;});

    // Error handle unwanted circle not found in selected array
    if (unwantedIndex == -1) {
        return;
    }
    selected.splice(unwantedIndex, 1);

    // Reset the circle's appearance back to unselected
    unwanted.style.backgroundColor = '#243023';
    unwanted.style.borderColor = '#000000';
    unwanted.classList.remove('filled');
    unwanted.querySelector('span').textContent = '';

    fixRanking();
}

// Function to reorder all the selections with the correct rank
function fixRanking() {
    for (let i = 0; i < selected.length; i++) {
        selected[i].querySelector('span').textContent = `${i + 1}`;
    }
}