package com.pengtoolbox.cfw.validation;

import java.io.File;
import java.io.IOException;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.utils.Ternary;


/**************************************************************************************
 * The FileCanWriteArgumentValidator will validate if the value of the ArgumentDefinition
 * is a filepath and the application can write to this path.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class FileCanWriteValidator extends AbstractPropertyValidator {

	public FileCanWriteValidator(IValidatable validatable) {
		super(validatable);
		// TODO Auto-generated constructor stub
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
		}else{
			this.setInvalidMessage("Unsupported type for FileCanWriteValidator: '"+value.getClass().getName()+"'");
		}
		

		if(file.exists()){
			if(!file.canWrite()){
				this.setInvalidMessage("File cannot be written: '"+file.getAbsolutePath()+"'");
				return false;
			}
		}else{
			try {
				file.mkdirs();
				file.createNewFile();
				file.delete();
			} catch (IOException e) {
				this.setInvalidMessage("File cannot be written: '"+file.getAbsolutePath()+"'");
				return false;
			}
		}
		
		return true;
	}
}
