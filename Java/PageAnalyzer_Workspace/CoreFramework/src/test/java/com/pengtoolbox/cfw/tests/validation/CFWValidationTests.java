package com.pengtoolbox.cfw.tests.validation;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw.validation.AbstractValidatable;
import com.pengtoolbox.cfw.validation.BooleanValidator;
import com.pengtoolbox.cfw.validation.IValidatable;
import com.pengtoolbox.cfw.validation.LengthValidator;
import com.pengtoolbox.cfw.validation.PasswordValidator;

public class CFWValidationTests {


	
	@Test
	public void testBooleanValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		BooleanValidator bv = new BooleanValidator(validateThis);
		
		
		Assertions.assertTrue(validateThis.setValue(true),
				" Boolean value 'true' is recognized as boolean.");
		
		Assertions.assertTrue(validateThis.setValue(false),
				" Boolean value 'true' is recognized as boolean.");
		
		Assertions.assertTrue(validateThis.setValue("true"),
				"String value 'true' is recognized as boolean.");
		
		Assertions.assertTrue(validateThis.setValue("FALSE"),
				"String value 'FALSE' is recognized as boolean.");
		
		Assertions.assertFalse(validateThis.setValue("NotABoolean"),
				"String value 'NotABoolean' is not a boolean.");
		
	}
	
	@Test
	public void testLengthValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		LengthValidator lv = new LengthValidator(validateThis, 5, 10);
		
		Assertions.assertFalse(validateThis.setValue("4444"), "Value is invalid below min length.");
		Assertions.assertTrue(validateThis.setValue("55555"), "Value is valid at min length.");	
		Assertions.assertTrue(validateThis.setValue("1010101010"), "Value is valid at max length.");
		Assertions.assertFalse(validateThis.setValue("11111111111"), "Value is invalid above max length.");
	}
	
	@Test
	public void testPassowordValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		PasswordValidator pv = new PasswordValidator(validateThis);
		
		Assertions.assertTrue( validateThis.setValue("Aa123456"), "Is a valid password");
		Assertions.assertTrue( validateThis.setValue("Aa------"), "Is a valid password");
		Assertions.assertTrue( validateThis.setValue("A-aaaaaa"), "Is a valid password");
		Assertions.assertTrue( validateThis.setValue("-aaaaaaA"), "Is a valid password");
		
		Assertions.assertFalse(validateThis.setValue("Aa12345"), "Password is invalid below min length.");
		Assertions.assertFalse(validateThis.setValue("Aaaaaaaa"), "Password is invalid when missing non-letter character.");
		Assertions.assertFalse(validateThis.setValue("A1234567"), "Password is invalid when missing small letter character.");
		Assertions.assertFalse(validateThis.setValue("a1234567"), "Password is invalid when missing capital letter character.");
	}
	
	
}
