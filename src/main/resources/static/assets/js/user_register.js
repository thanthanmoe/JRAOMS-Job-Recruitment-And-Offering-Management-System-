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

    function clearSession(){
		$.ajax({
			url: '/auth/clearSessionMessage',
			method: 'GET',
			dataType: 'json',
			success: function(response) {
				if (response.status === 'okey') {
					console.log("success");
				}
			},
			error: function(error) {
				console.log("Fail to clear session" + error);
			}
		});
	}
	if ($('#message').html() == 'registerSuccess') {
		iziToast.success({ icon: 'bi bi-person-check', message: 'User Registration Successful' });
		clearSession();
	}
	if ($('#message').html() == 'registerExists') {
		iziToast.warning({ icon: 'bi bi-person-dash', message: 'User Already Exists in System' });
		clearSession();
	}
	if ($('#message').html() == 'registerError') {
		iziToast.error({ icon: 'bi bi-emoji-frown', message: 'Something went wrong!' });
		clearSession();
	}
	
	let name = $('#name'),email = $('#email'),department = $('#department'),role = $('#role'),depError = true,nameError = false,emailError = false;
	
	department.change(function(){
		depError = false;
		department.removeClass("border border-danger");
	});
	
	//validate input name field
	name.keyup(function() {
		
		let nameVal = name.val();
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
			name.addClass("border border-danger");
		}else{
			name.removeClass("border border-danger");
		}
	});
	name.mouseout(function(){
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		if(nameError === true){
			iziToast.warning({ title: 'Error', message: 'Name can\'t contain special character and Number' });
		}
      
	});
	//validate input email field
	email.keyup(function() {
		
		if (!(email.val() === '' || email.val().endsWith(".com") || email.val().endsWith(".org")
			|| email.val().endsWith(".cc") || email.val().endsWith(".net"))) {

			emailError = true;
			email.addClass("border border-danger");
			

		} else {
			emailError = false;
			email.removeClass("border border-danger");
		}

	});
	email.mouseout(function(){
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
	
	$('[type=submit]').click(function(event){
		var regex = /^\s*$/;
		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				})
			}
		});
		
		if(regex.test(name.val()) || regex.test(email.val()) || depError === true || regex.test(role.val())){
			iziToast.error({ icon: 'bi bi-exclamation-triangle', message: 'Complete all required fields!' });
			if(regex.test(name.val())){
				name.addClass("border border-danger");
			}else{
				name.removeClass("border border-danger");
			}
			if(regex.test(email.val())){
				email.addClass("border border-danger");
			}else{
				email.removeClass("border border-danger");
			}
			if(depError === true){
				department.addClass("border border-danger");
			}else{
				department.removeClass("border border-danger");
			}
			 
			event.preventDefault();
			return false;
		}
		if (nameError === true || emailError === true) {
			if (nameError === true) {
				iziToast.warning({ title: 'Error', message: 'Name can\'t contain special character and Number' });
				name.addClass("border border-danger");
			}
			if (emailError === true) {
				email.addClass("border border-danger");
				iziToast.warning({ title: 'Error', message: 'Enter vaild Email' });
			}
			event.preventDefault();
			return false;
		}
	});
});