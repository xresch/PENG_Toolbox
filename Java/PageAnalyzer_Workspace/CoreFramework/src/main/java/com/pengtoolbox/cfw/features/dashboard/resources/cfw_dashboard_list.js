
/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/

var CFW_DASHBOARDLIST_URL = "./list";

/******************************************************************
 * Reset the view.
 ******************************************************************/
function cfw_dashboardlist_reset(){
	
	$("#cfw-container").html("");
}

/******************************************************************
 * Create Role
 ******************************************************************/
function cfw_dashboardlist_createDashboard(){
	
	var html = $('<div id="cfw-usermgmt-createDashboard">');	

	CFW.http.getForm('cfwCreateDashboardForm', html);
	
	CFW.ui.showModal(CFWL('cfw_dashboardlist_createDashboard', 
			CFWL("cfw_dashboardlist_createDashboard", "Create Dashboard")), 
			html, "CFW.cache.clearCache(); cfw_dashboardlist_draw({tab: 'mydashboards'})");
	
}
/******************************************************************
 * Edit Role
 ******************************************************************/
function cfw_dashboardlist_editDashboard(roleID){
	
	var allDiv = $('<div id="cfw-usermgmt">');	

	//-----------------------------------
	// Role Details
	//-----------------------------------
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	detailsDiv.append('<h2>'+CFWL('cfw_dashboardlist_dashboard', "Dashboard")+' Details</h2>');
	allDiv.append(detailsDiv);
	

	CFW.ui.showModal(
			CFWL("cfw_dashboardlist_editDashboard","Edit Dashboard"), 
			allDiv, 
			"CFW.cache.clearCache(); cfw_dashboardlist_draw({tab: 'mydashboards'})"
	);
	
	//-----------------------------------
	// Load Form
	//-----------------------------------
	CFW.http.createForm(CFW_DASHBOARDLIST_URL, {action: "getform", item: "editdashboard", id: roleID}, detailsDiv);
	
}

/******************************************************************
 * Delete
 ******************************************************************/
function cfw_dashboardlist_delete(item, ids){
	
	params = {action: "delete", item: item, ids: ids};
	CFW.http.getJSON(CFW_DASHBOARDLIST_URL, params, 
		function(data) {
			if(data.success){
				//CFW.ui.showSmallModal('Success!', '<span>The selected '+item+' were deleted.</span>');
				//clear cache and reload data
				CFW.cache.data[item] = null;
				cfw_dashboardlist_draw({tab: item});
			}else{
				CFW.ui.showSmallModal("Error!", '<span>The selected '+item+' could <b style="color: red">NOT</b> be deleted.</span>');
			}
	});
}

/******************************************************************
 * Print the list of roles;
 * 
 * @param data as returned by CFW.http.getJSON()
 * @return 
 ******************************************************************/
function cfw_dashboardlist_printDashboardList(data){
	
	parent = $("#cfw-container");
	
	//--------------------------------
	// Button
	var createButton = $('<button class="btn btn-sm btn-success mb-2" onclick="cfw_dashboardlist_createDashboard()">'
							+ '<i class="fas fa-plus-circle"></i> '+ CFWL('cfw_dashboardlist_createDashboard')
					   + '</button>');
	
	parent.append(createButton);
	
	//--------------------------------
	// Table
	
	var cfwTable = new CFWTable();
	cfwTable.addHeaders(['ID', "Name", "Description"]);
	
	if(data.payload != undefined){
		
		var resultCount = data.payload.length;
		if(resultCount == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any dashboards in the list.");
		}

		htmlString = "";
		for(var i = 0; i < resultCount; i++){
			var current = data.payload[i];
			htmlString += '<tr>';
			htmlString += '<td>'+current.PK_ID+'</td>';
			htmlString += '<td>'+current.NAME+'</td>';
			htmlString += '<td>'+current.DESCRIPTION+'</td>';
			
			//Edit Button
			htmlString += '<td><button class="btn btn-primary btn-sm" alt="Edit" title="Edit" '
				+'onclick="cfw_dashboardlist_editDashboard('+current.PK_ID+');">'
				+ '<i class="fa fa-pen"></i>'
				+ '</button></td>';
			
			//Delete Button
			if(current.IS_DELETABLE){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" title="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the dashboard?\', \'Delete\', \'cfw_dashboardlist_delete(\\\'mydashboards\\\','+current.PK_ID+');\')">'
					+ '<i class="fa fa-trash"></i>'
					+ '</button></td>';
			}else{
				htmlString += '<td>&nbsp;</td>';
			}
			
			htmlString += '</tr>';
		}
		
		cfwTable.addRows(htmlString);
		
		cfwTable.appendTo(parent);
	}else{
		CFW.ui.addAlert('error', 'Something went wrong and no users can be displayed.');
	}
}

/******************************************************************
 * Main method for building the different views.
 * 
 * @param options Array with arguments:
 * 	{
 * 		tab: 'users|roles|permissions', 
 *  }
 * @return 
 ******************************************************************/

function cfw_dashboardlist_initialDraw(options){
	CFW.lang.loadLocalization();
	cfw_dashboardlist_draw(options);
}

function cfw_dashboardlist_draw(options){
	
	cfw_dashboardlist_reset();
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		switch(options.tab){
					
			case "mydashboards":	CFW.http.fetchAndCacheData(CFW_DASHBOARDLIST_URL, {action: "fetch", item: "mydashboards"}, "mydashboards", cfw_dashboardlist_printDashboardList);
									break;
									
			default:				CFW.ui.addToastDanger('This tab is unknown: '+options.tab);
		}
		
		CFW.ui.toogleLoader(false);
	}, 100);
}