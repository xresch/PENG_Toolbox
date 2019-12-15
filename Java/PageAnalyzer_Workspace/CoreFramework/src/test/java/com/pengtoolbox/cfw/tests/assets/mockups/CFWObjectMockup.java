package com.pengtoolbox.cfw.tests.assets.mockups;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import com.pengtoolbox.cfw.datahandling.CFWAutocompleteHandler;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class CFWObjectMockup extends CFWObject{
	
	private CFWField<String> firstname 		= CFWField.newString(FormFieldType.TEXT, "FIRSTNAME");
	private CFWField<String> lastname 		= CFWField.newString(FormFieldType.TEXT, "LASTNAME").setLabel("Lastname with custom Label");
	private CFWField<String> withValue 		= CFWField.newString(FormFieldType.TEXT, "WITH_VALUE").setLabel("With Value");
	private CFWField<String> description 	= CFWField.newString(FormFieldType.TEXTAREA, "A_LONG_DESCRIPTION")
			.addValidator(new LengthValidator(5, 10));
	
	private CFWField<String> textarea = CFWField.newString(FormFieldType.TEXTAREA, "10ROW_TEXTAREA").setLabel("10 Row Textarea")
			.addAttribute("rows", "10");
	
	private CFWField<Integer> number = CFWField.newInteger(FormFieldType.NUMBER, "Number_Fieldxyz").setLabel("Enter a Number");
	
	private CFWField<Date> date = CFWField.newDate(FormFieldType.DATEPICKER, "DATE")
			.setValue(new Date(1580053600000L));
	
	private CFWField<Timestamp> timestamp = CFWField.newTimestamp(FormFieldType.DATETIMEPICKER, "TIMESTAMP");
	
	private CFWField<String> select = CFWField.newString(FormFieldType.SELECT, "SELECT")
			.setOptions(new String[] {"Option A","Option B","Option C","Option D"});
	
	private CFWField<Integer> keyValSelect = CFWField.newInteger(FormFieldType.SELECT, "KEY_VAL_SELECT")
											.setValue(2);
	
	private CFWField<String> editor = CFWField.newString(FormFieldType.WYSIWYG, "EDITOR")
			.setValue("<b>Intial Value:</b> successfull!!!");
	
	private CFWField<String> tags = CFWField.newString(FormFieldType.TAGS, "TAGS")
			.setValue("foo,test,bar,BAR,bla");
	
	private CFWField<String> autocomplete = CFWField.newString(FormFieldType.TEXT, "AUTOCOMPLETE")
			.setAutocompleteHandler(new CFWAutocompleteHandler() {
				public String getAutocompleteData(String inputValue) {
					// TODO Auto-generated method stub
					return null;
				}
			});
	
	public CFWObjectMockup() {
		
		initialize();
	}
		
	public void initialize() {
		withValue.setValueValidated("This is the Value");
		LinkedHashMap<Integer, String> options = new LinkedHashMap<Integer, String>();
		options.put(1, "Apple");
		options.put(2, "Banana");
		options.put(3, "Plumb");
		options.put(4, "Strawwberry");
		keyValSelect.setValueLabelOptions(options);
		
		this.addFields(firstname, lastname, withValue, description, textarea, number, date, timestamp, select, keyValSelect, editor, tags, autocomplete);
	}

}
