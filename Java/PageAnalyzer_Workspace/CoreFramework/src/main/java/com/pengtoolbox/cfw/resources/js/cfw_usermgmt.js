
/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/

var CFW_USRMGMT_URL = "./usermanagement/data";

/******************************************************************
 * Reset the view.
 ******************************************************************/
function cfw_usermgmt_reset(){
	
	$("#tab-content").html("");
}

/******************************************************************
 * 
 ******************************************************************/
function cfw_usermgmt_createToggleTable(parent, mapName, itemID){

	CFW.http.getJSON(CFW_USRMGMT_URL, {action: "fetch", item: mapName, id: itemID}, 
		function(data) {
			if(data.payload != null){
				var htmlString = "";
				htmlString += '';
				var cfwTable = CFW.ui.createTable();
				
				cfwTable.addHeaders(['&nbsp;','Name','Description']);
				var resultCount = data.payload.length;
				if(resultCount == 0){
					CFW.ui.addAlert("info", "Hmm... seems there aren't any roles in the list.");
				}

				for(var i = 0; i < resultCount; i++){
					var current = data.payload[i];
					var row = $('<tr>');
					
					//Toggle Button
					var params = {action: "update", item: mapName, itemid: itemID, listitemid: current.PK_ID};
					var cfwToggleButton = CFW.ui.createToggleButton(CFW_USRMGMT_URL, params, (current.ITEM_ID == itemID));
					
					if(current.IS_DELETABLE != null && !current.IS_DELETABLE){
						cfwToggleButton.setLocked();
					}
					var buttonCell = $("<td>");
					cfwToggleButton.appendTo(buttonCell);
					row.append(buttonCell);
					
					row.append('<td>'+current.NAME+'</td>'
							  +'<td>'+current.DESCRIPTION+'</td>');
					
					cfwTable.addRow(row);
				}
				
				
				cfwTable.appendTo(parent);
				
			}else{
				CFW.ui.addAlert('error', '<span>The '+mapName+' data for the id '+itemID+' could not be loaded.</span>');
			}	
		}
	);
}

/******************************************************************
 * Create user
 ******************************************************************/
function cfw_usermgmt_createUser(){
	
	var html = $('<div id="cfw-usermgmt-createUser">');	

	CFW.http.getForm('cfwCreateUserForm', html);
	
	CFW.ui.showModal("Create User", html, "CFW.cache.clearCache(); cfw_usermgmt_draw({tab: 'users'})");
	
}

/******************************************************************
 * Edit user
 ******************************************************************/
function cfw_usermgmt_editUser(userID){
	
	var url = "./usermanagement/data";
	var allDiv = $('<div id="cfw-usermgmt">');	

	//-----------------------------------
	// User Details
	//-----------------------------------
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	detailsDiv.append('<h2>User Details</h2>');
	allDiv.append(detailsDiv);
	
	//-----------------------------------
	// Roles
	//-----------------------------------
	var roleDiv = $('<div id="cfw-usermgmt-roles">');
	roleDiv.append('<h2>Roles</h2>');
	allDiv.append(roleDiv);
	
	cfw_usermgmt_createToggleTable(roleDiv, "userrolemap", userID)
	
	CFW.ui.showModal("Edit User", allDiv, "CFW.cache.clearCache(); cfw_usermgmt_draw({tab: 'users'})");
	
	//-----------------------------------
	// Load Form
	//-----------------------------------
	CFW.http.createForm(CFW_USRMGMT_URL, {action: "getform", item: "edituser", id: userID}, detailsDiv);
	
}

/******************************************************************
 * ResetPassword
 ******************************************************************/
function cfw_usermgmt_resetPassword(userID){
	
	var url = "./usermanagement/data";
	var allDiv = $('<div id="cfw-usermgmt">');	

	//-----------------------------------
	// User Details
	//-----------------------------------
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	allDiv.append(detailsDiv);
		
	
	CFW.ui.showModal("Reset Password", allDiv);
	
	//-----------------------------------
	// Load Form
	//-----------------------------------
	CFW.http.createForm(CFW_USRMGMT_URL, {action: "getform", item: "resetpw", id: userID}, detailsDiv);
	
}

