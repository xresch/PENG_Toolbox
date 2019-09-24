package com.pengtoolbox.cfw._main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup.GroupDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;

public class CFWObject {
	
	private static Logger logger = CFWLog.getLogger(CFWObject.class.getName());
	protected String tableName; 
	
	public LinkedHashMap<String, CFWField> fields = new LinkedHashMap<String, CFWField>();
	public CFWField primaryField = null;
	
	public CFWObject() {

	}
	
	public boolean mapRequestParameters(HttpServletRequest request) {
		
		return CFWField.mapAndValidateParamsToFields(request, fields);
	}
	
	public boolean mapResultSet(ResultSet result) {

		return CFWField.mapResultSetColumnsToFields(result, fields);
	}
	
	public BTForm toForm(String formID, String submitLabel) {
		
		BTForm form = new BTForm(formID, submitLabel);
		form.setOrigin(this);
		
		for(CFWField field : fields.values()) {
			form.addChild(field);
		}
		
		return form;
	}
	
	public BTForm toForm(String formID, String submitLabel, String ...fieldNames) {
		
		BTForm form = new BTForm(formID, submitLabel);
		form.setOrigin(this);
		
		for(String fieldName : fieldNames) {
			if(fields.containsKey(fieldName)) {
				form.addChild(fields.get(fieldName));
			}else {
				new CFWLog(logger)
				.method("toForm")
				.severe("The field '"+fieldName+"' is not known for this CFWObject.");
			}
		}
		
		return form;
	}
	
	
	public void addField(CFWField field) {
		
		if(!fields.containsKey(field.getName())) {
			fields.put(field.getName(), field);
		}else {
			new CFWLog(logger)
			.method("addField")
			.severe("The field with name '"+field.getName()+"' was already added to this object. Check the naming of the field.");}
	}
	
	public void addAllFields(CFWField[] fields) {
		for(CFWField field : fields) {
			this.addField(field);
		}
	}
	
	public void addFields(CFWField ...fields) {
		for(CFWField field : fields) {
			this.addField(field);
		}
	}
	
	public CFWField getField(String name) {
		return fields.get(name);
	}
	
	public LinkedHashMap<String, CFWField> getFields(){
		return fields;
	}
	
	public String getTableName() {
		return tableName;
	}

	public CFWObject setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	

	public CFWField getPrimaryField() {
		return primaryField;
	}

	public CFWObject setPrimaryField(CFWField primaryField) {
		this.primaryField = primaryField;
		return this;
	}

	public String getFieldsAsKeyValueString() {
		
		StringBuilder builder = new StringBuilder();
		
		for(CFWField field : fields.values()) {
			builder.append("\n")
			.append(field.getName())
			.append(": ")
			.append(field.getValue());
		}

		return builder.toString();
	}
	
	//##############################################################################
	// DATABASE
	//##############################################################################
	
	/***************************************************************
	 * Select a an object by the specified field and it's current value.
	 * @param String name of the field
	 * @return Returns a CFWObject or null if not found or in case of exception.
	 ****************************************************************/
	public CFWObject selectFirstBy(String fieldName) {
		
		if(!fields.containsKey(fieldName)) {
			new CFWLog(logger)
			.method("selectFirstBy")
			.severe("Unknown field: "+fieldName);
			return null;
		}
		String selectByName = 
				"SELECT * "
				+" FROM "+tableName
				+" WHERE "
				+ fieldName + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, fields.get(fieldName).getValue());
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				CFWObject object = this.getClass().newInstance();
				object.mapResultSet(result);
				return object;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			new CFWLog(logger)
			.method("selectFirstBy")
			.severe("Error reading object from database.", e);
			
		}finally {
			CFWDB.close(result);
		}
		
		return null;
		
	}
	
	/********************************************************************************************
	 * Creates a new entry in the DB with the values of the fields associated with this object. 
	 * Ignores the primaryField.
	 * 
	 ********************************************************************************************/
	public boolean create() {
		
		StringBuilder columnNames = new StringBuilder("(");
		StringBuilder placeholders = new StringBuilder("(");
		ArrayList<Object> values = new ArrayList<Object>();
		
		for(CFWField field : fields.values()) {
			if(field != primaryField) {
				columnNames.append(field.getName()).append(",");
				placeholders.append("?,");
				values.add(field.getValue());
			}
		}
		
		//Replace last comma with closing brace
		columnNames.deleteCharAt(columnNames.length()-1).append(")");
		placeholders.deleteCharAt(placeholders.length()-1).append(")");
		
		String createSQL = "INSERT INTO "+this.getTableName()+" "+columnNames
				  + " VALUES "+placeholders+";";
		
		return CFWDB.preparedExecute(createSQL, 
				values.toArray()
				);
	}
	
	/***************************************************************
	 * Updates the object selected by the primaryField.
	 * @return true or false
	 ****************************************************************/
	public boolean update() {
		
		StringBuilder columnNames = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();
		ArrayList<Object> values = new ArrayList<Object>();
		
		for(CFWField field : fields.values()) {
			if(!field.equals(primaryField)) {
				columnNames.append(field.getName()).append(",");
				placeholders.append("?,");
				values.add(field.getValue());
			}
		}
		
		//Replace last comma with closing brace
		columnNames.deleteCharAt(columnNames.length()-1);
		placeholders.deleteCharAt(placeholders.length()-1);
		
		String createSQL = "UPDATE "+this.getTableName()+" SET ("+columnNames
				  + ") = ("+placeholders+")"
				  +" WHERE "
					+ primaryField.getName()+" = ?";

		values.add(primaryField.getValue());
		return CFWDB.preparedExecute(createSQL, 
				values.toArray()
				);
		
	}
	

}
