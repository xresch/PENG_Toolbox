package com.pengtoolbox.cfw.utils;

import java.util.Arrays;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWArrayUtils {
	
	
	public static boolean contains(Object[] array, Object object) {
		if(array != null) {
			return Arrays.asList(array).contains(object);
		}
		return false;
	}
	
	public static Object[] add(Object[] array, Object object) {
		Object[] copy = new Object[array.length + 1];
		System.arraycopy(array, 0, copy, 0, array.length);
		copy[copy.length-1] = object;
		return copy;
	}
	
	public static String[] add(String[] array, String string) {
		String[] copy = new String[array.length + 1];
		System.arraycopy(array, 0, copy, 0, array.length);
		copy[copy.length-1] = string;
		return copy;
	}

}
