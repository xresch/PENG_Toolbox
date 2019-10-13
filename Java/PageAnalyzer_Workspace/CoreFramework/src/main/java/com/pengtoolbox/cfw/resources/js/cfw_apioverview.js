
/******************************************************************
 * Global
 ******************************************************************/
var MODAL_CURRENT_NAME = "";
var MODAL_CURRENT_ACTION = "";

/******************************************************************
 * 
 ******************************************************************/
function cfw_apioverview_formResult(data, status, xhr){
	
	//-------------------------------
	// URL
	form = $('#cfw-apioverview-samplemodal form');
	serialized = form.serialize();
	
	//-------------------------------
	// Regex hack remove empty params
	serialized = serialized.replace(/cfw-formID.*?&/g, "&");
	console.log(serialized)
	serialized = serialized.replace(/&[^=]+=&/g, "&");
	console.log(serialized)
	serialized = serialized.replace(/&[^=]+=&/g, "&");
	console.log(serialized)
	serialized = serialized.replace(/&[^=]+=&/g, "&");
	console.log(serialized)
	serialized = serialized.replace(/&[^=]+=$/g, "&");
	console.log(serialized)
	
	sampleURL = $('#cfw-apioverview-sampleurl');
	
	var url = window.location.href 
			+ "?apiName="+MODAL_CURRENT_NAME
			+ "&actionName="+MODAL_CURRENT_ACTION
			+ serialized;
	sampleURL.html('<a target="_blank" href="'+url+'">'+url+'</a>');

	hljs.highlightBlock(sampleURL.get(0));
	
	//-------------------------------
	// Sample CURL
	curl = $('#cfw-apioverview-samplecurl');
	cookie = CFW.http.readCookie("JSESSIONID");
	curlString = 'curl -H "Cookie: JSESSIONID='+cookie+'" -X GET "'+url+'"';
	curl.text(curlString);
	hljs.highlightBlock(curl.get(0));
	//-------------------------------
	// Sample Response
	responseElement = $('#cfw-apioverview-response');
	responseElement.html('');
    
	var contentType = xhr.getResponseHeader("content-type") || "";
    if (contentType.indexOf('json') > -1) {
    	responseElement.text(JSON.stringify(data, null, 2));
    }else{
    	//responseElement.text(data.replace(/\r\n/g, "<br/>"));
    	responseElement.text(data);
    }
	
	
	responseElement.text();
	
	hljs.highlightBlock(responseElement.get(0));
}

/******************************************************************
 * Edit user
 ******************************************************************/
function cfw_apioverview_createExample(apiName, actionName){
	
	MODAL_CURRENT_NAME = apiName;
	MODAL_CURRENT_ACTION = actionName;
	var allDiv = $('<div id="cfw-apioverview-samplemodal">');	

	//-----------------------------------
	// User Details
	//-----------------------------------
	var formDiv = $('<div id="cfw-apioverview-sampleform">');
	formDiv.append('<h2>Sample Form</h2>');
	allDiv.append(formDiv);
	
	//-----------------------------------
	// Placeholders
	//-----------------------------------
	allDiv.append('<h4>URL:</h4>');
	allDiv.append('<pre class="m-3" style="height: 50px;" ><code id="cfw-apioverview-sampleurl"></code></pre>');
	
	allDiv.append('<h4>CURL:</h4>');
	allDiv.append('<pre class="m-3" style="height: 50px;" ><code id="cfw-apioverview-samplecurl"></code></pre>');
	
	allDiv.append('<h4>Response:</h4>');
	allDiv.append('<pre class="m-3" style="max-height: 400px; display:block; white-space:pre-wrap" ><code id="cfw-apioverview-response"></code></pre>');
	//-----------------------------------
	//Show Modal and Load Form
	//-----------------------------------
	CFW.ui.showModal("Example for "+apiName+": "+actionName, allDiv);
	
	CFW.http.createForm("./api", {formName: apiName, actionName: actionName, callbackMethod: "cfw_apioverview_formResult"}, formDiv);
	
}

/******************************************************************
 * Print the overview of the apis .
 * 
 ******************************************************************/
