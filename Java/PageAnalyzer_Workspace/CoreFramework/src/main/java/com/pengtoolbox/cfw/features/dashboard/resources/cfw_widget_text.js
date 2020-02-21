(function (){

	CFW.dashboard.registerWidget("cfw_text",
		{
			category: "Static Widgets",
			menuicon: "fas fa-font",
			menulabel: CFWL('cfw_dashboard_widget_cfwtext', 'Text'),
			description: CFWL('cfw_dashboard_widget_cfwtext_desc', 'Display static text. Can be used to create labels and descriptions.'),
			
			createWidgetInstance: function (widgetObject, callback) {			
				
				var textRenderer = CFW.render.getRenderer('html');
				var content = textRenderer.render({data: widgetObject.JSON_SETTINGS.content});
				
				callback(widgetObject, content);
				
			},
			
			getEditForm: function (widgetObject) {
				return CFW.dashboard.getSettingsForm(widgetObject);
			},
			
			onSave: function (form, widgetObject) {
				var settingsForm = $(form);
				widgetObject.JSON_SETTINGS.content = settingsForm.find('textarea[name="content"]').val();
				
				return true;
			}
			
		}
	);
})();