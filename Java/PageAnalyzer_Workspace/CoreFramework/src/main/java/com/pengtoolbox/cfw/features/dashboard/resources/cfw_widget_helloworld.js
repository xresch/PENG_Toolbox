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
	
				//var content = textRenderer.render({data: merged.JSON_SETTINGS.content});
				var content = textRenderer.render({data: 'Hello '+merged.JSON_SETTINGS.name+'!'});
				callback(widgetData, content);
				
			},
			getEditForm: function (widgetData) {
				return CFW.dashboard.getSettingsForm(widgetData);
			},
			onSave: function (form, widgetData) {
				console.log(" ==== onSave =====");
				console.log(form);
				var settingsForm = $(form);
				
				widgetData.JSON_SETTINGS.name = settingsForm.find('input[name="name"]').val();
			}
			
		}
	);
})();