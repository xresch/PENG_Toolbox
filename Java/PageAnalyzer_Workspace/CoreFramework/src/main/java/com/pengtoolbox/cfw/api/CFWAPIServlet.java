package com.pengtoolbox.cfw.api;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User.UserFields;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.BTFormHandler;
import com.pengtoolbox.cfw.validation.LengthValidator;
import com.pengtoolbox.cfw.validation.NotNullOrEmptyValidator;
import com.pengtoolbox.cfw.validation.PasswordValidator;

public class CFWAPIServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(CFWAPIServlet.class.getName());
	

	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		CFWLog log = new CFWLog(logger).method("doGet");
	
		if(CFW.Context.Request.hasPermission(Permission.CFW_API)) {
			String name = request.getParameter("name");
			String action = request.getParameter("action");
			String data = request.getParameter("overviewdata");
			
			//Use the name of the APIDefinition
			String form = request.getParameter("form");
			

			
			if(name != null) {
				handleAPIRequest(request, response);
				return;
			}
			
			//--------------------------------
			// Return example form
			if(form != null) {
				createForm(request, response);
				return;
			}
			
			//--------------------------------
			// Return data for overview Page
			if(data != null) {
				JSONResponse json = new JSONResponse();
				json.getContent().append(CFW.Registry.API.getJSONArray());
				return;
			}
		
			//---------------------------
			// Create Overview Page
			createOverview(request, response);
		}else {
			HTMLResponse html = new HTMLResponse("Error");
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied.");
		}
        
    }
	
	/***************************************************************************************************
	 * 
	 * @param request
	 * @param response
	 ****************************************************************************************************/
	private void createOverview(HttpServletRequest request, HttpServletResponse response) {
		HTMLResponse html = new HTMLResponse("API");
		
		StringBuffer content = html.getContent();
		
		if(CFW.Context.Request.hasPermission(Permission.CFW_API)) {
			
			//html.addJSFileBottomSingle(new FileDefinition(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_apioverview.js"));
			html.addJSFileBottomAssembly(HandlingType.JAR_RESOURCE, FileDefinition.CFW_JAR_RESOURCES_PATH+".js", "cfw_apioverview.js");
			
			html.addJavascriptCode("cfw_apioverview_draw();");
			
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
			
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access denied!!!");
		}
	}
	
	
	/***************************************************************************************************
	 * 
	 * @param request
	 * @param response
	 ****************************************************************************************************/
	private void handleAPIRequest(HttpServletRequest request, HttpServletResponse response) {
		
		String apiName = request.getParameter("name");
		String action = request.getParameter("action");

		//--------------------------------------
		// Get API Definition
		APIDefinition definition = CFW.Registry.API.getDefinition(apiName, action);
		
		JSONResponse json = new JSONResponse();
		if(definition == null) {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The API definition could not be found - name: "+apiName+", action: "+action);
			json.setSuccess(false);
			return;
		}
		
		definition.getRequestHandler().handleRequest(request, response, definition.getObjectClass());
		
	}
	
	/***************************************************************************************************
	 * 
	 * @param request
	 * @param response
	 ****************************************************************************************************/
	private void createForm(HttpServletRequest request, HttpServletResponse response) {
		
		String apiName = request.getParameter("form");
		String action = request.getParameter("action");
		String callbackMethod = request.getParameter("callbackMethod");
		//--------------------------------------
		// Get API Definition
		APIDefinition definition = CFW.Registry.API.getDefinition(apiName, action);
		
		JSONResponse json = new JSONResponse();
		if(definition == null) {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The API definition could not be found - name: "+apiName+", action: "+action);
			json.setSuccess(false);
			return;
		}
		
		//--------------------------------------
		// Create User Form
		CFWObject object;
		try {
			object = (CFWObject)definition.getObjectClass().newInstance();
		} catch (Exception e) {
			new CFWLog(logger)
			.method("handleRequest")
			.severe("Could not create instance for '"+definition.getObjectClass().getSimpleName()+"'. Check if you have a constructor without parameters.", e);
	
			json.setSuccess(false);
			return;
		
		} 
		
		BTForm sampleForm = object.toForm("cfwAPIFormExample"+apiName+action, "Submit", definition.getParameterNames().toArray(new String[] {}));	
		sampleForm.isAPIForm(true);
		sampleForm.setResultCallback(callbackMethod);
		sampleForm.setFormHandler(new BTFormHandler() {
			
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form, CFWObject origin) {
				
				definition.getRequestHandler().handleRequest(request, response, definition.getObjectClass());
				
			}
		});
		
		sampleForm.appendToPayload(json);		
	}
	
}