//Get local date
const currentDate = new Date();
const year = currentDate.getFullYear();
const month = currentDate.getMonth(); // Note: Months are zero-based (0-11)
const day = currentDate.getDate();
let localDate = ` (${day}-${month + 1}-${year}) `;
let candidate_name="Ms";
let statusStage;
//invite Mail 
	function inviteMailModal(data, stage,email,name,interviewHistory) {
		console.log("inviteMailModal");
	    let modalElement = document.getElementById('invite-mail');
	    modalElement.dataset.id = data;
	    modalElement.dataset.stage = stage;
	    modalElement.dataset.email = email;
	    modalElement.dataset.name = name;
        modalElement.dataset.interviewHistory = interviewHistory;
        let select = modalElement.querySelector('#interview');
        select.value = '1st Interview';
        $('#second, #third,#fourth,#fifth').hide();
        $('#first').show();
        statusStage=stage;
	  candidate_name = name;

	  updateEditorContent();
	    $(document).ready(function() {
	        $('#to').val(email);
	        $('#to-offer').val(email);
	        $('#candidate_id').val(data);
	        $('#offer_candidate_id').val(data);
	    
	   	    });
	}
function interviewInviteMailModal(data, stage, email, name,interviewHistory) {
    console.log("interviewInviteMailModal");

    // 1. Updating the modal with provided data attributes
    var modalElement = document.getElementById('invite-mail');
    modalElement.dataset.id = data;
    modalElement.dataset.stage = stage;
    modalElement.dataset.email = email;
    modalElement.dataset.name = name;
    modalElement.dataset.interviewHistory = interviewHistory;
    statusStage=stage;
    candidate_name = name;
    if(interviewHistory==="1st Interview") {
        let select = modalElement.querySelector('#interview');
        select.value = '2nd Interview';
    }else if(interviewHistory==="2nd Interview"){
        let select = modalElement.querySelector('#interview');
        select.value = '3rd Interview';
    }else if(interviewHistory==="3rd Interview"){
        let select = modalElement.querySelector('#interview');
        select.value = '4th Interview';
    }else {
        let select = modalElement.querySelector('#interview');
        select.value = '5th Interview';
    }


    $('#first').hide();
        $('#second, #third, #fourth, #fifth').show();


    // 4. Update editor content
    // Assuming you have the methods 'updateEditorContent' and 'updateEditorOfferContent' defined elsewhere in your code
    updateEditorContent();

    // 5. Setting email and candidate id for both invitation and offer sections
    $(document).ready(function() {
        $('#to').val(email);
        $('#to-offer').val(email);
        $('#candidate_id').val(data);
        $('#offer_candidate_id').val(data);
    });
}
function offerMailModal(data, stage, email, name) {
    console.log("interviewInviteMailModal");

    // 1. Updating the modal with provided data attributes
    var modalElement = document.getElementById('offer-mail');
    modalElement.dataset.id = data;
    modalElement.dataset.stage = stage;
    modalElement.dataset.email = email;
    modalElement.dataset.name = name;
    statusStage=stage;
    candidate_name = name;
    updateEditorOfferContent();

    // 5. Setting email and candidate id for both invitation and offer sections
    $(document).ready(function() {
        $('#to').val(email);
        $('#to-offer').val(email);
        $('#candidate_id').val(data);
        $('#offer_candidate_id').val(data);
    });
}
//Get full name of day
const optionsOfDayName = { weekday: 'long' };
let dayName = currentDate.toLocaleString('en-US', optionsOfDayName);

//Get start time
const optionsOfTimeWithAmPm = { hour: 'numeric', minute: 'numeric', hour12: true };
let startTimeWithAmPm = currentDate.toLocaleString('en-US', optionsOfTimeWithAmPm);

//Get end time
const oneHourInMillis = 60 * 60 * 1000;
const newDate = new Date(currentDate.getTime() + oneHourInMillis);
let endTimeWithAmPm = newDate.toLocaleString('en-US', optionsOfTimeWithAmPm);

