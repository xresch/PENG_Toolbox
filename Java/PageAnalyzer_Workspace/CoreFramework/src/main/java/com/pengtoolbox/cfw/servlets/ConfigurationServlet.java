package com.pengtoolbox.cfw.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.config.Configuration;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.BTFormHandler;

public class ConfigurationServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(ConfigurationServlet.class.getName());
	
	public ConfigurationServlet() {
	
	}
	
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
		
		log.info(request.getRequestURL().toString());
				
		HTMLResponse html = new HTMLResponse("Configuration Management");
		
		StringBuffer content = html.getContent();
		
		if(CFW.Context.Request.hasPermission(CFWDBPermission.CFW_CONFIG_MANAGEMENT)) {
			
			content.append("<h1>Configuration Management</h1>");
			
			BTForm configForm = createConfigForm();
			content.append(configForm.getHTML());
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
        
    }
	
	private static BTForm createConfigForm() {
				
		//--------------------------------------
		// Create Group Form
		
		BTForm configForm = new BTForm("cfw-config-mgmt", "Save");
		
		ArrayList<CFWObject> configObjects = CFW.DB.Config.getConfigObjectList();
		for(CFWObject object :  configObjects) {
			Configuration config = (Configuration)object;
			CFWField<String> field = CFWField.newString(FormFieldType.valueOf(config.type()), ""+config.id());
			field.setLabel(config.name());
			field.setValue(config.value());
			field.setDescription(config.description());
			field.setOptions(config.options());			
			
			configForm.addField(field);
		}
		
		configForm.setFormHandler(new BTFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form, CFWObject origin) {
								
					form.mapRequestParameters(request);
					
					JSONResponse json = new JSONResponse();
			    	json.addAlert(MessageType.SUCCESS, "Form was recieved!");
			    	json.addAlert(MessageType.INFO, form.getFieldsAsKeyValueHTML());
			    	
			    	for(CFWField<String> field : form.getFields().values() ) {
			    		CFW.DB.Config.updateValue(Integer.parseInt(field.getName()), field.getValue());
			    	}
			    	CFW.DB.Config.updateCache();
			    	
				}
				
			}
		);
		
		return configForm;
	}
	
}