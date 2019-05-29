package com.pengtoolbox.cfw.validation;

import java.io.File;

import com.pengtoolbox.cfw.utils.Ternary;


/**************************************************************************************
 * The FileCanReadArgumentValidator will validate if the value is a filepath or a file
 * is readable.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class FileCanReadValidator extends AbstractPropertyValidator {

	
	public FileCanReadValidator(IValidatable validatable) {
		super(validatable);
	}

	@Override
	public boolean validate(Object value) {
		
		
		Ternary result = validateNullEmptyAllowed(value);
		if(result != Ternary.DONTCARE ) return result.toBoolean();
		
		File file = null;
		
		if(value instanceof String) {
			file = new File((String)value);
		}else if(value instanceof File) {
			file = (File)value;
		}
		
		
		if(file.canRead()){
			return true;
		}else {
			this.setInvalidMessage("File cannot be read: '"+value+"'");
			return false;
		}
	}

}
