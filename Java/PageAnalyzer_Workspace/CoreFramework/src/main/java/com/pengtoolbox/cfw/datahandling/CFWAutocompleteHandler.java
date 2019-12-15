package com.pengtoolbox.cfw.datahandling;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public abstract class CFWAutocompleteHandler {
	
	private int maxResults = 10;
	
	public CFWAutocompleteHandler() {
		
	}
	
	public CFWAutocompleteHandler(int maxResults) {
		this.maxResults = maxResults;
	}
	
	/*******************************************************************************
	 * 
	 * @return JSON string
	 *******************************************************************************/
	public abstract String getAutocompleteData(String inputValue);

}
