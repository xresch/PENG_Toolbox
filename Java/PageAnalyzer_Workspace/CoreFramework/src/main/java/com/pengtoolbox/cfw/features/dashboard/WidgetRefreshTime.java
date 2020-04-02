package com.pengtoolbox.cfw.features.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.response.JSONResponse;

public class WidgetRefreshTime extends WidgetDefinition {

	@Override
	public String getWidgetType() {return "cfw_refreshtime";}

	@Override
	public CFWObject getSettings() {
		return new CFWObject()
				.addField(CFWField.newString(FormFieldType.SELECT, "direction")
						.setLabel("{!cfw_widget_cfwlabel_direction!}")
						.setDescription("{!cfw_widget_cfwlabel_direction_desc!}")
						.setOptions(new String[] {"Left to Right", "Bottom to Top", "Top to Bottom", "Upside Down"})
						.setValue("Left to Right")
						)
				.addField(CFWField.newString(FormFieldType.SELECT, "sizefactor")
						.setLabel("{!cfw_dashboard_sizefactor!}")
						.setDescription("{!cfw_dashboard_sizefactor_desc!}")
						.setOptions(new String[]{"0.5", "1", "1.25", "1.5", "1.75", "2.0", "2.5", "3.0", "4.0"})
						.setValue("1")
				)
		;
	}

	@Override
	public void fetchData(JSONResponse response, JsonObject settings) {
		
		response.getContent().append(System.currentTimeMillis());
	}

	@Override
	public ArrayList<FileDefinition> getJavascriptFiles() {
		FileDefinition js = new FileDefinition(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_widget_refreshtime.js");
		ArrayList<FileDefinition> array = new ArrayList<FileDefinition>();
		array.add(js);
		return array;
	}

	@Override
	public ArrayList<FileDefinition> getCSSFiles() { return null; }

	@Override
	public HashMap<Locale, FileDefinition> getLocalizationFiles() {
		HashMap<Locale, FileDefinition> map = new HashMap<Locale, FileDefinition>();
		return map;
	}

}