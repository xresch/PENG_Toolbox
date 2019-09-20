package com.pengtoolbox.cfw.response.bootstrap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWHttp;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.utils.TextUtils;
import com.pengtoolbox.cfw.validation.IValidatable;
import com.pengtoolbox.cfw.validation.IValidator;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * 
 * When adding more supported types, you have to update as well:
 *   - CFWDB.prepareStatement()
 *   -
 *   
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class CFWField<T> extends HierarchicalHTMLItem implements IValidatable<T> {
	
	private Class<T> fieldClass;
	private FormFieldType type;
	private String formLabel = "&nbsp;";
	
	private ArrayList<IValidator> validatorArray = new ArrayList<IValidator>();
	private String name = "";
	private T value;
	
	private ArrayList<String> invalidMessages;
	
	public enum FormFieldType{
		TEXT, TEXTAREA, PASSWORD, HIDDEN, NONE,
	}
	
	//###################################################################################
	// CONSTRUCTORS
	//###################################################################################
	private CFWField(Class clazz, FormFieldType type, String fieldID) {
		this.fieldClass = clazz;
		this.type = type;
		this.name = fieldID;
		this.formLabel = TextUtils.fieldNameToLabel(fieldID);
	}
			
	//###################################################################################
	// Initializer
	//###################################################################################
	public static CFWField<String> newString(FormFieldType type, String fieldID){
		return new CFWField<String>(String.class, type, fieldID);
	}
	
	public static CFWField<Integer> newInteger(FormFieldType type, String fieldID){
		return new CFWField<Integer>(Integer.class, type, fieldID);
	}
	
	public static CFWField<Boolean> newBoolean(FormFieldType type, String fieldID){
		return new CFWField<Boolean>(Boolean.class, type, fieldID);
	}
		
	//###################################################################################
	// METHODS
	//###################################################################################
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected void createHTML(StringBuilder html) {

		//---------------------------------------------
		// Create Form Group
		//---------------------------------------------
		if(type != FormFieldType.HIDDEN && type != FormFieldType.NONE) {
			html.append("<div class=\"form-group row\">");
			html.append("  <label class=\"col-sm-3 col-form-label\" for=\""+name+"\" >"+formLabel+":</label> ");
			html.append("  <div class=\"col-sm-9\">");
		}
		
		//---------------------------------------------
		// Create Field
		//---------------------------------------------
		this.addAttribute("placeholder", formLabel);
		this.addAttribute("name", name);
		
		if(value != null) {	this.addAttribute("value", value.toString()); };
		
		switch(type) {
			case TEXT:  		html.append("<input type=\"text\" class=\"form-control\" "+this.getAttributesString()+"/>");
								break;
								
			case TEXTAREA: 		createTextArea(html);
								break;
								
			case HIDDEN:  		html.append("<input type=\"hidden\" "+this.getAttributesString()+"/>");
								break;
								
			case PASSWORD:  	html.append("<input type=\"password\" class=\"form-control\" "+this.getAttributesString()+"/>");
								break;
			
			case NONE:			//do nothing
								break;
			
		}
		
		//---------------------------------------------
		// Close Form
		//---------------------------------------------
		if(type != FormFieldType.HIDDEN && type != FormFieldType.NONE) {
			html.append("</div>");
			html.append("</div>");
		}
	}

	public String getLabel() {
		return formLabel;
	}

	public CFWField<T> setLabel(String label) {
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
	
	public CFWField<T> addValidator(IValidator validator) {
		if(!validatorArray.contains(validator)) {
			validatorArray.add(validator);
			validator.setValidateable(this);
		}
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public CFWField<T> addAttribute(String name, String value) {
		return (CFWField<T>)super.addAttribute(name, value);
	}

	public boolean removeValidator(IValidator o) {
		return validatorArray.remove(o);
	}
	
	public CFWField<T> setName(String propertyName) {
		this.name = propertyName;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean setValueValidated(T value) {
		
		boolean result = true;
		if(this.validateValue(value)) {
			this.value = value;
		}else {
			result = false;
			StringBuilder errorMessage = new StringBuilder("The field '"+formLabel+"' cannot be set to the value '"+value+"': <ul>");
			for(String message : invalidMessages) {
				errorMessage.append("<li>"+message+"</li>");
				CFW.Context.Request.addAlertMessage(MessageType.ERROR, message);
			}
			errorMessage.append("</ul>");
		}
		return result;
	}
	
	public CFWField<T> setValueNotValidated(T value) {
		this.value = value;
		return this;
	}
	
	public T getValue() {
		return value;
	}
	
	public Class<T> getFieldClass() {
		return fieldClass;
	}

	/******************************************************************************************************
	 * Map the values of request parameters to CFWFields.
	 * @param url used for the request.
	 * @return true if successful, false otherwise
	 ******************************************************************************************************/
	public static boolean mapAndValidateParamsToFields(HttpServletRequest request, HashMap<String,CFWField> fields) {
		
		Enumeration<String> parameters = request.getParameterNames();
		boolean result = true;
		
		while(parameters.hasMoreElements()) {
			String key = parameters.nextElement();
			
			if(!key.equals(BTForm.FORM_ID)) {
				if (fields.containsKey(key)) {
					CFWField field = fields.get(key);
					
					if(!field.setValueValidated(request.getParameter(key)) ){
						result = false;
					}
				}else {
					new CFWLog(CFWHttp.logger)
						.method("CFWObject<init>")
						.severe("The field with name '"+key+"' is unknown for this type.");
				}
			}
		}
		
		return result;
	}
	
	/******************************************************************************************************
	 * Map the values of request parameters to CFWFields.
	 * @param url used for the request.
	 * @return true if successful, false otherwise
	 ******************************************************************************************************/
	public static boolean mapResultSetColumnsToFields(ResultSet result, HashMap<String,CFWField> fields) {
		
		ResultSetMetaData metadata;
		boolean success = true;
		try {
			
			if(result == null) {
				return false;
			}
			//--------------------------------------
			// Check has results
			if(result.isBeforeFirst()) {
				result.next();
			}
			metadata = result.getMetaData();

			
			for(int i=1; i <= metadata.getColumnCount(); i++) {
				String colName = metadata.getColumnName(i);
				
				if(fields.containsKey(colName)) {
					CFWField current = fields.get(colName);
					
					if      (current.getFieldClass() == String.class)  { current.setValueValidated(result.getString(colName)); }
					else if(current.getFieldClass() == Integer.class)  { current.setValueValidated(result.getInt(colName)); }
					else if(current.getFieldClass() == Boolean.class)  { current.setValueValidated(result.getBoolean(colName)); }
				}
			}
		
		} catch (SQLException e) {
			success = false;
			e.printStackTrace();
		}
		
		
//		for(CFWField field : fields.values()) {
//			this.id = result.getInt(PermissionDBFields.PK_ID.toString());
//			this.name = result.getString(PermissionDBFields.NAME.toString());
//			this.description = result.getString(PermissionDBFields.DESCRIPTION.toString());
//			this.isDeletable = result.getBoolean(PermissionDBFields.IS_DELETABLE.toString());
//		}
		
		return success;
	}
		
}
