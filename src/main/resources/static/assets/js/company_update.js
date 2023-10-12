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
			url: '/system/killCompanyMessage',
			method: 'GET',
			success: function(data) {
				if (response.status === 'okey') {
					console.log("success");
				}
			},
			error: function(xhr, status, error) {
				console.error('Failed to clear session attribute:', error);
			}
		});
	}
	if ($('#companyMessage').html() === 'duplicate') {
		iziToast.error({ timeout: 3000, icon: 'bi bi-exclamation-triangle', message: 'Duplicate Department Name!' });
		clearMessage();
	}
	if ($('#companyMessage').html() === 'nullError') {
		iziToast.error({ timeout: 3000, icon: 'bi bi-exclamation-triangle', message: 'Department Name cannot be null!' });
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
	var delCount = 0;
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

			var regex = /^\s*$/;
		if (regex.test(name.val()) || regex.test(email.val()) || regex.test(phone.val()) || regex.test(about.val())
			|| regex.test(location.val()) || regex.test(websiteLink.val())) {

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

	$('.deleteBtn').click(function(event) {
		event.preventDefault();

	});
	$('.editBtn').click(function(event) {
		event.preventDefault();

	});
	$('.saveEditBtn').click(function() {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		var departmentId = $(this).closest('.modal').find('input[type="hidden"]').val();
		var departmentName = $(this).closest('.modal').find('input[type="text"]').val();
		var regex = /^\s*$/;
		if (regex.test(departmentName)) {
			iziToast.warning({ title: 'Error', message: 'Department name cannot be empty!' });
		} else {
			$.ajax({
				url: '/updatedepartment/' + departmentId + '/' + departmentName,
				method: 'GET',
				dataType: 'json',
				success: function(data) {
                    console.log(data + "_"+ data.status);
                    if(data.status === "okey"){
					iziToast.success({ timeout: 3000, icon: 'bi bi-check', message: 'Department name successfully changed!' });
					var selector = '#department' + departmentId;
					$(selector).val(departmentName);
					}
                    if(data.status === "error"){
						iziToast.warning({ message: 'Duplicate department name!' });
					}
				},
				error: function(xhr, status, error) {
					console.error('Failed to clear session attribute:', error);
				}
			});
			$(this).closest('.modal').modal('hide');
		}
	});
	
	$('.deleteBtn').click(function() {
		 
		var departmentId = $(this).closest('.modal').find('input[type="hidden"]').val();
		 
			$.ajax({
				url: '/deletedepartment/' + departmentId,
				method: 'GET',
				dataType: 'json',
				success: function(data) {
                    console.log(data + "_"+ data.status);
                    if(data.status === "okey"){
					iziToast.success({ timeout: 3000, icon: 'bi bi-check', message: 'Department  successfully deleted!' });
					 var selector = '#div' + departmentId;
					$(selector).addClass("d-none");
					}
					if(data.status === "error"){
						iziToast.warning({ message: 'Can\'t delete department that have user!' });
					}
					 
				},
				error: function(xhr, status, error) {
					console.error('Failed to clear session attribute:', error);
				}
		});
		$(this).closest('.modal').modal('hide');
	});
});