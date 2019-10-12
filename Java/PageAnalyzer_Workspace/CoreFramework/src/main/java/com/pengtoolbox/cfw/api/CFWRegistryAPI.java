package com.pengtoolbox.cfw.api;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
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
	
	private static LinkedHashMap<String, APIDefinition> definitionArray = new LinkedHashMap<String, APIDefinition>();
	
	public static String getFullyQualifiedName(APIDefinition definition) {
		return definition.getApiName()+"-"+definition.getActionName();
	}
	public static String getFullyQualifiedName(String name, String action) {
		return name+"-"+action;
	}
	/***********************************************************************
	 * Adds a APIDefinition class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static void add(APIDefinition definition)  {
		String fullname = getFullyQualifiedName(definition);
		if(!definitionArray.containsKey(fullname)) {
			definitionArray.put(fullname,definition);
		}else {
			new CFWLog(logger)
				.method("add")
				.warn("An API definition with name'"+fullname+"' was already defined. Appending a number to the name.");
			
			int i = 0;
			do {
				i++;
				definition.setApiName(definition.getApiName()+i);
			}while ( definitionArray.containsKey(getFullyQualifiedName(definition)) );
			
			definitionArray.put(getFullyQualifiedName(definition), definition);
		}
	}
	
	/***********************************************************************
	 * Adds a APIDefinition class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static void addAll(ArrayList<APIDefinition> definitions)  {
		if(definitions != null) {
			for(APIDefinition definition : definitions) {
				CFWRegistryAPI.add(definition);
			}
		}
	}
	
	/***********************************************************************
	 * Removes a APIDefinition class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static void remove(APIDefinition definition)  {
		definitionArray.remove(definition);
	}
	
	/***********************************************************************
	 * Returns a APIDefinition class for the given name.
	 * @param definition
	 ***********************************************************************/
	public static APIDefinition getDefinition(String apiName, String actionName)  {
		return definitionArray.get(getFullyQualifiedName(apiName, actionName));
	}
	
	/***********************************************************************
	 * Removes a APIDefinition class to the registry.
	 * @param definition
	 ***********************************************************************/
	public static LinkedHashMap<String, APIDefinition> getAPIDefinitions()  {
		return definitionArray;
	}
	
	/***********************************************************************
	 * Returns all API definitions as JSON array.
	 * @param definition
	 ***********************************************************************/
	public static String getJSONArray()  {
		
		StringBuilder json = new StringBuilder();
		
		json.append("["); 
		for(APIDefinition definition : definitionArray.values()) {
			json.append(definition.getJSON());
			json.append(",");
		}
		
		//--------------------------
		//remove last comma
		if(definitionArray.size()>0) {
			json.deleteCharAt(json.length()-1); 
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
					String currentValue = request.getParameter(current);
					
					CFWField field = object.getFieldIgnoreCase(current);

					if(field != null) {
						if(currentValue != null && !currentValue.isEmpty()) {
							field.setValueValidated(request.getParameter(current));
							affectedFields.add(field);
							fieldnames.add(field.getName());
						}
						
					}
				}
				
				//----------------------------------
				// Create Response
				if(success) {
					
					CFWStatement statement = object.select(fetchAPI.getReturnFieldNames().toArray(new String[] {}));
					
					for(int i = 0; i < affectedFields.size(); i++) {
						CFWField<?> currentField = affectedFields.get(i);
						if(i == 0) {
							statement.where(currentField.getName(), currentField.getValue(), false);
						}else {
							statement.and(currentField.getName(), currentField.getValue(), false);
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
