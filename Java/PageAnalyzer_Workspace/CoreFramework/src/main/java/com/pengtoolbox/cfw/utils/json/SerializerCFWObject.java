package com.pengtoolbox.cfw.utils.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWField;
import com.pengtoolbox.cfw.datahandling.CFWObject;

public class SerializerCFWObject implements JsonSerializer<CFWObject> {

	@Override
	public JsonElement serialize(CFWObject object, Type type, JsonSerializationContext context) {
		
		JsonObject result = new JsonObject();
		
		for(CFWField field : object.getFields().values()) {
			String name = field.getName();
			Object value = field.getValue();
			
			if(name.startsWith("JSON")) {
				JsonElement asElement = new JsonParser().parse(value.toString());
				result.add(name, asElement);
			}else {
				if(value instanceof Number) {
					result.addProperty(name, (Number)value);
				}else if(value instanceof Object[]){
					JsonArray jsonArray = new JsonArray();
					for(Object o : (Object[])value) {
						jsonArray.add(string);
					}
					json.append(CFW.JSON.toJSON(object)).append(",");
				}else{
					json.append("\"").append(CFW.JSON.escapeString(resultSet.getString(i))).append("\",");
				}
			}
			fields.add(field.getName(), field.getValue());
			
		}
		
		return null;
	}

}
