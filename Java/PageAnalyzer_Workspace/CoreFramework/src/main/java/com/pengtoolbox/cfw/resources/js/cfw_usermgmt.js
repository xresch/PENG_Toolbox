
/******************************************************************
 * Contains the functions for the user management
 ******************************************************************/
CFW.DATA = {};
CFW.usermgmt = {};

/******************************************************************
 * Method to fetch data from the server with CFW.http.getJSON(). 
 * The result is cached in the global variable CFW.DATA[key].
 *
 * @param url
 * @param params the query params as a json object e.g. {myparam: "value", otherkey: "value2"}
 * @param key under which the data will be stored
 * @param callback method which should be called when the data is available.
 * @return nothing
 *
 ******************************************************************/
function fetchAndCacheData(url, params, key, callback){
	console.log("fetchAndCacheData-A");
	//---------------------------------------
	// Fetch and Return Data
	//---------------------------------------
	if (CFW.DATA[key] == undefined || CFW.DATA[key] == null){
		console.log("fetchAndCacheData-B");
		CFW.http.getJSON(url, params, 
			function(data) {
			console.log("fetchAndCacheData-C");
				CFW.DATA[key] = data;	
				if(callback != undefined && callback != null ){
					callback(data);
				}
		});
	}else{
		console.log("fetchAndCacheData-D");
		if(callback != undefined && callback != null){
			console.log("fetchAndCacheData-E");
			callback(CFW.DATA[key]);
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
 * Print the list of users;
 * 
 * @param data as returned by CFW.http.getJSON()
 * @return 
 ******************************************************************/
CFW.usermgmt.printUserList = function (data){
	
	console.log("printUserList");
	parent = $("#tab-content");
	
	var cfwTable = CFW.ui.createTable();
	cfwTable.addHeaders(['Username', "eMail", "Firstname", "Lastname", "Status", "Date Created"]);
	
	if(data.payload != undefined){
		
		var resultCount = data.payload.length;
		if(resultCount == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any users in the list.");
		}

		htmlString = "";
		for(var i = 0; i < resultCount; i++){
			var current = data.payload[i];
			htmlString += '<tr>';
			htmlString += '<td>'+current.USERNAME+'</td>';
			htmlString += '<td>'+current.EMAIL+'</td>';
			htmlString += '<td>'+current.FIRSTNAME+'</td>';
			htmlString += '<td>'+current.LASTNAME+'</td>';
			htmlString += '<td>'+current.STATUS+'</td>';
			htmlString += '<td>'+current.DATE_CREATED+'</td>';
			
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
	
	console.log("DRAW");
	CFW.usermgmt.reset();
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		url = "./usermanagement/data"
		switch(options.tab){
		
			case "users":			fetchAndCacheData(url, {action: "fetch"}, "allusers", CFW.usermgmt.printUserList);
									break;
											
			default:				CFW.ui.addAlert("error", "Some error occured, be patient while nobody is looking into it.");
		}
		
		CFW.ui.toogleLoader(false);
	}, 100);
}