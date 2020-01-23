package com.pengtoolbox.cfw.features.config;

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
import com.pengtoolbox.cfw.features.usermgmt.Permission;
import com.pengtoolbox.cfw.datahandling.CFWForm;
import com.pengtoolbox.cfw.datahandling.CFWFormHandler;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class ServletConfiguration extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(ServletConfiguration.class.getName());
	
	public ServletConfiguration() {
	
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
		
		if(CFW.Context.Request.hasPermission(FeatureConfiguration.PERMISSION_CONFIGURATION)) {
			
			content.append("<h1>Configuration Management</h1>");
			
			CFWForm configForm = createConfigForm();
			content.append(configForm.getHTML());
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
        
    }
	
	private static CFWForm createConfigForm() {
				
		//--------------------------------------
		// Create Group Form
		
		CFWForm configForm = new CFWForm("cfwConfigMgmt", "Save");
		
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
		
		configForm.setFormHandler(new CFWFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, CFWForm form, CFWObject origin) {
								
					form.mapRequestParameters(request);
					
					JSONResponse json = new JSONResponse();
			    	
			    	boolean success = true;
			    	
			    	for(CFWField<?> field : form.getFields().values() ) {
			    		String value = (field.getValue() != null) ? field.getValue().toString() : "";
			    		success = success && CFW.DB.Config.updateValue(Integer.parseInt(field.getName()), value);
			    	}
			    	CFW.DB.Config.updateCache();
			    	
			    	if(success) {
			    		json.addAlert(MessageType.SUCCESS, "Saved!");
			    	}else {
			    		json.addAlert(MessageType.SUCCESS, "Something went wrong!");
			    	}
			    	
				}
				
			}
		);
		
		return configForm;
	}
	
}