
/******************************************************************
 * Contains the functions for the user management
 ******************************************************************/
CFW.cache = { data: {}};
CFW.usermgmt = {};

/******************************************************************
 * Method to fetch data from the server with CFW.http.getJSON(). 
 * The result is cached in the global variable CFW.cache.data[key].
 *
 * @param url
 * @param params the query params as a json object e.g. {myparam: "value", otherkey: "value2"}
 * @param key under which the data will be stored
 * @param callback method which should be called when the data is available.
 * @return nothing
 *
 ******************************************************************/
function fetchAndCacheData(url, params, key, callback){
	//---------------------------------------
	// Fetch and Return Data
	//---------------------------------------
	if (CFW.cache.data[key] == undefined || CFW.cache.data[key] == null){
		CFW.http.getJSON(url, params, 
			function(data) {
				CFW.cache.data[key] = data;	
				if(callback != undefined && callback != null ){
					callback(data);
				}
		});
	}else{
		if(callback != undefined && callback != null){
			callback(CFW.cache.data[key]);
		}
	}
}



/******************************************************************
 * Reset the 
 ******************************************************************/
CFW.usermgmt.reset = function (){
	
	$("#tab-content").html("");
}

/******************************************************************
 * Reset the 
 ******************************************************************/
CFW.usermgmt.delete = function (item, ids){
	
	url = "./usermanagement/data";
	
	params = {action: "delete", item: item, ids: ids};
	CFW.http.getJSON(url, params, 
			function(data) {
				if(data.success){
					CFW.ui.showSmallModal('Success!', '<span>The selected '+item+' were deleted.</span>');
					//clear cache and reload data
					CFW.cache.data[item] = null;
					CFW.usermgmt.draw({tab: item});
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
CFW.usermgmt.printUserList = function (data){
	
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
			htmlString += '<td><button class="btn btn-primary btn-sm" alt="Edit" '
				+'onclick="todo();">'
				+ '<i class="fa fa-pen"></i>'
				+ '</button></td>';
			
			//Delete Button
			if(current.IS_DELETABLE.toLowerCase() == "true"){
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the user?\', \'Delete\', \'CFW.usermgmt.delete(\\\'users\\\','+current.PK_ID+');\')">'
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
CFW.usermgmt.printGroupList = function (data){
	
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
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the group?\', \'Delete\', \'CFW.usermgmt.delete(\\\'groups\\\','+current.PK_ID+');\')">'
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
CFW.usermgmt.printPermissionList = function (data){
	
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
				htmlString += '<td><button class="btn btn-danger btn-sm" alt="Delete" '
					+'onclick="CFW.ui.confirmExecute(\'Do you want to delete the group?\', \'Delete\', \'CFW.usermgmt.delete(\\\'permissions\\\','+current.PK_ID+');\')">'
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
CFW.usermgmt.draw = function (options){
	
	CFW.usermgmt.reset();
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		url = "./usermanagement/data"
		switch(options.tab){
		
			case "users":			fetchAndCacheData(url, {action: "fetch", item: "users"}, "users", CFW.usermgmt.printUserList);
									break;
									
			case "groups":			fetchAndCacheData(url, {action: "fetch", item: "groups"}, "groups", CFW.usermgmt.printGroupList);
									break;
									
			case "permissions":		fetchAndCacheData(url, {action: "fetch", item: "permissions"}, "permissions", CFW.usermgmt.printPermissionList);
									break;	
									
			default:				CFW.ui.addAlert("error", "Some error occured, be patient while nobody is looking into it.");
		}
		
		CFW.ui.toogleLoader(false);
	}, 100);
}