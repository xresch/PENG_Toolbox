
/******************************************************************
 * 
 ******************************************************************/

CFW.render.registerRenderer("html",
	new CFWRenderer(
		function (renderDefinition) {
			if(typeof renderDefinition.data == "object"){
				return CFW.format.objectToHTMLList(renderDefinition.data)
			}else{
				return renderDefinition.data;
			}
		})
);

/******************************************************************
 * 
 ******************************************************************/
CFW.render.registerRenderer("table",
		new CFWRenderer(
			function (renderDef) {
				
				//-----------------------------------
				// Check Data
				if(renderDef.datatype != "array"){
					return "<span>Unable to convert data into table.</span>";
				}
				
				//===================================================
				// Create Table
				//===================================================
				var cfwTable = CFW.ui.createTable();
				cfwTable.tableFilter = false;
				
				//-----------------------------------
				// Create Headers
				var selectorGroupClass;
				if(renderDef.multiActions != null){
					selectorGroupClass = "table-checkboxes-"+CFW.utils.randomString(16);
					var checkbox = $('<input type="checkbox" onclick="$(\'.'+selectorGroupClass+'\').prop(\'checked\', $(this).is(\':checked\') )" >');
					
					cfwTable.addHeader(checkbox);
				}
				
				for(var key in renderDef.visiblefields){
					var fieldname = renderDef.visiblefields[key];
					cfwTable.addHeader(renderDef.labels[fieldname]);
				}
				
				for(var key in renderDef.actions){
					cfwTable.addHeader("&nbsp;");
				}
				
				//-----------------------------------
				// Print Records
				var count = renderDef.data.length;
				
				for(var i = 0; i < count; i++ ){
					var currentRecord = renderDef.data[i];
					var row = $('<tr class="cfwRecordContainer">');
					
					//-------------------------
					// Add Styles
					if(renderDef.bgstylefield != null){
						row.addClass('table-'+currentRecord[renderDef.bgstylefield]);
					}
					
					if(renderDef.textstylefield != null){
						row.addClass('text-'+currentRecord[renderDef.textstylefield]);
					}
					
					//-------------------------
					// Checkboxes for selects
					var cellHTML = '';
					if(renderDef.multiActions != null){
						
						var value = "";
						if(renderDef.idfield != null){
							value = currentRecord[renderDef.idfield];
						}
						var checkboxCell = $('<td>');
						var checkbox = $('<input class="'+selectorGroupClass+'" type="checkbox" value="'+value+'">');
						checkbox.data('idfield', renderDef.idfield);
						checkbox.data('record', currentRecord);
						checkboxCell.append(checkbox);
						row.append(checkboxCell);
					}
					
					//-------------------------
					// Add field Values as Cells
					for(var key in renderDef.visiblefields){
						var fieldname = renderDef.visiblefields[key];
						var value = currentRecord[fieldname];
						
						if(renderDef.customizers[fieldname] == null){
							if(value != null){
								cellHTML += '<td>'+value+'</td>';
							}else{
								cellHTML += '<td>&nbsp;</td>';
							}
						}else{
							var customizer = renderDef.customizers[fieldname];
							cellHTML += '<td>'+customizer(currentRecord, value)+'</td>';
						}
					}
					
					//-------------------------
					// Add Action buttons
					var id = null;
					if(renderDef.idfield != null){
						id = currentRecord[renderDef.idfield];
					}
					for(var fieldKey in renderDef.actions){
						
						cellHTML += '<td>'+renderDef.actions[fieldKey](currentRecord, id )+'</td>';
					}
					row.append(cellHTML);
					cfwTable.addRow(row);
				}
				
				//----------------------------------
				// Create multi buttons
				if(renderDef.multiActions == null){
					return cfwTable.getTable();
				}else{
					var wrapperDiv = cfwTable.getTable();
					
					for(var buttonLabel in renderDef.multiActions){
						var func = renderDef.multiActions[buttonLabel];
						var button = $('<button class="btn btn-sm btn-primary mr-1" onclick="cfw_internal_executeMultiAction(this)">'+buttonLabel+'</button>');
						button.data('checkboxSelector', '.'+selectorGroupClass); 
						button.data("function", func); 
						wrapperDiv.append(button);
					}
					
					return wrapperDiv;
				}
		})
);

/******************************************************************
 * Execute a multi action.
 * Element needs the following JQuery.data() attributes:
 *   - checkboxSelector: JQuery selection string without ":checked"
 *   - function: the function that should be executed
 ******************************************************************/
function cfw_internal_executeMultiAction(buttonElement){
	
	var checkboxSelector = $(buttonElement).data('checkboxSelector');
	var callbackFunction = $(buttonElement).data('function');
		
	var recordContainerArray = [];
	var valuesArray = [];
	var recordsArray = [];
	
	$.each($(checkboxSelector+':checked'), function(){
		valuesArray.push( $(this).val() );
		recordsArray.push( $(this).data('record') );
		recordContainerArray.push( $(this).closest('.cfwRecordContainer').get(0) );
	});
	
	callbackFunction(recordContainerArray, recordsArray, valuesArray);
	
}