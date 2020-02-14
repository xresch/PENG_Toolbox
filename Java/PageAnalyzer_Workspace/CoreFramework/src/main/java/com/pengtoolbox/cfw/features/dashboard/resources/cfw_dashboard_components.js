

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
			menulabel: "Table",
			menuicon: "fas fa-table",
			renderers: [],
			defaultValues: {
    			title: "Table", 
    			data: "",
    		},
    		createWidgetInstance: function (widgetData) {
								
				var merged = Object.assign({}, this.defaultValues, widgetData);
				var tableRenderer = CFW.render.getRenderer('table');

				var cfwTable = tableRenderer.render(merged.data);
				
				merged.content = cfwTable;
				return CFW.dashboard.createWidget(merged);
				
			},
    		getEditForm: function (widgetData) {
    			
    			var customForm = '<form>';
    			
    			//------------------------------
    			// Content
    			customForm += new CFWFormField({ type: "textarea", name: "userTableData", value: widgetData.userTableData, description: 'Values separated by semicolon, first row will be used as header.' }).createHTML();
    			customForm += '</form>';
    			
    			return customForm;
    		},
			onSave: function (form, widgetData) {
				console.log(form);
				var settingsForm = $(form);
				
				widgetData.userTableData = settingsForm.find('textarea[name="userTableData"]').val();
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