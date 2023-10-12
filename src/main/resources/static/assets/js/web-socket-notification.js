const userRole = document.getElementById('user-role-for-notification').textContent;
console.log('UserRole : ',userRole);
let socket = new SockJS('/ws'); // WebSocket endpoint URL
let stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected to WebSocket: ' + frame);

    // Subscribe to a topic
    stompClient.subscribe('/all/messages', function(message) {
        let body = JSON.parse(message.body);
        console.log('Received message:', body.message);

        if (userRole === 'SENIOR'){
            console.log('call totalNotification() : ');
            totalNotification();
        }else if (userRole === 'INTERVIEWER'){
            console.log('call totalInterviewerNotification() : ');
            totalInterviewerNotification();
        }
    });
});

function sendMessage() {
    let message = {
        message: 'Hello'
    };
    stompClient.send('/app/application', {}, JSON.stringify(message));
}

/*async function saveNotification(data) {

    console.log('vacancyId : ',data);

    const notificationDto = {
        vacancyId : data
    }

    const url = '/job/saveNotification';
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(notificationDto)
    };

    try {
        const response = await fetch(url, requestOptions);

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else {
            console.log("save notification");
            sendMessage();
        }
    } catch (error) {
        console.error('Error saving notification:', error);
        // Handle error here
    }
}*/

async function totalNotification() {
    console.log('now totalNotification() : ');

    let seniorTotalNotification = null;
    let totalOfferMailNotification = null;

    try {
        const responses = await Promise.all([
            fetch("/totalNotification").then(response => response.json()),
            fetch("/totalOfferMailNotification").then(response => response.json())
        ]);

        seniorTotalNotification = responses[0].length;
        totalOfferMailNotification = responses[1].length;

        console.log('total seniorTotalNotification : ', seniorTotalNotification);
        console.log('totalOfferMailNotification : ', totalOfferMailNotification);

        let totalNotification = seniorTotalNotification+totalOfferMailNotification;
        console.log('total notification : ', totalNotification);

        const a = document.getElementById('total-notification');

        // Clear previous content and attributes
        a.innerHTML = '';
        a.removeAttribute('href');
        a.removeAttribute('data-bs-toggle');

        // Set base class
        a.className = 'nav-link nav-icon';

        const bellIcon = document.createElement('i');
        bellIcon.className = 'bi bi-bell';
        a.appendChild(bellIcon);

        if (responses[0] && responses[0].length > 0 || responses[1] && responses[1].length > 0) {
            a.href = '#';
            a.setAttribute('data-bs-toggle', 'dropdown');

            const badge = document.createElement('span');
            badge.className = 'badge bg-primary badge-number';
            badge.textContent = totalNotification;
            a.appendChild(badge);

            notificationContent(totalNotification,responses);
        }

    } catch (error) {
        console.error('Error fetching notification count:', error);
    }
}

