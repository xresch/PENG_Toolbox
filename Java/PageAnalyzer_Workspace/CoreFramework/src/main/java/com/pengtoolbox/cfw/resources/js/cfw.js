/**************************************************************************************
 * CFW.js
 * ======
 * Javascript library for the CFW framework.
 * 
 * @author Reto Scheiwiller, 2019
 *************************************************************************************/

/**************************************************************************************
 * Filter the rows of a table by the value of the search field.
 * This method is best used by triggering it on the onchange-event on the search field
 * itself.
 * The search field has to have an attached JQuery data object($().data(name, value)), ¨
 * pointing to the table that should be filtered.
 * 
 * @param searchField 
 * @return nothing
 *************************************************************************************/
function cfw_filterTable(searchField){
	
	var table = $(searchField).data("table");
	var input = searchField;
	
	filter = input.value.toUpperCase();

	table.find("tbody tr, >tr").each(function( index ) {
		  //console.log( index + ": " + $(this).text() );
		  
		  if ($(this).html().toUpperCase().indexOf(filter) > -1) {
			  $(this).css("display", "");
		  } else {
			  $(this).css("display", "none");
			}
	});

}
/******************************************************************
 * Print the list of results found in the database.
 * 
 * @param parent JQuery object
 * @param data object containing the list of results.
 * 
 ******************************************************************/
 class CFWTable{
	
	 constructor(){
		 this.table = $('<table class="table">');
		 
		 this.thead = $('<thead>');
		 this.table.append(this.thead);
		 
		 this.tbody = $('<tbody>');
		 this.table.append(this.tbody);
		 
		 this.tableFilter = true;
		 this.isResponsive = true;
		 this.isHover = true;
		 this.isStriped = true;
		 this.isNarrow = false;
	 }
	
	 /********************************************
	  * Toggle the table filter, default is true.
	  ********************************************/
	 filter(isFilter){
		 this.tableFilter = isFilter;
	 }
	 /********************************************
	  * Toggle the table filter, default is true.
	  ********************************************/
	 responsive(isResponsive){
		 this.isResponsive = isResponsive;
	 }
	 
	 /********************************************
	  * Adds a header using a string.
	  ********************************************/
	 addHeader(label){
		 this.thead.append('<th>'+label+'</th>');
	 }
	 
	 /********************************************
	  * Adds headers using a string array.
	  ********************************************/
	 addHeaders(stringArray){
		 
		 var htmlString = "";
		 for(var i = 0; i < stringArray.length; i++){
			 htmlString += '<th>'+stringArray[i]+'</th>';
		 }
		 this.thead.append(htmlString);
	 }
	 
	 /********************************************
	  * Adds a row using a html string or a 
	  * jquery object .
	  ********************************************/
	 addRow(htmlOrJQueryObject){
		 this.tbody.append(htmlOrJQueryObject);
	 }
	 
	 /********************************************
	  * Adds rows using a html string or a 
	  * jquery object .
	  ********************************************/
	 addRows(htmlOrJQueryObject){
		 this.tbody.append(htmlOrJQueryObject);
	 }
	 
	 /********************************************
	  * Append the table to the jquery object.
	  * @param parent JQuery object
	  ********************************************/
	 appendTo(parent){
		  
		 if(this.isStriped){		this.table.addClass('table-striped'); }
		 if(this.isHover){			this.table.addClass('table-hover'); }
		 if(this.isNarrow){			this.table.addClass('table-sm'); }
		 
		 if(this.tableFilter){
			 var filter = $('<input type="text" class="form-control" onkeyup="cfw_filterTable(this)" placeholder="Filter Table...">');
			 parent.append(filter);
			 //jqueryObject.append('<span style="font-size: xx-small;"><strong>Hint:</strong> The filter searches through the innerHTML of the table rows. Use &quot;&gt;&quot; and &quot;&lt;&quot; to search for the beginning and end of a cell content(e.g. &quot;&gt;Test&lt;&quot; )</span>');
			 filter.data("table", this.table);
		 }
		 
		 if(this.isResponsive){
			var responsiveDiv = $('<div class="table-responsive">');
			responsiveDiv.append(this.table);
			
			parent.append(responsiveDiv);
		 }else{
			 parent.append(this.table);
		 }
		 
	 }
	 
	 
	 
}

