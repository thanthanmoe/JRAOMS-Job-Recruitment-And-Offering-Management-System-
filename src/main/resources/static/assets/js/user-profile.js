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

	let email = $('#email');
	let otpBtn = $('#getOtp');
	let otpCode = $('#otpCode');
	let mailMessage = $('#updateMailMessage');
	let second = 30;
	let emailError = false;
	let sameEmail = false;
	let oldPassword = $('#currentPassword');
	let newPassword = $('#newPassword');
	let renewPassword = $('#renewPassword');
	let newPasswordError = false;
	let renewPasswordError = false;
	let emptyPwForm = false;

	function killUpdateMailMessage() {
		$.ajax({
			url: '/killUpdateMailMessage',
			method: 'GET',
			success: function(data) {
				console.log('Session attribute cleared successfully.');
			},
			error: function(xhr, status, error) {
				console.error('Failed to clear session attribute:', error);
			}
		});
	};

	if (mailMessage.html() === 'error') {
		iziToast.error({ timeout: 3000, title: 'error', message: 'Invaild Verification Code or Email!' });

		$('#overview').removeClass('active');
		$('#pf-edit').addClass('active');
		$('#profile-edit').addClass('show active');
		$('#profile-overview').removeClass('show active');

		killUpdateMailMessage();
	}

	if (mailMessage.html() === 'success') {
		iziToast.success({ timeout: 3000, title: 'success', message: 'Email Successfully Changed!' });
		killUpdateMailMessage();
	}
	if (mailMessage.html() === 'oldVsNewMatch' || mailMessage.html() === 'wrongCurrentPw') {
		if (mailMessage.html() === 'oldVsNewMatch') {
			iziToast.error({ timeout: 3000, title: 'error', message: 'Please choose a new password different from the current one' });
		}
		if (mailMessage.html() === 'wrongCurrentPw') {
			iziToast.error({ timeout: 3000, title: 'error', message: 'Wrong Current Password!' });
		}

		$('#overview').removeClass('active');
		$('#change-pw').addClass('active');
		$('#profile-change-password').addClass('show active');
		$('#profile-overview').removeClass('show active');
		killUpdateMailMessage();
	}
	if (mailMessage.html() === 'pwSuccess') {
		iziToast.success({ timeout: 3000, title: 'Success', message: 'Password Successfully Changed!' });
		killUpdateMailMessage();
	}
	if (mailMessage.html() === 'successAvatar') {
		iziToast.success({ timeout: 3000, title: 'Success', message: 'Avatar Successfully Changed!' });
		killUpdateMailMessage();
	}

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
			|| email.val().endsWith(".cc") || email.val().endsWith(".net"))) {

			emailError = true;
			iziToast.warning({ title: 'Error', message: 'Enter vaild Email' });
			email.addClass("border border-danger");
		} else {
			emailError = false;
			email.removeClass("border border-danger");
		}

	});
	//to request otp code
	otpBtn.click(function(event) {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			},
		});

		if (emailError === false) {
			$.ajax({
				url: '/sendOtpCode/' + email.val(),
				method: 'GET',
				dataType: 'json',
				success: function(response) {
					console.log(response.status);
					if (response.status === 'okey') {
						sameEmail = false;
						iziToast.info({ timeout: 3000, title: 'Success', message: 'Verification code Successfully Send!' });
					} else if (response.status === 'exists') {
						sameEmail = true;
						otpBtn.prop('disabled', false);
						otpBtn.html("Get OTP Code");
						second = 30;
						clearInterval(intervalId);
						iziToast.error({ timeout: 3000, title: 'Error', message: 'Email Already Exists.Try with another One' });
					} else {
						sameEmail = false;
						otpBtn.prop('disabled', false);
						otpBtn.html("Resend OTP Code");
						second = 30;
						clearInterval(intervalId);
						iziToast.error({ timeout: 3000, title: 'Error', message: 'Something went wrong' });
					}
				},
				error: function(error) {
					console.log('Email send failed' + error)
					iziToast.error({ timeout: 3000, title: 'error', message: 'Something went wrong!' });
				}
			});

			otpBtn.prop('disabled', true);
			function updateTimer() {
				if (second >= 0) {
					otpBtn.html('Dont receive OTP? Try again in ' + second + 's');
				} else {
					otpBtn.prop('disabled', false);
					otpBtn.html("Resend OTP Code");
					second = 30;
					clearInterval(intervalId);
				}
				second--;

			};

			const intervalId = setInterval(updateTimer, 1000);
		}
		event.preventDefault();
		return false;
	});

	//validate otp when submit
	$('#saveEmail').click(function(event) {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			},
		});

		if (sameEmail === true) {
			iziToast.error({ title: 'Error', message: 'Email is same as old one' });
			event.preventDefault();
			return false;
		}
		if (emailError === true || otpCode.val() === "") {
			iziToast.error({ title: 'Error', message: 'Complete All Required Fields' });
			otpCode.addClass("border border-danger")
			event.preventDefault();
			return false;
		}
		else {
			otpCode.removeClass("border border-danger");
			sameEmail = false;
		}

	});

	//validate input newPassword field
	newPassword.mouseout(function() {
		if (emptyPwForm === false) {
			iziToast.settings({
				function(instance, toast) {
					instance.hide({
						transitionOut: 'fadeOutUp'
					})
				}
			});
		}
		if (!(newPassword.val() === '' || newPassword.val().length >= 6)) {
			emptyPwForm = false;
			newPasswordError = true;
			iziToast.warning({ title: 'Error', message: 'Enter vaild Password (lenght > 5)' });
			newPassword.addClass("border border-danger");
		} else {
			newPasswordError = false;
			newPassword.removeClass("border border-danger");
		}

	});

	//validate input renewPassword field
	renewPassword.mouseout(function() {
		if (emptyPwForm === false) {
			iziToast.settings({
				function(instance, toast) {
					instance.hide({
						transitionOut: 'fadeOutUp'
					})
				}
			});
		}
		if (!(renewPassword.val() === '' || newPassword.val() === renewPassword.val())) {
			emptyPwForm = false;
			renewPasswordError = true;
			iziToast.warning({ title: 'Error', message: 'New Password and Re-enter new Password does not match!' });
			renewPassword.addClass("border border-danger");
		} else {
			renewPasswordError = false;
			renewPassword.removeClass("border border-danger");
		}

	});

	oldPassword.mouseout(function() { oldPassword.removeClass("border border-danger"); });
	//validate changePassword form
	$('#changePwBtn').click(function(event) {
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			},
		});
		if (oldPassword.val() === "" || newPassword.val() === "" || renewPassword.val() === "") {
			if (oldPassword.val() === "") {
				oldPassword.addClass("border border-danger")
			}
			if (newPassword.val() === "") {
				newPassword.addClass("border border-danger")
			}
			if (renewPassword.val() === "") {
				renewPassword.addClass("border border-danger")
			}
			emptyPwForm = true;
			iziToast.error({ title: 'Error', message: 'Complete All Required Fields!' });
			event.preventDefault();
			return false;
		}
		emptyPwForm = false;
		if (newPasswordError === true) {
			iziToast.warning({ title: 'Error', message: 'Enter vaild Password (lenght > 5)' });
			newPassword.addClass("border border-danger");
			event.preventDefault();
			return false;
		} else {
			newPassword.removeClass("border border-danger");
		}
		if (renewPasswordError === true) {
			iziToast.warning({ title: 'Error', message: 'New Password and Re-enter new Password does not match!' });
			renewPassword.addClass("border border-danger");
			event.preventDefault();
			return false;
		} else {
			renewPassword.removeClass("border border-danger");
		}
	});

});