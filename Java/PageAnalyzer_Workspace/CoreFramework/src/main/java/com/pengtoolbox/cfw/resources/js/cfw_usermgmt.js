
/******************************************************************
 * Contains the functions for the user management
 ******************************************************************/

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
					CFW.ui.addAlert("info", "Hmm... seems there aren't any groups in the list.");
				}

				for(var i = 0; i < resultCount; i++){
					var current = data.payload[i];
					var row = $('<tr>');
					
					//Toggle Button
					var params = {action: "update", item: mapName, itemid: itemID, listitemid: current.PK_ID};
					var cfwToggleButton = CFW.ui.createToggleButton(CFW_USRMGMT_URL, params, (current.ITEM_ID == itemID));
					
					if(current.IS_DELETABLE == "FALSE"){
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
				CFW.ui.addAlert('error', '<span>The data for the userID '+userID+' could not be loaded.</span>');
			}	
		}
	);
}

/******************************************************************
 * Create user
 ******************************************************************/
function cfw_usermgmt_createUser(){
	
	var html = $('<div id="cfw-usermgmt-createUser">');	

	CFW.http.getForm('cfw-createUserForm', html);
	
	CFW.ui.showModal("Create User", html);
	
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
	// Groups
	//-----------------------------------
	var groupDiv = $('<div id="cfw-usermgmt-groups">');
	groupDiv.append('<h2>Groups</h2>');
	allDiv.append(groupDiv);
	
	cfw_usermgmt_createToggleTable(groupDiv, "usergroupmap", userID)
	
	CFW.ui.showModal("Edit User", allDiv);
	
	//-----------------------------------
	// Load Form
	//-----------------------------------
	CFW.http.createForm(CFW_USRMGMT_URL, {action: "getform", item: "edituser", id: userID}, detailsDiv);
	
	
}

/******************************************************************
 * Create Group
 ******************************************************************/
function cfw_usermgmt_createGroup(){
	
	var html = $('<div id="cfw-usermgmt-createGroup">');	

	CFW.http.getForm('cfw-createGroupForm', html);
	
	CFW.ui.showModal("Create Group", html);
	
}
/******************************************************************
 * Edit Group
 ******************************************************************/
function cfw_usermgmt_editGroup(groupID){
	
	var allDiv = $('<div id="cfw-usermgmt">');	

	//-----------------------------------
	// Group Details
	//-----------------------------------
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	detailsDiv.append('<h2>Group Details</h2>');
	allDiv.append(detailsDiv);
	
	//-----------------------------------
	// Groups
	//-----------------------------------
	var groupDiv = $('<div id="cfw-usermgmt-groups">');
	groupDiv.append('<h2>Groups</h2>');
	allDiv.append(groupDiv);
	
	cfw_usermgmt_createToggleTable(groupDiv, "grouppermissionmap", groupID)
	
	CFW.ui.showModal("Edit Group", allDiv);
	
	//-----------------------------------
	// Load Form
	//-----------------------------------
	CFW.http.createForm(CFW_USRMGMT_URL, {action: "getform", item: "editgroup", id: groupID}, detailsDiv);
	
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
	var createButton = $('<button class="btn btn-success mb-2" alt="Create" title="Delete" onclick="cfw_usermgmt_createUser()">'
							+ '<i class="fas fa-plus-circle"></i> Create User'
					   + '</button>');
	
	parent.append(createButton);
	
	//--------------------------------
	// Table
	cfwTable.addHeaders(['ID', 'Username', "eMail", "Firstname", "Lastname", "Status", "Date Created", "&nbsp;", "&nbsp;"]);
	
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
			htmlString += '<td>'+current.STATUS+'</td>';
			htmlString += '<td>'+current.DATE_CREATED+'</td>';
			
			//Edit Button
			htmlString += '<td><button class="btn btn-primary btn-sm" alt="Edit" title="Edit" '
				+'onclick="cfw_usermgmt_editUser('+current.PK_ID+');">'
				+ '<i class="fa fa-pen"></i>'
				+ '</button></td>';
			
			//Delete Button
			if(current.IS_DELETABLE.toLowerCase() == "true"){
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
 * Print the list of groups;
 * 
 * @param data as returned by CFW.http.getJSON()
 * @return 
 ******************************************************************/
function cfw_usermgmt_printGroupList(data){
	
	parent = $("#tab-content");
	
	//--------------------------------
	// Button
	var createButton = $('<button class="btn btn-success mb-2" alt="Create" title="Delete" onclick="cfw_usermgmt_createGroup()">'
							+ '<i class="fas fa-plus-circle"></i> Create Group'
					   + '</button>');
	
	parent.append(createButton);
	
	//--------------------------------
	// Table
	
	var cfwTable = CFW.ui.createTable();
	cfwTable.addHeaders(['ID', "Name", "Description"]);
	
	if(data.payload != undefined){
		
		var resultCount = data.payload.length;
		if(resultCount == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any groups in the list.");
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
				+'onclick="cfw_usermgmt_editGroup('+current.PK_ID+');">'
				+ '<i class="fa fa-pen"></i>'
				+ '</button></td>';
			
			//Delete Button
			if(current.IS_DELETABLE.toLowerCase() == "true"){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" title="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the group?\', \'Delete\', \'cfw_usermgmt_delete(\\\'groups\\\','+current.PK_ID+');\')">'
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
			if(current.IS_DELETABLE.toLowerCase() == "true"){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" title="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the group?\', \'Delete\', \'cfw_usermgmt_delete(\\\'permissions\\\','+current.PK_ID+');\')">'
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
 * 		tab: 'users|groups|permissions', 
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
									
			case "groups":			CFW.http.fetchAndCacheData(url, {action: "fetch", item: "groups"}, "groups", cfw_usermgmt_printGroupList);
									break;
									
			case "permissions":		CFW.http.fetchAndCacheData(url, {action: "fetch", item: "permissions"}, "permissions", cfw_usermgmt_printPermissionList);
									break;	
									
			default:				CFW.ui.addToastDanger('This tab is unknown: '+options.tab);
		}
		
		CFW.ui.toogleLoader(false);
	}, 100);
}