package com.pengtoolbox.cfw.api;

import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;

public class APIDefinition {
	
	public static Logger logger = CFWLog.getLogger(APIDefinition.class.getName());
	
	private String apiName;
	private String actionName;
	private String description;

	private Class<? extends CFWObject> object;
	private CFWObject instance;
	
	private APIRequestHandler requestHandler;

	public APIDefinition(String apiName, String actionName, Class<? extends CFWObject> clazz) {
		this.apiName = apiName;
		this.actionName = actionName;
		this.object = object;
		
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

	public Class<? extends CFWObject> getObject() {
		return object;
	}

	public void setObject(Class<? extends CFWObject> object) {
		this.object = object;
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
			.append("\", \"params\"").append(": [");
		
		for(CFWField field : instance.getFields().values()) {
			
			json.append("{\"name\"").append(": \"").append(field.getName().toLowerCase())
			.append("\", \"type\"").append(": \"").append(field.getValueClass().getSimpleName())
			.append("\", \"description\"").append(": \"").append(field.getDescription()).append("\"},");
		}
		
		if(instance.getFields().size() > 0) {
			json.deleteCharAt(json.length()-1); //remove last comma
		}
		json.append("]}");
		
		return json.toString();
	}
	
	
	
	

}
