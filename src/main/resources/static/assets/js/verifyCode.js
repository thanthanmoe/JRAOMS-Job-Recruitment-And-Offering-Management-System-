/**
 * 
 */
const form = document.querySelector('form')
const inputs = form.querySelectorAll('input')
const KEYBOARDS = {
	backspace: 8,
	arrowLeft: 37,
	arrowRight: 39,
}

function handleInput(e) {
	const input = e.target
	const nextInput = input.nextElementSibling
	if (nextInput && input.value) {
		nextInput.focus()
		if (nextInput.value) {
			nextInput.select()
		}
	}
}

function handlePaste(e) {
	e.preventDefault()
	const paste = e.clipboardData.getData('text')
	inputs.forEach((input, i) => {
		input.value = paste[i] || ''
	})
}

function handleBackspace(e) {
	const input = e.target
	if (input.value) {
		input.value = ''
		return
	}

	input.previousElementSibling.focus()
}

function handleArrowLeft(e) {
	const previousInput = e.target.previousElementSibling
	if (!previousInput) return
	previousInput.focus()
}

function handleArrowRight(e) {
	const nextInput = e.target.nextElementSibling
	if (!nextInput) return
	nextInput.focus()
}

form.addEventListener('input', handleInput)
inputs[0].addEventListener('paste', handlePaste)

inputs.forEach(input => {
	input.addEventListener('focus', e => {
		setTimeout(() => {
			e.target.select()
		}, 0)
	})

	input.addEventListener('keydown', e => {
		switch (e.keyCode) {
			case KEYBOARDS.backspace:
				handleBackspace(e)
				break
			case KEYBOARDS.arrowLeft:
				handleArrowLeft(e)
				break
			case KEYBOARDS.arrowRight:
				handleArrowRight(e)
				break
			default:
		}
	})
})

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
	if ($('#message').html() == 'wrongCode') {

		iziToast.error({ title: 'Error', message: 'Invaild Varification Code' });
		$.ajax({
			url: '/auth/clearSessionMessage',
			method: 'GET',
			dataType: 'json',
			success: function(response) {
				console.log("success");
			},
			error: function(error) {
				console.log("Fail to clear session" + error);
			}
		});
	}
	let second = 44;
	function updateTimer() {
		if (second > 0) {
			$('#resend').html('Don\'t receive the email? Try again in ' + second + 's');
			second--;
		} else {

			$('#resend').html('Resend verification Code');
			$('#resend').addClass("text-decoration-underline text-primary");
			clearInterval(intervalId);
		}

	};

	const intervalId = setInterval(updateTimer, 1000);

	let one = $('#one');
	let two = $('#two');
	let three = $('#three');
	let four = $('#four');
	let five = $('#five');
	let six = $('#six');
	let verifyCode = $('#verifyCode');

	const arr = [one, two, three, four, five, six];
	$('#continue').click(function(event) {
		if (one.val() == '' || two.val() == '' || three.val() == '' || four.val() == '' || five.val() == '' || six.val() == '') {
			for (let i = 0; i < arr.length; i++) {
				arr[i].addClass("border border-danger");
			}

			setTimeout(function() {
				for (let i = 0; i < arr.length; i++) {
					arr[i].removeClass("border border-danger");
				}
			}, 3000);

			event.preventDefault();
			return false;
		}
		verifyCode.val(one.val() + '' + two.val() + '' + three.val() + '' + four.val() + '' + five.val() + '' + six.val());
	});

	$('#resend').click(function() {
		if (second == 0) {
			if (second == 0) {
				$.ajax({
					url: '/auth/resendVerificationCode/' + $('#email').val(),
					method: 'GET',
					dataType: 'json',
					success: function(response) {
						console.log(response.status);
						if (response.status === 'okey') {
							sameEmail = false;
							iziToast.info({ timeout: 3000, title: 'Success', message: 'Verification code Successfully Send!' });
						}
					},
					error: function(error) {
						console.log('Email send failed' + error)
						iziToast.error({ timeout: 3000, title: 'error', message: 'Something went wrong!' });
					}
				});
			}
			$('#resend').removeClass("text-decoration-underline text-primary");
			second = 45;
			updateTimer();
			const intervalId = setInterval(updateTimer, 1000);

		}
	});
});