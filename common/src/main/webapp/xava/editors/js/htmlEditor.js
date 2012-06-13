openxava.addEditorInitFunction(function() {	
	for (var instance in CKEDITOR.instances) {
		CKEDITOR.instances[instance].destroy(true);		
	}
	var config = { language: openxava.language };
	$('.ox-ckeditor').ckeditor(config);
});




