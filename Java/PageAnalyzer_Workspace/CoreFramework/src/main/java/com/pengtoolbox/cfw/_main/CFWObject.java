package com.pengtoolbox.cfw._main;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
		
		return CFWField.mapAndValidateParamsToFields(request, fields);
	}
	
	public boolean mapResultSet(ResultSet result) {

		return CFWField.mapResultSetColumnsToFields(result, fields);
	}
	
	public BTForm toForm(String formID, String submitLabel) {
		
		BTForm form = new BTForm(formID, submitLabel);
		form.setOrigin(this);
		
		for(CFWField field : fields.values()) {
			form.addChild(field);
		}
		
		return form;
	}
	
	
	public void addField(CFWField field) {
		
		if(!fields.containsKey(field.getName())) {
			fields.put(field.getName(), field);
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The field with name '"+field.getName()+"' was already added to the object.");
		}
	}
	
	public void addAllFields(CFWField[] fields) {
		for(CFWField field : fields) {
			this.addField(field);
		}
	}
	
	public void addFields(CFWField ...fields) {
		for(CFWField field : fields) {
			this.addField(field);
		}
	}
	
	public CFWField getField(String name) {
		return fields.get(name);
	}
	
	public LinkedHashMap<String, CFWField> getFields(){
		return fields;
	}
	
	

}
