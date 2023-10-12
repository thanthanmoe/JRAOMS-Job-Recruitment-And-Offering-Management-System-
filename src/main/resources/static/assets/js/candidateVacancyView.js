/**
 * 
 */

iziToast.settings({
	timeout: 3000,
	resetOnHover: true,
	transitionIn: 'flipInX',
	transitionOut: 'flipOutX',
	position: 'topCenter',
	onOpen: function() {
		console.log('callback abriu!');
	},
	onClose: function() {
		console.log("callback fechou!");
	}
});

$(document).ready(async function () {

	$('#search').click(function (event) {
		event.preventDefault();
	});
	let candidateEmail = $('#candidate-email').text().trim();
	let candidateName = $('#candidate-name').text().trim();
	let vacancyName = $('#vacancy-name').text().trim();
	let companyName = $('#company-name').text().trim();
	let vacancyId = $('#vacancy-id').text().trim();

	var myValue = $('#myDiv').text().trim();
	if (myValue === 'cvSuccess') {
		iziToast.info({
			timeout: 3000,
			icon: 'fa-solid fa-check',
			title: 'Success',
			message: 'Your Cv Successfully Submitted'
		});

		$.ajax({
			url: '/jobs/toKillSuccessMessage',
			method: 'GET',
			success: function (data) {
				console.log('Session attribute cleared successfully.');
			},
			error: function (xhr, status, error) {
				console.error('Failed to clear session attribute:', error);
			}
		});

		console.log("vacancyId : " + vacancyId);
		console.log("candidateEmail : " + candidateEmail);
		console.log("candidateName : " + candidateName);
		console.log("vacancyName : " + vacancyName);
		console.log("companyName : " + companyName);

		// Call saveNotification function fo web-socket-notification.js
		await saveNotification(vacancyId);

		//Call sendWelcomeMail function
		sendWelcomeMail(candidateEmail, candidateName, vacancyName, companyName)
			.then(result => {
				console.log(result);
			})
			.catch(error => {
				console.error('Failed to send email:', error);
			});
	}

	let gridBtn = $('#gridView');
	let listBtn = $('#listView');
	let jobPost = $('.jobPost');

	gridBtn.click(function () {
		gridBtn.removeClass("btn-outline-secondary");
		gridBtn.addClass("btn-secondary");
		listBtn.removeClass("btn-secondary");
		listBtn.addClass("btn-outline-secondary");

		jobPost.removeClass("col-md-10");
		jobPost.addClass("col-md-5");

	});

	listBtn.click(function () {
		listBtn.removeClass("btn-outline-secondary");
		listBtn.addClass("btn-secondary");
		gridBtn.removeClass("btn-secondary");
		gridBtn.addClass("btn-outline-secondary");

		jobPost.removeClass("col-md-5");
		jobPost.addClass("col-md-10");
	});

	$('.companyName').click(function () {
		let name = $(this).html();
		let posts = $('.jobPost');
		let companyNames = $('.jobCompanyName');
		let companies = $('.companyName');
		$('#contactUsBtn').addClass("collapsed");
		$("#allVacancy").addClass("collapsed");
		$('#contactUsBtn').find("i").removeClass("text-primary");
		$('#contactUsBtn').find("span").removeClass("text-primary");
		contactUsDiv.css("display", "none");

		$("#emptyVacancy").addClass("d-none");
		for (let i = 0; i < companies.length; i++) {
			$(companies[i]).removeClass("active");
		}
		$(this).addClass("active");

		for (let i = 0; i < posts.length; i++) {
			if (name.includes($(companyNames[i]).html())) {
				$(posts[i]).removeClass("d-none");
				$(posts[i]).addClass("d-inline");
				$(posts[i]).addClass("inlinePost");
			} else {
				$(posts[i]).addClass("d-none");
				$(posts[i]).removeClass("d-inline");
				$(posts[i]).removeClass("inlinePost")
			}
		}
		let searchJob = $(".inlinePost");
		if (searchJob.length === 0) {
			$("#empty").removeClass("d-none");
		} else {
			$("#empty").addClass("d-none");
		}

	});

	$('[name=query]').keyup(function () {
		$("#emptyVacancy").addClass("d-none");
		let posts = $('.jobPost');
		let companyNames = $('.jobName');
		let input = $(':text').val();
		$("#allVacancy").removeClass("collapsed");
		$('#contactUsBtn').addClass("collapsed");
		contactUsDiv.css("display", "none");
		for (let i = 0; i < posts.length; i++) {
			if ($(companyNames[i]).html().toLowerCase().includes(input.toLowerCase())) {
				$(posts[i]).removeClass("d-none");
				$(posts[i]).addClass("d-inline");
				$(posts[i]).addClass("inlinePost");
			} else {
				$(posts[i]).addClass("d-none");
				$(posts[i]).removeClass("d-inline");
				$(posts[i]).removeClass("inlinePost");
			}
		}
		let searchJob = $(".inlinePost");
		if (searchJob.length === 0) {
			$("#empty").removeClass("d-none");
		} else {
			$("#empty").addClass("d-none");
		}
	});

	let contactUsDiv = $('.contact-container');
	contactUsDiv.css("display", "none");
	$('#contactUsBtn').click(function (event) {
		$("#emptyVacancy").addClass("d-none");
		$("#empty").addClass("d-none");
		let companies = $('.companyName');
		$("#allVacancy").addClass("collapsed");
		event.preventDefault();
		$('#contactUsBtn').removeClass("collapsed");
		$("#jobs-nav").addClass("collapse");
		$("a[data-bs-target='#jobs-nav']").addClass("collapsed");
		$("#jobs-nav").removeClass("show");
		for (let i = 0; i < companies.length; i++) {
			$(companies[i]).removeClass("active");
		}

		contactUsDiv.css("display", "block");
		$('.jobPost').addClass("d-none");
		$('.jobPost').removeClass("d-inline");
	});
});