/******************************************************************
 * Create Role
 ******************************************************************/
function cfw_usermgmt_createRole(){
	
	var html = $('<div id="cfw-usermgmt-createRole">');	

	CFW.http.getForm('cfwCreateRoleForm', html);
	
	CFW.ui.showModal("Create Role", html, "CFW.cache.clearCache(); cfw_usermgmt_draw({tab: 'roles'})");
	
}
/******************************************************************
 * Edit Role
 ******************************************************************/
function cfw_usermgmt_editRole(roleID){
	
	var allDiv = $('<div id="cfw-usermgmt">');	

	//-----------------------------------
	// Role Details
	//-----------------------------------
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	detailsDiv.append('<h2>Role Details</h2>');
	allDiv.append(detailsDiv);
	
	//-----------------------------------
	// Roles
	//-----------------------------------
	var roleDiv = $('<div id="cfw-usermgmt-roles">');
	roleDiv.append('<h2>Roles</h2>');
	allDiv.append(roleDiv);
	
	cfw_usermgmt_createToggleTable(roleDiv, "rolepermissionmap", roleID)
	
	CFW.ui.showModal("Edit Role", allDiv, "CFW.cache.clearCache(); cfw_usermgmt_draw({tab: 'roles'})");
	
	//-----------------------------------
	// Load Form
	//-----------------------------------
	CFW.http.createForm(CFW_USRMGMT_URL, {action: "getform", item: "editrole", id: roleID}, detailsDiv);
	
}

/******************************************************************
 * Delete
 ******************************************************************/
function cfw_usermgmt_delete(item, ids){
	
	url = "./usermanagement/data";
	
	params = {action: "delete", item: item, ids: ids};
	CFW.http.getJSON(url, params, 
		function(data) {
			if(data.success){
				//CFW.ui.showSmallModal('Success!', '<span>The selected '+item+' were deleted.</span>');
				//clear cache and reload data
				CFW.cache.data[item] = null;
				cfw_usermgmt_draw({tab: item});
			}else{
				CFW.ui.showSmallModal("Error!", '<span>The selected '+item+' could <b style="color: red">NOT</b> be deleted.</span>');
			}
	});
}

/******************************************************************
 * Print the list of users;
 * 
 * @param data as returned by CFW.http.getJSON()
 * @return 
 ******************************************************************/
