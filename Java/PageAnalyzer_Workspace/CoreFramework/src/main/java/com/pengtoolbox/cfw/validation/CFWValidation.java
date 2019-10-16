package com.pengtoolbox.cfw.validation;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWValidation {

	/***************************************************************************
	 * Check if the object is not null or not an empty string.
	 * @param value
	 * @return
	 ***************************************************************************/
	public static boolean isNotNullNotEmptyString(Object value) {
		
		if(value == null) return false;
		
		if(value instanceof String && ((String)value).isEmpty()) {
			return false;
		}
		
		return true;
	}
}