function cfw_createTable(){
	return new CFWTable();
}

/**************************************************************************************
 * Sort an object array by the values for the given key.
 * @param array the object array to be sorted
 * @param key the name of the field that should be used for sorting
 * @return sorted array
 *************************************************************************************/
function cfw_sortArrayByValueOfObject(array, key){
	array.sort(function(a, b) {
		
			var valueA = a[key];
			var valueB = b[key];
			
			if(isNaN(valueA)) valueA = 9999999;
			if(isNaN(valueB)) valueB = 9999999;
			
		return valueA - valueB;
	});
	
	return array;
}

/**************************************************************************************
 * Add an alert message to the message section.
 * Ignores duplicated messages.
 * @param type the type of the alert: INFO, SUCCESS, WARNING, ERROR
 * @param message
 *************************************************************************************/
function cfw_addAlertMessage(type, message){
	
	var clazz = "";
	
	switch(type.toLowerCase()){
		
		case "success": clazz = "alert-success"; break;
		case "info": 	clazz = "alert-info"; break;
		case "warning": clazz = "alert-warning"; break;
		case "error": 	clazz = "alert-danger"; break;
		case "severe": 	clazz = "alert-danger"; break;
		case "danger": 	clazz = "alert-danger"; break;
		default:	 	clazz = "alert-info"; break;
		
	}
	
	var htmlString = '<div class="alert alert-dismissible '+clazz+'" role=alert>'
		+ '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
		+ message
		+"</div>\n";
	
	//----------------------------------------------
	// Add if not already exists
	var messages = $("#cfw-messages");
	
	if (messages.html().indexOf(message) <= 0) {
		messages.append(htmlString);
	}
	
}

/**************************************************************************************
 * Create a table of contents for the h-elements on the page.
 * @param contentAreaSelector the jQuery selector for the element containing all 
 * the headers to show in the table of contents
 * @param targetSelector the jQuery selector for the resulting element
 * @return nothing
 *************************************************************************************/
function cfw_table_toc(contentAreaSelector, resultSelector){
	
	var target = $(resultSelector);
	var headers = $(contentAreaSelector).find("h1:visible, h2:visible, h3:visible, h4:visible, h5:visible, h6:visible, h7:visible, h8:visible, h9:visible");
	
	//------------------------------
	//Loop all visible headers
	currentLevel = 1;
	resultHTML = "<h1>Table of Contents</h1><ul>";
	for(i = 0; i < headers.length ; i++){
		head = headers[i];
		headLevel = head.tagName[1];
		
		//------------------------------
		//increase list depth
		while(currentLevel < headLevel){
			resultHTML += "<ul>";
			currentLevel++;
		}
		//------------------------------
		//decrease list depth
		while(currentLevel > headLevel){
			resultHTML += "</ul>";
			currentLevel--;
		}
		resultHTML += '<li><a href="#toc_anchor_'+i+'">'+head.innerHTML+'</li>';
		$(head).before('<a name="toc_anchor_'+i+'"></a>');
	}
	
	//------------------------------
	// Close remaining levels
	while(currentLevel > 1){
		resultHTML += "</ul>";
		currentLevel--;
	}
	
	target.html(resultHTML);
	
}


/*******************************************************************************
 * Set if the Loading animation is visible or not.
 * 
 * The following example shows how to call this method to create a proper rendering
 * of the loader:
 * 	
 *  CFW.ui.toogleLoader(true);
 *	window.setTimeout( 
 *	  function(){
 *	    // Do your stuff
 *	    CFW.ui.toogleLoader(false);
 *	  }, 100);
 *
 * @param isVisible true or false
 ******************************************************************************/
