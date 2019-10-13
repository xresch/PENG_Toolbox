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

	private String[] inputFieldnames = null;
	private String[] outputFieldnames = null;
	
	private Class<? extends CFWObject> clazz;
	private CFWObject instance;
	
	private APIRequestHandler requestHandler;

	public APIDefinition(			  
			  Class<? extends CFWObject> clazz,
			  String apiName, 
			  String actionName, 
			  String[] inputFieldnames,
			  String[] outputFieldnames) {
		
		this.apiName = apiName;
		this.actionName = actionName;
		this.clazz = clazz;
		this.inputFieldnames = inputFieldnames;
		this.outputFieldnames = outputFieldnames;
		
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			new CFWLog(logger)
				.method("APIDefinition.<init>")
				.severe("Could not create instance for '"+clazz.getSimpleName()+"'. Check if you have a constructor without parameters.", e);
			return;
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
		return clazz;
	}
	
	public CFWObject getObjectInstance() {
		CFWObject object = null;
		try {
			object = getObjectClass().newInstance();
		} catch (Exception e) {
			new CFWLog(logger)
				.method("handleRequest")
				.severe("Could not create instance for '"+getObjectClass().getSimpleName()+"'. Check if you have a constructor without parameters.", e);
		}
		return object;
	}
	


	public void setObject(Class<? extends CFWObject> clazz) {
		this.clazz = clazz;
	}
	
	
	public void setInputFieldnames(String[] inputFieldnames) {
		this.inputFieldnames = inputFieldnames;
	}


	public String[] getInputFieldnames() {
		return inputFieldnames;
	}

	public void setOutputFieldnames(String[] outputFieldnames) {
		this.outputFieldnames = outputFieldnames;
	}
	
	public String[] getOutputFieldnames() {
		return outputFieldnames;
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
			
		for(String name : inputFieldnames) {
			CFWField field = instance.getField(name);
			json.append("{\"name\"").append(": \"").append(field.getName().toLowerCase())
			.append("\", \"type\"").append(": \"").append(field.getValueClass().getSimpleName())
			.append("\", \"description\"").append(": \"").append(field.getDescription()).append("\"},");
		}
		
		if(inputFieldnames.length > 0) {
			json.deleteCharAt(json.length()-1); //remove last comma
		}
		json.append("]");
		
		//-----------------------------------
		//resolve parameters
		json.append(", \"returnValues\"").append(": [");
			
		for(String name : outputFieldnames) {
			CFWField field = instance.getField(name);
			json.append("{\"name\"").append(": \"").append(field.getName().toLowerCase())
			.append("\", \"type\"").append(": \"").append(field.getValueClass().getSimpleName())
			.append("\", \"description\"").append(": \"").append(field.getDescription()).append("\"},");
		}
		
		if(inputFieldnames.length > 0) {
			json.deleteCharAt(json.length()-1); //remove last comma
		}
		json.append("]");
		
		json.append("}");
		return json.toString();
	}
	
	
	
	

}