function cfw_usermgmt_printUserList(data){
	
	parent = $("#tab-content");
	
	var cfwTable = CFW.ui.createTable();
	
	//--------------------------------
	// Button
	var createButton = $('<button class="btn btn-success mb-2" alt="Create" title="Create" onclick="cfw_usermgmt_createUser()">'
							+ '<i class="fas fa-plus-circle"></i> Create User'
					   + '</button>');
	
	parent.append(createButton);
	
	//--------------------------------
	// Table
	cfwTable.addHeaders(['ID', 'Username', "eMail", "Firstname", "Lastname", "Status", "Date Created", "&nbsp;", "&nbsp;", "&nbsp;"]);
	
	if(data.payload != undefined){
		
		var resultCount = data.payload.length;
		if(resultCount == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any users in the list.");
		}

		
		htmlString = "";
		for(var i = 0; i < resultCount; i++){
			var current = data.payload[i];
			htmlString += '<tr>';
			htmlString += '<td>'+current.PK_ID+'</td>';
			htmlString += '<td>'+current.USERNAME+'</td>';
			htmlString += '<td>'+current.EMAIL+'</td>';
			htmlString += '<td>'+current.FIRSTNAME+'</td>';
			htmlString += '<td>'+current.LASTNAME+'</td>';
			htmlString += '<td><span class="badge badge-'+((current.STATUS.toLowerCase() == "active")? 'success' : 'danger') +'">'+current.STATUS+'</td>';
			htmlString += '<td>'+current.DATE_CREATED+'</td>';
			
			//Reset Password Button
			if(!current.IS_FOREIGN){
				htmlString += '<td><button class="btn btn-warning btn-sm" alt="Reset Password" title="Reset Password" '
					+'onclick="cfw_usermgmt_resetPassword('+current.PK_ID+');">'
					+ '<i class="fas fa-unlock-alt"></i>'
					+ '</button></td>';
			}else{
				htmlString += '<td></td>';
			}
			
			//Edit Button
			
			htmlString += '<td><button class="btn btn-primary btn-sm" alt="Edit" title="Edit" '
				+'onclick="cfw_usermgmt_editUser('+current.PK_ID+');">'
				+ '<i class="fa fa-pen"></i>'
				+ '</button>&nbsp;</td>';
			
			//Delete Button
			if(current.IS_DELETABLE){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" title="Delete"  '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the user?\', \'Delete\', \'cfw_usermgmt_delete(\\\'users\\\','+current.PK_ID+');\')">'
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
		CFW.ui.addToastDanger('Something went wrong and no users can be displayed.');
		
	}
}

/******************************************************************
 * Print the list of roles;
 * 
 * @param data as returned by CFW.http.getJSON()
 * @return 
 ******************************************************************/
function cfw_usermgmt_printRoleList(data){
	
	parent = $("#tab-content");
	
	//--------------------------------
	// Button
	var createButton = $('<button class="btn btn-success mb-2" alt="Create" title="Delete" onclick="cfw_usermgmt_createRole()">'
							+ '<i class="fas fa-plus-circle"></i> Create Role'
					   + '</button>');
	
	parent.append(createButton);
	
	//--------------------------------
	// Table
	
	var cfwTable = CFW.ui.createTable();
	cfwTable.addHeaders(['ID', "Name", "Description"]);
	
	if(data.payload != undefined){
		
		var resultCount = data.payload.length;
		if(resultCount == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any roles in the list.");
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
				+'onclick="cfw_usermgmt_editRole('+current.PK_ID+');">'
				+ '<i class="fa fa-pen"></i>'
				+ '</button></td>';
			
			//Delete Button
			if(current.IS_DELETABLE){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" title="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the role?\', \'Delete\', \'cfw_usermgmt_delete(\\\'roles\\\','+current.PK_ID+');\')">'
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
 * Print the list of permissions;
 * 
 * @param data as returned by CFW.http.getJSON()
 * @return 
 ******************************************************************/
function cfw_usermgmt_printPermissionList(data){
	
	parent = $("#tab-content");
	
	var cfwTable = CFW.ui.createTable();
	cfwTable.addHeaders(['ID', "Name", "Description"]);
	
	if(data.payload != undefined){
		
		var resultCount = data.payload.length;
		if(resultCount == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any permissions in the list.");
		}

		htmlString = "";
		for(var i = 0; i < resultCount; i++){
			var current = data.payload[i];
			htmlString += '<tr>';
			htmlString += '<td>'+current.PK_ID+'</td>';
			htmlString += '<td>'+current.NAME+'</td>';
			htmlString += '<td>'+current.DESCRIPTION+'</td>';
			
			//Delete Button
			if(current.IS_DELETABLE){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" title="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the role?\', \'Delete\', \'cfw_usermgmt_delete(\\\'permissions\\\','+current.PK_ID+');\')">'
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
function cfw_usermgmt_draw(options){
	
	cfw_usermgmt_reset();
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		url = "./usermanagement/data"
		switch(options.tab){
		
			case "users":			CFW.http.fetchAndCacheData(url, {action: "fetch", item: "users"}, "users", cfw_usermgmt_printUserList);
									break;
									
			case "roles":			CFW.http.fetchAndCacheData(url, {action: "fetch", item: "roles"}, "roles", cfw_usermgmt_printRoleList);
									break;
									
			case "permissions":		CFW.http.fetchAndCacheData(url, {action: "fetch", item: "permissions"}, "permissions", cfw_usermgmt_printPermissionList);
									break;	
									
			default:				CFW.ui.addToastDanger('This tab is unknown: '+options.tab);
		}
		
		CFW.ui.toogleLoader(false);
	}, 100);
}