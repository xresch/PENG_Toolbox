(function (){

	CFW.dashboard.registerWidget("cfw_verticallabel",
		{
			category: "Static Widgets",
			menuicon: "fas fa-font fa-rotate-270",
			menulabel: CFWL('cfw_widget_cfwverticallabel', 'Vertical Label'),
			description: CFWL('cfw_widget_cfwverticallabel_desc', 'Displays a vertical label.'),
			
			createWidgetInstance: function (widgetObject, callback) {	
				var settings = widgetObject.JSON_SETTINGS;
				if(settings.label != null){
					
					var labelHTML = '<div class="w-100 h-100 d-flex align-items-center justify-content-center"><span style="transform: rotate(-90deg); font-size: '+24*settings.sizefactor+'px;"">'+widgetObject.JSON_SETTINGS.label+'</span></div>'; 
					callback(widgetObject, labelHTML);
				}else{
					callback(widgetObject, '');
				}

			},
			
			getEditForm: function (widgetObject) {
				return CFW.dashboard.getSettingsForm(widgetObject);
			},
			
			onSave: function (form, widgetObject) {
				widgetObject.JSON_SETTINGS = CFW.format.formToObject(form);
				return true;
			}
			
		}
	);
})();