
/********************************************************************
 * CFW FRAMEWORK STRUCTURE
 * -----------------------
 ********************************************************************/
var CFW = {
	array: {
		sortArrayByValueOfObject: null
	},
	general: {
		getURLParams: null,
		secureDecodeURI: null
	},
	selection: {
		selectElementContent: null
	},
	ui: {
		toc: null,
		showModal: null,
		confirmExecute: null,
		toogleLoader: null
	},

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
CFW.array.sortArrayByValueOfObject = cfw_sortArrayByValueOfObject;

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
CFW.ui.toc = cfw_table_toc;

/*******************************************************************************
 * Set if the Loading animation is visible or not.
 * @param isVisible true or false
 ******************************************************************************/
function cfw_toogleLoader(isVisible){
	
	var loader = $("#cfw-loader");
	if(loader.length == 0){
		loader = $('<div id="cfw-loader">'
				+'<i class="fa fa-cog fa-spin fa-3x fa-fw margin-bottom"></i>'
				+'<p>Loading...</p>'
			+'</div>');	
		
		loader.css("position","absolute");
		loader.css("top","50%");
		loader.css("left","50%");
		loader.css("transform","translateX(-50%) translateY(-50%);");
		loader.css("visibility","hidden");
		
		$("body").append(loader);
	}
	if(isVisible){
		loader.css("visibility", "visible");
	}else{
		loader.css("visibility", "hidden");
	}
}
CFW.ui.toogleLoader = cfw_toogleLoader;

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
				+ '         <button type="button" class="btn btn-info" data-dismiss="modal">Close</button>'
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
CFW.ui.showModal = cfw_showModal;

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
	
	var cancelButton = $('<button type="button" class="btn btn-info">Cancel</button>');
	cancelButton.attr('onclick', 'cfw_confirmExecution_Execute(this, \'cancel\')');
	cancelButton.data('modalID', modalID);
	
	var confirmButton = $('<button type="button" class="btn btn-info">'+confirmLabel+'</button>');
	confirmButton.attr('onclick', 'cfw_confirmExecution_Execute(this, \'confirm\')');
	confirmButton.data('modalID', modalID);
	confirmButton.data('jsCode', jsCode);
	
	modal.find('.modal-header').append(closeButton);
	modal.find('.modal-footer').append(cancelButton).append(confirmButton);
	
	modal.modal('show');
}

CFW.ui.confirmExecute = cfw_confirmExecution;

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
CFW.general.getURLParams = cfw_getURLParams;


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
CFW.general.secureDecodeURI = cfw_secureDecodeURI;

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

CFW.selection.selectElementContent = cfw_selectElementContent;