package com.pengtoolbox.cfw.response.bootstrap;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWObject;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.CFWField.FormFieldType;

/**********************************************************************************
 * Class for creating a form for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class BTForm extends HierarchicalHTMLItem {
	
	public static final String FORM_ID = "cfw-formID";
	private String formID = "";
	private String submitLabel = "";
	private String postURL;
	
	public LinkedHashMap<String, CFWField> fields = new LinkedHashMap<String, CFWField>();
	
	private BTFormHandler formHandler = null;
	
	public BTForm(String formID, String submitLabel) {
		this.formID = formID;
		this.submitLabel = submitLabel;
		
		this.addChild(new CFWField(FormFieldType.HIDDEN, BTForm.FORM_ID).setValue(formID));
		
		// Default post to servlet creating the form
		postURL = CFW.Context.Request.getRequest().getRequestURI();
		
		CFW.Context.Session.addForm(this);
	}
	
	public BTForm(String formID, String submitLabel, CFWObject origin) {
		this(formID, submitLabel);
		this.addFields(origin.getFields().values().toArray(new CFWField[]{}));
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected void createHTML(StringBuilder html) {

		html.append("<form id=\""+formID+"\" class=\"form\" method=\"post\">");
		
		if(this.hasChildren()) {
				
			for(HierarchicalHTMLItem child : children) {
				html.append("\n\t"+child.getHTML());
			}
		}
		
		if(this.hasOneTimeChildren()) {
			
			for(HierarchicalHTMLItem child : oneTimeChildren) {
				html.append("\n\t"+child.getHTML());
			}
		}

		String onclick = "cfw_postJSON('"+postURL+"', $('#"+formID+"').serialize())";
		html.append("<input type=\"button\" onclick=\""+onclick+"\" class=\"form-control btn-primary\" value=\""+submitLabel+"\">");
		html.append("</form>");
	}	

	public String getLabel() {
		return formID;
	}

	
	public void addField(CFWField field) {
		
		if(!fields.containsKey(field.getPropertyName())) {
			fields.put(field.getPropertyName(), field);
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The field with name '"+field.getPropertyName()+"' was already added to the object.");
		}
		
		this.addChild(field);
	}
	
	public void addFields(CFWField[] fields) {
		for(CFWField field : fields) {
			this.addField(field);
		}
	}
	
	public String getFormID() {
		return formID;
	}

	public BTForm setLabel(String label) {
		fireChange();
		this.formID = label;
		return this;
	}
	
	public BTForm setFormHandler(BTFormHandler formHandler) {
		fireChange();
		postURL = "/cfw/formhandler";
		this.formHandler = formHandler;
		return this;
	}
	
	public BTFormHandler getFormHandler() {
		return formHandler;
	}
	
	public boolean mapRequestParameters(HttpServletRequest request) {
		
		return CFW.HTTP.mapRequestParamsToFields(request, fields);
	}

}
