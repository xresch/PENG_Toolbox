package com.pengtoolbox.cfw.datahandling;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;

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
			form.addField(field);
		}
		
		return form;
	}
	
	public BTForm toForm(String formID, String submitLabel, String ...fieldNames) {
		
		BTForm form = new BTForm(formID, submitLabel);
		form.setOrigin(this);
		
		for(String fieldName : fieldNames) {
			if(fields.containsKey(fieldName)) {
				form.addField(fields.get(fieldName));
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
		
		for(CFWField<?> field : fields.values()) {
			builder.append("\n")
			.append(field.getName())
			.append(": ");
			if(!(field.getValue() instanceof Object[])) {
				builder.append(field.getValue());
			}else {
				builder.append(Arrays.toString((Object[])field.getValue()));
			}
			
		}

		return builder.toString();
	}
	
	public String getFieldsAsKeyValueHTML() {
		
		StringBuilder builder = new StringBuilder();
		
		for(CFWField<?> field : fields.values()) {
			builder.append("<br/>")
			.append(field.getName())
			.append(": ")
			.append(field.getValue());
		}

		return builder.toString();
	}
	
	//##############################################################################
	// DATABASE
	//##############################################################################
	
	/****************************************************************
	 * Create the table for this Object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean createTable() {
		return new CFWStatement(this).createTable();
	}
	
	/****************************************************************
	 * Will be executed after createTable(). Use this method to make
	 * manual changes to the database.
	 * 
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public void beforeAddData() {
		
	}
	/****************************************************************
	 * Will be executed after updateTable(). Use this method to create
	 * default entries in the database.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public void addTableData() {
		
	}
	
	/****************************************************************
	 * Will be executed after createTable(). Use this method to make
	 * manual changes to the database.
	 * 
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public void afterAddData() {
		
	}
	
	/****************************************************************
	 * Caches the query with the specified name for lower performance
	 * impact.
	 * @param Class of the class using the query.
	 * @param name of the query
	 ****************************************************************/
	public CFWStatement queryCache(Class<?> clazz, String name) {
		return new CFWStatement(this).queryCache(clazz, name);
	}
	
	/****************************************************************
	 * Begins a SELECT * statement.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement select() {
		return new CFWStatement(this).select();
	}
	
	/****************************************************************
	 * Begins a SELECT statement including the specified fields.
	 * @param field names
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement select(String ...fieldnames) {
		return new CFWStatement(this).select(fieldnames);
	}
	
	/****************************************************************
	 * Creates an insert statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean insert() {
		return new CFWStatement(this).insert();
	}
	
	/****************************************************************
	 * Creates an insert statement including the specified fields
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean insert(String ...fieldnames) {
		return new CFWStatement(this).insert(fieldnames);
	}
	
	/****************************************************************
	 * Creates an update statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean update() {
		return new CFWStatement(this).update();
	}
	
	/****************************************************************
	 * Creates an update statement including the specified fields
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean update(String ...fieldnames) {
		return new CFWStatement(this).update(fieldnames);
	}
	
	/****************************************************************
	 * Begins a DELETE statement.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement delete() {
		return new CFWStatement(this).delete();
	}

}