let editorInstance;
let contentData;

// Call CK text editor for invite mail
ClassicEditor
    .create(document.getElementById('editor'))
    .then(editor => {
        editorInstance = editor; // Store the editor instance
        editor.setData(contentData); // Set initial content
    })
    .catch(error => {
        console.error(error);
    });

const onlineRadio = document.getElementById('online');
const inPersonRadio = document.getElementById('in-person');
const date = document.getElementById('date');
const startTimeElement = document.getElementById('start-time');
const endTimeElement = document.getElementById('end-time');

startTimeElement.addEventListener('change', updateStartTimes);
endTimeElement.addEventListener('change', updateEndTimes);
date.addEventListener('input', updateLocalDate);
onlineRadio.addEventListener('change', updateEditorContent);
inPersonRadio.addEventListener('change', updateEditorContent);

//Update input start time
function updateStartTimes() {
    const selectedStartTime = startTimeElement.value;
    const [startHour, startMinute] = selectedStartTime.split(':');

    const startDateTime = new Date(year, month, day, startHour, startMinute);
    startTimeWithAmPm = startDateTime.toLocaleString('en-US', optionsOfTimeWithAmPm);

    updateEditorContent();
}

//Update input end time
function updateEndTimes() {

    const selectedEndTime = endTimeElement.value;
    const [endHour, endMinute] = selectedEndTime.split(':');

    const endDateTime = new Date(year, month, day, endHour, endMinute);
    endTimeWithAmPm = endDateTime.toLocaleString('en-US', optionsOfTimeWithAmPm);

    updateEditorContent();
}

// Update input LocalDate
function updateLocalDate() {
    const selectedDate = new Date(date.value);
    const year = selectedDate.getFullYear();
    const month = selectedDate.getMonth();
    const day = selectedDate.getDate();
    const optionsOfDayName = { weekday: 'long' };

    localDate = ` (${day}-${month + 1}-${year}) `;
    dayName = selectedDate.toLocaleString('en-US', optionsOfDayName);
    updateEditorContent();
}

//Update Text Editor's Content Data
function updateEditorContent() {

    // Update contentData based on the selected radio button
    if (onlineRadio.checked) {
        console.log("Interview Type : Online");

        contentData =
            "<p>Dear "+candidate_name+",</p>" +
            "<p>Welcome to ACE Data Systems Ltd. We would like to invite you for an interview at a Zoom Meeting to get to know you better. The appointment is on" +
            localDate + " " + dayName + " " + startTimeWithAmPm + " to " + endTimeWithAmPm +
            ". Please join using the Zoom link below. Thank you.</p>" +
            "<p>&nbsp;</p>" +
            "<p>Note: We want you to be in good condition during the interview, with a stable internet connection and in a quiet environment.</p>" +
            "<p>Join Zoom Meeting</p>" +
            "<p>https://zoom.us/j/92191528025?pwd=K1BMUzR4M0hQZDJqQm1DUWxsRTN3dz09</p>" +
            "<p>Meeting ID: 921 9152 8025</p>" +
            "<p>Passcode: 178426</p>" +
            "<p>&nbsp;</p>";

    }else if (inPersonRadio.checked){
        console.log("Interview Type : InPerson");
        contentData =
              "<p>Dear "+candidate_name+",</p>" +
            "<p>Welcome to ACE Data Systems Ltd. We would like to invite you for an interview at a in Person to get to know you better. The appointment is on" +
            localDate + " " + dayName + " " + startTimeWithAmPm + " to " + endTimeWithAmPm +
            "<p>&nbsp;</p>";

    }

    if (editorInstance) {
        editorInstance.setData(contentData);
    }
}

