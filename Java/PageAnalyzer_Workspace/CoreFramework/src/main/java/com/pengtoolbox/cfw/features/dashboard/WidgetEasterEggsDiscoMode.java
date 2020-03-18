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

public class WidgetEasterEggsDiscoMode extends WidgetDefinition {

	@Override
	public String getWidgetType() {return "emp_discomode";}

	@Override
	public CFWObject getSettings() {
		return new CFWObject()
		
				.addField(CFWField.newString(FormFieldType.SELECT, "discolevel")
						.setLabel("Discolevel")
						.setDescription("Sets the level of disco-ness!")
						.setOptions(new String[]{"300", "1000"})
						.setValue("300")
				)
		;
	}

	@Override
	public void fetchData(JSONResponse response, JsonObject settings) { }

	@Override
	public ArrayList<FileDefinition> getJavascriptFiles() {
		FileDefinition js = new FileDefinition(HandlingType.JAR_RESOURCE, FeatureDashboard.RESOURCE_PACKAGE, "cfw_widget_eastereggs_discomode.js");
		ArrayList<FileDefinition> array = new ArrayList<FileDefinition>();
		array.add(js);
		return array;
	}

	@Override
	public HashMap<Locale, FileDefinition> getLocalizationFiles() {
		HashMap<Locale, FileDefinition> map = new HashMap<Locale, FileDefinition>();
		return map;
	}

}
