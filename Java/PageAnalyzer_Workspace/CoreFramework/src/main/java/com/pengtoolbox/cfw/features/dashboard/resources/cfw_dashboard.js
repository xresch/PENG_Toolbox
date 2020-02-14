
var CFW_DASHBOARD_EDIT_MODE = true;
var CFW_DASHBOARD_FULLSCREEN_MODE = false;

var CFW_DASHBOARD_WIDGET_REGISTRY = {};

//saved with guid
var CFW_DASHBOARD_WIDGET_DATA = {};
var CFW_DASHBOARD_WIDGET_GUID = 0;

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
	defaultForm += new CFWFormField({ type: "textarea", name: "footer", value: widgetData.footer, description: 'The contents of the footer of the widget.' }).createHTML();;
	
	//defaultForm += cfw_dashboard_createFormField("Footer", 'The footer of the widget.', '<textarea class="form-control" rows="10" name="footer" placeholder="Footer Contents">'+widgetData.footer+'</textarea>');
	
	//------------------------------
	// Color Selectors
	var selectOptions = {
			"Default": "", 
			"Primary": "primary", 
			"Secondary": "secondary",
			"Info": "info", 
			"Success": "success", 
			"Warning": "warning", 
			"Danger": "danger", 
			"Dark": "dark", 
			"Light": "light"
	};
	
	defaultForm += new CFWFormField({ 
		type: "select", 
		name: "bgcolor", 
		label: "Background Color", 
		value: widgetData.bgcolor, 
		options: selectOptions,
		description: 'Define the color used for the background.' 
	}).createHTML();
	
	defaultForm += new CFWFormField({ 
		type: "select", 
		name: "textcolor", 
		label: "Text Color", 
		value: widgetData.textcolor, 
		options: selectOptions,
		description: 'Define the color used for the text and borders.' 
	}).createHTML();
	
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
	CFW.ui.confirmExecute('Do you really want to remove this widget?', 'Remove', "cfw_dashboard_removeWidget('"+widgetGUID+"')" );
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
			settings: {}
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
		'    <div class="grid-stack-item-content card d-flex '+bgcolorClass+' '+textcolorClass+'">'
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
	

	htmlString += 
		'<div class="cfw-dashboard-widget-body d-flex flex-grow-1">';
			if(merged.footer != null && merged.footer != ''){
				htmlString +=
				'		 <div class="cfw-dashboard-widget-footer border-top '+borderClass+'">'
				+			merged.footer
				+'		  </div>'
			}
	htmlString += '</div>';
	htmlString += '</div>';
	
	var widgetItem = $('<div id="'+merged.guid+'" data-id="'+merged.widgetID+'"  class="grid-stack-item">');
	widgetItem.append(htmlString);
	widgetItem.data("widgetData", merged)
	
	if(merged.content != null && merged.content != ''){
		widgetItem.find('.cfw-dashboard-widget-body').append(merged.content);
	}
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
	var gswidth = 3;
	var gsheight = 3;
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
    var widgetData = $(widgetInstance).data('widgetData');
    
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
		registerCategory: 	cfw_dashboard_registerCategory,
		createWidget:   	cfw_dashboard_createWidget,
};

/******************************************************************
 * 
 ******************************************************************/
function cfw_dashboard_toggleFullscreenMode(){
	var grid = $('.grid-stack').data('gridstack');
	
	if(CFW_DASHBOARD_FULLSCREEN_MODE){
		CFW_DASHBOARD_FULLSCREEN_MODE = false;

		$('.hideOnFullScreen').css('display', '');
		$('.navbar').css('display', '');
		$('#cfw-dashboard-control-panel').css('padding', '');
		
		$('#fullscreenButton')
			.removeClass('fullscreened-button');
		
		$('#fullscreenButtonIcon')
			.removeClass('fa-compress')
			.addClass('fa-expand');
		
	}else{
		CFW_DASHBOARD_FULLSCREEN_MODE = true;
		$('.hideOnFullScreen').css('display', 'none');
		$('.navbar').css('display', 'none');
		$('#cfw-dashboard-control-panel').css('padding', '0px');
		
		$('#fullscreenButton')
		.addClass('fullscreened-button');
		$('#fullscreenButtonIcon')
			.removeClass('fa-expand')
			.addClass('fa-compress');
	}
}

/******************************************************************
 * 
 ******************************************************************/
function cfw_dashboard_toggleEditMode(){
	var grid = $('.grid-stack').data('gridstack');
	if(CFW_DASHBOARD_EDIT_MODE){
		CFW_DASHBOARD_EDIT_MODE = false;
		$('.cfw-dashboard-widget-settings').css('display', 'none');
		grid.disable();
		
	}else{
		CFW_DASHBOARD_EDIT_MODE = true;
		$('.cfw-dashboard-widget-settings').css('display', '');
		grid.enable();
	}
}
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
		  var i = 0;
		  for(key in items){
			  var currentItem = items[key].el;

			  var widgetInstance = $(currentItem);
			  var widgetData 	 = widgetInstance.data("widgetData");
			  
			  widgetData.x			= widgetInstance.attr("data-gs-x");
			  widgetData.y		 	= widgetInstance.attr("data-gs-y");
			  widgetData.gswidth	= widgetInstance.attr("data-gs-width");
			  widgetData.gsheight	= widgetInstance.attr("data-gs-height");

		  }
	});
	
}

