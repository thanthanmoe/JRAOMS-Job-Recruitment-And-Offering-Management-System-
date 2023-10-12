function dashboardStatus(){
    fetch("/totalActiveVacancy")
        .then((response)=> response.json())
        .then((data)=>{
            let totalActiveVacancy = data.length;
            console.log("Active Job : "+totalActiveVacancy);

            const div = document.getElementById('active-job');
            div.innerHTML = '';
            const h6 = document.createElement('h6');
            h6.textContent = totalActiveVacancy;
            div.appendChild(h6);
        });

    fetch("/totalInactiveVacancy")
        .then((response)=> response.json())
        .then((data)=>{
            let totalActiveVacancy = data.length;
            console.log("Inactive Job : "+totalActiveVacancy);

            const div = document.getElementById('inactive-job');
            div.innerHTML = '';
            const h6 = document.createElement('h6');
            h6.textContent = totalActiveVacancy;
            div.appendChild(h6);
        });

    fetch("/totalCandidate")
        .then((response)=> response.json())
        .then((data)=>{
            let totalActiveVacancy = data.length;
            console.log("Total candidate : "+totalActiveVacancy);

            const div = document.getElementById('total-candidate');
            div.innerHTML = '';
            const h6 = document.createElement('h6');
            h6.textContent = totalActiveVacancy;
            div.appendChild(h6);
        });
}

document.addEventListener('DOMContentLoaded', function() {
    dashboardStatus();
});