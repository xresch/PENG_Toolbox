(function (){
	/******************************************************************
	 * 
	 ******************************************************************/
	CFW.dashboard.registerWidget("cfw_iframe",
		{
			category: "Static Widgets",
			menuicon: "fas fa-globe",
			menulabel: CFWL('cfw_dashboard_widget_cfwwebsite', "Website"),
			description: CFWL('cfw_dashboard_widget_cfwwebsite_desc', "Displays a website(doesn't work with all websites)."),
			
			createWidgetInstance: function (widgetObject, callback) {
								
				var merged = Object.assign({}, this.defaultValues, widgetObject);
				var textRenderer = CFW.render.getRenderer('html');

				content = textRenderer.render({data: '<iframe class="w-100 flex-grow-1" src="'+widgetObject.JSON_SETTINGS.url+'">'});
				callback(widgetObject, content);
			},
			
			getEditForm: function (widgetObject) {
				return CFW.dashboard.getSettingsForm(widgetObject);
			},
			
			onSave: function (form, widgetObject) {
				var settingsForm = $(form);
				widgetObject.JSON_SETTINGS.url = settingsForm.find('input[name="url"]').val();
				return true;
			}
		}
	);
})();