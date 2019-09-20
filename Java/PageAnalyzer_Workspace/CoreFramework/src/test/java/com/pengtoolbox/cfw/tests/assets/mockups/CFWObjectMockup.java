package com.pengtoolbox.cfw.tests.assets.mockups;

import com.pengtoolbox.cfw._main.CFWObject;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;
import com.pengtoolbox.cfw.response.bootstrap.CFWField.FormFieldType;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class CFWObjectMockup extends CFWObject{
	
	private CFWField<String> firstname 		= CFWField.newString(FormFieldType.TEXT, "FIRSTNAME");
	private CFWField<String> lastname 		= CFWField.newString(FormFieldType.TEXT, "LASTNAME").setLabel("Lastname with custom Label");
	private CFWField<String> withValue 		= CFWField.newString(FormFieldType.TEXT, "WITH_VALUE").setLabel("With Value");
	private CFWField<String> description 	= CFWField.newString(FormFieldType.TEXTAREA, "A_LONG_DESCRIPTION")
			.addValidator(new LengthValidator(5, 10));
	
	private CFWField<String> textarea = CFWField.newString(FormFieldType.TEXTAREA, "10ROW_TEXTAREA").setLabel("10 Row Textarea")
			.addAttribute("rows", "10");
	public CFWObjectMockup() {
		withValue.setValueValidated("This is the Value");
		addFields();
	}
		
	public void addFields() {
		this.addField(firstname);
		this.addField(lastname);
		this.addField(withValue);
		this.addField(description);
		this.addField(textarea);
	}

}
