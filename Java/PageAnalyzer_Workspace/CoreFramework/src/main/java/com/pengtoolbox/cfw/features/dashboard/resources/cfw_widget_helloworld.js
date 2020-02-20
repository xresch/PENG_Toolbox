(function (){
	/******************************************************************
	 * 
	 ******************************************************************/
	CFW.dashboard.registerCategory("fas fa-flask", "Server Side Category");
	
	/******************************************************************
	 * 
	 ******************************************************************/
	CFW.dashboard.registerWidget("cfw_helloworld",
		{
			category: "Server Side Category",
			menuicon: "fas fa-font",
			menulabel: 'Hello World',
			description: 'Prints a Name',
			createWidgetInstance: function (widgetData, callback) {
								
				var merged = Object.assign({}, this.defaultValues, widgetData);
				var textRenderer = CFW.render.getRenderer('html');
	
				var content = textRenderer.render({data: merged.JSON_SETTINGS.content});
				callback(widgetData, content);
				
			},
			getEditForm: function (widgetData) {
				
				var customForm = '<form>';
				
				//------------------------------
				// Content
				customForm += new CFWFormField({ type: "textarea", name: "content", value: widgetData.JSON_SETTINGS.content, description: 'The html contents of the widget.' }).createHTML();
				customForm += '</form>';
				
				return customForm;
			},
			onSave: function (form, widgetData) {
				console.log(" ==== onSave =====");
				console.log(form);
				var settingsForm = $(form);
				
				widgetData.JSON_SETTINGS.content = settingsForm.find('textarea[name="content"]').val();
			}
			
		}
	);
})();