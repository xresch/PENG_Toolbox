package com.pengtoolbox.cfw.datahandling;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw.api.APIDefinition;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWObject {
	
	private static Logger logger = CFWLog.getLogger(CFWObject.class.getName());
	
	//------------------------
	// General
	private LinkedHashMap<String, CFWField<?>> fields = new LinkedHashMap<String, CFWField<?>>();
	protected int hierarchyLevels = 0;
	protected  CFWObject parent;
	protected  LinkedHashMap<Integer, CFWObject> childObjects;
	
	//---------------------------
	// Database
	protected String tableName; 
	protected CFWField<Integer> primaryField = null;
	private ArrayList<ForeignKeyDefinition> foreignKeys = new ArrayList<ForeignKeyDefinition>();
	

	
	class ForeignKeyDefinition{
		public String fieldname;
		public String foreignFieldname;
		public Class<? extends CFWObject> foreignObject;
		public String ondelete;
	}
	
	public CFWObject() {

	}
	
	public boolean mapRequestParameters(HttpServletRequest request) {
		
		return CFWField.mapAndValidateParamsToFields(request, fields);
	}
	
	public boolean mapResultSet(ResultSet result) {

		return CFWField.mapResultSetColumnsToFields(result, fields);
	}
	
	public CFWForm toForm(String formID, String submitLabel) {
		
		CFWForm form = new CFWForm(formID, submitLabel);
		form.setOrigin(this);
		
		for(CFWField<?> field : fields.values()) {
			form.addField(field);
		}
		
		return form;
	}
	
	public CFWForm toForm(String formID, String submitLabel, String ...fieldNames) {
		
		CFWForm form = new CFWForm(formID, submitLabel);
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
	
	
	public void addField(CFWField<?> field) {
		
		if(!fields.containsKey(field.getName())) {
			fields.put(field.getName(), field);
		}else {
			new CFWLog(logger)
			.method("addField")
			.severe("The field with name '"+field.getName()+"' was already added to this object. Check the naming of the field.");}
	}
	
	public void addAllFields(CFWField<?>[] fields) {
		for(CFWField<?> field : fields) {
			this.addField(field);
		}
	}
	
	public void addFields(CFWField<?> ...fields) {
		for(CFWField<?> field : fields) {
			this.addField(field);
		}
	}
	
	public CFWField<?> getField(String name) {
		return fields.get(name);
	}
	
	public CFWField<?> getFieldIgnoreCase(String name) {
		
		for(String current : fields.keySet()) {
			if(current.toLowerCase().equals(name.toLowerCase())) {
				return fields.get(current);
			}
		}
		return null;
	}
	
	public LinkedHashMap<String, CFWField<?>> getFields(){
		return fields;
	}
	
	public String getTableName() {
		return tableName;
	}

	public CFWObject setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}
			
	/*****************************************************************************
	 * Override this method to register API definitions.
	 * 
	 *****************************************************************************/
	public ArrayList<APIDefinition> getAPIDefinitions() {
		return null;
	}

	public CFWField<?> getPrimaryField() {
		return primaryField;
	}

	protected CFWObject setPrimaryField(CFWField<Integer> primaryField) {
		if(this.primaryField == null) {
			this.primaryField = primaryField;
		}else {
			new CFWLog(logger)
				.method("setPrimaryField")
				.severe("Attempt to set a second primary key on CFWObject. ", new IllegalStateException());
		}
		return this;
	}
	
	/*****************************************************************************
	 * Add a foreign key definition to this object.
	 * Only one foreign key per foreign object is allowed.
	 * 
	 * @param foreignObject
	 * @param fieldname
	 * @return instance for chaining
	 *****************************************************************************/
	public CFWObject addForeignKey(String fieldname, Class<? extends CFWObject> foreignObject, String foreignFieldname, String ondelete) {
		ForeignKeyDefinition fkd = new ForeignKeyDefinition();
		fkd.fieldname = fieldname;
		fkd.foreignObject = foreignObject;
		fkd.foreignFieldname = foreignFieldname;
		fkd.ondelete = ondelete;
		
		foreignKeys.add(fkd);
		return this;
	}
	
	public ArrayList<ForeignKeyDefinition> getForeignKeys() {
		return foreignKeys;
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
	// HIERARCHY
	//##############################################################################
	public boolean isHierarchical() {
		return (hierarchyLevels > 0);
	}

	public int getHierarchyLevels() {
		return hierarchyLevels;		
	}

	public void setHierarchyLevels(int hierarchyLevels) {
		CFWHierarchyUtil.setHierarchyLevels(this, hierarchyLevels);
	}

	public LinkedHashMap<Integer, CFWObject> getChildObjects() {
		return childObjects;
	}

	public void getChildObjects(LinkedHashMap<Integer, CFWObject> childObjects) {
		this.childObjects = childObjects;
	}
	
//	private CFWObject addChildObject(CFWObject child) {
//		this.childObjects.put(((Integer)child.getPrimaryField().getValue()), child);
//		return this;
//	}
	
	public void removeChildObject(CFWObject child) {
		this.childObjects.remove(((Integer)child.getPrimaryField().getValue()));
	}
		
	public CFWObject getParent() {
		return parent;
	}
	/****************************************************************
	 * Set the parent object of this object and adds it to the 
	 * The childs db entry has to be updated manually afterwards.
	 * 
	 * @return true if succesful, false otherwise.
	 * 
	 ****************************************************************/
	public boolean setParent(CFWObject parent) {
		return CFWHierarchyUtil.setParent(parent, this);
	}
	
	
	//##############################################################################
	// DATABASE
	//##############################################################################
	
	/****************************************************************
	 * Executed before createTable() is executed. Can be overriden
	 * to migrate existing tables to use CFWObjects instead.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public void migrateTable() {
		
	}

	/****************************************************************
	 * Create the table for this Object.
	 * Will be executed after all migrateTable() methods where executed
	 * of all objects in the Registry. 
	 * 
	 * @return true if successful, false otherwise
	 ****************************************************************/
	public boolean createTable() {
		return new CFWSQL(this).createTable();
	}
	
	/****************************************************************
	 * Executed after createTable() and before initDB() is executed. 
	 * Can be overriden to update tables and table data.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public void updateTable() {
		
	}
	
	/****************************************************************
	 * Will be executed after all createTable() methods where executed
	 * of all objects in the Registry. 
	 * 
	 ****************************************************************/
	public void initDB() {
		
	}
	
	/****************************************************************
	 * Will be executed after all initDB() methods where executed
	 * of all objects in the Registry. Use this in case you have 
	 * dependency on other data created first.
	 ****************************************************************/
	public void initDBSecond() {
		
	}
	
	/****************************************************************
	 * Will be executed after all initDBSecond() methods where executed
	 * of all objects in the Registry. Use this in case you have 
	 * dependency on other data created first.
	 ****************************************************************/
	public void initDBThird() {
		
	}
	
	/****************************************************************
	 * Caches the query with the specified name for lower performance
	 * impact.
	 * @param Class of the class using the query.
	 * @param name of the query
	 ****************************************************************/
	public CFWSQL queryCache(Class<?> clazz, String name) {
		return new CFWSQL(this).queryCache(clazz, name);
	}
	
	/****************************************************************
	 * Begins a SELECT * statement.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWSQL select() {
		return new CFWSQL(this).select();
	}
	
	/****************************************************************
	 * Begins a SELECT statement including the specified fields.
	 * @param field names
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWSQL select(ArrayList<String> fieldnames) {
		return new CFWSQL(this).select(fieldnames.toArray(new String[] {}));
	}
	
	/****************************************************************
	 * Begins a SELECT statement including the specified fields.
	 * @param field names
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWSQL select(String ...fieldnames) {
		return new CFWSQL(this).select(fieldnames);
	}
	
	/****************************************************************
	 * Begins a SELECT statement including all fields except the 
	 * ones specified by the parameter.
	 * @param fieldnames
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWSQL selectWithout(String ...fieldnames) {
		return new CFWSQL(this).selectWithout(fieldnames);
	}
	
	/****************************************************************
	 * Creates an insert statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean insert() {
		return new CFWSQL(this).insert();
	}
	
	/****************************************************************
	 * Creates an insert statement including the specified fields
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean insert(String ...fieldnames) {
		return new CFWSQL(this).insert(fieldnames);
	}
	
	/****************************************************************
	 * Creates an update statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean update() {
		return new CFWSQL(this).update();
	}
	
	/****************************************************************
	 * Creates an update statement including the specified fields
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean update(String ...fieldnames) {
		return new CFWSQL(this).update(fieldnames);
	}
	
	/****************************************************************
	 * Begins a DELETE statement.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWSQL delete() {
		return new CFWSQL(this).delete();
	}

}
