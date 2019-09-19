package com.pengtoolbox.cfw.tests.assets.mockups;

import com.pengtoolbox.cfw._main.CFWObject;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;
import com.pengtoolbox.cfw.response.bootstrap.CFWField.FormFieldType;
import com.pengtoolbox.cfw.validation.LengthValidator;

public class CFWObjectMockup extends CFWObject{
	
	private CFWField<String> firstname 		= new CFWField<String>(FormFieldType.TEXT, "FIRSTNAME");
	private CFWField<String> lastname 		= new CFWField<String>(FormFieldType.TEXT, "LASTNAME", "Lastname with custom Label");
	private CFWField<String> withValue 		= new CFWField<String>(FormFieldType.TEXT, "WITH_VALUE", "With Value");
	private CFWField<String> description 	= (CFWField<String>)new CFWField<String>(FormFieldType.TEXTAREA, "A_LONG_DESCRIPTION")
			.addValidator(new LengthValidator(5, 10));
	
	private CFWField<String> textarea = (CFWField<String>)new CFWField<String>(FormFieldType.TEXTAREA, "10ROW_TEXTAREA", "10 Row Textarea")
											.addAttribute("rows", "10");
	public CFWObjectMockup() {
		withValue.setValue("This is the Value");
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
