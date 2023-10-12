 $(document).ready(function(){
     var fileInput = $(".input-file"),
		button = $(".input-file-trigger"),
		the_return = $(".file-return");

	button.keydown(function(event) {
		if (event.keyCode == 13 || event.keyCode == 32) {
			fileInput.focus();
		}
	});

	button.click(function(event) {
		fileInput.focus();
		return false;
	});

	fileInput.change(function(event) {
		$('#fileLabel').removeClass("inputError");
		const file = this.files[0]; // Get the selected file
		const arr = this.value.split("\\");
		const fileName = arr[arr.length - 1];

		// Display the filename
		the_return.html('Attach file : '+fileName);

		// Display the file size
		const fileSize = (file.size / (1024 * 1024)).toFixed(2);
		if (fileSize > 50) {
			iziToast.error({ title: 'Error', message: 'File Size cannot be larger than 50MB' });
			the_return.html("");
			$fileInput.val("");
		} else {
			the_return.append(" (" + fileSize + " MB)");
		}
	});

  $('#delete').click(function() {
    $('#email').summernote('code', ''); 
    fileInput.val('');
    the_return.html('');
    });
});