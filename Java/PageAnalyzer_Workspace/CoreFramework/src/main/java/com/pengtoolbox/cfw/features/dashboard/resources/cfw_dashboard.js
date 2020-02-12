
var CFW_DASHBOARD_WIDGET_REGISTRY = {};
var CFW_DASHBOARD_RENDERER_REGISTRY = {};

//saved with guid
var CFW_DASHBOARD_WIDGET_DATA = {};

var CFW_DASHBOARD_WIDGET_GUID = 0;

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_registerRenderer(rendererUniqueName, rendererObject){
	
	CFW_DASHBOARD_RENDERER_REGISTRY[rendererUniqueName] = rendererObject;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_getRenderer(rendererUniqueName){
	return CFW_DASHBOARD_RENDERER_REGISTRY[rendererUniqueName];
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_registerWidget(widgetUniqueName, widgetObject){
	
	CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueName] = widgetObject;
	
	var category =  widgetObject.category;
	var menulabel =  widgetObject.menulabel;
	var menuicon = widgetObject.menuicon;
	
	var categorySubmenu = $('ul[data-submenuof="'+category+'"]');
	console.log(categorySubmenu);
	
	var menuitemHTML = 
		'<li><a class="dropdown-item" onclick="cfw_dashboard_createWidgetByName(\''+widgetUniqueName+'\')" >'
			+'<div class="cfw-fa-box"><i class="'+menuicon+'"></i></div>'
			+'<span class="cfw-menuitem-label">'+menulabel+'</span>'
		+'</a></li>';
	
	categorySubmenu.append(menuitemHTML);
	
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
function cfw_dashboard_registerCategory(categoryName, faiconClasses){
	
	var categoryHTML = 
		'<li class="dropdown dropdown-submenu show">'
			+'<a href="#" class="dropdown-item dropdown-toggle" id="cfwMenuDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true"><div class="cfw-fa-box"><i class="'+faiconClasses+'"></i></div><span class="cfw-menuitem-label">'+categoryName+'</span><span class="caret"></span></a>'
			+'<ul class="dropdown-menu dropdown-submenu" aria-labelledby="cfwMenuDropdown" data-submenuof="'+categoryName+'">'
			+'</ul>'
		+'</li>'
		
	$('#addWidgetDropdown').append(categoryHTML);
}


/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_editWidget(widgetGUID){

		var widget = $('#'+widgetGUID);
		var widgetData = widget.data("widgetData");
		console.log(widget);
		console.log(widgetData);
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
function cfw_dashboard_createWidget(widgetData){
	CFW_DASHBOARD_WIDGET_GUID++;
	var defaultOptions = {
			guid: 'widget-'+CFW_DASHBOARD_WIDGET_GUID,
			widgetid: null,
			title: "",
			content: "",
			footer: "",
			bgcolor: "dark",
			textColor: "light",
	}
	
	var merged = Object.assign({}, defaultOptions, widgetData);
	
	var htmlString =
		'    <div class="grid-stack-item-content card bg-'+merged.bgcolor+' text-'+merged.textColor+'">'
		+'		<a type="button" role ="button" class="cfw-dashboard-widget-settings" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
		+'			<i class="fas fa-cog"></i>'
		+'		</a>'
		+'		<div class="dropdown-menu">'
		+'			<a class="dropdown-item" onclick="cfw_dashboard_editWidget(\''+merged.guid+'\')"><i class="fas fa-pen"></i>&nbsp;Edit</a>'
		+'			<div class="dropdown-divider"></div>'
		+'				<a class="dropdown-item text-danger" onclick="cfw_dashboard_removeWidget(\''+merged.guid+'\')"><i class="fas fa-trash"></i>&nbsp;Remove</a>'
		+'			</div>'

		
	if(merged.title != null && merged.title != ''){
		htmlString += 
		 '     	  <div class="cfw-dashboard-widget-title border-bottom border-'+merged.textColor+'">'
		+'		  	<span>'+merged.title+'</span>'
		+'		  </div>'
	}
	
	if(merged.content != null && merged.content != ''){
		htmlString += 
			'<div class="cfw-dashboard-widget-body">'
				+ merged.content
			+'</div>';
	}
	
	if(merged.footer != null && merged.footer != ''){
		htmlString +=
		'		 <div class="cfw-dashboard-widget-footer  border-top border-'+merged.textColor+'"">'
		+			merged.footer
		+'		  </div>'
	}
	
	htmlString += '</div>';
	
	var widgetItem = $('<div id="'+merged.guid+'" data-id="'+merged.widgetID+'"  class="grid-stack-item">');
	widgetItem.append(htmlString);
	widgetItem.data("widgetData", merged)
	console.log(merged);
	return widgetItem;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidgetByName(widgetUniqueName) {
	
	var widget = CFW.dashboard.getWidget(widgetUniqueName);
	var widgetInstance = widget.createWidgetInstance(widget.defaultValues);
	
    var grid = $('.grid-stack').data('gridstack');
    
    grid.addWidget($(widgetInstance), 0, 0, 2, 2, true);
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_addNewWidget() {
	
	var widgetHTML =
		cfw_dashboard_createWidgetHTML(
    		{
    			title: "New Widget", 
    			content: "Some Text", 
    			footer: "Some Footer",
    		}
    	);
    var grid = $('.grid-stack').data('gridstack');
    
    grid.addWidget($(widgetHTML), 0, 0, 2, 2, true);
}

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard = {
		registerWidget: 	cfw_dashboard_registerWidget,
		getWidget: 			cfw_dashboard_getWidget,
		registerRenderer: 	cfw_dashboard_registerRenderer,
		getRenderer: 		cfw_dashboard_getRenderer,
		registerCategory: 	cfw_dashboard_registerCategory,
		createWidget:   	cfw_dashboard_createWidget,
};

CFW.dashboard.registerCategory("Default Widgets", "fas fa-th-large");
CFW.dashboard.registerCategory("Test Category", "fas fa-cogs");
CFW.dashboard.registerCategory("Another Category", "fas fa-book");

CFW.dashboard.registerRenderer("text",
	{
		label: "Text",
		defaultValues: {
			title: "Text Widget", 
			content: null, 
			footer: null,
		},
		createWidget: function (widgetData) {
		
			var merged = Object.assign({}, this.defaults, widgetData);
			
			return CFW.dashboard.createWidget(merged);
		}
});


CFW.dashboard.registerWidget("cfw_html",
		{
			category: "Default Widgets",
			menulabel: "HTML",
			menuicon: "fas fa-code",
			renderers: [],
			defaultValues: {
    			title: "New Widget", 
    			content: null, 
    			footer: null,
    			bgcolor: "primary",
    			color: "light"
    		},
    		createWidgetInstance: function (widgetData) {
				
				var merged = Object.assign({}, this.defaults, widgetData);
				var textRenderer = CFW.dashboard.getRenderer('text');
				
				return textRenderer.createWidget(merged);
			}
		}
);

CFW.dashboard.registerWidget("cfw_html2",
		{
			category: "Default Widgets",
			menulabel: "Test HTML",
			menuicon: "fas fa-code",
			renderers: [],
			defaultValues: {
    			title: "Some very long title to check overflow", 
    			content: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
    			footer: "Some very long footer to test overflow",
    			bgcolor: "primary",
    			color: "light"
    		},
    		createWidgetInstance: function (widgetData) {
				
				widgetData.deepoptions = {
						bla: { array: [ "test", "bla", "blub"] }
				}
				
				var merged = Object.assign({}, this.defaults, widgetData);
				var textRenderer = CFW.dashboard.getRenderer('text');

				return textRenderer.createWidget(merged);
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

