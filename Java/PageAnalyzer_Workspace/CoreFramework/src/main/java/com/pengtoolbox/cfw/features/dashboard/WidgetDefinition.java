package com.pengtoolbox.cfw.features.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.response.JSONResponse;

public abstract class WidgetDefinition {

	/************************************************************
	 * Return the unique name of the widget.
	 * @return String name
	 ************************************************************/
	public abstract String getWidgetType();
	
	/************************************************************
	 * Return a CFWObject containing fields with default values.
	 * @return CFWObject
	 ************************************************************/
	public abstract CFWObject getSettings();
	
	/************************************************************
	 * Create a json response containing the data you need for 
	 * your widget.
	 * @return JSON string
	 ************************************************************/
	public abstract void fetchData(JSONResponse response, JsonObject settings);

	/************************************************************
	 * Return the file definitions of the javascript part of the 
	 * widget.
	 * @return file definition
	 ************************************************************/
	public abstract ArrayList<FileDefinition> getJavascriptFiles();

	/************************************************************
	 * Return the file definitions of the javascript part of the 
	 * widget.
	 * @return file definition
	 ************************************************************/
	public abstract ArrayList<FileDefinition> getCSSFiles();
	
	/************************************************************
	 * Return the file definitions of the client side part of the 
	 * script.
	 * @return file definition
	 ************************************************************/
	public abstract HashMap<Locale, FileDefinition> getLocalizationFiles();
	
	/************************************************************
	 * Check if the user has the required permission to use and
	 * view the widget.
	 * return true if has permission, false otherwise
	 ************************************************************/
	public abstract boolean hasPermission();
	
	
	
}
