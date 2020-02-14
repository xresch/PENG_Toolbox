
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

CFW.render.registerRenderer("table",
		new CFWRenderer(
			function (renderDef) {
				
				console.log('=========== Render Def =============');
				console.log(renderDef);
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
				for(var key in renderDef.visiblefields){
					var fieldname = renderDef.visiblefields[key];
					cfwTable.addHeader(renderDef.labels[fieldname]);
				}
				
				for(var key in renderDef.actionButtons){
					cfwTable.addHeader("&nbsp;");
				}
				
				//-----------------------------------
				// Print Records
				var count = renderDef.data.length;
				
				for(var i = 0; i < count; i++ ){
					var currentRecord = renderDef.data[i];
					var row = $('<tr>');
					
					//-------------------------
					// Check Style
					if(renderDef.bgstylefield != null){
						row.addClass('table-'+currentRecord[renderDef.bgstylefield]);
					}
					
					if(renderDef.textstylefield != null){
						row.addClass('text-'+currentRecord[renderDef.textstylefield]);
					}
					//-------------------------
					// Add field Values as Cells
					var cellHTML = '';
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
					for(var fieldKey in renderDef.actionButtons){
						
						cellHTML += '<td>'+renderDef.actionButtons[fieldKey](currentRecord, id )+'</td>';
					}
					row.append(cellHTML);
					cfwTable.addRow(row);
				}
				
				return cfwTable.getTable();
		})
);