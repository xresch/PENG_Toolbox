package com.pengtoolbox.cfw.validation;

import com.pengtoolbox.cfw.utils.Ternary;

/**************************************************************************************
 * The BooleanArgumentValidator will validate if the value of the ArgumentDefinition
 * is a string representation of "true" or "false".
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class IntegerValidator extends AbstractValidator {

	
	public IntegerValidator(IValidatable validateable) {
		super(validateable);
		// TODO Auto-generated constructor stub
	}
	
	public IntegerValidator() {}

	@Override
	public boolean validate(Object value) {
		
		Ternary result = validateNullEmptyAllowed(value);
		if(result != Ternary.DONTCARE ) return result.toBoolean();
		
		if(value instanceof Integer) {
			return true;
		}
		if(value instanceof String) {

			try {
				Integer.parseInt((String)value);
				return true;
			}catch(NumberFormatException e){
				this.setInvalidMessage("The value of "+validateable.getName()+" is not an Integer value.(value='"+value+"')");
				return false;
			}

		}
		this.setInvalidMessage("The value of "+validateable.getName()+" is not an Integer value.(value='"+value+"')");
		return false;
	}
	
}
