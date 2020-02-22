(function (){

	CFW.dashboard.registerWidget("cfw_htmleditor",
		{
			category: "Static Widgets",
			menuicon: "fas fa-code",
			menulabel: CFWL('cfw_dashboard_widget_cfwhtmleditor', 'HTML Editor'),
			description: CFWL('cfw_dashboard_widget_cfwhtmleditor_desc', 'Display static text. Can be used to create labels and descriptions.'),
			
			createWidgetInstance: function (widgetObject, callback) {			
				
				

				var noflexDiv = $('<div class="d-block w-100">')
				noflexDiv.append(widgetObject.JSON_SETTINGS.content);
				
				var textRenderer = CFW.render.getRenderer('html');
				var content = textRenderer.render({data: noflexDiv.prop("outerHTML")});
				
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