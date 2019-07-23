
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
 * Edit user
 ******************************************************************/
function cfw_usermgmt_toogleUserInGroup(button, userID, groupID){
	
	var url = "./usermanagement/data";
	var allDiv = $('<div id="cfw-usermgmt">');	

	//-----------------------------------
	// User Details
	//-----------------------------------
	var params = {action: "update", item: "usergroupmap", userid: userID, groupid: groupID};
	
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	allDiv.append(detailsDiv);
	
	CFW.http.getJSON(url, params, 
		function(data) {
			if(data.success == true){
				
				btn = $(button);
				if(btn.hasClass('btn-success')){
					btn.removeClass('btn-success').addClass('btn-danger');
					btn.find('i').removeClass('fa-check').addClass('fa-ban');
				}else{
					btn.addClass('btn-success').removeClass('btn-danger');
					btn.find('i').addClass('fa-check').removeClass('fa-ban');
				}
				
			}else{
				CFW.ui.showSmallModal("Error!", '<span>The group settings could not be updated!</span>');
			}	
	});
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
	var userFetchParams = {action: "fetch", item: "user", id: userID};
	
	var detailsDiv = $('<div id="cfw-usermgmt-details">');
	allDiv.append(detailsDiv);
	
	CFW.http.getJSON(url, userFetchParams, 
		function(data) {
			if(data.payload != null){
				var htmlString = "";
				htmlString += '<h2>User Details</h2>';
				htmlString += '<p><b>Username:</b> '+data.payload[0].USERNAME+'</p>';
				htmlString += '<p><b>Firstname:</b> '+data.payload[0].FIRSTNAME+'</p>';
				htmlString += '<p><b>Lastname:</b> '+data.payload[0].LASTNAME+'</p>';
				htmlString += '<p><b>Email:</b> '+data.payload[0].EMAIL+'</p>';
				
				detailsDiv.append(htmlString);
			}else{
				CFW.ui.addAlert('error', '<span>The data for the userID '+userID+' could not be loaded.</span>');
			}	
	});
	
	//-----------------------------------
	// Groups
	//-----------------------------------
	var groupFetchParams = {action: "fetch", item: "usergroupmap", id: userID};
	
	var groupDiv = $('<div id="cfw-usermgmt-groups">');
	groupDiv.append('<h2>Groups</h2>');
	allDiv.append(groupDiv);
	
	CFW.http.getJSON(url, groupFetchParams, 
		function(data) {
			if(data.payload != null){
				var htmlString = "";
				htmlString += '';
				var cfwTable = CFW.ui.createTable();
				
				cfwTable.addHeaders(['Name','Description','&nbsp;']);
				var resultCount = data.payload.length;
				if(resultCount == 0){
					CFW.ui.addAlert("info", "Hmm... seems there aren't any groups in the list.");
				}

				for(var i = 0; i < resultCount; i++){
					var current = data.payload[i];
					var row = $('<tr>');
					row.append('<td>'+current.NAME+'</td>'
							  +'<td>'+current.DESCRIPTION+'</td>');
					
					//Toggle Button
					var params = {action: "update", item: "usergroupmap", userid: userID, groupid: current.PK_ID};
					var cfwToggleButton = CFW.ui.createToggleButton(CFW_USRMGMT_URL, params, (current.FK_ID_USER == userID));
					
					var buttonCell = $("<td>");
					cfwToggleButton.appendTo(buttonCell);
					row.append(buttonCell);
					cfwTable.addRow(row);
				}
				
				
				
				cfwTable.appendTo(groupDiv);
				
			}else{
				CFW.ui.addAlert('error', '<span>The data for the userID '+userID+' could not be loaded.</span>');
			}	
		}
	);
	
	CFW.ui.showModal("Edit User", allDiv);
	
	
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
		CFW.ui.addAlert('error', 'Something went wrong and no users can be displayed.');
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
									
			default:				CFW.ui.addAlert("error", "Some error occured, be patient while nobody is looking into it.");
		}
		
		CFW.ui.toogleLoader(false);
	}, 100);
}