async function sendWelcomeMail(candidateEmail, candidateName, vacancyName, companyName) {
	return new Promise(async (resolve, reject) => {
		const welcomeMailDto = {
			candidateEmail: candidateEmail,
			candidateName: candidateName,
			vacancyName: vacancyName,
			companyName: companyName
		};

		const url = '/jobs/sendWelcomeMail';
		const requestOptions = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(welcomeMailDto)
		};

		try {
			const response = await fetch(url, requestOptions);

			if (!response.ok) {
				const errorMessage = await response.text();
				console.log(errorMessage || 'Network response was not ok');
				reject(errorMessage);
			} else {
				console.log('send welcome email');
				// Display success message to the user if needed
				resolve('Email sent successfully');
			}
		} catch (error) {
			console.error('Error sending email:', error);
			// Display error message to the user
			reject(error);
		}
	});
}


document.addEventListener("DOMContentLoaded", function() {
	const form = document.getElementById("candidate-contact-form");
	const submitButton = document.getElementById("submit-button");
	const spinner = document.getElementById("spinner");

	let candidateName = $('[name=candidateName]');
	let email = $('#email');
	let message = $('#message');
	form.addEventListener("submit", async function(event) {
		event.preventDefault();

		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		if (candidateName.val() === '' || email.val() === '' || message.val() === '') {
			iziToast.error({ message: 'Please Complete All Required Fields' });
		} else {
			submitButton.disabled = true;  // Disable the button
			spinner.classList.remove("d-none");  // Show the spinner

			const formData = new FormData(event.target);
			const url = '/jobs/contact';
			const requestOptions = {
				method: 'POST',
				body: formData
			};

			try {
				const response = await fetch(url, requestOptions);

				if (!response.ok) {
					const errorMessage = await response.text();
					console.log(errorMessage || 'Network response was not ok');
				} else {
					console.log('send contact email');
					iziToast.success({ icon: 'bi bi-shield-lock', title: 'Success', message: 'Send Mail Success' });
					candidateName.val("");
					email.val("");
					message.val("");
				}
			} catch (error) {
				console.error('Error sending email:', error);
			} finally {
				submitButton.disabled = false;  // Enable the button
				spinner.classList.add("d-none");  // Hide the spinner
			}
		}
	});
});
