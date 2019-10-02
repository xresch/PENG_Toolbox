package com.pengtoolbox.cfw.tests.assets.mockups;

import java.sql.Date;
import java.sql.Timestamp;

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
	
	private CFWField<Date> date = CFWField.newDate(FormFieldType.DATEPICKER, "DATE")
			.setValue(new Date(1580053600000L));
	
	private CFWField<Timestamp> timestamp = CFWField.newTimestamp(FormFieldType.DATEPICKER, "TIMESTAMP");
	
	public CFWObjectMockup() {
		withValue.setValueValidated("This is the Value");
		addFields();
	}
		
	public void addFields() {
		this.addFields(firstname, lastname, withValue, description, textarea, date, timestamp);
	}

}
