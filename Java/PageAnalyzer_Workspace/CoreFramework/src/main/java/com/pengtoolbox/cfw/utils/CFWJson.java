package com.pengtoolbox.cfw.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CFWJson {
	
	public static Gson gsonInstance = new GsonBuilder().serializeNulls().create();
	
	private static final String escapes[][] = new String[][]{
	        {"\\", "\\\\"},
	        {"\"", "\\\""},
	        {"\n", "\\n"},
	        {"\r", "\\r"},
	        {"\b", "\\b"},
	        {"\f", "\\f"},
	        {"\t", "\\t"}
	};
	
	public static Gson getGsonInstance() {
		return gsonInstance;
	}
	
	public static String toJSON(Object object) {
		return gsonInstance.toJson(object);
	}
	
	
	public static String escapeString(String string) {

		if(string != null) {
	        for (String[] esc : escapes) {
	            string = string.replace(esc[0], esc[1]);
	        }
		}
        return string;
    }
	
	

}
