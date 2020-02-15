

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerCategory("Static Widgets", "fas fa-th-large");
CFW.dashboard.registerCategory("Test Category", "fas fa-cogs");
CFW.dashboard.registerCategory("Another Category", "fas fa-book");

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_table",
		{
			category: "Static Widgets",
			menulabel: "CSV Table",
			menuicon: "fas fa-table",
			renderers: [],
			defaultValues: {
    			title: "Table", 
    			settings: {
    				narrow: false,
    				filterable: false,
    				striped: true,
    				delimiter: ';',
    				tableData: "ID;Firstname;Lastname\r\n0;Jane;Doe\r\n1;Testika;Testonia",
    			}
    		},
    		createWidgetInstance: function (widgetData) {
					
				//--------------------------
				// Get Values
				var merged = Object.assign({}, this.defaultValues, widgetData);
				var delimiter = merged.settings.delimiter;
				var tableData = merged.settings.tableData;
				
				var dataToRender = tableData;
				
				if(typeof tableData == 'string'){
					var objectArray = CFW.format.csvToObjectArray(tableData, delimiter)
					
					dataToRender = {
						data: objectArray,
						rendererSettings:{
							table: {
								narrow: 		merged.settings.narrow,
								filterable: 	merged.settings.filterable,
								striped: 		merged.settings.striped,
							}
					}};
					
					
				}
				
				console.log('==== render Table ====');
				console.log(objectArray);
				
				//--------------------------
				// Get Values
				var tableRenderer = CFW.render.getRenderer('table');
				var cfwTable = tableRenderer.render(dataToRender);
				
				merged.content = cfwTable;
				return CFW.dashboard.createWidget(merged);
				
			},
    		getEditForm: function (widgetData) {
    			
    			var customForm = '<form>';
    			
    			//------------------------------
    			// Content
    			customForm += new CFWFormField({ type: "textarea", name: "tableData", value: widgetData.settings.tableData, description: 'Values separated by the delimiter, first row will be used as header.' }).createHTML();
    			customForm += new CFWFormField({ type: "text", name: "delimiter", value: widgetData.settings.delimiter, description: 'The delimiter used for the data.' }).createHTML();
    			customForm += new CFWFormField({ type: "boolean", name: "narrow", value: widgetData.settings.narrow, description: 'Define if the table row height should be narrow or wide.' }).createHTML();
    			customForm += new CFWFormField({ type: "boolean", name: "filterable", value: widgetData.settings.filterable, description: 'Shall a filter be added to the table or not.' }).createHTML();
    			customForm += new CFWFormField({ type: "boolean", name: "striped", value: widgetData.settings.striped, description: 'Define if the table should have striped rows.' }).createHTML();
    			customForm += '</form>';
    			
    			return customForm;
    		},
			onSave: function (form, widgetData) {
				console.log(form);
				var settingsForm = $(form);
				widgetData.settings.delimiter 	= settingsForm.find('input[name="delimiter"]').val();
				widgetData.settings.tableData 	= settingsForm.find('textarea[name="tableData"]').val();
				widgetData.settings.narrow 		= ( settingsForm.find('input[name="narrow"]:checked').val() == "true" );
				widgetData.settings.filterable 	= ( settingsForm.find('input[name="filterable"]:checked').val() == "true" );
				widgetData.settings.striped 	= ( settingsForm.find('input[name="striped"]:checked').val() == "true" );
			}
    		
		}
);

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_iframe",
		{
			category: "Static Widgets",
			menulabel: "iFrame",
			menuicon: "far fa-square",
			renderers: [],
			defaultValues: {
    			title: "iFrame", 
    			data: "",
    		},
    		createWidgetInstance: function (widgetData) {
								
				var merged = Object.assign({}, this.defaultValues, widgetData);
				var textRenderer = CFW.render.getRenderer('html');

				merged.content = textRenderer.render({data: '<iframe class="w-100 flex-grow-1" src="'+widgetData.url+'">'});
				return CFW.dashboard.createWidget(merged);
				
			},
    		getEditForm: function (widgetData) {
    			
    			var customForm = '<form>';
    			
    			//------------------------------
    			// Content
    			customForm += new CFWFormField({ type: "text", name: "url", value: widgetData.url, description: 'The url uf the page that should be displayed in the iFrame.' }).createHTML();
    			customForm += '</form>';
    			
    			return customForm;
    		},
			onSave: function (form, widgetData) {
				console.log(form);
				var settingsForm = $(form);
				
				widgetData.url = settingsForm.find('input[name="url"]').val();
			}
    		
		}
);

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_html",
		{
			category: "Static Widgets",
			menulabel: "HTML",
			menuicon: "fas fa-code",
			renderers: [],
			defaultValues: {
    			title: "Some very long title to check overflow", 
    			data: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
    			footer: "Some very long footer to test overflow",
    		},
    		createWidgetInstance: function (widgetData) {
								
				var merged = Object.assign({}, this.defaultValues, widgetData);
				var textRenderer = CFW.render.getRenderer('html');

				merged.content = textRenderer.render({data: merged.data});
				return CFW.dashboard.createWidget(merged);
				
			},
    		getEditForm: function (widgetData) {
    			
    			var customForm = '<form>';
    			
    			//------------------------------
    			// Content
    			customForm += new CFWFormField({ type: "textarea", name: "content", value: widgetData.content, description: 'The html contents of the widget.' }).createHTML();
    			customForm += '</form>';
    			
    			return customForm;
    		},
			onSave: function (form, widgetData) {
				console.log(" ==== onSave =====");
				console.log(form);
				var settingsForm = $(form);
				
				widgetData.content = settingsForm.find('textarea[name="content"]').val();
			}
    		
		}
);