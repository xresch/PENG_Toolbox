package com.pengtoolbox.cfw.tests.validation;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw.validation.AbstractValidatable;
import com.pengtoolbox.cfw.validation.BooleanValidator;
import com.pengtoolbox.cfw.validation.IValidatable;
import com.pengtoolbox.cfw.validation.IntegerValidator;
import com.pengtoolbox.cfw.validation.LengthValidator;
import com.pengtoolbox.cfw.validation.PasswordValidator;

public class CFWValidationTests {


	
	@Test
	public void testBooleanValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		BooleanValidator bv = new BooleanValidator(validateThis);
		
		
		Assertions.assertTrue(validateThis.setValueValidated(true),
				" Boolean value 'true' is recognized as boolean.");
		
		Assertions.assertTrue(validateThis.setValueValidated(false),
				" Boolean value 'true' is recognized as boolean.");
		
		Assertions.assertTrue(validateThis.setValueValidated("true"),
				"String value 'true' is recognized as boolean.");
		
		Assertions.assertTrue(validateThis.setValueValidated("FALSE"),
				"String value 'FALSE' is recognized as boolean.");
		
		Assertions.assertFalse(validateThis.setValueValidated("NotABoolean"),
				"String value 'NotABoolean' is not a boolean.");
		
	}
	
	@Test
	public void testIntegerValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		IntegerValidator bv = new IntegerValidator(validateThis);
		
		Assertions.assertTrue(validateThis.setValueValidated(1),
				" Integer value '1' is recognized as Integer.");
		
		Assertions.assertTrue(validateThis.setValueValidated(-1),
				" Integer value '-1' is recognized as Integer.");
		
		Assertions.assertTrue(validateThis.setValueValidated("123456"),
				" String value '123456' is recognized as Integer.");
		
		Assertions.assertTrue(validateThis.setValueValidated("-123456"),
				" String value '-123456' is recognized as Integer.");
		
		Assertions.assertTrue(validateThis.setValueValidated("+123456"),
				" String value '+123456' is recognized as Integer.");
		
		Assertions.assertTrue(validateThis.setValueValidated("0"),
				" String value '0' is recognized as Integer.");
		
		Assertions.assertFalse(validateThis.setValueValidated("1.23456"),
				" String value '1.23456' is not recognized as Integer.");
		
		Assertions.assertFalse(validateThis.setValueValidated("1,23456"),
				" String value '1,23456' is not recognized as Integer.");
		
		Assertions.assertFalse(validateThis.setValueValidated("a1"),
				" String value 'a1' is not recognized as Integer.");
	}
	
	@Test
	public void testLengthValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		LengthValidator lv = new LengthValidator(validateThis, 5, 10);
		
		Assertions.assertFalse(validateThis.setValueValidated("4444"), "Value is invalid below min length.");
		Assertions.assertTrue(validateThis.setValueValidated("55555"), "Value is valid at min length.");	
		Assertions.assertTrue(validateThis.setValueValidated("1010101010"), "Value is valid at max length.");
		Assertions.assertFalse(validateThis.setValueValidated("11111111111"), "Value is invalid above max length.");
	}
	
	@Test
	public void testPasswordValidator() {
		
		IValidatable validateThis = new AbstractValidatable() {};
		PasswordValidator pv = new PasswordValidator(validateThis);
		
		Assertions.assertTrue( validateThis.setValueValidated("Aa123456"), "Is a valid password");
		Assertions.assertTrue( validateThis.setValueValidated("Aa------"), "Is a valid password");
		Assertions.assertTrue( validateThis.setValueValidated("A-aaaaaa"), "Is a valid password");
		Assertions.assertTrue( validateThis.setValueValidated("-aaaaaaA"), "Is a valid password");
		
		Assertions.assertFalse(validateThis.setValueValidated("Aa12345"), "Password is invalid below min length.");
		Assertions.assertFalse(validateThis.setValueValidated("Aaaaaaaa"), "Password is invalid when missing non-letter character.");
		Assertions.assertFalse(validateThis.setValueValidated("A1234567"), "Password is invalid when missing small letter character.");
		Assertions.assertFalse(validateThis.setValueValidated("a1234567"), "Password is invalid when missing capital letter character.");
	}
	
	
}
