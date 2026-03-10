let selected = [];

// get all rank circles
const circles = document.querySelectorAll(".rank");

// clear any numbers that came from the server/html on first load
circles.forEach(circle => {
    const span = circle.querySelector("span");
    if (span) {
        span.textContent = "";
    }
    circle.classList.remove("filled");
    circle.style.backgroundColor = "";
    circle.style.borderColor = "";
});

// add click event to each circle
circles.forEach(circle => {
    circle.addEventListener("click", selectCircle);
});

function selectCircle(event) {
    const clickedCircle = event.currentTarget;

    // if already selected, deselect it
    if (selected.includes(clickedCircle)) {
        deselectCircle(clickedCircle);
        return;
    }

    // add to selected list
    selected.push(clickedCircle);

    // style selected circle
    clickedCircle.classList.add("filled");
    clickedCircle.style.backgroundColor = "#c3f775";
    clickedCircle.style.borderColor = "#c3f775";

    // update ranking numbers
    fixRanking();
}

function deselectCircle(circle) {
    const index = selected.indexOf(circle);
    if (index === -1) return;

    selected.splice(index, 1);

    circle.classList.remove("filled");
    circle.style.backgroundColor = "";
    circle.style.borderColor = "";

    const span = circle.querySelector("span");
    if (span) {
        span.textContent = "";
    }

    fixRanking();
}

function fixRanking() {
    // first clear all displayed numbers
    circles.forEach(circle => {
        const span = circle.querySelector("span");
        if (span) {
            span.textContent = "";
        }
    });

    // then number only selected ones in order
    selected.forEach((circle, index) => {
        const span = circle.querySelector("span");
        if (span) {
            span.textContent = index + 1;
        }
    });
}