function notificationContent(totalNotification,responses){

    console.log('now notificationContent() : ');

    // -----------------------------------------------------------------------------

    const div = document.getElementById('notification-content');
    div.innerHTML = ''; // Clear the previous content in the div
    div.style.overflowY = 'auto';
    div.style.height = '400px';

    const header = document.createElement('li');
    header.classList.add('dropdown-header');
    header.innerHTML = 'You have '+totalNotification+' new notifications <a id="clear-all-link" href="#"><span class="badge rounded-pill bg-primary p-2 ms-2">Clear all</span></a>';
    div.appendChild(header);

    // --------------------------------------------------------------------------------

    if (responses[0] && responses[0].length > 0 && responses[1] && responses[1].length > 0){
        console.log('have both');
    }
    else if (responses[0] && responses[0].length > 0){
        console.log('have senior notification : ');
    }
    else {
        console.log('have offerMail notification : ');
    }

    //----------------------------------------------------------------------------------

    fetch("/notificationContent")
        .then((response) => response.json())
        .then((data) => {

            console.log('senior notification content : ',data);

            data.forEach(content => {
                const li = document.createElement('li');
                const li2 = document.createElement('li');
                li2.classList.add('notification-item');
                li2.setAttribute('id', 'clearNotification');

                const hr = document.createElement('hr');
                hr.classList.add('dropdown-divider');

                const icon = document.createElement('i');
                icon.classList.add('bi', 'bi-exclamation-circle', 'text-warning');

                const div1 = document.createElement('div');

                const h4 = document.createElement('h4');
                h4.textContent = content.vacancy.position;

                const p = document.createElement('p');
                p.textContent = content.notifications.length + ' new CV received';

                div.appendChild(li);
                div.appendChild(li2);

                li.appendChild(hr);

                li2.appendChild(icon);
                li2.appendChild(div1);
                div1.appendChild(h4);
                div1.appendChild(p);

                div1.addEventListener('click', async () => {
                    console.log('save saveNotificationUser :', content);
                    await saveNotificationUser(content);
                });

            });

            console.log('OfferMailNotification content : ', responses[1]);

            responses[1].forEach(content => {
                const li = document.createElement('li');
                const li2 = document.createElement('li');
                li2.classList.add('notification-item');
                li2.setAttribute('id', 'clearNotification');

                const hr = document.createElement('hr');
                hr.classList.add('dropdown-divider');

                const icon = document.createElement('i');
                icon.classList.add('bi', 'bi-check-circle', 'text-success');

                const div1 = document.createElement('div');

                const h4 = document.createElement('h4');
                h4.textContent = content.candidate.name;

                const p = document.createElement('p');
                p.textContent = 'passed interview for '+content.vacancy.position+' position.';

                const p2 = document.createElement('p');
                p2.textContent = showElapsedTime(content.receive);

                div.appendChild(li);
                div.appendChild(li2);

                li.appendChild(hr);

                li2.appendChild(icon);
                li2.appendChild(div1);
                div1.appendChild(h4);
                div1.appendChild(p);
                div1.appendChild(p2);

                div1.addEventListener('click',async ()=>{
                    console.log(' save OfferEmailNotification - user : ');
                    console.log(content);
                    await saveOfferMailNotificationUser(content);
                });

            });

            const clearAll = document.getElementById('clear-all-link')
            clearAll.addEventListener('click', async ()=>{
                if (data.length > 0 ){
                    console.log('clear all senior notification:',data);
                    await clearAllNotification(data);
                }
                if (responses[1].length > 0 ){
                    console.log('clear all offer mail notification: ',responses[1]);
                    await clearAllOfferMailNotification(responses[1]);
                }
            });
        });
}

async function saveNotificationUser(data){
    console.log("save notification_user :");
    console.log(data);

    const url = "/saveNotificationUser";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            const vacancyId = data.vacancy.id;
            window.location.href = "/manage/status/"+vacancyId;
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }
}

async function clearAllNotification(data){

    console.log("clear all notification :");
    console.log(data);

    const url = "/clearAllNotification";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            totalNotification();
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }

}

// Function to calculate and display the time elapsed
function showElapsedTime(receiveDate) {
    const startDate = new Date(receiveDate);
    const currentDate = new Date();
    const timeDifference = currentDate.getTime() - startDate.getTime();

    if (timeDifference < 60000) { // Less than a minute ( 60000 ms = 1 min)
        return Math.floor(timeDifference / 1000) + ' seconds ago';
    } else if (timeDifference < 86400000) { // Less than a day (86400000 ms = 1 day)
        const minutes = Math.floor(timeDifference / 60000);
        if (minutes < 60) {
            return minutes + ' minutes ago';
        } else {
            const hours = Math.floor(minutes / 60);
            return hours + ' hr ago';
        }
    } else { // More than a day
        return Math.floor(timeDifference / 86400000) + ' days ago';
    }
}

// -------------------------------------------------------------------------------------------

async function saveInterviewerNotification(data){
    console.log('now saveInterviewerNotification() :');

    const url = "/saveInterviewerNotification";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    const response = await fetch(url, requestOptions);
    if (response.ok){
        console.log('saveInterviewerNotification Successful!!');
        console.log('call sendMessage() : ');
        sendMessage();
    }else {
        console.log('error saveInterviewerNotification');
    }
}

async function saveInterviewerNotificationUser(data){

    console.log("save interviewer notification user :");
    console.log(data);

    const url = "/saveInterviewerNotificationUser";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            const vacancyId = data.vacancyId;
            if (data.action === '1st Interview'){
                window.location.href = "/interview/status/"+vacancyId;
            }else {
                window.location.href = "/interview/statusnoti/"+vacancyId;
            }
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }
}

async function clearAllInterviewerNotification(data){

    console.log("clear all interviewer notification :");
    console.log(data);

    const url = "/clearAllInterviewerNotification";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            totalInterviewerNotification();
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }

}

