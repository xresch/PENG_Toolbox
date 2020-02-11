
var CFW_DASHBOARD_WIDGET_REGISTRY = {};

var CFW_DASHBOARD_WIDGET_GUID = 0;

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_registerWidget(widgetUniqueName, widgetObject){
	
	CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueName] = widgetObject;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_getWidget(widgetUniqueName){
	
	return CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueName];
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_removeWidget(widgetGUID){
	
	CFW.ui.confirmExecute('Do you really want to remove this widget?', 'Remove', function () {
		
		var widget = $('#'+widgetGUID);
		console.log(widget);
		
		var grid = $('.grid-stack').data('gridstack');
		grid.removeWidget(widget);
	});
	
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidgetHTML(options){
	CFW_DASHBOARD_WIDGET_GUID++;
	var defaultOptions = {
			guid: 'widget-'+CFW_DASHBOARD_WIDGET_GUID,
			widgetID: null,
			title: "",
			body: "",
			footer: "",
			bgcolor: "",
			textColor: "",
	}
	
	var merged = Object.assign({}, defaultOptions, options);
	
	var htmlString =
		'<div id="'+merged.guid+'" data-id="'+merged.widgetID+'"  class="grid-stack-item" data-gs-width="6" data-gs-height="3">'
		+'    <div class="grid-stack-item-content card bg-'+merged.bgcolor+' text-'+merged.textColor+'">'
		+'     	<div class="card-body h-100">';
		
	if(merged.title != null && merged.title != ''){
		htmlString += 
		 '     	  <div class="card-title">'
		+'		  	<h5>'+merged.title+'</h5>'
		+'		  </div>'
	}
	
	htmlString +=
		 '			  <a type="button" role ="button" class="cfw-dashboard-widget-settings" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
		+'			    <i class="fas fa-cog"></i>'
		+'			  </a>'
		+'			  <div class="dropdown-menu" style="z-index: 128;">'
		+'			    <a class="dropdown-item" href="#">Settings</a>'
		+'				<div class="dropdown-divider"></div>'
		+'				<a class="dropdown-item text-danger" href="#" onclick="cfw_dashboard_removeWidget(\''+merged.guid+'\')"><i class="fas fa-trash"></i>&nbsp;Remove</a>'
		+'			  </div>'
		
	if(merged.body != null && merged.body != ''){
		htmlString +='		  <div>'+merged.body+'</div>'
	}
	
	if(merged.footer != null && merged.footer != ''){
		htmlString +=
		'		 <div class="cfw-dashboard-widget-footer">'
		+			merged.footer
		+'		  </div>'
	}
		+'		</div>'
		+'    </div>'
		+'</div>';
	
	return htmlString;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidgetByName(widgetUniqueName) {
	
	var widget = CFW.dashboard.getWidget(widgetUniqueName);
	var widgetHTML = widget.getWidgetHTML(widget.defaultValues);
	
    var grid = $('.grid-stack').data('gridstack');
    
    grid.addWidget($(widgetHTML), 0, 0, 2, 2, true);
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_addNewWidget() {
	
	var widgetHTML =
		cfw_dashboard_createWidgetHTML(
    		{
    			title: "New Widget", 
    			body: "Some Text", 
    			footer: "Some Footer",
    		}
    	);
    var grid = $('.grid-stack').data('gridstack');
    
    grid.addWidget($(widgetHTML), 0, 0, 2, 2, true);
}

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
CFW.dashboard = {
		registerWidget: 	cfw_dashboard_registerWidget,
		createWidgetHTML:   cfw_dashboard_createWidgetHTML,
		getWidget: cfw_dashboard_getWidget
};

CFW.dashboard.registerWidget("cfw_plainText",
		{
			topmenu: "Default Widgets",
			topmenuicon: "fas fa-th-large",
			menulabel: "Text",
			menuicon: "fas fa-font",
			defaultValues: {
    			title: "New Widget", 
    			body: null, 
    			footer: null,
    			bgcolor: "primary"
    			
    		},
			getWidgetHTML: function (widgetData) {
				
				var merged = Object.assign({}, this.defaults, widgetData);
				
				return CFW.dashboard.createWidgetHTML(
						merged
			    	);
			}
		}
);

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_dashboard_draw(){
	
	console.log('draw');
	$('.grid-stack').gridstack({
		alwaysShowResizeHandle: /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent),
		resizable: {
		    handles: 'e, se, s, sw, w'
		  },
		cellHeight: 60
	});
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		//CFW.http.fetchAndCacheData("./manual", {action: "fetch", item: "menuitems"}, "menuitems", cfw_manual_printMenu);
		
		CFW.ui.toogleLoader(false);
	}, 100);
}

