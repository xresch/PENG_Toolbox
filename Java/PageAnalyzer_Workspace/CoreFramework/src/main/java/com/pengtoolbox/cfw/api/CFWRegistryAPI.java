package com.pengtoolbox.cfw.api;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWStatement;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class CFWRegistryAPI {
	
	public static Logger logger = CFWLog.getLogger(CFWRegistryAPI.class.getName());
	
	private static ArrayList<APIDefinition> definitionArray = new ArrayList<APIDefinition>();
	
	/***********************************************************************
	 * Adds a APIConfiguration class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static void add(APIDefinition definition)  {
		definitionArray.add(definition);
	}
	
	/***********************************************************************
	 * Adds a APIConfiguration class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static void addAll(ArrayList<APIDefinition> definitions)  {
		if(definitions != null) {
			definitionArray.addAll(definitions);
		}
	}
	
	/***********************************************************************
	 * Removes a APIConfiguration class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static void remove(APIDefinition definition)  {
		definitionArray.remove(definition);
	}
	
	/***********************************************************************
	 * Removes a APIConfiguration class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static ArrayList<APIDefinition> getAPIDefinitions()  {
		return definitionArray;
	}
	
	/***********************************************************************
	 * Returns all API definitions as JSON array.
	 * @param definition
	 ***********************************************************************/
	public static String getJSONArray()  {
		
		StringBuilder json = new StringBuilder();
		
		json.append("["); 
		for(APIDefinition definition : definitionArray) {
			json.append(definition.getJSON());
			json.append(",");

		}
		
		if(definitionArray.size()>0) {
			json.deleteCharAt(json.length()-1); //remove last comma
		}
		json.append("]");
		return json.toString();
	}
	
	/***********************************************************************
	 * Creates default APIs for the object.
	 * 
	 ***********************************************************************/
	public static void createDefaults(CFWObject object) {
		
		Class clazz = object.getClass();
		String apiName = clazz.getSimpleName();
		
		//-----------------------------
		// Fetch
		APIDefinition fetchAPI = new APIDefinition(apiName, "fetch", clazz);
		fetchAPI.setDescription("Fetch "+apiName+" data. Use the parameters to specify which data you want to select. This will generate a select with WHERE ... AND clauses for the parameters defined.");
		fetchAPI.setRequestHandler(new APIRequestHandler() {
			
			@Override
			public void handleRequest(HttpServletRequest request, HttpServletResponse response, Class<? extends CFWObject> clazz) {
				
				JSONResponse json = new JSONResponse();
				
				//----------------------------------
				// Resolve Fields
				Enumeration<String> params = request.getParameterNames();
				
				CFWObject object;
				try {
					object = clazz.newInstance();
				} catch (Exception e) {
					new CFWLog(logger)
						.method("handleRequest")
						.severe("Could not create instance for '"+clazz.getSimpleName()+"'. Check if you have a constructor without parameters.", e);
				
					json.setSuccess(false);
					return;
				}
				ArrayList<CFWField> affectedFields = new ArrayList<CFWField>();
				ArrayList<String> fieldnames = new ArrayList<String>();
				boolean success = true;
				
				// iterate parameters
				while(params.hasMoreElements()) {
					String current = params.nextElement();
					CFWField field = object.getFieldIgnoreCase(current);
					if(field != null) {
						success = success && field.setValueValidated(request.getParameter(current));
						affectedFields.add(field);
						fieldnames.add(field.getName());
						
					}else {
						CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The parameter '"+current+"' is not supported.");
						success = false;
					}
				}
				
				//----------------------------------
				// Create Response
				if(success) {
					
					CFWStatement statement = object.select();
					
					for(int i = 0; i < affectedFields.size(); i++) {
						CFWField<?> currentField = affectedFields.get(i);
						if(i == 0) {
							statement.where(currentField.getName(), currentField.getValue());
						}else {
							statement.and(currentField.getName(), currentField.getValue());
						}
					}
					String payload = statement.getAsJSON();
					json.getContent().append(payload);
					
				}else {
					response.setStatus(HttpStatus.BAD_REQUEST_400);
				}
				
				json.setSuccess(success);

			}
		});
		
		CFW.Registry.API.add(fetchAPI);
		
	}
	 
}
