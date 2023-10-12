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
	
	if ($('#message').html() == 'userUpdateSuccess') {
		iziToast.success({ icon: 'bi bi-person-check', message: 'User\' info Successfully Updated' });
		clearSession();
	}
	 
	if ($('#message').html() == 'userUpdateError') {
		iziToast.error({ icon: 'bi bi-emoji-frown', message: 'Something went wrong!' });
		clearSession();
	}
	
	let status = $('#status');
	let activeBtn = $(':checkbox');
	if (activeBtn.is(":checked")){
			activeBtn.val(1);
			status.html('Active');
		}else {
			activeBtn.val(0);
			status.html('InActive');
		}
	
	activeBtn.click(function(){
		if (activeBtn.is(":checked")){
			activeBtn.val(1);
			status.html('Active');
		}else {
			activeBtn.val(0);
			status.html('InActive');
		}
        console.log(activeBtn.val())
	});
	let name = $('#name');
	let nameError = false;
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

	$("[type=submit]").click(function(event) {

		if (name.val() == '') {
			$('#nameError').html("Invaild User Name");
			setTimeout(function(){ $('#nameError').html(""); },3000);
			event.preventDefault();
			return false;
		}
		if(nameError === true){
			iziToast.warning({ title: 'Error', message: 'Name can\'t contain special character and Number' });
			event.preventDefault();
			return false;
		}

	});

});