(function (){
	
	/******************************************************************
	 * 
	 ******************************************************************/
	CFW.dashboard.registerCategory("fas fa-desktop", "Monitoring");
	
//	if (current.VALUE == 100) 		{ current.alertstyle = "cfw-excellent"; } 
//	
//	else if (current.VALUE >= 75) 	{ current.alertstyle = "cfw-good"; } 
//	else if (current.VALUE >= 50) 	{ current.alertstyle = "cfw-warning"; } 
//	else if (current.VALUE >= 25) 	{ current.alertstyle = "cfw-emergency"; } 
//	else if (current.VALUE >= 0)  	{ current.alertstyle = "cfw-danger"; } 
//	else if (current.VALUE == 'NaN' 
//		  || current.VALUE < 0) { 		  current.alertstyle = "cfw-gray"; } 
	
	/******************************************************************
	 * 
	 ******************************************************************/
	CFW.dashboard.registerWidget("emp_discomode",
		{
			category: "Easter Eggs",
			menuicon: "fas fa-globe fa-spin",
			menulabel: CFWL('cfw_widget_discomode', "Disco!!!"),
			description: CFWL('cfw_widget_discomode_desc', "Toggles discomode."),
			createWidgetInstance: function (widgetObject, callback) {		
				var html = 
					 '<button class="btn btn-sm btn-primary fa fa-globe" onclick="Disco('+widgetObject.JSON_SETTINGS.discolevel+')" style="height: 100%; width:100%;"></button>'
					;
				callback(widgetObject, html);
				
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

function getRandomColor() {
	var letters = '0123456789ABCDEF';
	var color = '#';
	for (var i = 0; i < 6; i++) {
	  color += letters[Math.floor(Math.random() * 16)];
	}
	return color;
}

var discoSet = window.localStorage.getItem("Disco");
var discoToggle = null;

function Disco(level) {
	if (discoSet == null && discoToggle == null) {
		$('[id^="widget-"]').addClass("fa-spin");
		discoSet = setInterval(function(){ 
			var bodyColor = getRandomColor();
			$('[id^="widget-"]').addClass("fa-spin");
			$('body').css("background-color", bodyColor);
		}, level);
		window.localStorage.setItem('Disco', discoSet);
		discoToggle = discoSet;
	}else {
		$('body').css("background-color", "");
		clearInterval(discoSet);
		window.localStorage.removeItem("Disco");
		discoSet = null;
		discoToggle = null;
		$('[id^="widget-"]').removeClass("fa-spin");
	}
	
	
}