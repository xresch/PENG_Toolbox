package com.pengtoolbox.cfw.validation;

import com.pengtoolbox.cfw.utils.Ternary;

/**************************************************************************************
 * The BooleanArgumentValidator will validate if the value of the ArgumentDefinition
 * is a string representation of "true" or "false".
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class BooleanValidator extends AbstractPropertyValidator {

	
	public BooleanValidator(IValidatable validateable) {
		super(validateable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(Object value) {
		
		Ternary result = validateNullEmptyAllowed(value);
		if(result != Ternary.DONTCARE ) return result.toBoolean();
		
		if(value instanceof Boolean) {
			return true;
		}
		if(value instanceof String) {

			if(((String)value).toLowerCase().matches("true|false")){
				return true;
			}else{
				this.setInvalidMessage("The value of "+validateable.getPropertyName()+" is not a boolean value.(value='"+value+"')");
				return false;
			}
		}
		this.setInvalidMessage("The value of "+validateable.getPropertyName()+" is not a boolean value.(value='"+value+"')");
		return false;
	}
	
}
