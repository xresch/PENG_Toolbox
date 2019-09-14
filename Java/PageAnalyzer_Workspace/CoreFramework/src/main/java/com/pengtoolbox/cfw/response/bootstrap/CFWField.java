package com.pengtoolbox.cfw.response.bootstrap;

import java.util.ArrayList;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.utils.TextUtils;
import com.pengtoolbox.cfw.validation.IValidatable;
import com.pengtoolbox.cfw.validation.IValidator;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class CFWField<T> extends HierarchicalHTMLItem implements IValidatable<T> {
	
	private FormFieldType type;
	private String formLabel = "&nbsp;";
	
	private ArrayList<IValidator> validatorArray = new ArrayList<IValidator>();
	private String propertyName = "";
	protected T value;
	
	private ArrayList<String> invalidMessages;
	
	public enum FormFieldType{
		TEXT, TEXTAREA, HIDDEN
	}
	
	public CFWField(FormFieldType type, String fieldID) {
		this.type = type;
		this.propertyName = fieldID;
		this.formLabel = TextUtils.fieldNameToLabel(fieldID);
	}
	public CFWField(FormFieldType type, String fieldID, String fieldLabel) {
		this.type = type;
		this.propertyName = fieldID;
		this.formLabel = fieldLabel;
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected void createHTML(StringBuilder html) {

		//---------------------------------------------
		// Create Form Group
		//---------------------------------------------
		if(type != FormFieldType.HIDDEN) {
			html.append("<div class=\"form-group row\">");
			html.append("  <label class=\"col-sm-3 col-form-label\" for=\""+propertyName+"\" >"+formLabel+":</label> ");
			html.append("  <div class=\"col-sm-9\">");
		}
		
		//---------------------------------------------
		// Create Field
		//---------------------------------------------
		this.addAttribute("placeholder", formLabel);
		this.addAttribute("name", propertyName);
		
		if(value != null) {	this.addAttribute("value", value.toString()); };
		
		switch(type) {
			case TEXT:  		html.append("<input type=\"text\" class=\"form-control\" "+this.getAttributesString()+"/>");
								break;
								
			case TEXTAREA: 		createTextArea(html);
								break;
								
			case HIDDEN:  		html.append("<input type=\"hidden\" "+this.getAttributesString()+"/>");
			break;
		}
		
		//---------------------------------------------
		// Close Form
		//---------------------------------------------
		if(type != FormFieldType.HIDDEN) {
			html.append("</div>");
			html.append("</div>");
		}
	}

	public String getLabel() {
		return formLabel;
	}

	public CFWField setLabel(String label) {
		fireChange();
		this.formLabel = label;
		return this;
	}
	
	/***********************************************************************************
	 * Create the text area
	 ***********************************************************************************/
	private void createTextArea(StringBuilder html) {
		
		if(!this.attributes.containsKey("rows")) {
			this.addAttribute("rows", "5");
		}
		html.append("<textarea class=\"form-control\" "+this.getAttributesString()+"></textarea>");
	}
	
	
	
	//######################################################################################
	// IValidatable Implementation 
	//######################################################################################
	
	/*************************************************************************
	 * Executes all validators added to this instance and validates the current
	 * value.
	 * 
	 * @return true if all validators returned true, false otherwise
	 *************************************************************************/ 
	public boolean validate(){
		
		boolean isValid = true;
		invalidMessages = new ArrayList<String>();
		
		for(IValidator validator : validatorArray){
			
			if(!validator.validate(value)){
				invalidMessages.add(validator.getInvalidMessage());
				isValid=false;
			}
		}
		
		return isValid;
	}
	
	/*************************************************************************
	 * Executes all validators added to the instance of this class.
	 * 
	 * @return true if all validators returned true, false otherwise
	 *************************************************************************/ 
	public boolean validateValue(Object value){
		
		boolean isValid = true;
		invalidMessages = new ArrayList<String>();
		
		for(IValidator validator : validatorArray){
			
			if(!validator.validate(value)){
				invalidMessages.add(validator.getInvalidMessage());
				
				isValid=false;
			}
		}
		
		return isValid;
	}
	
	/*************************************************************************
	 * Returns all the InvalidMessages from the last validation execution. 
	 *************************************************************************/ 
	public ArrayList<String> getInvalidMessages() {
		return invalidMessages;
	}
	
	public IValidatable<T> addValidator(IValidator validator) {
		if(!validatorArray.contains(validator)) {
			validatorArray.add(validator);
			validator.setValidateable(this);
		}
		
		return this;
	}

	public boolean removeValidator(IValidator o) {
		return validatorArray.remove(o);
	}
	
	public IValidatable<T> setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public CFWField<T> setValue(T value) {
		System.out.println("######### VALUE: "+value);
		if(this.validateValue(value)) {
			this.value = value;
		}else {
			StringBuilder errorMessage = new StringBuilder("The field '"+formLabel+"' cannot be set to the value '"+value+"': <ul>");
			for(String message : invalidMessages) {
				errorMessage.append("<li>"+message+"</li>");
				CFW.Context.Request.addAlertMessage(MessageType.ERROR, message);
			}
			errorMessage.append("</ul>");
		}
		return this;
	}
	
	public T getValue() {
		return value;
	}
	
}
