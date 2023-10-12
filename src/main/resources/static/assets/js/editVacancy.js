/**
 * 
 */
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


	$('#responsibility').summernote({
		placeholder: 'Responsibility',
		height: 120,
		toolbar: [
			['font', ['bold', 'underline', 'clear']],
			['para', ['ul', 'ol']]
		]
	});

	$('#requirement').summernote({
		placeholder: 'Requirement',
		height: 120,
		toolbar: [
			['font', ['bold', 'underline', 'clear']],
			['para', ['ul', 'ol']]
		]
	});

	$('#preference').summernote({
		placeholder: 'Preference',
		height: 120,
		toolbar: [
			['font', ['bold', 'underline', 'clear']],
			['para', ['ul', 'ol']]
		]
	});


	$("#workFrom").change(function() {
		let timeFrom = $("#workFrom").val();
		$("#workTo").attr("min", timeFrom);
	});

	$("#workTo").change(function() {
		let timeTo = $("#workTo").val();
		$("#workFrom").attr("max", timeTo);
	});


	$("[type=submit]").click(function(event) {

		iziToast.settings({
			function(instance, toast) {
				instance.hide({
					transitionOut: 'fadeOutUp'
				}
				)
			}
		});

		let position = $("#floatingName");
		let posts = $("#numberOfPost");
		let workingDays = $('input[name="workingDays"]:checked');
		let workHourFrom = $("#workFrom");
		let workHourTo = $("#workTo");
		let salary = $("#salary");
		let jobType = $('input[name="jobType"]:checked');
		let desc = $("#description");
		let respon = $("#responsibility");
		let require = $("#requirement");
		let prefe = $("#preference");
		let startTime = $("#workFrom").val();
		let endTime = $("#workTo").val();
		var startDate = new Date("1970-01-01T" + startTime);
		var endDate = new Date("1970-01-01T" + endTime);
		let department = $("#department");

		function testForEditor(inputValue) {
			let first_modifiedVal = inputValue.replace("<p>", "");
			let sec_modifiedVal = first_modifiedVal.replace("</p>", "");
			var regexF = new RegExp("&nbsp;", 'g');
			return last_modifiedVal = sec_modifiedVal.replace(regexF, "");
		}
		// Compare the time values
		if (startDate > endDate) {
			iziToast.warning({ title: 'Error', message: 'Start time is after End time.' });
		}
		var regex = /^\s*$/;
		if (regex.test(position.val()) || regex.test(posts.val()) || workingDays.length < 1 || workHourFrom.val() === "" || workHourTo.val() === ""
			|| regex.test(salary.val()) || regex.test(desc.val()) || regex.test(testForEditor(respon.val())) || regex.test(testForEditor(require.val())) || regex.test(testForEditor(prefe.val())) || regex.test(department.val())) {


			iziToast.error({ title: 'Error', message: 'Complete All Required Fields' });

			var fieldIds = ['floatingName', 'numberOfPost', 'workingDays', 'workFrom', 'workTo', 'salary', 'description', 'responsibility', 'requirement', 'preference', 'department'];

			var hasError = false;

			if (regex.test(testForEditor(respon.val()))) {
				$('#respo').addClass("border border-danger");
			} else {
				$('#respo').removeClass("border border-danger");
			}
			if (regex.test(testForEditor(require.val()))) {
				$('#requ').addClass("border border-danger");
			} else {
				$('#requ').removeClass("border border-danger");
			}
			if (regex.test(testForEditor(prefe.val()))) {
				$('#prefer').addClass("border border-danger");
			} else {
				$('#prefer').removeClass("border border-danger");
			}
			for (var i = 0; i < fieldIds.length; i++) {
				var fieldId = fieldIds[i];
				var field = $('#' + fieldId);

				if (field.val() === "") {
					field.addClass("border border-danger");
					hasError = true;

				} else {
					field.removeClass("border border-danger");
				}
			}
			if (posts.val() < 1 || posts.val() > 100) {
				posts.addClass("border border-danger");
			}
			event.preventDefault();
			return false;
		}
		if (posts.val() < 1 || posts.val() > 100) {
			posts.addClass("border border-danger");
			iziToast.warning({ icon: 'bi bi-exclamation-triangle', message: 'Enter vaild posts value.' });
			event.preventDefault();
			return false;
		}
	});


	const companyId = 1;

	const departmentSelect = $('#department');

	if (companyId) {
		// Fetch the department list based on the selected company
		$.ajax({
			url: `/departmentList/${companyId}`,
			dataType: 'json',
			success: function(data) {
				// Iterate over the department data and create option elements
				$.each(data, function(index, department) {
					if (departmentSelect.val() != department.id) {
						const option = $('<option></option>').val(department.id).text(department.name);
						if (department.enable == true) {
							departmentSelect.append(option);
						}
					}
				});
			},
			error: function(error) {
				console.error('Error fetching department list:', error);
			}
		});
	}


	var myValue = $('#myDiv').text();
	if (myValue === 'error') {
		iziToast.error({ icon: 'bi bi-exclamation', message: 'Data Too Long!' });

		$.ajax({
			url: '/junior/toKillSession',
			method: 'GET',
			success: function(data) {
				console.log('Session attribute cleared successfully.');
			},
			error: function(xhr, status, error) {
				console.error('Failed to clear session attribute:', error);
			}
		});
	}
	if (myValue === 'nullError') {
		iziToast.error({ icon: 'bi bi-exclamation', message: 'Complete all required fields!' });

		$.ajax({
			url: '/junior/toKillSession',
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
