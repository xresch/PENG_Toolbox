

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerCategory("fas fa-th-large", "Static Widgets", CFWL('cfw_dashboard_category_static'));
CFW.dashboard.registerCategory("fas fa-cogs", "Test Category");
CFW.dashboard.registerCategory("fas fa-book", "Another Category");

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_text",
	{
		category: "Static Widgets",
		menuicon: "fas fa-font",
		menulabel: CFWL('cfw_dashboard_widget_cfwtext', 'Text'),
		description: CFWL('cfw_dashboard_widget_cfwtext_desc', 'Display static text. Can be used to create labels and descriptions.'),
		defaultValues: {
			TITLE: CFWL('cfw_dashboard_widget_cfwtext', 'Text'), 
			JSON_SETTINGS: {
				content: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 	
			}	
		},
		createWidgetInstance: function (widgetData, callback) {
							
			var merged = Object.assign({}, this.defaultValues, widgetData);
			var textRenderer = CFW.render.getRenderer('html');

			var content = textRenderer.render({data: merged.JSON_SETTINGS.content});
			callback(widgetData, content);
			
		},
		getEditForm: function (widgetData) {
			
			var customForm = '<form>';
			
			//------------------------------
			// Content
			customForm += new CFWFormField({ type: "textarea", name: "content", value: widgetData.JSON_SETTINGS.content, description: 'The html contents of the widget.' }).createHTML();
			customForm += '</form>';
			
			return customForm;
		},
		onSave: function (form, widgetData) {
			console.log(" ==== onSave =====");
			console.log(form);
			var settingsForm = $(form);
			
			widgetData.JSON_SETTINGS.content = settingsForm.find('textarea[name="content"]').val();
		}
		
	}
);

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_table",
	{
		category: "Static Widgets",
		menuicon: "fas fa-table",
		menulabel: CFWL('cfw_dashboard_widget_csvtable', "CSV Table"),
		description: CFWL('cfw_dashboard_widget_csvtable_desc', "Takes values in CSV format and displays them as a table."),
		defaultValues: {
			TITLE: "Table", 
			JSON_SETTINGS: {
				narrow: false,
				filterable: false,
				striped: true,
				delimiter: ';',
				tableData: "ID;Firstname;Lastname\r\n0;Jane;Doe\r\n1;Testika;Testonia",
			}
		},
		createWidgetInstance: function (widgetData, callback) {
				
			//--------------------------
			// Get Values
			var merged = Object.assign({}, this.defaultValues, widgetData);
			var delimiter = merged.JSON_SETTINGS.delimiter;
			var tableData = merged.JSON_SETTINGS.tableData;
			
			var dataToRender = tableData;
			
			if(typeof tableData == 'string'){
				var objectArray = CFW.format.csvToObjectArray(tableData, delimiter)
				
				dataToRender = {
					data: objectArray,
					rendererSettings:{
						table: {
							narrow: 		merged.JSON_SETTINGS.narrow,
							filterable: 	merged.JSON_SETTINGS.filterable,
							striped: 		merged.JSON_SETTINGS.striped,
						}
				}};
				
				
			}
			
			//--------------------------
			// Get Values
			var tableRenderer = CFW.render.getRenderer('table');
			var cfwTable = tableRenderer.render(dataToRender);

			callback(widgetData, cfwTable);
		},
		getEditForm: function (widgetData) {
			
			var customForm = '<form>';
			
			//------------------------------
			// Content
			customForm += new CFWFormField({ type: "textarea", name: "tableData", value: widgetData.JSON_SETTINGS.tableData, description: 'Values separated by the delimiter, first row will be used as header.' }).createHTML();
			customForm += new CFWFormField({ type: "text", name: "delimiter", value: widgetData.JSON_SETTINGS.delimiter, description: 'The delimiter used for the data.' }).createHTML();
			customForm += new CFWFormField({ type: "boolean", name: "narrow", value: widgetData.JSON_SETTINGS.narrow, description: 'Define if the table row height should be narrow or wide.' }).createHTML();
			customForm += new CFWFormField({ type: "boolean", name: "filterable", value: widgetData.JSON_SETTINGS.filterable, description: 'Shall a filter be added to the table or not.' }).createHTML();
			customForm += new CFWFormField({ type: "boolean", name: "striped", value: widgetData.JSON_SETTINGS.striped, description: 'Define if the table should have striped rows.' }).createHTML();
			customForm += '</form>';
			
			return customForm;
		},
		onSave: function (form, widgetData) {
			console.log(form);
			var settingsForm = $(form);
			widgetData.JSON_SETTINGS.delimiter 	= settingsForm.find('input[name="delimiter"]').val();
			widgetData.JSON_SETTINGS.tableData 	= settingsForm.find('textarea[name="tableData"]').val();
			widgetData.JSON_SETTINGS.narrow 		= ( settingsForm.find('input[name="narrow"]:checked').val() == "true" );
			widgetData.JSON_SETTINGS.filterable 	= ( settingsForm.find('input[name="filterable"]:checked').val() == "true" );
			widgetData.JSON_SETTINGS.striped 	= ( settingsForm.find('input[name="striped"]:checked').val() == "true" );
		}
		
	}
);

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_image",
	{
		category: "Static Widgets",
		menuicon: "far fa-image",
		menulabel: CFWL('cfw_dashboard_widget_cfwimage', "Image"),
		description: CFWL('cfw_dashboard_widget_cfwimage_desc', "Displays an image."),
		defaultValues: {
			TITLE: CFWL('cfw_dashboard_widget_cfwimage', "Image"), 
			JSON_SETTINGS: {
				url: '/resources/images/login_background.jpg'
			}
		},
		createWidgetInstance: function (widgetData, callback) {
							
			var merged = Object.assign({}, this.defaultValues, widgetData);

			var content = 
				CFW.render.getRenderer('html').render({
					data: '<div class="dashboard-image flex-grow-1" style="background-image: url(\''+widgetData.JSON_SETTINGS.url+'\');">'
				});
			
			callback(widgetData, content);
			
		},
		getEditForm: function (widgetData) {
			
			var customForm = '<form>';
			
			//------------------------------
			// Content
			customForm += new CFWFormField({ type: "text", name: "url", label: "URL", value: widgetData.JSON_SETTINGS.url, description: 'The url uf the image that should be displayed.' }).createHTML();
			customForm += '</form>';
			
			return customForm;
		},
		onSave: function (form, widgetData) {
			console.log(form);
			var settingsForm = $(form);
			
			widgetData.JSON_SETTINGS.url = settingsForm.find('input[name="url"]').val();
		}
		
	}
);

