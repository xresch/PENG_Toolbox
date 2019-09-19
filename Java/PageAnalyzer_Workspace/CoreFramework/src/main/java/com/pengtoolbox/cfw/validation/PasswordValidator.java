package com.pengtoolbox.cfw.validation;

/**************************************************************************************
 * The RegexValidator will validate if value.toString() is matching the given 
 * regular expression.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class PasswordValidator extends AbstractValidator {
	
	public PasswordValidator(IValidatable validatable){
		super(validatable);
	}
	
	public PasswordValidator(){}
	
	@Override
	public boolean validate(Object value) {
		
		if(   value.toString().matches(".*[A-Z]+.*")
		   && value.toString().matches(".*[a-z]+")
		   && value.toString().matches(".*[0-9]+.*") ){
			return true;
		}else{
			this.setInvalidMessage("The value of "+validateable.getName()+
			" must contain at least one uppercase, one lowercase and one special character or number.");
			
			return false;
		}
	}


}
