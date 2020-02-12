
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
function cfw_dashboard_registerWidget(widgetUniqueType, widgetObject){
	
	CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueType] = widgetObject;
	
	var category =  widgetObject.category;
	var menulabel =  widgetObject.menulabel;
	var menuicon = widgetObject.menuicon;
	
	var categorySubmenu = $('ul[data-submenuof="'+category+'"]');
	console.log(categorySubmenu);
	
	var menuitemHTML = 
		'<li><a class="dropdown-item" onclick="cfw_dashboard_createWidgetByType(\''+widgetUniqueType+'\')" >'
			+'<div class="cfw-fa-box"><i class="'+menuicon+'"></i></div>'
			+'<span class="cfw-menuitem-label">'+menulabel+'</span>'
		+'</a></li>';
	
	categorySubmenu.append(menuitemHTML);
	
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_getWidget(widgetUniqueType){
	
	return CFW_DASHBOARD_WIDGET_REGISTRY[widgetUniqueType];
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

function cfw_dashboard_createFormField(label, infotext, fieldHTML){
	
	var html = 	
	  '<div class="form-group row ml-1">'
		+'<label class="col-sm-3 col-form-label" for="3">'+label+':</label>'
		+'<div class="col-sm-9">'
			+'<span class="badge badge-info cfw-decorator" data-toggle="tooltip" data-placement="top" data-delay="500" title=""'
				+'data-original-title="'+infotext+'"><i class="fa fa-sm fa-info"></i></span>'
				+ fieldHTML
		+'</div>'
	+'</div>';
	
	return html;
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_editWidget(widgetGUID){
	var widgetInstance = $('#'+widgetGUID);
	var widgetData = widgetInstance.data("widgetData");
	console.log(widgetInstance);
	console.log(widgetData);
	
	//##################################################
	// Show Form for Default values
	//##################################################
	var defaultForm = '<h2>Widget Default Settings</h2><form id="form-edit-'+widgetGUID+'">';
	
	//------------------------------
	// Title
	
	defaultForm += new CFWFormField({ type: "text", name: "title", value: widgetData.title, description: 'The title of the widget.' }).createHTML();;
	
	//------------------------------
	// Footer
	defaultForm += cfw_dashboard_createFormField("Footer", 'The footer of the widget.', '<textarea class="form-control" rows="10" name="footer" placeholder="Footer Contents">'+widgetData.footer+'</textarea>');
	
	//------------------------------
	// Background Color
	var styles = ["", "primary", "secondary", "info", "success", "warning", "danger", "dark", "light"];
	
	var bgSelectHTML = '<select class="form-control" name="bgcolor" value="'+widgetData.bgcolor+'">';
		for(key in styles){
			var current = styles[key];
			var selected = "";
			if(styles[key] == widgetData.bgcolor){
				selected =' selected="selected" ';
			}
			bgSelectHTML += '<option value="'+current+'" '+selected+'>'+current+'</option>';
		}
	bgSelectHTML += '</select>';
	defaultForm += cfw_dashboard_createFormField("Background Style:", 'Define the color used for the background.', bgSelectHTML);

	//------------------------------
	// Text Color
	var textcolorSelectHTML = '<select class="form-control" name="textcolor" value="'+widgetData.textcolor+'">';
		for(key in styles){
			var current = styles[key];
			var selected = "";
			if(styles[key] == widgetData.textcolor){
				selected =' selected="selected" ';
			}
			textcolorSelectHTML += '<option value="'+current+'" '+selected+'>'+current+'</option>';
		}
	textcolorSelectHTML += '</select>';
	defaultForm += cfw_dashboard_createFormField("Text Style:", 'Define the color used for the background.', textcolorSelectHTML);

	//------------------------------
	// Save Button
	defaultForm += '<input type="button" onclick="cfw_dashboard_saveDefaultSettings(\''+widgetGUID+'\')" class="form-control btn-primary" value="Save">';
	
	//##################################################
	// Create Widget Specific Form
	//##################################################
	var widgetDef = CFW.dashboard.getWidget(widgetData.widgetType);
	var customForm = $(widgetDef.getEditForm(widgetData));
	var buttons = customForm.find('button');
	if(buttons.length > 0){
		buttons.remove();
	}
	var customFormButton = '<input type="button" onclick="cfw_dashboard_saveCustomSettings(this, \''+widgetGUID+'\')" class="form-control btn-primary" value="Save">';
	
	customForm.append(customFormButton)
//	
	//##################################################
	// Create and show Modal
	//##################################################
	var compositeDiv = $('<div>');
	compositeDiv.append(defaultForm);
	compositeDiv.append('<h2>Settings for '+widgetDef.menulabel+' Widget</h2>');
	compositeDiv.append(customForm);
	
	CFW.ui.showModal("Edit Widget", compositeDiv, "CFW.cache.clearCache();");
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_saveDefaultSettings(widgetGUID){
	var widget = $('#'+widgetGUID);
	var widgetData = widget.data("widgetData");
	var settingsForm = $('#form-edit-'+widgetGUID);
			
	widgetData.title = settingsForm.find('input[name="title"]').val();
	widgetData.footer = settingsForm.find('textarea[name="footer"]').val();
	widgetData.bgcolor = settingsForm.find('select[name="bgcolor"]').val();
	widgetData.textcolor = settingsForm.find('select[name="textcolor"]').val();
	
	cfw_dashboard_rerenderWidget(widgetGUID);
	
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_saveCustomSettings(formButton, widgetGUID){
	var widget = $('#'+widgetGUID);
	var widgetData = widget.data("widgetData");
	var settingsForm = $('#form-edit-'+widgetGUID);
	console.log("====== Before =======");
	console.log(widgetData);
	var widgetDef = CFW.dashboard.getWidget(widgetData.widgetType);
	widgetDef.onSave($(formButton).parent(), widgetData);
	console.log("====== After =======");
	console.log(widgetData);
	cfw_dashboard_rerenderWidget(widgetGUID);
	
}
/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_removeWidgetConfirmed(widgetGUID){
	CFW.ui.confirmExecute('Do you really want to remove this widget?', 'Remove', cfw_dashboard_removeWidget(widgetGUID) );
}

/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_removeWidget(widgetGUID) {
	var widget = $('#'+widgetGUID);
	console.log("#### Add remove from Database.");
	
	var grid = $('.grid-stack').data('gridstack');
	grid.removeWidget(widget);
};


/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidget(widgetData){
	
	//---------------------------------------
	// Merge Data
	CFW_DASHBOARD_WIDGET_GUID++;
	var defaultOptions = {
			guid: 'widget-'+CFW_DASHBOARD_WIDGET_GUID,
			widgetid: null,
			title: "",
			content: "",
			footer: "",
			bgcolor: "",
			textcolor: "",
	}
	
	var merged = Object.assign({}, defaultOptions, widgetData);
	
	//---------------------------------------
	// Resolve Classes
	var textcolorClass = '';
	var borderClass = '';
	if(merged.textcolor != null && merged.textcolor.trim().length > 0){
		textcolorClass = 'text-'+merged.textcolor;
		borderClass = 'border-'+merged.textcolor;
	}
	
	var bgcolorClass = '';
	if(merged.bgcolor != null && merged.bgcolor.trim().length > 0){
		bgcolorClass = 'bg-'+merged.bgcolor;
	}
	
	var htmlString =
		'    <div class="grid-stack-item-content card '+bgcolorClass+' '+textcolorClass+'">'
		+'		<a type="button" role ="button" class="cfw-dashboard-widget-settings" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
		+'			<i class="fas fa-cog"></i>'
		+'		</a>'
		+'		<div class="dropdown-menu">'
		+'			<a class="dropdown-item" onclick="cfw_dashboard_editWidget(\''+merged.guid+'\')"><i class="fas fa-pen"></i>&nbsp;Edit</a>'
		+'			<div class="dropdown-divider"></div>'
		+'				<a class="dropdown-item text-danger" onclick="cfw_dashboard_removeWidgetConfirmed(\''+merged.guid+'\')"><i class="fas fa-trash"></i>&nbsp;Remove</a>'
		+'			</div>'

		
	if(merged.title != null && merged.title != ''){
		htmlString += 
		 '     	  <div class="cfw-dashboard-widget-title border-bottom '+borderClass+'">'
		+'		  	<span>'+merged.title+'</span>'
		+'		  </div>'
	}
	
	if(merged.content != null && merged.content != ''){
		htmlString += 
			'<div class="cfw-dashboard-widget-body">'
				+ merged.content
				
				if(merged.footer != null && merged.footer != ''){
					htmlString +=
					'		 <div class="cfw-dashboard-widget-footer border-top '+borderClass+'">'
					+			merged.footer
					+'		  </div>'
				}
		htmlString += '</div>';
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
function cfw_dashboard_rerenderWidget(widgetGUID) {
	var widget = $('#'+widgetGUID);
	var widgetData = widget.data("widgetData");
	
	cfw_dashboard_removeWidget(widgetGUID);
	cfw_dashboard_createWidgetByType(widgetData.widgetType, widgetData)
	
}
/************************************************************************************************
 * 
 ************************************************************************************************/
function cfw_dashboard_createWidgetByType(widgetType, widgetData) {
	
	var widget = CFW.dashboard.getWidget(widgetType);
	
	var x = 0;
	var y = 0;
	var gswidth = 2;
	var gsheight = 2;
	var doAutoposition = true;
	
	if(widgetData != null){
		x = widgetData.x;
		y = widgetData.y;
		gswidth = widgetData.gswidth;
		gsheight = widgetData.gsheight;
		doAutoposition = false;
	}else{
		widgetData = widget.defaultValues;
	}
	
	var widgetInstance = widget.createWidgetInstance(widgetData);
	
    var grid = $('.grid-stack').data('gridstack');
    
    grid.addWidget($(widgetInstance), x, y, gswidth, gsheight, doAutoposition);
    
    //----------------------------
    // Update Data
    var widgetData = widgetInstance.data('widgetData');
    
    widgetData.widgetType	= widgetType;
    widgetData.gswidth	= widgetInstance.attr("data-gs-width");
    widgetData.gsheight	= widgetInstance.attr("data-gs-height");
    widgetData.x		= widgetInstance.attr("data-gs-x");
    widgetData.y		= widgetInstance.attr("data-gs-y");
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


/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerCategory("Default Widgets", "fas fa-th-large");
CFW.dashboard.registerCategory("Test Category", "fas fa-cogs");
CFW.dashboard.registerCategory("Another Category", "fas fa-book");

/******************************************************************
 * 
 ******************************************************************/
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

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_html",
		{
			category: "Default Widgets",
			menulabel: "Test HTML",
			menuicon: "fas fa-code",
			renderers: [],
			defaultValues: {
    			title: "Some very long title to check overflow", 
    			content: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
    			footer: "Some very long footer to test overflow",
    		},
    		createWidgetInstance: function (widgetData) {
				
				widgetData.deepoptions = {
						bla: { array: [ "test", "bla", "blub"] }
				}
				
				var merged = Object.assign({}, this.defaults, widgetData);
				var textRenderer = CFW.dashboard.getRenderer('text');

				return textRenderer.createWidget(merged);
			},
    		getEditForm: function (widgetData) {
    			
    			var customForm = '<form>';
    			
    			//------------------------------
    			// Content
    			customForm += cfw_dashboard_createFormField("Content", 'The html content of the widget.', '<textarea class="form-control" rows="10" name="content" placeholder="HTML Content">'+widgetData.content+'</textarea>');
    			customForm += '</form>';
    			
    			return customForm;
    		},
			onSave: function (form, widgetData) {
				console.log(" ==== onSave =====");
				console.log(form);
				var settingsForm = $(form);
				
				widgetData.content = settingsForm.find('textarea[name="content"]').val();
			}
    		
		}
);

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_dashboard_initializeGridstack(){
	
	//-----------------------------
	// Set options 
	$('.grid-stack').gridstack({
		alwaysShowResizeHandle: /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent),
		resizable: {
		    handles: 'e, se, s, sw, w'
		  },
		cellHeight: 60
	});
	
	//-----------------------------
	// Set update on dragstop 
	$('.grid-stack').on('change', function(event, items) {
		  var grid = this;
		  console.log("======= ITEMS ======= ");
		  console.log(items);
		  var i = 0;
		  for(key in items){
			  var currentItem = items[key].el;
			  console.log("======= CHANGE "+(++i)+"======= ");
			  console.log(currentItem);
			  var widgetInstance = $(currentItem);
			  var widgetData 	 = widgetInstance.data("widgetData");
			  
			  widgetData.x		 = widgetInstance.attr("data-gs-x");
			  widgetData.y		 = widgetInstance.attr("data-gs-y");
			  widgetData.gswidth		= widgetInstance.attr("data-gs-width");
			  widgetData.gsheight	= widgetInstance.attr("data-gs-height");
				 console.log("===== Dragged ======");
				 console.log(widgetData);
		  }
	});
	
}
/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_dashboard_draw(){
	
	console.log('draw');
	
	cfw_dashboard_initializeGridstack();
	
	// Test Data
	cfw_dashboard_createWidgetByType('cfw_html', {x:0, y:0, gsheight: 2, gswidth: 2, title: "Test Success", bgcolor: "success", textcolor: "light"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:11, y:0, gsheight: 5, gswidth: 2, title: "Test Danger", bgcolor: "danger", textcolor: "light"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:8, y:0, gsheight: 3, gswidth: 2, title: "Test Primary", bgcolor: "primary", textcolor: "light"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:7, y:0, gsheight: 1, gswidth: 3, title: "Test Light", bgcolor: "light", textcolor: "secondary"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:2, y:0, gsheight: 2, gswidth: 4, title: "Test Matrix", bgcolor: "dark", textcolor: "success", content: "Mister ÄÄÄÄÄÄÄÄÄÄÄnderson."});
	cfw_dashboard_createWidgetByType('cfw_html', {x:9, y:0, gsheight: 2, gswidth: 4, title: "Test Warning", bgcolor: "warning", textcolor: "dark"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:3, y:0, gsheight: 4, gswidth: 5});
	cfw_dashboard_createWidgetByType('cfw_html', {x:0, y:0, gsheight: 3, gswidth: 3});
	cfw_dashboard_createWidgetByType('cfw_html');
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		//CFW.http.fetchAndCacheData("./manual", {action: "fetch", item: "menuitems"}, "menuitems", cfw_manual_printMenu);
		
		CFW.ui.toogleLoader(false);
	}, 100);
}

