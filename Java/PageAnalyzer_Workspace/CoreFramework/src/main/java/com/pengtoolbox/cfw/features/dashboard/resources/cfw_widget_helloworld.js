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
				var textRenderer = CFW.render.getRenderer('html');
				var content = textRenderer.render({data: 'Hello '+widgetData.JSON_SETTINGS.name+'!'});
				callback(widgetData, content);
				
			},
			getEditForm: function (widgetData) {
				return CFW.dashboard.getSettingsForm(widgetData);
			},
			onSave: function (form, widgetData) {
				var settingsForm = $(form);
				widgetData.JSON_SETTINGS.name = settingsForm.find('input[name="name"]').val();
			}
			
		}
	);
})();