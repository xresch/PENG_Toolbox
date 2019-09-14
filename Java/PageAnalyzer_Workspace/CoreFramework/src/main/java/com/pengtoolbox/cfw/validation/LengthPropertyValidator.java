package com.pengtoolbox.cfw.validation;

import com.pengtoolbox.cfw.utils.Ternary;

/**************************************************************************************
 * The StringLengthArgumentValidator will validate if the value of the ArgumentDefinition
 * has a certain lenght in a minimum and maximum range.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class LengthPropertyValidator extends AbstractPropertyValidator {

	private int minLength;
	private int maxLength;

	public LengthPropertyValidator(IValidatable validatable, int minLength, int maxLength) {
		super(validatable);
		this.minLength = minLength;
		this.maxLength = maxLength;
		
		if(minLength > 0) {
			this.setNullAllowed(false);
		}
	}
	
	public LengthPropertyValidator(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		
		if(minLength > 0) {
			this.setNullAllowed(false);
		}
	}
	
	@Override
	public boolean validate(Object value) {

		Ternary result = validateNullEmptyAllowed(value);
		if(result != Ternary.DONTCARE ) return result.toBoolean();
		
		String string = "";
		if (value instanceof String) {
			string = ((String)value);
		}else if (value instanceof Number) {
			string = ((Number)value).toString();
		}
		
		if(   (string.length() >= minLength || minLength == -1) 
		   && (string.length() <= maxLength || maxLength == -1) ){
			return true;
		}else{
			if(minLength == -1){
				this.setInvalidMessage("The value of the argument "+validateable.getPropertyName()+
						" should be at maximum "+maxLength+" characters long.(value='"+value+"')");
			}else if(maxLength == -1){
				this.setInvalidMessage("The value of the argument "+validateable.getPropertyName()+
						" should be at least "+minLength+" characters long.(value='"+value+"')");
			}else {
				this.setInvalidMessage("The value of the argument "+validateable.getPropertyName()+
						" should be between "+minLength+" and "+maxLength+" characters long.(value='"+value+"')");
			}
			
			return false;
		}
		
	}

}
