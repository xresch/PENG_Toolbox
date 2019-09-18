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
	
	public LinkedHashMap<String, CFWField> fields = new LinkedHashMap<String, CFWField>();
	
	public CFWObject() {
		
	}
	
	public boolean mapRequestParameters(HttpServletRequest request) {
		
		return CFW.HTTP.mapRequestParamsToFields(request, fields);
	}
	
	public BTForm toForm(String formID, String submitLabel) {
		
		BTForm form = new BTForm(formID, submitLabel);
		
		for(CFWField field : fields.values()) {
			form.addChild(field);
		}
		
		return form;
	}
	
	
	public void addField(CFWField field) {
		
		if(!fields.containsKey(field.getPropertyName())) {
			fields.put(field.getPropertyName(), field);
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The field with name '"+field.getPropertyName()+"' was already added to the object.");
		}
	}
	
	public void addFields(CFWField[] fields) {
		for(CFWField field : fields) {
			this.addField(field);
		}
	}
	
	public LinkedHashMap<String, CFWField> getFields(){
		return fields;
	}
	
	

}
