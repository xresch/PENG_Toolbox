package com.pengtoolbox.cfw.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CFWJson {
	
	public static Gson gsonInstance = new GsonBuilder().serializeNulls().create();
	
	public static Gson getGsonInstance() {
		return gsonInstance;
	}
	
	public static String toJSON(Object object) {
		return gsonInstance.toJson(object);
	}
	
	

}