// Declare the function outside of the DOMContentLoaded event listener
function refreshCandidatesData() {
    const vacancyIdInput = document.querySelector("input[name='vacancyId']");

    if (!vacancyIdInput) {
        console.warn("No input element with name 'vacancyId' found on the page.");
        return;
    }

    const vacancyId = vacancyIdInput.value;
    const statusList = ["RECEIVE", "VIEW", "CONSIDERING", "PENDING", "PASSED", "CANCEL", "NOTINTERVIEW"];
    const statusElementMap = {
        "RECEIVE": 'receiveCount',
        "VIEW": 'viewCount',
        "CONSIDERING": 'consideringCount',
        "PENDING": 'pendingCount',
        "PASSED": 'passedCount',
        "CANCEL": 'cancelCount',
        "NOTINTERVIEW": 'notInterviewCount'
    };

    const promises = statusList.map(status => {
        const url = `/countsCandidate/${vacancyId}/${status}`;

        return fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error for status ${status}! Status: ${response.status}`);
                }
                return response.json();
            });
    });

    Promise.all(promises)
        .then(results => {
            results.forEach((candidate, index) => {
                const status = statusList[index];
                const elementId = statusElementMap[status];
                document.getElementById(elementId).textContent = candidate.length;
            });
        })
        .catch(error => {
            console.error("Failed to fetch candidate data:", error);
        });
}

document.addEventListener('DOMContentLoaded', function() {
    refreshCandidatesData(); // Call the function when the document content is loaded

    // If these functions are defined elsewhere in your code, they'll work as intended
    updateEditorContent();
    updateEditorOfferContent();
});


//Sent Invite Email
    const uploadForm = document.getElementById("uploadForm");
    uploadForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const submitButton = document.getElementById("invite-button");
        const spinner = document.getElementById("invite-spinner");

        const form = event.target;
        const formData = new FormData(form);

        const ccEmailsArray = Array.from(addedEmails);
        console.log(ccEmailsArray);
        formData.append("cc", (ccEmailsArray.join(",")));
        console.log("changeArray", ccEmailsArray.join(","));

        submitButton.disabled = true; // Disable the button
        spinner.classList.remove("d-none");  // Show the spinner

        let mailModal = document.getElementById('invite-mail');
        let newUrl;
        let table;
        try {
            const url = "/sendEmail";
            const requestOptions = {
                method: 'POST',
                body: formData
            };

            const response = await fetch(url, requestOptions);
            var vacancyIdInput = document.querySelector("input[name='vacancyId']");
            var vacancyId = vacancyIdInput.value;

            if (response.ok) {
                const errorText = await response.text();
                console.log(errorText);

                iziToast.success({message: 'Interview Invite Mail Send Successfully.'});
                let inputs = mailModal.querySelectorAll('input');
                inputs.forEach(input => {
                    input.value = '';
                });
                if (statusStage === "CONSIDERING") {
                    let select = mailModal.querySelectorAll('select');
                    select.forEach(select => {
                        select.value = '1st Interview';
                    });
                } else {
                    let select = mailModal.querySelectorAll('select');
                    select.forEach(select => {
                        select.value = '2nd Interview';
                    });
                }

                newUrl = '/candidatePage/' + vacancyId + '/' + statusStage;
                table = $('#example').DataTable();
                table.ajax.url(newUrl).load();

                // Fetch interviewHistory to save in InterviewerNotification
                await fetch('/getInterviewerNotificationDtoSession')
                    .then(response => response.json())
                    .then(data => {
                        // 'data' will contain the session value returned by the REST endpoint
                        console.log('InterviewerNotificationDto', data);
                        // You can now use 'data' as needed in your JavaScript code
                        console.log('call saveInterviewerNotification : ')
                        saveInterviewerNotification(data);
                    })
                    .catch(error => {
                        console.error('Error fetching data:', error);
                    });

                await fetch('/killInterviewerNotificationDtoSession')
                    .then(response => {
                        if (response.ok) {
                            console.log('killInterviewerNotificationDtoSession Successful !!'); // If you're returning a message from the server
                        } else {
                            throw new Error('Failed to clear session.');
                        }
                    });

            } else {
                iziToast.error({message: 'Invalid Email.Interview Invite Mail can not send!'});

                newUrl = '/candidatePage/' + vacancyId + '/' + statusStage;
                table = $('#example').DataTable();
                table.ajax.url(newUrl).load();
            }
        } catch (error) {

            let newUrl = '/candidatePage/' + vacancyId + '/' + statusStage;
            let table = $('#example').DataTable();
            table.ajax.url(newUrl).load();
            console.error("Error:", error);
            console.log("Error: Something went wrong");
        } finally {
            let mailModelInstance = bootstrap.Modal.getOrCreateInstance(mailModal);
            mailModelInstance.hide();
            submitButton.disabled = false;  // Enable the button
            spinner.classList.add("d-none");  // Hide the spinner

            newUrl = '/candidatePage/' + vacancyId + '/' + statusStage;
            table = $('#example').DataTable();
            table.ajax.url(newUrl).load();
        }
    });

// ------------------------------------------------------------------------------------------------------
    let vacancy_name = document.getElementById('vacancyNameElement').textContent;
    let editorInstanceOffer;
    let offerContentData = '';
    let candidate_salary = '300000';
    let allowance = '240000';
let vacancyDepartment;
var vacancyDepartmentInput = document.querySelector("input[name='vacancyDepartment']");
vacancyDepartment = vacancyDepartmentInput.value;

// Call CK text editor for invite mail
    ClassicEditor
        .create(document.getElementById('editor-offer'))
        .then(editorOffer => {
            editorInstanceOffer = editorOffer
            editorOffer.setData(offerContentData)
        })
        .catch(error => {
            console.error(error);
        });

    const offerDate = document.getElementById('date-offer');
    const salary = document.getElementById('candidate_salary');
    salary.addEventListener('input', updateSalary);
    offerDate.addEventListener('input', updateLocalDateOfOfferMail);

    function updateSalary() {
        candidate_salary = salary.value;
        updateEditorOfferContent();
    }

// Update input LocalDate of offer mail
    function updateLocalDateOfOfferMail() {

        const selectedDateOffer = new Date(offerDate.value);
        const year = selectedDateOffer.getFullYear();
        const month = selectedDateOffer.getMonth();
        const day = selectedDateOffer.getDate();

        localDate = ` ${day}/${month + 1}/${year} `;
        updateEditorOfferContent();

    }

    function updateEditorOfferContent() {
        allowance = candidate_salary - 60000;
        offerContentData = "<p><br>Dear " + candidate_name + ",</p><p>&nbsp; &nbsp;We are pleased to inform you that you are appointed as <b>" + vacancy_name + "</b> in <b>"+vacancyDepartment+"</b> of ACE Data Systems Ltd. Please see your entitlement information detail and brief HR rule of company in below.</p><p>Entitlement Pay Information</p><p>Joined Start Date &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;– <b>" +
            localDate + "</b></p><p>Position &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;– <b>" + vacancy_name + "</b></p><p>Basic Pay &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;– <b>" + candidate_salary + "</b> MMK</p><p>Project Allowance &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; – <b>" + allowance + "</b> MMK &nbsp; &nbsp;</p><p>Meal + Transportation Allowance &nbsp; – 3000 * (Working Days per Month) = Normally <b>60, 000</b> MMK</p><p>Note for probation period:</p><p>*After probation and if you are selected, you must work minimum 2 years contract at ACE<br>*If you wish to resign in violation of within 2 years Agreement, you must give three months’ notice and your two months’ current net salary must be refunded. &nbsp;&nbsp;<br>*No leaves are entitled in the probation period.<br>*Any leave must be informed in advance by phone.<br>*You must inform us 1 month in advance to resign.<br>*ACE will inform you 1 month in advance if we want to terminate.<br>*You must learn all technical knowledge required by project (.NET, Java or any other technologies)</p><p>HR Rule of Company (Brief)</p><p>(1) &nbsp; Working Hour &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : From 9:00 AM To 06:00 PM<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; : From Monday to Friday (Except gazette holidays)</p><p>(2) &nbsp;Leave Entitlement (After probation period)</p><p>a. &nbsp; &nbsp; Earn Leave &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; :10 days per year after one year service history.</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; (Need to apply one month in advance)</p><p><br>b. &nbsp; &nbsp; Casual Leave &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;:10 days per year after probation period&nbsp;<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; (Maximum 3 days per Month)</p><p>c. &nbsp; &nbsp; Medical Leave &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;:30 days per year (With approved medical certification)</p><p><br>(3) &nbsp; Resignation after Employment Period &nbsp; &nbsp; &nbsp; &nbsp;</p><p>a. &nbsp; &nbsp; The employee shall give not less than three months written notice to the Employer for his/her resignation.</p><p>b. &nbsp; &nbsp; In case the employee for any reason, leave the services of the Company before the agreement period then employee shall pay 3 times of the current net salary.</p><p>(4) &nbsp; Confidentiality &nbsp; &nbsp; &nbsp; &nbsp;</p><p>a. &nbsp; &nbsp; The employee agrees not to disclose any of Company’s confidential information, Company’s trademarks or knowledge pertaining to the business of the Company during or after the employment.</p><p>&nbsp;</p>";

        if (editorInstanceOffer) {
            editorInstanceOffer.setData(offerContentData);
        }
    }

//Sent Offer Email
    document.getElementById("offer-form").addEventListener("submit", async function (event) {
        event.preventDefault();

        const form = event.target;
        const formData = new FormData(form);

        const submitButton = document.getElementById("offer-button");
        const spinner = document.getElementById("offer-spinner");

        const ccEmailsArray = Array.from(addedOfferEmails);
        console.log("offer cc mails", ccEmailsArray);
        formData.append("cc-offer", (ccEmailsArray.join(",")));
        console.log("offer cc mails", ccEmailsArray.join(","));

        submitButton.disabled = true; // Disable the button
        spinner.classList.remove("d-none");  // Show the spinner

        let offerModal = document.getElementById('offer-mail');
        let newUrl;
        let table;
        try {
            const url = "/sendOfferEmail";
            const requestOptions = {
                method: 'POST',
                body: formData
            };
            var vacancyIdInput = document.querySelector("input[name='vacancyId']");

            var vacancyId = vacancyIdInput.value;

            const response = await fetch(url, requestOptions);

            if (response.ok) {
                const errorText = await response.text();
                console.log(errorText);

                iziToast.success({message: 'Offer Mail Send Successfully.'});

                newUrl = '/candidatePage/' + vacancyId + '/PASSED';
                table.ajax.url(newUrl).load();
            } else {
                iziToast.error({message: 'Invalid Email.Job Offer Mail can not send!'});

                newUrl = '/candidatePage/' + vacancyId + '/PASSED';
                table = $('#example').DataTable();
                table.ajax.url(newUrl).load();
                console.log(errorText);
            }
        } catch (error) {
            table.ajax.url(newUrl).load();

            /*   alert("Error: Something went wrong");*/
        } finally {
            let inputs = offerModal.querySelectorAll('input');
            inputs.forEach(input => {
                input.value = '';
            });
            let offerModelInstance = bootstrap.Modal.getOrCreateInstance(offerModal);
            offerModelInstance.hide();

            submitButton.disabled = false;  // Enable the button
            spinner.classList.add("d-none");// Hide the spinner

            newUrl = '/candidatePage/' + vacancyId + '/PASSED';
            table = $('#example').DataTable();
            table.ajax.url(newUrl).load();
        }

})