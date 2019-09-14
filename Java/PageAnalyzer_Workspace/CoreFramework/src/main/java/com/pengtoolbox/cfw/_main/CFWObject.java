package com.pengtoolbox.cfw._main;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;

public class CFWObject {
	
	private static Logger logger = CFWLog.getLogger(CFWObject.class.getName());
	
	public LinkedHashMap<String, CFWField> properties = new LinkedHashMap<String, CFWField>();
	
	public CFWObject() {
		
	}
	
	public CFWObject mapRequestParameters(HttpServletRequest request) {
		
		Enumeration<String> parameters = request.getParameterNames();
		
		while(parameters.hasMoreElements()) {
			String key = parameters.nextElement();
			
			if(!key.equals(BTForm.FORM_ID)) {
				if (properties.containsKey(key)) {
					CFWField field = properties.get(key);
					
					field.setValue(request.getParameter(key));
				}else {
					new CFWLog(logger)
						.method("CFWObject<init>")
						.severe("The field with name '"+key+"' is unknown for this type.");
				}
			}
		}
		
		return this;
	}
	
	public BTForm toForm(String formID, String submitLabel) {
		
		BTForm form = new BTForm(formID, submitLabel);
		
		for(CFWField field : properties.values()) {
			form.addChild(field);
		}
		
		return form;
	}
	
	
	public void addField(CFWField field) {
		
		if(!properties.containsKey(field.getPropertyName())) {
			properties.put(field.getPropertyName(), field);
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The field with name '"+field.getPropertyName()+"' was already added to the object.");
		}
	}
	
	

}
