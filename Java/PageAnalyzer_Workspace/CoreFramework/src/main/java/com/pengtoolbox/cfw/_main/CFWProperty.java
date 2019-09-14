package com.pengtoolbox.cfw._main;

import com.pengtoolbox.cfw.validation.AbstractValidatable;

public class CFWProperty<T> extends AbstractValidatable<T> {
	
	private String formLabel;
	
	//#############################################################
	// CONSTRUCTORS
	//#############################################################
	public CFWProperty(String propertyName, String formLabel) {
		this.setPropertyName(propertyName);
		this.formLabel = formLabel;
	}
	
	public CFWProperty(String propertyName, String formLabel, T value) {
		this.setPropertyName(propertyName);
		this.formLabel = formLabel;
		this.value = value;
	}

	//#############################################################
	// Getters and Setters
	//#############################################################
	public String getFormLabel() {
		return formLabel;
	}

	public void setFormLabel(String formLabel) {
		this.formLabel = formLabel;
	}



}
