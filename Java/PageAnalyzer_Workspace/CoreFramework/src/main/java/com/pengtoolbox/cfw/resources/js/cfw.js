/**************************************************************************************
 * CFW.js
 * ======
 * Javascript library for the CFW framework.
 * 
 * @author Reto Scheiwiller, 2019
 *************************************************************************************/

/******************************************************************
 * Class to wrap a date for easier formatting.
 * 
 ******************************************************************/
class CFWDate{
	
	 constructor(dateArgument){
		 if(dateArgument != null){
			 this.date = new Date(dateArgument);
		 }else{
			 this.date = new Date();
		 }
		 
	 }
	
	 /********************************************
	  * Returns a String in the format YYYY-MM-DD
	  ********************************************/
	 getDateForInput(){
		 var datestring = this.fillDigits(this.date.getFullYear(), 4)+"-"
		 				+ this.fillDigits(this.date.getMonth()+1, 2)+"-"
		 				+ this.fillDigits(this.date.getDate(), 2);
		 
		 return datestring;
	 }
	 
	 /********************************************
	  * Returns a String in the format HH:MM:ss.SSS
	  ********************************************/
	 getTimeForInput(){
		 var datestring = this.fillDigits(this.date.getHours(), 2)+":"
		 				+ this.fillDigits(this.date.getMinutes(), 2);
		 				/* +":"
		 				+ this.fillDigits(this.date.getSeconds(), 2)+"."
		 				+ this.f illDigits(this.date.getMilliseconds(), 3);*/
		 
		 return datestring;
	 }
	 