function totalInterviewerNotification() {
    console.log('now totalInterviewerNotification() !!');

    fetch("/totalInterviewerNotification")
        .then(response => response.json())
        .then(data => {
            const a = document.getElementById('total-notification');

            // Clear previous content and attributes
            a.innerHTML = '';
            a.removeAttribute('href');
            a.removeAttribute('data-bs-toggle');

            // Set base class
            a.className = 'nav-link nav-icon';

            const bellIcon = document.createElement('i');
            bellIcon.className = 'bi bi-bell';
            a.appendChild(bellIcon);

            if (data && data.length > 0) {
                a.href = '#';
                a.setAttribute('data-bs-toggle', 'dropdown');

                const badge = document.createElement('span');
                badge.className = 'badge bg-primary badge-number';
                badge.textContent = data.length;
                a.appendChild(badge);

                //Start notification content

                data.forEach(content => {

                    const div = document.getElementById('notification-content');
                    div.innerHTML = ''; // Clear the previous content in the div
                    div.style.overflowY = 'auto';
                    div.style.height = '400px';

                    const header = document.createElement('li');
                    header.classList.add('dropdown-header');
                    header.innerHTML = 'You have '+data.length+' new notifications <a id="clear-all-link" href="#"><span class="badge rounded-pill bg-primary p-2 ms-2">Clear all</span></a>';
                    div.appendChild(header);
                    data.forEach(content => {
                        const li = document.createElement('li');
                        const li2 = document.createElement('li');
                        li2.classList.add('notification-item');
                        li2.setAttribute('id', 'clearNotification');

                        const hr = document.createElement('hr');
                        hr.classList.add('dropdown-divider');

                        const icon = document.createElement('i');
                        icon.classList.add('bi', 'bi-exclamation-circle', 'text-warning');

                        const div1 = document.createElement('div');

                        const h4 = document.createElement('h4');
                        h4.textContent = content.candidate.name;

                        console.log(content.vacancyId);
                        const p = document.createElement('p');
                        p.textContent = content.action+" / "+content.interviewFormat+" / "+content.interviewDate;

                        const p2 = document.createElement('p');
                        p2.textContent = showElapsedTime(content.receive);

                        div.appendChild(li);
                        div.appendChild(li2);

                        li.appendChild(hr);

                        li2.appendChild(icon);
                        li2.appendChild(div1);
                        div1.appendChild(h4);
                        div1.appendChild(p);
                        div1.appendChild(p2);

                        div1.addEventListener('click',async ()=>{
                            await saveInterviewerNotificationUser(content);
                        });

                    });

                    const clearAll = document.getElementById('clear-all-link')
                    clearAll.addEventListener('click', async ()=>{
                        await clearAllInterviewerNotification(data);
                    });

                });
            }
        })
        .catch(error => {
            console.error('Error fetching notification count:', error);
        });
}

//--------------------------------------------------------------------------------------

async function saveOfferMailNotification(rowId,vacancyId){
    const url = "/saveOfferMailNotification";
    const data = {
        candidateId : rowId,
        vacancyId : vacancyId
    };
    const requestOptions = {

        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };
    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            console.log('saveOfferMailNotification Successful : ')
            sendMessage();
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }
}

async function saveOfferMailNotificationUser(data){

    const url = "/saveOfferMailNotificationUser";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            const vacancyId = data.vacancy.id;
            window.location.href = "/manage/statusnoti/"+vacancyId;
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }
}

async function clearAllOfferMailNotification(data){
    console.log("clear all OfferMailNotification :");
    console.log(data);

    const url = "/clearAllOfferMailNotification";
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }else{
            totalNotification();
        }
    } catch (error) {
        console.error('Error saving notification:', error);
    }
}

//--------------------------------------------------------------------------------------

document.addEventListener('DOMContentLoaded', function() {
    if (userRole === 'SENIOR'){
        console.log('call totalNotification() : ')
        totalNotification();
    }else if (userRole === 'INTERVIEWER'){
        console.log('call totalInterviewerNotification() : ')
        totalInterviewerNotification();
    }
    /*else {
        const a = document.getElementById('total-notification');

        // Clear previous content and attributes
        a.innerHTML = '';
        a.removeAttribute('href');
        a.removeAttribute('data-bs-toggle');

        // Set base class
        a.className = 'nav-link nav-icon';

        const bellIcon = document.createElement('i');
        bellIcon.className = 'bi bi-bell';
        a.appendChild(bellIcon);
    }*/
});