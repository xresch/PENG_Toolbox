(function (){

	CFW.dashboard.registerWidget("cfw_verticallabel",
		{
			category: "Static Widgets",
			menuicon: "fas fa-font fa-rotate-270",
			menulabel: CFWL('cfw_widget_cfwverticallabel', 'Vertical Label'),
			description: CFWL('cfw_widget_cfwverticallabel_desc', 'Displays a vertical label.'),
			defaulttitle: "",
			defaultwidth: 1,
			defaultheight: 5,
			createWidgetInstance: function (widgetObject, callback) {	
				var settings = widgetObject.JSON_SETTINGS;
				if(settings.label != null){
					
					var labelHTML = '<div class="label-box"><span class="rotate-270 text-center" style="white-space: nowrap; font-size: '+24*settings.sizefactor+'px;"">';
					
					if(widgetObject.JSON_SETTINGS.link != null && widgetObject.JSON_SETTINGS.link != ''){
						labelHTML += '<a target="_blank" class="text-'+widgetObject.FGCOLOR+'" href="'+widgetObject.JSON_SETTINGS.link+'">'+widgetObject.JSON_SETTINGS.label+'</a>'
						+'</span></div>'; 
					}else{
						labelHTML += widgetObject.JSON_SETTINGS.label
						+'</span></div>'; 
					}
					
					callback(widgetObject, labelHTML);
				}else{
					callback(widgetObject, '');
				}

			},
						
		}
	);
})();