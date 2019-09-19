package com.pengtoolbox.cfw.validation;

import com.pengtoolbox.cfw.cli.ArgumentDefinition;

/**************************************************************************************
 * The BooleanArgumentValidator will validate if the value of the ArgumentDefinition
 * is a string representation of "true" or "false".
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class NotNullOrEmptyValidator extends AbstractValidator {

	public NotNullOrEmptyValidator(IValidatable validatable) {
		super(validatable);
		// TODO Auto-generated constructor stub
	}
	
	public NotNullOrEmptyValidator() {
	}

	@Override
	public boolean validate(Object value) {
		
		if(value != null && !value.equals("")){
			return true;
		}else{
			this.setInvalidMessage("The field "+validateable.getName()+" cannot be empty.");
			return false;
		}
		
	}

}