function cfw_apioverview_printOverview(data){
	
	parent = $("#cfw-container");
	
	parent.append("<h1>API Overview</h1>");
	
	if(data.payload != undefined){
		
		//--------------------------------
		// Initialization
		var panels = {}
		var count = data.payload.length;
		if(count == 0){
			CFW.ui.addAlert("info", "Hmm... seems there aren't any APIs in the list.");
		}

		//--------------------------------
		// Create Data Structure
		for(var i = 0; i < count; i++){
			current = data.payload[i];
			name = current.name;
			action = current.action;
			if(panels[name] == undefined){
				panels[name] = {}
			}
			
			if(panels[name][action] == undefined){
				panels[name][action] = {
						description: current.description,
						params: current.params,
						returnValues: current.returnValues
				}
			}else{
				CFW.ui.addToastDanger("The action '"+action+"' seems to be defined multiple times for the same API name: '"+name+"'");
			}
		}
		
		//--------------------------------
		// Create Panels
		
		for(name in panels){
			current = panels[name];
			cfwPanel = new CFWPanel('primary');
			cfwPanel.title = name;
			cfwPanel.body = $('<div>');
			
			for(action in current){
				sub = current[action];
				
				//----------------------------------------
				// Create Panel Content
				//----------------------------------------
				var content = $('<div>');
				content.append('<p>'+sub.description+'</p>');
				
				
				//----------------------------
				// Create Parameter Table
				
				content.append('<h3>Parameters:</h3>');
				
				var cfwTable = CFW.ui.createTable();
				cfwTable.isNarrow = true;
				cfwTable.isHover = true;
				cfwTable.isResponsive = true;
				cfwTable.isStriped = true;
				cfwTable.filter(false);
				
				cfwTable.addHeaders(['Name','Type','Description']);
				
				htmlRows = '';
				for(var j = 0; j < sub.params.length; j++){
					//{"name": "pk_id", "type": "Integer", "description": "null"}
					paramDef = sub.params[j];
					htmlRows += '<tr>'
					htmlRows += '<td>'+paramDef.name+'</td>';
					htmlRows += '<td>'+paramDef.type+'</td>';
					htmlRows += '<td>'+((paramDef.description != "null") ? paramDef.description : '') +'</td>';
					htmlRows += '</tr>'
					
				}

				cfwTable.addRows(htmlRows);
				cfwTable.appendTo(content);
				
				//----------------------------
				// Create Return Value
				content.append('<h3>Return Values:</h3>');
				
				var returnTable = CFW.ui.createTable();
				returnTable.isNarrow = true;
				returnTable.isHover = true;
				returnTable.isResponsive = true;
				returnTable.isStriped = true;
				returnTable.filter(false);
				
				returnTable.addHeaders(['Name','Type','Description']);
				
				htmlRows = '';
				for(var j = 0; j < sub.params.length; j++){
					//{"name": "pk_id", "type": "Integer", "description": "null"}
					returnValue = sub.returnValues[j];
					htmlRows += '<tr>'
					htmlRows += '<td>'+returnValue.name+'</td>';
					htmlRows += '<td>'+returnValue.type+'</td>';
					htmlRows += '<td>'+((returnValue.description != "null") ? returnValue.description : '') +'</td>';
					htmlRows += '</tr>'
					
				}

				returnTable.addRows(htmlRows);
				returnTable.appendTo(content);

				//----------------------------
				// Create Example Button
				content.append('<button class="btn btn-primary" onclick="cfw_apioverview_createExample(\''+name+'\', \''+action+'\')">Example</button>');
				
				//----------------------------------------
				// Create Panel
				//----------------------------------------
				
				subPanel = new CFWPanel('success');
				subPanel.title = action;
				subPanel.body = content;
				subPanel.appendTo(cfwPanel.body);

			}
			
			cfwPanel.appendTo(parent);
		}


	}else{
		CFW.ui.addToastDanger('Something went wrong and no APIs can be displayed.');
		
	}
}

/******************************************************************
 * Main method for building the view.
 * 
 ******************************************************************/
function cfw_apioverview_draw(){
	
	CFW.ui.toogleLoader(true);
	
	window.setTimeout( 
	function(){

		CFW.http.fetchAndCacheData("./api", {overviewdata: "fetch"}, "api_definitions", cfw_apioverview_printOverview);
		
		CFW.ui.toogleLoader(false);
	}, 100);
}