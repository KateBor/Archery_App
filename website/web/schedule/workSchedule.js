document.querySelector('.days').addEventListener('click', function () {
    if (!event.target.classList.contains("next-date") && !event.target.classList.contains("prev-date")) {
        if (document.querySelector('#todayid') !== null) {
            const t = document.querySelector('#todayid').classList;
            t.toggle("today");
            document.querySelector('#todayid').removeAttribute("id");
        }
        event.target.className = "today";
        event.target.setAttribute("id","todayid");
        var m = document.querySelector(".date h1").innerHTML.valueOf();
        var d = document.querySelector(".today").innerHTML.valueOf();
        document.querySelector(".date p").innerHTML = d + " " + m;
    }
});


document.querySelector('.schedule .days').addEventListener('click', function () {
    cleaar();
    if (!event.target.classList.contains("next-date") && !event.target.classList.contains("prev-date")) {
        let time= "";
        let startTime = 17;
        let min;

        for (let x = 1; x<=8; x++) { //поменять надо
            if(x%2 === 1 ){
                startTime +=1;
                min = ":00";
                time += `<div class="time--content">${startTime}${min}</div>`;
            } else {
                min = ":30";
                time += `<div class="time--content">${startTime}${min}</div>`;
            }
        }

        var monthDays = document.querySelector(".timeSchedule");
        for(let x = 1; x<=8; x++){
            monthDays.innerHTML = time;
        }
    }
});

function cleaar() {
    document.getElementById("timeS").innerHTML = "";
}
