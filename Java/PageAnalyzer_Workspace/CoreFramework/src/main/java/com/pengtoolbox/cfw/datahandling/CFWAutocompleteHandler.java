package com.pengtoolbox.cfw.datahandling;

import java.util.LinkedHashMap;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public abstract class CFWAutocompleteHandler {
	
	private int maxResults = 10;
	private CFWField parent = null;
	
	public CFWAutocompleteHandler() {
		
	}
	
	public CFWAutocompleteHandler(int maxResults) {
		this.maxResults = maxResults;
	}
	
	/*******************************************************************************
	 * Return a hashmap with value / label combinations
	 * @return JSON string
	 *******************************************************************************/
	public abstract LinkedHashMap<Object, Object> getAutocompleteData(String inputValue);

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public CFWField getParent() {
		return parent;
	}

	public void setParent(CFWField parent) {
		this.parent = parent;
	}

	
	
	
}
