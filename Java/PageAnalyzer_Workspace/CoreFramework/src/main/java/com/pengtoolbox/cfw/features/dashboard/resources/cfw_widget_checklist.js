(function (){

	CFW.dashboard.registerWidget("cfw_checklist",
		{
			category: "Static Widgets",
			menuicon: "fas fa-tasks",
			menulabel: CFWL('cfw_dashboard_widget_cfwchecklist', 'Checklist'),
			description: CFWL('cfw_dashboard_widget_cfwchecklist_desc', 'Displays a checklist. Every line in the text will be shown as a separate item.'),
			
			createWidgetInstance: function (widgetObject, callback) {			
				
				//-------------------------
				//Create List HTML
				var lines = widgetObject.JSON_SETTINGS.content.trim().split(/\r\n|\r|\n/);
				
				var checkboxGroup = $('<div class="form-group">');
				checkboxGroup.data('widgetObject', widgetObject);
				if(widgetObject.JSON_SETTINGS.isordered){ listHTML = '<ol>';}
				
			 	for(var i = 0; i < lines.length; i++){
			 		var checkboxGUID = "checkbox-"+CFW.utils.randomString(16);
			 		var value = lines[i].trim();
			 		var checkboxHTML = 
			 			'<div class="form-check">'
			 				+'<input class="form-check-input" type="checkbox" value="'+value+'" id="'+checkboxGUID+'" onchange="cfw_widget_checklist_checkboxChange(this)">'
			 				+'<label class="form-check-label" for="'+checkboxGUID+'">'+value+'</label>'
			 			+'</div>';
			 		
			 		checkboxGroup.append(checkboxHTML);
			 	}
			 				
				callback(widgetObject, checkboxGroup);
				
			},
			
			getEditForm: function (widgetObject) {
				return CFW.dashboard.getSettingsForm(widgetObject);
			},
			
			onSave: function (form, widgetObject) {
				var settingsForm = $(form);
				widgetObject.JSON_SETTINGS.content = settingsForm.find('textarea[name="content"]').val();
				widgetObject.JSON_SETTINGS.isordered = ( settingsForm.find('input[name="strikethrough"]:checked').val() == "true" );
				return true;
			}
			
		}
	);
})();


function cfw_widget_checklist_checkboxChange(checkboxElement){
	
	var checkbox = $(checkboxElement);
	var group = checkbox.closest('.form-group');
	var widgetObject = group.data('widgetObject');

	var newContent = '';
	group.find('input[type="checkbox"]').each(function(){
		var currentBox = $(this);
		var value = currentBox.attr('value');
		var checked = currentBox.is(':checked');
		
		if (checked){
			newContent += 'X '+value;
		}else{
			newContent += value;
		}
		newContent += "\r\n";
	});
	
	widgetObject.JSON_SETTINGS.content = newContent;
	
	//TODO: Force save if can edit
	

};