	 /********************************************
	  * Fill the digits to the needed amount
	  ********************************************/
	 fillDigits(value, digits){
		
		var stringValue = ''+value;
		var length = stringValue.length;
		
		for(var i = stringValue.length; i < digits;i++){
			stringValue = '0'+stringValue;
		}
		
		return stringValue;
		
	}
}

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
		 this.isSticky = false;
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
		 
		 if(this.isSticky){
			 this.thead.find("th").addClass("cfw-sticky-th bg-dark text-light");
			 this.isResponsive = false;
			 this.table.css("width", "100%");
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

/******************************************************************
 * Print the list of results found in the database.
 * 
 * @param parent JQuery object
 * @param data object containing the list of results.
 * 
 ******************************************************************/
class CFWToogleButton{
	
	constructor(url, params, isEnabled){
		console.log(url);
		console.log(params);
		this.url = url;
		this.params = params;
		this.isLocked = false;
		this.button = $('<button class="btn btn-sm">');
		this.button.data('instance', this);
		this.button.attr('onclick', 'cfw_toggleTheToggleButton(this)');
		this.button.html($('<i class="fa"></i>'));
		
		if(isEnabled){
			this.setEnabled();
		}else{
			this.setDisabled();
		}
	}
	
	/********************************************
	 * Change the display of the button to locked.
	 ********************************************/
	setLocked(){
		this.isLocked = true;
		this.button
		.prop('disabled', this.isLocked)
		.attr('title', "Cannot be changed")
		.find('i')
			.removeClass('fa-check')
			.removeClass('fa-ban')
			.addClass('fa-lock');
	}
	/********************************************
	 * Change the display of the button to enabled.
	 ********************************************/
	setEnabled(){
		this.isEnabled = true;
		this.button.addClass("btn-success")
			.removeClass("btn-danger")
			.attr('title', "Click to Disable")
			.find('i')
				.addClass('fa-check')
				.removeClass('fa-ban');
	}
	
	/********************************************
	 * Change the display of the button to locked.
	 ********************************************/
	setDisabled(){
		this.isEnabled = false;
		this.button.removeClass("btn-success")
			.addClass("btn-danger")
			.attr('title', "Click to Enable")
			.find('i')
				.removeClass('fa-check')
				.addClass('fa-ban');
	}

	/********************************************
	 * toggle the Button
	 ********************************************/
	toggleButton(){
		if(this.isEnabled){
			this.setDisabled();
		}else{
			this.setEnabled();
		}
	}
	
	/********************************************
	 * Send the request and toggle the button if
	 * successful.
	 ********************************************/
	onClick(){
		var button = this.button;
		console.log(this.url);
		console.log(this.params);
		CFW.http.getJSON(this.url, this.params, 
			function(data){
				if(data.success){
					var instance = $(button).data('instance');
					instance.toggleButton();
					CFW.ui.addToast("Saved!", null, "success", CFW.config.toastDelay);
				}
			}
		);
	}
	
	/********************************************
	 * Toggle the table filter, default is true.
	 ********************************************/
	appendTo(parent){
		parent.append(this.button);
	}
}

function cfw_createToggleButton(url, params, isEnabled){
	return new CFWToogleButton(url, params, isEnabled);
}

function cfw_toggleTheToggleButton(button){
	var cfwToogleButton = $(button).data('instance');
	cfwToogleButton.onClick();
}

/**************************************************************************************
 * Initialize a DatePicker created with the Java object CFWField.
 * @param name the name of the field
 * @param epochInitial the initial date in epoch time
 * @return nothing
 *************************************************************************************/
//function cfw_initializeDatePicker(name, epochInitial) {
//	
//	$('#'+name+'-datepicker').datepicker({
//	  	dateFormat: 'dd.mm.yy',
//		altField: '#'+name,
//	  	altFormat: '@',
//		showOptions: { direction: 'up' },
//	  	beforeShow: function(input, inst) {
//		        $('#ui-datepicker-div').addClass('bg-dark');
//	  		}
//	});
//	
//	if(epochInitial != null){
//		$('#'+name+'-datepicker').datepicker( 'setDate', new Date(epochInitial) );	
//	}
//
//}

/**************************************************************************************
 * Initialize a Date and/or Timepicker created with the Java object CFWField.
 * @param fieldID the name of the field
 * @param epochMillis the initial date in epoch time or null
 * @return nothing
 *************************************************************************************/
function cfw_initializeTimefield(fieldID, epochMillis){
	
	
	var id = '#'+fieldID;
	var datepicker = $(id+'-datepicker');
	var timepicker = $(id+'-timepicker');
	
	if(datepicker.length == 0){
		CFW.ui.addToastDanger('Error: the datepicker field is unknown: '+fieldID);
		return;
	}
	
	if(epochMillis != null){
		date = new CFWDate(epochMillis);
	}else{
		date = new CFWDate();
	}
		
	datepicker.first().val(date.getDateForInput());
	
	if(timepicker.length > 0 != null){
		timepicker.first().val(date.getTimeForInput());
	}
}

/**************************************************************************************
 * Update a Date and/or Timepicker created with the Java object CFWField.
 * @param fieldID the name of the field
 * @return nothing
 *************************************************************************************/
function cfw_updateTimeField(fieldID){
	
	var id = '#'+fieldID;
	var datepicker = $(id+'-datepicker');
	var timepicker = $(id+'-timepicker');
	
	if(datepicker.length == 0){
		CFW.ui.addToastDanger('Error: the datepicker field is unknown: '+fieldID)
	}
	
	var dateString = datepicker.first().val();
	console.log("dateString: "+dateString);
	
	if(timepicker.length > 0){
		var timeString = timepicker.first().val();
		if(timeString.length > 0 ){
			dateString = dateString +"T"+timeString;
		}
	}
	
	if(dateString.length > 0){
		$(id).val(Date.parse(dateString));
		console.log("Epoch: "+Date.parse(dateString));
	}
	
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
 * Create a new Toast.
 * @param toastTitle the title for the toast
 * @param toastBody the body of the toast (can be null)
 * @param style bootstrap style like 'info', 'success', 'warning', 'danger'
 * @param delay in milliseconds for autohide
 * @return nothing
 *************************************************************************************/
function cfw_addToast(toastTitle, toastBody, style, delay){
	
	var body = $("body");
	var toastsID = 'cfw-toasts';
	
	//--------------------------------------------
	// Create Toast Wrapper if not exists
	//--------------------------------------------
	var toastDiv = $("#"+toastsID);
	if(toastDiv.length == 0){
	
		var toastWrapper = $(
				'<div id="cfw-toasts-wrapper" aria-live="polite" aria-atomic="true">'
			  + '  <div id="cfw-toasts"></div>'
			  + '</div>');
		
		toastWrapper;
		
		body.prepend(toastWrapper);
		toastDiv = $("#"+toastsID);
	}

	//--------------------------------------------
	// Prepare arguments
	//--------------------------------------------
	
	if(style == null){
		style = "primary";
	}
	
	var clazz = style;
	switch(style.toLowerCase()){
	
		case "success": clazz = "success"; break;
		case "info": 	clazz = "info"; break;
		case "warning": clazz = "warning"; break;
		case "error": 	clazz = "danger"; break;
		case "severe": 	clazz = "danger"; break;
		case "danger": 	clazz = "danger"; break;
		default:	 	clazz = style; break;
		
	}
	
	var autohide = 'data-autohide="false"';
	if(delay != null){
		autohide = 'data-autohide="true" data-delay="'+delay+'"';
	}
	//--------------------------------------------
	// Create Toast 
	//--------------------------------------------
		
	var toastHTML = '<div class="toast bg-'+clazz+' text-light" role="alert" aria-live="assertive" aria-atomic="true" data-animation="true" '+autohide+'>'
			+ '  <div class="toast-header bg-'+clazz+' text-light">'
			//+ '	<img class="rounded mr-2" alt="...">'
			+ '	<strong class="mr-auto">'+toastTitle+'</strong>'
			//+ '	<small class="text-muted">just now</small>'
			+ '	<button type="button" class="ml-2 mb-auto close" data-dismiss="toast" aria-label="Close">'
			+ '	  <span aria-hidden="true">&times;</span>'
			+ '	</button>'
			+ '  </div>';
	
	if(toastBody != null){
		toastHTML += '  <div class="toast-body">'+ toastBody+'</div>';	
	}
	toastHTML += '</div>';
	
	var toast = $(toastHTML);
	
	toastDiv.append(toast);
	toast.toast('show');
}

/**************************************************************************************
 * Create a model with content.
 * @param modalTitle the title for the modal
 * @param modalBody the body of the modal
 * @param jsCode to execute on modal close
 * @return nothing
 *************************************************************************************/
function cfw_showModal(modalTitle, modalBody, jsCode){
	
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

	//---------------------------------
	// Add Callback
	if(jsCode != null){
		defaultModal.on('hidden.bs.modal', function () {
			eval(jsCode);
			$("#"+modalID).off('hidden.bs.modal');
		});	
	}
	
	//---------------------------------
	// ShowModal
	defaultModal.find(".modal-title").html("").append(modalTitle);
	defaultModal.find('.modal-body').html("").append(modalBody);
	
	defaultModal.modal('show');
}

/**************************************************************************************
 * Create a model with content.
 * @param modalTitle the title for the modal
 * @param modalBody the body of the modal
 * @param jsCode to execute on modal close
 * @return nothing
 *************************************************************************************/
function cfw_showSmallModal(modalTitle, modalBody, jsCode){
	
	var body = $("body");
	var modalID = 'cfw-small-modal';
	
	var smallModal = $("#"+modalID);
	if(smallModal.length == 0){
	
		smallModal = $(
				'<div id="'+modalID+'" class="modal fade"  tabindex="-1" role="dialog">'
				+ '  <div class="modal-dialog modal-sm" role="document">'
				+ '    <div class="modal-content">'
				+ '      <div class="modal-header p-2">'
				+ '        <h4 class="modal-title">Title</h4>'
				+ '        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times</span></button>'
				+ '      </div>'
				+ '      <div class="modal-body">'
				+ '      </div>'
				+ '      <div class="modal-footer  p-2">'
				+ '         <button type="button" class="btn btn-primary btn-sm" data-dismiss="modal">Close</button>'
				+ '      </div>'
				+ '    </div>'
				+ '  </div>'
				+ '</div>');

		smallModal.modal();
		$('body').prepend(smallModal);
		
	}

	//---------------------------------
	// Add Callback
	if(jsCode != null){
		smallModal.on('hidden.bs.modal', function () {
			eval(jsCode);
			$("#"+modalID).off('hidden.bs.modal');
		});	
	}
	
	//---------------------------------
	// Show Modal
	smallModal.find(".modal-title").html("").append(modalTitle);
	smallModal.find('.modal-body').html("").append(modalBody);
	
	smallModal.modal('show');
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
 * Handle messages of a standard JSON response.
 * 
 * The structure of the response has to contain a array with messages:
 * {
 * 		messages: [
 * 			{
 * 				type: info | success | warning | danger
 * 				message: "string",
 * 				stacktrace: null | "stacketrace string"
 * 			},
 * 			{...}
 * 		],
 * }
 * 
 * @param response the response with messages
 **************************************************************************************/
function cfw_handleMessages(response){
	
	var msgArray = response.messages;
	  
	  if(msgArray != undefined
	  && msgArray != null
	  && msgArray.length > 0){
		  for(var i = 0; i < msgArray.length; i++ ){
			  CFW.ui.addToast(msgArray[i].message, null, msgArray[i].type, CFW.config.toastErrorDelay);
		  }
	  }
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
			  CFW.ui.addToast("Request failed", "URL: "+url, "danger", CFW.config.toastErrorDelay)
			  //callbackFunc(response);
		  })
		  .always(function(response) {
			  cfw_handleMessages(response);
		  });
}

/**************************************************************************************
 * Executes a post request with JQuery and retrieves a standard JSON format of the CFW
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
function cfw_postJSON(url, params, callbackFunc){

	$.post(url, params)
		  .done(function(response) {
		    //alert( "done" );
			  if(callbackFunc != null) callbackFunc(response);
		  })
		  .fail(function(response) {
			  console.error("Request failed: "+url);
			  CFW.ui.addToast("Request failed", "URL: "+url, "danger", CFW.config.toastErrorDelay)
			  //callbackFunc(response);
		  })
		  .always(function(response) {
			  cfw_handleMessages(response);
		  });
}

/**************************************************************************************
 * Get a form created with the class BTForm on server side using the formid.
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
 * 		payload: {html: "the html form"}
 * }
 * 
 * @param formid the id of the form
 * @param targetElement the element in which the form should be placed
 *************************************************************************************/
function cfw_getForm(formid, targetElement){

	$.get('/cfw/formhandler', {id: formid})
		  .done(function(response) {
			  $(targetElement).html(response.payload.html);
		      formID = $(targetElement).find('form').attr("id");
              eval("intializeForm_"+formID+"();");
		  })
		  .fail(function(response) {
			  console.error("Request failed: "+url);
			  CFW.ui.addToast("Request failed", "URL: "+url, "danger", CFW.config.toastErrorDelay)

		  })
		  .always(function(response) {
			  cfw_handleMessages(response);			  
		  });
}

/**************************************************************************************
 * Calls a rest service that creates a form and returns a standard json format,
 * containing the html of the form in the payload.
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
 * 		payload: {html: "the html form"}
 * }
 * 
 * @param url to call
 * @param params to pass
 * @param targetElement the element in which the form should be placed
 *************************************************************************************/
function cfw_createForm(url, params, targetElement){

	$.get(url, params)
		  .done(function(response) {
		      $(targetElement).html(response.payload.html);
		      formID = $(targetElement).find('form').attr("id");
              eval("intializeForm_"+formID+"();");
		  })
		  .fail(function(response) {
			  console.error("Request failed: "+url);
			  CFW.ui.addToast("Request failed", "URL: "+url, "danger", CFW.config.toastErrorDelay)

		  })
		  .always(function(response) {
			  cfw_handleMessages(response);			  
		  });
}

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
function cfw_fetchAndCacheData(url, params, key, callback){
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
 * Method to remove the cached data under the specified key.
 *
 * @param key under which the data is stored
 * @return nothing
 *
 ******************************************************************/
function cfw_removeFromCache(key){
	CFW.cache.data[key] = null;
}

/******************************************************************
 * Method to remove all the data in the cache.
 *
 * @return nothing
 *
 ******************************************************************/
function cfw_clearCache(){
	CFW.cache.data = {};
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

/**************************************************************************************
 * Checks if the user has the specified permission
 * @param permissionName the name of the Permission
 *************************************************************************************/
function  cfw_hasPermission(permissionName){
	$.ajaxSetup({async: false});
	cfw_fetchAndCacheData("./usermanagement/permissions", null, "userPermissions")
	$.ajaxSetup({async: true});
	
	if(CFW.cache.data["userPermissions"] != null
	&& CFW.cache.data["userPermissions"].payload.includes(permissionName)){
		return true;
	}
	
	return false;
}

/********************************************************************
 * CFW FRAMEWORK STRUCTURE
 * -----------------------
 ********************************************************************/

var CFW = {
	config: {
		toastDelay: 	 3000,
		toastErrorDelay: 10000
	},
	cache: { 
		data: {},
		removeFromCache: cfw_removeFromCache,
		clearCache: cfw_clearCache
	},
	array: {
		sortArrayByValueOfObject: cfw_sortArrayByValueOfObject
	},
	
	http: {
		getURLParams: cfw_getURLParams,
		secureDecodeURI: cfw_secureDecodeURI,
		getJSON: cfw_getJSON,
		postJSON: cfw_postJSON,
		getForm: cfw_getForm,
		createForm: cfw_createForm,
		fetchAndCacheData: cfw_fetchAndCacheData

	},
	
	selection: {
		selectElementContent: cfw_selectElementContent
	},

	ui: {
		createTable: cfw_createTable,
		createToggleButton: cfw_createToggleButton,
		toc: cfw_table_toc,
		addToast: cfw_addToast,
		addToastSuccess: function(text){cfw_addToast(text, null, "success", CFW.config.toastDelay);},
		addToastDanger: function(text){cfw_addToast(text, null, "danger", CFW.config.toastErrorDelay);},
		showModal: cfw_showModal,
		showSmallModal: cfw_showSmallModal,
		confirmExecute: cfw_confirmExecution,
		toogleLoader: cfw_toogleLoader,
		addAlert: cfw_addAlertMessage
	},
	hasPermission: cfw_hasPermission,

}


/********************************************************************
 * General initialization
 ********************************************************************/

$(function () {
	  $('[data-toggle="tooltip"]').tooltip()
})