function cfw_toogleLoader(isVisible){
	
	var loader = $("#cfw-loader");
	
	if(loader.length == 0){
		loader = $('<div id="cfw-loader">'
				+'<i class="fa fa-cog fa-spin fa-3x fa-fw margin-bottom"></i>'
				+'<p>Loading...</p>'
			+'</div>');	
		
//		loader.css("position","absolute");
//		loader.css("top","50%");
//		loader.css("left","50%");
//		loader.css("transform","translateX(-50%) translateY(-50%);");
//		loader.css("visibility","hidden");
		
		$("body").append(loader);
	}
	if(isVisible){
		loader.css("visibility", "visible");
	}else{
		loader.css("visibility", "hidden");
	}
	
}


/**************************************************************************************
 * Create a model with content.
 * @param modalTitle the title for the modal
 * @param modalBody the body of the modal
 * @return nothing
 *************************************************************************************/
function cfw_showModal(modalTitle, modalBody){
	
	var body = $("body");
	var modalID = 'cfw-default-modal';
	
	var defaultModal = $("#"+modalID);
	if(defaultModal.length == 0){
	
		defaultModal = $(
				'<div id="'+modalID+'" class="modal fade"  tabindex="-1" role="dialog">'
				+ '  <div class="modal-dialog modal-lg" role="document">'
				+ '    <div class="modal-content">'
				+ '      <div class="modal-header">'
				+ '        <h3 class="modal-title">Title</h3>'
				+ '        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times</span></button>'
				+ '      </div>'
				+ '      <div class="modal-body" >'
				+ '      </div>'
				+ '      <div class="modal-footer">'
				+ '         <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>'
				+ '      </div>'
				+ '    </div>'
				+ '  </div>'
				+ '</div>');
		
		defaultModal.modal();
		$('body').prepend(defaultModal);
	}

	defaultModal.find(".modal-title").html(modalTitle);
	defaultModal.find('.modal-body').html("").append(modalBody);
	
	defaultModal.modal('show');
}


/**************************************************************************************
 * Create a confirmation modal panel that executes the function passed by the argument
 * @param message the message to show
 * @param confirmLabel the text for the confirm button
 * @param jsCode the javascript to execute when confirmed
 * @return nothing
 *************************************************************************************/
function cfw_confirmExecution(message, confirmLabel, jsCode){
	
	var body = $("body");
	var modalID = 'cfw-confirm-dialog';
	
	
	var modal = $('<div id="'+modalID+'" class="modal fade" tabindex="-1" role="dialog">'
				+ '  <div class="modal-dialog" role="document">'
				+ '    <div class="modal-content">'
				+ '      <div class="modal-header">'
				+ '        '
				+ '        <h3 class="modal-title">Confirm</h3>'
				+ '      </div>'
				+ '      <div class="modal-body">'
				+ '        <p>'+message+'</p>'
				+ '      </div>'
				+ '      <div class="modal-footer">'
				+ '      </div>'
				+ '    </div>'
				+ '  </div>'
				+ '</div>');

	modal.modal();
	
	body.prepend(modal);	
	
	var closeButton = $('<button type="button" class="close"><span aria-hidden="true">&times</span></button>');
	closeButton.attr('onclick', 'cfw_confirmExecution_Execute(this, \'cancel\')');
	closeButton.data('modalID', modalID);
	
	var cancelButton = $('<button type="button" class="btn btn-primary">Cancel</button>');
	cancelButton.attr('onclick', 'cfw_confirmExecution_Execute(this, \'cancel\')');
	cancelButton.data('modalID', modalID);
	
	var confirmButton = $('<button type="button" class="btn btn-primary">'+confirmLabel+'</button>');
	confirmButton.attr('onclick', 'cfw_confirmExecution_Execute(this, \'confirm\')');
	confirmButton.data('modalID', modalID);
	confirmButton.data('jsCode', jsCode);
	
	modal.find('.modal-header').append(closeButton);
	modal.find('.modal-footer').append(cancelButton).append(confirmButton);
	
	modal.modal('show');
}


