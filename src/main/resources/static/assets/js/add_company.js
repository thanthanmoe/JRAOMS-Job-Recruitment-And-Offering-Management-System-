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
$(document).ready(function() {

    function clearMessage() {
		$.ajax({
            url: '/admin/killCompanyMessage',  
            method: 'GET',  
            success: function(data) {
                console.log('Session attribute cleared successfully.');
            },
            error: function(xhr, status, error) {
                console.error('Failed to clear session attribute:', error);
            }
        });
	}
	if ($('#companyMessage').html() === 'success') {
		iziToast.success({ timeout: 3000, icon: 'fa-solid fa-check', title: 'success', message: 'Company Successfully Added!' });
		clearMessage();
	}
	if ($('#companyMessage').html() === 'existsCompany') {
		iziToast.error({ timeout: 3000, icon: 'bi bi-exclamation', message: 'Company Already Exists!' });
		clearMessage();
	}
	
	let name = $('#name');
	let email = $('#email');
	let phone = $('#phone');
	let location = $('#location');
	let about = $('#about');
	let websiteLink = $('#websiteLink');
	let department = $('#department');
	var phoneError = false;
	var emailError = false;

	var inputFields = [name, email, phone, location, about, websiteLink, department];
	//validate null when click submit
	$('button[type |="submit"').click(function(event) {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		if (name.val() === "" || email.val() === "" || phone.val() === "" || about.val() === ""
			|| location.val() === "" || websiteLink.val() === "" || department.val() === "") {

			for (let i = 0; i < inputFields.length; i++) {
				if (inputFields[i].val() === "") {
					inputFields[i].addClass("border border-danger");
					if (inputFields[i] === about) {
						$('#companyAbout').addClass("border border-danger");
					} else {
						$('#companyAbout').removeClass("border border-danger");
					}

				} else {
					inputFields[i].removeClass("border border-danger");
				}
			}
			iziToast.error({ title: 'Error', message: 'Complete All Required Fields.' });
			event.preventDefault();
			return false;
		}
		if (phoneError === true || emailError === true) {
			if (phoneError === true) {
				iziToast.warning({ title: 'Error', message: 'Enter vaild Phone Number' });
				phone.addClass("border border-danger");
			}
			if (emailError === true) {
				iziToast.warning({ title: 'Error', message: 'Enter vaild Email' });
				email.addClass("border border-danger");
			}
			event.preventDefault();
			return false;
		}

	});
	//validate input email field
	email.mouseout(function() {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});

		if (!(email.val() === '' || email.val().endsWith(".com") || email.val().endsWith(".org")
			|| email.val().endsWith(".cc") || email.val().endsWith(".net") || email.val().endsWith(".mm"))) {

			emailError = true;
			iziToast.warning({ title: 'Error', message: 'Enter vaild Email' });
			email.addClass("border border-danger");
		} else {
			emailError = false;
		}

	});
	//validate input phone field
	phone.mouseout(function() {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		if (!(phone.val().startsWith("09") && phone.val().length > 7 && phone.val().length < 12 || phone.val() === "")) {
			phoneError = true;
			iziToast.warning({ title: 'Error', message: 'Enter vaild Phone Number' });
			phone.addClass("border border-danger");
		} else {
			phoneError = false;
		}

	});
	//reset 
	$('button[type |="reset"').click(function() {
		for (let i = 0; i < inputFields.length; i++) {

			inputFields[i].removeClass("border border-danger");
			if (inputFields[i] === about) {
				$('#companyAbout').removeClass("border border-danger");
			}
			if (inputFields[i] === department) {
				$('#companyDepartment').removeClass("border border-danger");
			}
		}
	});

});