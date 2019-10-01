package com.pengtoolbox.cfw.datahandling;

import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWHttp;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.HierarchicalHTMLItem;
import com.pengtoolbox.cfw.utils.TextUtils;
import com.pengtoolbox.cfw.validation.BooleanValidator;
import com.pengtoolbox.cfw.validation.IValidatable;
import com.pengtoolbox.cfw.validation.IValidator;
import com.pengtoolbox.cfw.validation.IntegerValidator;

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
	
	private Class<T> valueClass;
	private FormFieldType type;
	private String formLabel = "&nbsp;";
	private String columnDefinition = null;
	private boolean isDisabled = false;
	
	private CFWFieldChangeHandler changeHandler = null;
	
	private ArrayList<IValidator> validatorArray = new ArrayList<IValidator>();
	private String name = "";
	private Object value;
	
	private ArrayList<String> invalidMessages;
	
	public enum FormFieldType{
		TEXT, TEXTAREA, PASSWORD, HIDDEN, BOOLEAN, DATEPICKER, NONE
	}
		
	//###################################################################################
	// CONSTRUCTORS
	//###################################################################################
	private CFWField(Class clazz, FormFieldType type, String fieldID) {
		this.valueClass = clazz;
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
		return new CFWField<Integer>(Integer.class, type, fieldID)
				.addValidator(new IntegerValidator());
	}
	
	public static CFWField<Boolean> newBoolean(FormFieldType type, String fieldID){
		return new CFWField<Boolean>(Boolean.class, type, fieldID)
				.addValidator(new BooleanValidator());
	}
	
	public static CFWField<Boolean> newTimestamp(FormFieldType type, String fieldID){
		return new CFWField<Boolean>(Timestamp.class, type, fieldID)
				.addValidator(new BooleanValidator());
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
		
		if(isDisabled) {	this.addAttribute("disabled", "disabled");};
		if(value != null) {	this.addAttribute("value", value.toString()); };
		
		switch(type) {
			case TEXT:  		html.append("<input type=\"text\" class=\"form-control\" "+this.getAttributesString()+"/>");
								break;
								
			case TEXTAREA: 		createTextArea(html);
								break;
								
			case HIDDEN:  		html.append("<input type=\"hidden\" "+this.getAttributesString()+"/>");
								break;
			
			case BOOLEAN:  		createBooleanRadiobuttons(html);
								break;		
								
			case DATEPICKER:  	createDatePicker(html);
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

	private void createBooleanRadiobuttons(StringBuilder html) {
		
		String falseChecked = "";
		String trueChecked = "";
		
		if(value.toString().trim().toLowerCase().equals("true")) {
			trueChecked = "checked";
		}else {
			falseChecked = "checked";
		}
		
		String disabled = "";
		if(isDisabled) {	disabled = "disabled=\"disabled\""; };
		
		html.append("<div class=\"form-check form-check-inline col-form-labelmt-5\">" + 
			"  <input class=\"form-check-input\" type=\"radio\" value=\"true\" name="+name+" "+disabled+" "+trueChecked+"/>" + 
			"  <label class=\"form-check-label\" for=\"inlineRadio1\">true</label>" + 
			"</div>");
		
		html.append("<div class=\"form-check form-check-inline col-form-label\">" + 
				"  <input class=\"form-check-input\" type=\"radio\" value=\"false\" name="+name+" "+disabled+" "+falseChecked+"/>" + 
				"  <label class=\"form-check-label\" for=\"inlineRadio1\">false</label>" + 
				"</div>");
	}
	
	/***********************************************************************************
	 * Create DatePicker
	 ***********************************************************************************/
	private void createDatePicker(StringBuilder html) {
		
		//---------------------------------
		// Set initial value
		String epochTime = null;
		if(this.value != null) {
			
			if(value instanceof Date) {
				epochTime = ""+((Date)value).getTime();
				this.addAttribute("value", ""+epochTime);
			}else if(value instanceof Timestamp) {
				epochTime = ""+((Timestamp)value).getTime();
				this.addAttribute("value", ""+epochTime);
			}else {
				epochTime = value.toString();
				this.addAttribute("value", ""+value.toString());
			}

		}
		
		//---------------------------------
		// Create Field
		html.append("<input id=\""+name+"-datepicker\" type=\"text\" class=\"form-control\" placeholder=\"Date\" >\r\n" + 
				"	<input id=\""+name+"\" type=\"hidden\" class=\"form-control\" "+this.getAttributesString()+">\r\n" + 
				"	<script>\r\n" + 
				"		window.addEventListener('DOMContentLoaded', function() {\r\n" + 
				"			cfw_initializeDatePicker('"+name+"', "+epochTime+")"+
				"			}\r\n" + 
				"		);\r\n" 
				);
		
		html.append("</script>");
		
	}
	
	/***********************************************************************************
	 * Create the text area
	 ***********************************************************************************/
	private void createTextArea(StringBuilder html) {
		
		if(!this.attributes.containsKey("rows")) {
			this.addAttribute("rows", "5");
		}
		this.removeAttribute("value");
		String value = "";
		if(this.value != null) {
			value = this.value.toString();
		}
		html.append("<textarea class=\"form-control\" "+this.getAttributesString()+">"+value+"</textarea>");
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
	
	@SuppressWarnings("unchecked")
	public CFWField<T> removeAttribute(String name) {
		return (CFWField<T>)super.removeAttribute(name);
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
	
	public String getLabel() {
		return formLabel;
	}

	public CFWField<T> setLabel(String label) {
		fireChange();
		this.formLabel = label;
		return this;
	}
	
	
	
	public String getColumnDefinition() {
		return columnDefinition;
	}

	public CFWField<T> setColumnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
		return this;
	}
	
	public CFWField<T> setPrimaryKeyAutoIncrement() {
		this.columnDefinition = "INT PRIMARY KEY AUTO_INCREMENT";
		return this;
	}
	
	

	public boolean isDisabled() {
		return isDisabled;
	}

	/******************************************************************************************************
	 * Change if this field should be enabled or disabled when represented as a form field.
	 * 
	 ******************************************************************************************************/
	public CFWField<T> isDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
		return this;
	}

	/******************************************************************************************************
	 * Change the value and trigger the change handler if specified.
	 * @param value to apply.
	 * 
	 ******************************************************************************************************/
	private boolean changeValue(Object value) {
		
		if(changeHandler != null) {
			if(changeHandler.handle(this.value, value)) {
				this.value = value;
			}else {
				return false;
			}
		}else {
			this.value = value;
		}
		return true;
	}
	
	private boolean setValueConvert(T value) {
		boolean success = true;
		
		if(value == null) {
			return this.changeValue(value);
		}
		if(value.getClass() == this.valueClass) {
			this.changeValue(value);
			return true;
		}
		
		if(value.getClass() == String.class) {
			if     (valueClass == Integer.class) 	{ this.changeValue(Integer.parseInt((String)value)); return true;}
			else if(valueClass == Boolean.class) 	{ this.changeValue(Boolean.parseBoolean( ((String)value).trim()) ); return true;}
			else if(valueClass == Timestamp.class)  { this.changeValue(new Timestamp(Long.parseLong( ((String)value).trim()) )); return true; }
			else if(valueClass == Date.class)  		{ this.changeValue(new Date(Long.parseLong( ((String)value).trim()) )); return true; }
			else {return false;}
		}
		
		return success;
	}
	
	public boolean setValueValidated(T value) {
		
		boolean result = true;
		if(this.validateValue(value)) {
			result = this.setValueConvert(value);
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
	
	public CFWField<T> setValue(T value) {
		this.changeValue(value);
		return this;
	}
	
	public T getValue() {
		return (T)value;
	}
	
	protected Class<T> getValueClass() {
		return valueClass;
	}

	public CFWFieldChangeHandler getChangeHandler() {
		return changeHandler;
	}

	public CFWField<T> setChangeHandler(CFWFieldChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
		return this;
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
						.method("mapAndValidateParamsToFields")
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
					
					if      (current.getValueClass() == String.class)  { current.setValueValidated(result.getString(colName)); }
					else if(current.getValueClass() == Integer.class)  { current.setValueValidated(result.getInt(colName)); }
					else if(current.getValueClass() == Boolean.class)  { current.setValueValidated(result.getBoolean(colName)); }
					else if(current.getValueClass() == Timestamp.class)  { current.setValueValidated(result.getTimestamp(colName)); }
					else if(current.getValueClass() == Date.class)  { current.setValueValidated(result.getDate(colName)); }
				}else {
					success = false;
					new CFWLog(CFWHttp.logger)
						.method("mapResultSetColumnsToFields")
						.silent()
						.warn("The object doesn't contain a field with name '"+colName+"'.");
				}
			}
		
		} catch (SQLException e) {
			success = false;
			new CFWLog(CFWHttp.logger)
				.method("mapResultSetColumnsToFields")
				.severe("SQL Exception occured while trying to map ResultSet to fields. Check Cursor position.", e);
		}
		
		return success;
	}
		
}
