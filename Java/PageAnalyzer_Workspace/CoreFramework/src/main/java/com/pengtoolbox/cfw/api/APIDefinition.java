package com.pengtoolbox.cfw.api;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;

public class APIDefinition {
	
	public static Logger logger = CFWLog.getLogger(APIDefinition.class.getName());
	
	private String apiName;
	private String actionName;
	private String description;

	private ArrayList<String> parameterNames = new ArrayList<String>();
	private ArrayList<String> returnFieldNames = new ArrayList<String>();
	
	private Class<? extends CFWObject> object;
	private CFWObject instance;
	
	private APIRequestHandler requestHandler;

	public APIDefinition(String apiName, String actionName, Class<? extends CFWObject> clazz) {
		this.apiName = apiName;
		this.actionName = actionName;
		this.object = clazz;
		
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			new CFWLog(logger)
				.method("APIDefinition.<init>")
				.severe("Could not create instance for '"+clazz.getSimpleName()+"'. Check if you have a constructor without parameters.", e);
			return;
		}
		
		for(CFWField field : instance.getFields().values()) {
			if(field.isAPIParameter()) {
				parameterNames.add(field.getName());
			}
			if(field.isAPIReturnValue()) {
				returnFieldNames.add(field.getName());
			}
		}
	}
	
	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	
	public String getFullyQualifiedName() {
		return apiName+"-"+actionName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class<? extends CFWObject> getObjectClass() {
		return object;
	}

	public void setObject(Class<? extends CFWObject> object) {
		this.object = object;
	}
	
	public ArrayList<String> getParameterNames() {
		return parameterNames;
	}

	public ArrayList<String> getReturnFieldNames() {
		return returnFieldNames;
	}

	public APIRequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(APIRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	public String getJSON() {
		
		StringBuilder json = new StringBuilder("{");
		json.append("\"name\"").append(": \"").append(apiName)
			.append("\", \"action\"").append(": \"").append(actionName)
			.append("\", \"description\"").append(": \"").append(description);
		
		//-----------------------------------
		//resolve parameters
		json.append("\", \"params\"").append(": [");
			
		for(String name : parameterNames) {
			CFWField field = instance.getField(name);
			json.append("{\"name\"").append(": \"").append(field.getName().toLowerCase())
			.append("\", \"type\"").append(": \"").append(field.getValueClass().getSimpleName())
			.append("\", \"description\"").append(": \"").append(field.getDescription()).append("\"},");
		}
		
		if(parameterNames.size() > 0) {
			json.deleteCharAt(json.length()-1); //remove last comma
		}
		json.append("]");
		
		//-----------------------------------
		//resolve parameters
		json.append(", \"returnValues\"").append(": [");
			
		for(String name : returnFieldNames) {
			CFWField field = instance.getField(name);
			json.append("{\"name\"").append(": \"").append(field.getName().toLowerCase())
			.append("\", \"type\"").append(": \"").append(field.getValueClass().getSimpleName())
			.append("\", \"description\"").append(": \"").append(field.getDescription()).append("\"},");
		}
		
		if(parameterNames.size() > 0) {
			json.deleteCharAt(json.length()-1); //remove last comma
		}
		json.append("]");
		
		json.append("}");
		return json.toString();
	}
	
	
	
	

}
