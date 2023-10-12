iziToast.settings({
	timeout: 2000,
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

$(document).ready(function() {

	const today = new Date();
	const sevenYearsAgo = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
	const dateInput = $("#dob");
	dateInput.attr("max", sevenYearsAgo.toISOString().slice(0, 10));

	$(document).ready(function() {
		var fileInput = $("#cvFile"),
			the_return = $(".file-return");

		var fileInput = $("#cvFile");

		fileInput.change(function(event) {

			const file = this.files[0];
			// Display the file size
			let fileSize = (file.size / (1024 * 1024)).toFixed(2);
			if (fileSize > 10) {
				iziToast.error({ icon: 'bi bi-file-earmark-minus', message: 'File Size cannot be larger than 10MB' });
				fileInput.val("");
			}

			// Display the file size
			fileSize = (file.size / (1024 * 1024)).toFixed(2);
			if (fileSize > 10) {
				console.log(' file size error');
				iziToast.error({ title: 'Error', message: 'File Size cannot be larger than 10MB' });
				the_return.html("");
				fileInput.val("");
			}
		});
	});

	var nameInput = $("#position");
	var dobInput = $("#dob");
	var emailInput = $("#email");
	var genderInput = $('input[name="gender"]');
	var phoneInput = $("#phone");
	var bachelorInput = $("#education");
	var technicalSkillInput = $("#skill");
	var languageSkillInput = $("#language");
	var levelInput = $("#level");
	var specialistSkillInput = $("#mainTechnical");
	var experienceInput = $("#experiences");
	var salaryInput = $("#salary");
	var cvInput = $("#cvFile");
	var emailError = false;
	var nameError = false;
	var phoneError = false;
	var regex = /^\s*$/;

	nameInput.change(function() {
		if (nameError === false) {
			nameInput.removeClass("border border-danger");
		}
	});

	dobInput.change(function() {
		dobInput.removeClass("border border-danger");
	});

	emailInput.change(function() {
		if (emailError === false) {
			emailInput.removeClass("border border-danger");
		}
	});

	genderInput.change(function() {
		genderInput.removeClass("border border-danger");
	});

	phoneInput.change(function() {
		if (phoneError === false) {
			phoneInput.removeClass("border border-danger");
		}
	});

	bachelorInput.change(function() {
		bachelorInput.removeClass("border border-danger");
	});

	technicalSkillInput.change(function() {
		technicalSkillInput.removeClass("border border-danger");
	});

	languageSkillInput.change(function() {
		languageSkillInput.removeClass("border border-danger");
	});

	levelInput.change(function() {
		levelInput.removeClass("border border-danger");
	});

	specialistSkillInput.change(function() {
		specialistSkillInput.removeClass("border border-danger");
	});

	experienceInput.change(function() {
		experienceInput.removeClass("border border-danger");
	});

	salaryInput.change(function() {
		salaryInput.removeClass("border border-danger");
	});

	cvInput.change(function() {
		cvInput.removeClass("border border-danger");
	});

	var inputFields = [
		nameInput,
		dobInput,
		emailInput,
		genderInput,
		phoneInput,
		bachelorInput,
		technicalSkillInput,
		languageSkillInput,
		levelInput,
		specialistSkillInput,
		experienceInput,
		salaryInput,
		cvInput
	];
	//validate null when submit form
	$("[type=submit]").click(function(event) {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				}
				)
			}
		});
		if (
			regex.test(nameInput.val()) ||
			regex.test(dobInput.val()) ||
			regex.test(emailInput.val()) ||
			genderInput.filter(":checked").length === 0 ||
			regex.test(phoneInput.val()) ||
			regex.test(bachelorInput.val()) ||
			regex.test(technicalSkillInput.val()) ||
			regex.test(languageSkillInput.val()) ||
			levelInput.val() === null ||
			regex.test(specialistSkillInput.val()) ||
			regex.test(experienceInput.val()) ||
			regex.test(salaryInput.val()) ||
			cvInput.val() === ""
		) {


			for (var i = 0; i < inputFields.length; i++) {
				var field = inputFields[i];

				if (field.val() === "") {
					field.addClass("border border-danger");
				} else {
					field.removeClass("border border-danger");
				}
				if (levelInput.val() === null) {
					levelInput.addClass("border border-danger");
				}
			}

			iziToast.error({ title: 'Error', message: 'Complete All Required Fields' });

			event.preventDefault();
			return false;
		}
		if (nameError === true || phoneError === true || emailError === true) {
			if (nameError === true) {
				iziToast.warning({ title: 'Error', message: 'Name can\'t contain special character and Number' });
				nameInput.addClass("border border-danger");
			}
			if (phoneError === true) {
				iziToast.warning({ title: 'Error', message: 'Enter vaild Phone Number' });
				phoneInput.addClass("border border-danger");
			}
			if (emailError === true) {
				emailInput.addClass("border border-danger");
				iziToast.warning({ title: 'Error', message: 'Enter vaild Email' });
			}
			event.preventDefault();
			return false;
		}
		if (experienceInput.val() < 0) {
			iziToast.error({ title: 'Error', message: 'Invaild Experience(Years) Input' });
			event.preventDefault();
			return false;
		}

	});
	//click reset to clean error border
	var resetButton = $('button[type="reset"]');
	resetButton.on("click", function() {


		for (var i = 0; i < inputFields.length; i++) {
			var field = inputFields[i];
			field.removeClass("border border-danger");

		}
	});
	//validate input name field
	nameInput.keyup(function() {
		
		nameVal = nameInput.val();
		for (let i = 0; i < nameVal.length; i++) {
			let temp = nameVal.charCodeAt(i);

			if (!(temp > 64 && temp < 91 || temp > 96 && temp < 123 || temp === 32)) {
				nameError = true;
			}
			else {
				nameError = false;
			}
		}
		if (nameError === true && nameVal !== '') {
			 
			nameInput.addClass("border border-danger");
		}else{
			nameInput.removeClass("border border-danger");
		}
	});
	
	nameInput.mouseout(function(){
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});

		if (nameError === true && nameVal !== '') {
			iziToast.warning({ title: 'Error', message: 'Name can\'t contain special character and Number' });
			} 
	});
	//validate input email field
	emailInput.keyup(function() {
		
		if (!(emailInput.val() === '' || emailInput.val().endsWith(".com") || emailInput.val().endsWith(".org")
			|| emailInput.val().endsWith(".cc") || emailInput.val().endsWith(".net"))) {

			emailError = true;
			emailInput.addClass("border border-danger");
			

		} else {
			emailError = false;
			emailInput.removeClass("border border-danger");
		}

	});
	emailInput.mouseout(function(){
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});

		if(emailError === true){
			iziToast.warning({ title: 'Error', message: 'Enter vaild Email' });
		}
	});
	//validate input phone field
	phoneInput.keyup(function() {
		let textError = false,fomatError  = false;
		
		phone = phoneInput.val();
		for (let i = 0; i < phone.length; i++) {
			let temp = phone.charCodeAt(i);

			if (!(temp > 47 && temp < 59)) {
				textError = true;
				phoneInput.addClass("border border-danger");
				break;
			}
			else {
				textError = false;
				phoneInput.removeClass("border border-danger");
			}
		}
		
		if (!(phoneInput.val().startsWith("09") && phoneInput.val().length > 7 && phoneInput.val().length < 12)) {
			fomatError = true;
			phoneInput.addClass("border border-danger");
		} else {
			fomatError = false;
			phoneInput.removeClass("border border-danger");
		}
		
		if(textError === false && fomatError === false){
			phoneError = false;
			phoneInput.addClass("border border-danger");
		}else{
			phoneError = true;
			phoneInput.removeClass("border border-danger");
		}
        
	});
	phoneInput.mouseout(function(){
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		if(phoneError === true){
			iziToast.warning({ title: 'Error', message: 'Enter vaild Phone Number' });
		}else {
			phoneInput.removeClass("border border-danger");
		}
	});

	var myValue = $('#myDiv').text();
	if (myValue === "duplicate") {
		iziToast.warning({ icon: 'fa-solid fa-check', message: 'This email already send Cv to this Vacancy!' });

		$.ajax({
			url: '/jobs/toKillSuccessMessage',
			method: 'GET',
			success: function(data) {
				console.log('Session attribute cleared successfully.');
			},
			error: function(xhr, status, error) {
				console.error('Failed to clear session attribute:', error);
			}
		});
	}
});