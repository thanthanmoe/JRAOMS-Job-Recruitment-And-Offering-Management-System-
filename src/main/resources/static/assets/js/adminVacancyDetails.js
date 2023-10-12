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
	var myValue = $('#myDiv').text();
    if(myValue === 'success'){
		
		iziToast.success({ timeout: 3000, icon: 'fa-solid fa-check', title: 'Success', message: 'Vacancy Sucessfully Edited!' });
		
		$.ajax({
            url: '/killVacancyEditSession',
            method: 'GET',  
            success: function(data) {
                console.log('Session attribute cleared successfully.');
            },
            error: function(xhr, status, error) {
                console.error('Failed to clear session attribute:', error);
            }
        });
	}
	
	    if(myValue === 'closed'){
		
		iziToast.success({ timeout: 3000, icon: 'fa-solid fa-check', title: 'Success', message: 'Vacancy Sucessfully Closed!' });
		
		$.ajax({
            url: '/killVacancyEditSession',
            method: 'GET',  
            success: function(data) {
                console.log('Session attribute cleared successfully.');
            },
            error: function(xhr, status, error) {
                console.error('Failed to clear session attribute:', error);
            }
        });
	}

    if(myValue === 'reopen'){
		
		iziToast.success({ timeout: 3000, icon: 'fa-solid fa-check', title: 'Success', message: 'Vacancy Sucessfully Activate!' });
		
		$.ajax({
            url: '/killVacancyEditSession',
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