function addTestdata(){
	
	var rendererTestdata = {
		 	idfield: 'id',
		 	bgstylefield: 'bgstyle',
		 	textstylefield: 'textstyle',
		 	titlefields: ['firstname', 'lastname'],
		 	titledelimiter: ' ',
		 	visiblefields: ['id', 'firstname', 'lastname', 'postal_code', 'status'],
		 	labels: {
		 		id: 'ID'
		 	},
		 	customizers: {
		 		status: function(record, value) { return (value == 'active') ? '<div class="badge badge-success">'+value+'</div>' : '<div class="badge badge-danger">'+value+'</div>' }
		 	},
			actions: [ 
				function (record, id){ return '<button class="btn btn-sm btn-primary" onclick="alert(\'Edit record '+id+'\')"><i class="fas fa-pen"></i></button>'},
				function (record, id){ return '<button class="btn btn-sm btn-danger" onclick="alert(\'Delete record '+id+'\')"><i class="fas fa-trash"></i></button>'},
			],
			multiActions: {
				"Edit": function (elements, records, values){ alert('Edit records '+values.join(',')+'!'); },
				"Delete": function (elements, records, values){ $(elements).remove(); },
			},
			data: [
				{id: 0, firstname: "Jane", lastname: "Doe", city: "Nirwana", postal_code: 8008, status: 'active'},
				{id: 1, firstname: "Testika", lastname: "Testonia", city: "Manhattan", postal_code: 9000, status: 'active', bgstyle: 'success', textstyle: 'dark'},
				{id: 2, firstname: "Theus", lastname: "De Natore", city: "Termi-Nation", postal_code: 666, status: 'blocked', bgstyle: 'danger', textstyle: 'dark'},
				{id: 3, firstname: "Jane", lastname: "De Natore", city: "Termi-Nation", postal_code: 666, status: 'blocked', bgstyle: 'info', textstyle: 'dark'},
			],
		};

		var rendererTestdataMinimal = {
				data: [
					{id: 0, firstname: "Jane", lastname: "Doe", city: "Nirwana", postal_code: 8008, status: 'active'},
					{id: 1, firstname: "Testika", lastname: "Testonia", city: "Manhattan", postal_code: 9000, status: 'active', bgstyle: 'success', textstyle: 'dark'},
					{id: 2, firstname: "Theus", lastname: "De Nator", city: "Termi-Nation", postal_code: 666, status: 'blocked', bgstyle: 'danger', textstyle: 'dark'},
				],
			};
		
	cfw_dashboard_createWidgetByType('cfw_html', {x:0, y:0, gsheight: 2, gswidth: 2, title: "Test Success", bgcolor: "success", textcolor: "light"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:11, y:0, gsheight: 5, gswidth: 2, title: "Test Danger", bgcolor: "danger", textcolor: "light"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:8, y:0, gsheight: 3, gswidth: 2, title: "Test Primary and Object", bgcolor: "primary", textcolor: "light", data: {firstname: "Jane", lastname: "Doe", street: "Fantasyroad 22", city: "Nirwana", postal_code: "8008" }});
	cfw_dashboard_createWidgetByType('cfw_html', {x:7, y:0, gsheight: 5, gswidth: 3, title: "Test Light and Array", bgcolor: "light", textcolor: "secondary", data: ["Test", "Foo", "Bar", 3, 2, 1]});
	cfw_dashboard_createWidgetByType('cfw_html', {x:2, y:0, gsheight: 2, gswidth: 4, title: "Test Matrix", bgcolor: "dark", textcolor: "success", data: "Mister ÄÄÄÄÄÄÄÄÄÄÄnderson."});
	cfw_dashboard_createWidgetByType('cfw_html', {x:9, y:0, gsheight: 2, gswidth: 4, title: "Test Warning", bgcolor: "warning", textcolor: "dark"});
	cfw_dashboard_createWidgetByType('cfw_html', {x:3, y:0, gsheight: 4, gswidth: 5});
	cfw_dashboard_createWidgetByType('cfw_html', {x:0, y:0, gsheight: 3, gswidth: 3});
	cfw_dashboard_createWidgetByType('cfw_html');
	
	cfw_dashboard_createWidgetByType('cfw_table', {x:0, y:0, gsheight: 4, gswidth: 5, title: "Table Test Minimal", 
		settings: {
			tableData: rendererTestdataMinimal 
		}
	});
	
	cfw_dashboard_createWidgetByType('cfw_iframe', {x:6, y:0, gsheight: 4, gswidth: 7, title: "", url: "https://api.jquery.com/data/" });
	
	cfw_dashboard_createWidgetByType('cfw_table', {x:0, y:0, gsheight: 5, gswidth: 5, title: "Table Test Maximal",
		settings: {
			tableData: rendererTestdata
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
	addTestdata();
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		//CFW.http.fetchAndCacheData("./manual", {action: "fetch", item: "menuitems"}, "menuitems", cfw_manual_printMenu);
		
		CFW.ui.toogleLoader(false);
	}, 100);
}