/******************************************************************
 * 
 ******************************************************************/
CFW.dashboard.registerWidget("cfw_iframe",
	{
		category: "Static Widgets",
		menuicon: "fas fa-globe",
		menulabel: CFWL('cfw_dashboard_widget_cfwwebsite', "Website"),
		description: CFWL('cfw_dashboard_widget_cfwwebsite_desc', "Displays a website(doesn't work with all websites)."),
		defaultValues: {
			TITLE: CFWL('cfw_dashboard_widget_cfwwebsite', "Website"), 
			JSON_SETTINGS: {
				url: "/resources/images/login_background.jpg"
			}
		},
		createWidgetInstance: function (widgetData, callback) {
							
			var merged = Object.assign({}, this.defaultValues, widgetData);
			var textRenderer = CFW.render.getRenderer('html');

			content = textRenderer.render({data: '<iframe class="w-100 flex-grow-1" src="'+widgetData.JSON_SETTINGS.url+'">'});
			callback(widgetData, content);
			
		},
		getEditForm: function (widgetData) {
			
			var customForm = '<form>';
			
			//------------------------------
			// Content
			customForm += new CFWFormField({ type: "text", name: "url", label: "URL", value: widgetData.JSON_SETTINGS.url, description: 'The url uf the page that should be displayed in the iFrame.' }).createHTML();
			customForm += '</form>';
			
			return customForm;
		},
		onSave: function (form, widgetData) {
			console.log(form);
			var settingsForm = $(form);
			
			widgetData.JSON_SETTINGS.url = settingsForm.find('input[name="url"]').val();
		}
		
	}
);
