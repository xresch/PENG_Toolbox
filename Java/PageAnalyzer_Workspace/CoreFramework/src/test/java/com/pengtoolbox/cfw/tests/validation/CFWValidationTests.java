package com.pengtoolbox.cfw.tests.validation;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw.validation.AbstractValidatable;
import com.pengtoolbox.cfw.validation.BooleanValidator;
import com.pengtoolbox.cfw.validation.IValidatable;

public class CFWValidationTests {

	@Test
	public void testBooleanValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		
		BooleanValidator bv = new BooleanValidator(validateThis);
		
		validateThis.setValue(true);
		Assertions.assertTrue(bv.validate(),
				" Boolean value 'true' is recognized as boolean.");
		
		validateThis.setValue(false);
		Assertions.assertTrue(bv.validate(),
				" Boolean value 'true' is recognized as boolean.");
		
		
		validateThis.setValue("true");
		Assertions.assertTrue(bv.validate(),
				"String value 'true' is recognized as boolean.");
		
		validateThis.setValue("FALSE");
		Assertions.assertTrue(bv.validate(),
				"String value 'FALSE' is recognized as boolean.");
		
		validateThis.setValue("NotABoolean");
		Assertions.assertFalse(bv.validate(),
				"String value 'NotABoolean' is not a boolean.");
		
	}
}
