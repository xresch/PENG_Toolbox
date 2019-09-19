package com.pengtoolbox.cfw.validation;

import com.pengtoolbox.cfw.cli.ArgumentDefinition;

/**************************************************************************************
 * The BooleanArgumentValidator will validate if the value of the ArgumentDefinition
 * is a string representation of "true" or "false".
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class NotNullValidator extends AbstractPropertyValidator {

	public NotNullValidator(IValidatable validatable) {
		super(validatable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(Object value) {
		
		if(value != null){
			return true;
		}else{
			this.setInvalidMessage("The value of the argument "+validateable.getName()+" cannot be null, please specify the argument.");
			return false;
		}
		
	}

}
