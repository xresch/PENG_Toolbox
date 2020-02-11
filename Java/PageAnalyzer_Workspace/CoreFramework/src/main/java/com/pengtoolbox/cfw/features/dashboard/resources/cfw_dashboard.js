
var CFW_DASHBOARD_WIDGET_GUID = 0;

function cfw_dashboard_createWidget(options){

	var defaultOptions = {
			guid: ++CFW_DASHBOARD_WIDGET_GUID,
			widgetID: null,
			title: "",
			body: "",
			footer: "",
			color: "",
			textColor: "",
	}
	
	var merged = Object.assign({}, defaultOptions, options);
	
	var htmlString =
		'<div class="grid-stack-item" data-gs-width="6" data-gs-height="3">'
		+'    <div class="grid-stack-item-content card bg-'+merged.color+' text-'+merged.textColor+'">'
		+'     	<div class="card-body h-100">'
		+'     	  '
		+'     	  <div class="card-title">'
		+'		  	<h5>'+merged.title+'</h5>'
		+'			  <a type="button" role ="button" class="cfw-dashboard-widget-settings" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
		+'			    <i class="fas fa-cog"></i>'
		+'			  </a>'
		+'			  <div class="dropdown-menu">'
		+'			    <a class="dropdown-item" href="#">Settings</a>'
		+'				<div class="dropdown-divider"></div>'
		+'				<a class="dropdown-item text-danger" href="#">Remove</a>'
		+'			  </div>'
		+'		  </div>'
		+''
		+'		  <div>'+merged.body+'</div>'
		+'		 <div class="cfw-dashboard-widget-footer">'
		+			merged.footer
		+'		  </div>'
		+'		</div>'
		+'    </div>'
		+'</div>';
	
	return htmlString;
}

function cfw_dashboard_addNewWidget() {
	
	var widgetHTML =
		cfw_dashboard_createWidget(
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