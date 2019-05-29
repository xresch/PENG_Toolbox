package com.pengtoolbox.cfw.cli;

import java.util.ArrayList;

import com.pengtoolbox.cfw.validation.IValidator;
import com.pengtoolbox.cfw.validation.AbstractValidatable;
import com.pengtoolbox.cfw.validation.IValidatable;

/**************************************************************************************
 * The ArgumentDefinition represents an argument with a key value pair.
 * It contains the default value, syntax and a description of the argument.
 * 
 * @author Reto Scheiwiller, 2015
 *
 **************************************************************************************/
public class ArgumentDefinition extends AbstractValidatable<String> {
	
	private String key = "";
	private String syntax = "";
	private String defaultValue = "";
	private String description = "";
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public ArgumentDefinition(String key){
		
		this.key = key;
		this.setValue(key);
		
		this.syntax = syntax;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	public ArgumentDefinition(String key, String syntax, String defaultValue, String description){
		
		this.key = key;
		this.setPropertyName(key);
		
		this.syntax = syntax;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	
	//####################################################################################
	// GETTERS & SETTERS
	//####################################################################################
	
	
	public ArgumentDefinition syntax(String syntax) {
		this.syntax = syntax;
		return this;
	}
	
	public String getSyntax() {
		return syntax;
	}
	
	public ArgumentDefinition defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public ArgumentDefinition description(String description) {
		this.description = description;
		return this;
	}
	public String getDescription() {
		return description;
	}
	
	
}
