$(window).adaptTo("foundation-registry")
.register(
    "foundation.validation.validator", {
	selector : "#alt-special", // validates the specific alt field
	validate : function(el) {
		var $el = $(el);
		var $form = $el.closest('form'); // get the form
		var $upload = $form.find("coral-fileupload[name$=image]"); // find the file upload widget
		if ($upload.hasClass('is-filled') && !$el.val()) { // if class exists, return the validation message
			return "Enter Alt Text";
		} else {
			return;
		}
	}
});

