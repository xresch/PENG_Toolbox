
/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
/******************************************************************
 * Global
 ******************************************************************/
var CFW_MANUAL_COUNTER = 0;
var CFW_MANUAL_GUID_PAGE_MAP = {};

/******************************************************************
 * 
 ******************************************************************/
function cfw_manual_printMenu(data){
	
	console.log(data.payload);
	
	var parent = $('#menu-content');
	var htmlString = '';
	
	var pageArray = data.payload;
	for(var i = 0; i < pageArray.length; i++){
		htmlString += cfw_manual_createMenuItem(pageArray[i]);
	}
	
	parent.append(htmlString);
	
}

/******************************************************************
 * 
 ******************************************************************/
function cfw_manual_createMenuItem(pageData){
	CFW_MANUAL_COUNTER++;
	CFW_MANUAL_GUID_PAGE_MAP[CFW_MANUAL_COUNTER] = pageData;
	
	var collapseID = 'collapse-'+CFW_MANUAL_COUNTER;	
	//-------------------------
	// arrow
	var arrow = '<div class="cfw-fa-box">';
	var dataToggle = '';
	if(pageData.children != null && pageData.children.length > 0){
		dataToggle = ' data-toggle="collapse" data-target="#'+collapseID+'" '
		arrow += '<i class="arrow" '+dataToggle+'></i>';
	}
	arrow += '</div>';
	
	//-------------------------
	// faicon
	var faicon = "";
	if(pageData.faiconClasses != null){
		faicon = '<i class="'+pageData.faiconClasses+'"></i>';
	}
	
	//-------------------------
	// Title
	var onclick = '';
	if(pageData.hasContent){
		onclick = 'onclick="cfw_manual_printContent(this)"';
	}
	
	//-------------------------
	// Put everything together
	var htmlString = '<li>';
	htmlString += arrow+'<a id="'+CFW_MANUAL_COUNTER+'" '+onclick+' '+dataToggle+'>'+faicon+' <span>'+pageData.title+'</span> </a>';
	htmlString += '</li>';
	
	//-------------------------
	// Title
	if(pageData.children != null && pageData.children.length > 0){
		htmlString += '<ul class="sub-menu collapse" id="'+collapseID+'">';
		for(var i = 0; i < pageData.children.length; i++){
			htmlString += cfw_manual_createMenuItem(pageData.children[i]);
		}
		htmlString += '</ul>';
	}
	
	return htmlString;
}

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_manual_printContent(domElement){
	var id = $(domElement).attr('id');
	var page = CFW_MANUAL_GUID_PAGE_MAP[id];
	
	var target = $('#cfw-manual-page-content');
	target.html('');
	
	CFW.http.fetchAndCacheData("./manual", {action: "fetch", item: "page", path: page.path}, "page"+page.path, function (data){
		if(data.payload != undefined){
			var pageData = data.payload;
			target.html(pageData.content);
			target.prepend('<h1>'+pageData.title+'</h1>');
		}
	})
	
	
	
}
/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_manual_draw(){
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		CFW.http.fetchAndCacheData("./manual", {action: "fetch", item: "menuitems"}, "menuitems", cfw_manual_printMenu);
		
		CFW.ui.toogleLoader(false);
	}, 100);
}