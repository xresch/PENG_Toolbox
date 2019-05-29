package com.pengtoolbox.cfw.validation;

import java.util.ArrayList;

public abstract class AbstractValidatable<T> implements IValidatable<T> {
	
	
	private ArrayList<IValidator> validatorArray = new ArrayList<IValidator>();
	private String propertyName = "";
	private T value;
	
	private ArrayList<String> invalidMessages;
	
	/*************************************************************************
	 * Executes all validators added to the argument.
	 * 
	 * @return true if all validators returned true, false otherwise
	 *************************************************************************/ 
	public boolean validateValue(Object value){
		
		boolean isValid = true;
		invalidMessages = new ArrayList<String>();
		
		for(IValidator validator : validatorArray){
			
			if(!validator.validate(value)){
				invalidMessages.add(validator.getInvalidMessage());
				
				isValid=false;
			}
		}
		
		return isValid;
	}
	
	/*************************************************************************
	 * Returns all the InvalidMessages from the last validation execution. 
	 *************************************************************************/ 
	public ArrayList<String> getInvalidMessages() {
		return invalidMessages;
	}
	
	public boolean addValidator(IValidator e) {
		return validatorArray.add(e);
	}

	public boolean removeValidator(IValidator o) {
		return validatorArray.remove(o);
	}
	
	public IValidatable<T> setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public IValidatable<T> setValue(T value) {
		this.value = value;
		return this;
	}
	
	public T getValue() {
		return value;
	}
	
}