function cfw_confirmExecution_Execute(source, action){
	
	var source = $(source);
	var modalID = source.data('modalID');
	var jsCode = source.data('jsCode');
	
	var modal = $('#'+modalID);
	
	if(action == 'confirm'){
		eval(jsCode);
	}
	
	//remove modal
	modal.modal('hide');
	modal.remove();
	$('.modal-backdrop').remove();
	$('body').removeClass('modal-open');
	modal.remove();
}


/******************************************************************
 * Reads the parameters from the URL and returns an object containing
 * name/value pairs like {"name": "value", "name2": "value2" ...}.
 * @param 
 * @return object
 ******************************************************************/
function cfw_getURLParams()
{
    var vars = {};
    
    var keyValuePairs = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < keyValuePairs.length; i++)
    {
        splitted = keyValuePairs[i].split('=');
        vars[splitted[0]] = splitted[1];
    }
    
    //console.log(vars);
    return vars;
}

/**************************************************************************************
 * Tries to decode a URI and handles errors when they are thrown.
 * If URI cannot be decoded the input string is returned unchanged.
 * 
 * @param uri to decode
 * @return decoded URI or the same URI in case of errors.
 *************************************************************************************/
function cfw_secureDecodeURI(uri){
	try{
		decoded = decodeURIComponent(uri);
	}catch(err){
		decoded = uri;
	}
	
	return decoded;
}

/**************************************************************************************
 * Executes a get request with JQuery and retrieves a standard JSON format of the CFW
 * framework. Handles alert messages if there are any.
 * 
 * The structure of the response has to adhere to the following structure:
 * {
 * 		success: true|false,
 * 		messages: [
 * 			{
 * 				type: info | success | warning | danger
 * 				message: "string",
 * 				stacktrace: null | "stacketrace string"
 * 			},
 * 			{...}
 * 		],
 * 		payload: {...}|[...] object or array
 * }
 * 
 * @param uri to decode
 * @return decoded URI or the same URI in case of errors.
 *************************************************************************************/
function cfw_getJSON(url, params, callbackFunc){

	$.get(url, params)
		  .done(function(response) {
		    //alert( "done" );
			  callbackFunc(response);
		  })
		  .fail(function(response) {
			  console.error("Request failed: "+url);
			  
			  CFW.ui.addAlert("error", "Request to URL failed: "+url);
			  
			  //callbackFunc(response);
		  })
		  .always(function(response) {
			  var msgArray = response.messages;
			  
			  if(msgArray != undefined
			  && msgArray != null
			  && msgArray.length > 0){
				  for(var i = 0; i < msgArray.length; i++ ){
					  CFW.ui.addAlert(msgArray[i].type, msgArray[i].message);
				  }
			  }
			  
		  });
}

/**************************************************************************************
 * Select all the content of the given element.
 * For example to select everything inside a given DIV element using 
 * <div onclick="selectElementContent(this)">.
 * @param el the dom element 
 *************************************************************************************/
function cfw_selectElementContent(el) {
    if (typeof window.getSelection != "undefined" && typeof document.createRange != "undefined") {
        var range = document.createRange();
        range.selectNodeContents(el);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    } else if (typeof document.selection != "undefined" && typeof document.body.createTextRange != "undefined") {
        var textRange = document.body.createTextRange();
        textRange.moveToElementText(el);
        textRange.select();
    }
}

/********************************************************************
 * CFW FRAMEWORK STRUCTURE
 * -----------------------
 ********************************************************************/
var CFW = {
	array: {
		sortArrayByValueOfObject: cfw_sortArrayByValueOfObject
	},
	
	http: {
		getURLParams: cfw_getURLParams,
		secureDecodeURI: cfw_secureDecodeURI,
		getJSON: cfw_getJSON
	},
	
	selection: {
		selectElementContent: cfw_selectElementContent
	},
	
	ui: {
		createTable: cfw_createTable,
		toc: cfw_table_toc,
		showModal: cfw_showModal,
		confirmExecute: cfw_confirmExecution,
		toogleLoader: cfw_toogleLoader,
		addAlert: cfw_addAlertMessage
	},

}