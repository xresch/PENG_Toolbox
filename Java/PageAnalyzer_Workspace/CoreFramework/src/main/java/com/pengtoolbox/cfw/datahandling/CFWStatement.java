package com.pengtoolbox.cfw.datahandling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWStatement {
	
	private static Logger logger = CFWLog.getLogger(CFWStatement.class.getName());
	
	private CFWObject object;
	private LinkedHashMap<String, CFWField> fields;
	
	private StringBuilder query = new StringBuilder();
	private ArrayList<Object> values = new ArrayList<Object>();
	
	private ResultSet result = null;
	
	
	public CFWStatement(CFWObject object) {
		this.object = object;
		this.fields = object.getFields();
	} 
	
	/****************************************************************
	 * 
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean createTable() {
		
		boolean success = true;
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+object.getTableName();
		success &= CFWDB.preparedExecute(createTableSQL);
		
		for(CFWField field : fields.values()) {
			if(field.getColumnDefinition() != null) {
				String addColumnIsRenamable = "ALTER TABLE "+object.getTableName()
				 +" ADD COLUMN IF NOT EXISTS "+field.getName()+" "+field.getColumnDefinition();
				success &= CFWDB.preparedExecute(addColumnIsRenamable);
			}else {
				new CFWLog(logger)
					.method("createTable")
					.severe("The field "+field.getName()+" is missing a columnDefinition. Use CFWField.setColumnDefinition(). ");
				success &= false;
			}
		}
		
		return success;
		
	}
	
	/****************************************************************
	 * Check if the fieldname is valid.
	 * @return true if valid, false otherwise
	 ****************************************************************/
	private boolean isFieldnameValid(String fieldname) {
		
		if(!fields.containsKey(fieldname)) {
			new CFWLog(logger)
			.method("selectBy")
			.severe("Field is not available: "+fieldname);
			return false;
		}
		
		return true;
	}
	
	/****************************************************************
	 * Begins a SELECT * statement.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement select() {
		query.append("SELECT * FROM "+object.getTableName());
		return this;
	}
	
	/****************************************************************
	 * Begins a SELECT statement including the specified fields.
	 * @param field names
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement select(String ...fieldnames) {
		
		query.append("SELECT");
		for(String fieldname : fieldnames) {
			if(isFieldnameValid(fieldname)) {
				query.append(" ").append(fieldname).append(",");
			}
		}
		query.deleteCharAt(query.length()-1);
		query.append(" FROM "+object.getTableName());
		return this;
	}
	
	/****************************************************************
	 * Creates an insert statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean insert() {
		return insert(fields.keySet().toArray(new String[] {}));
	}
	
	/****************************************************************
	 * Creates an insert statement including the specified fields
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean insert(String ...fieldnames) {
		
		StringBuilder columnNames = new StringBuilder("(");
		StringBuilder placeholders = new StringBuilder("(");
		
		for(CFWField field : fields.values()) {
			if(field != object.getPrimaryField()) {
				columnNames.append(field.getName()).append(",");
				placeholders.append("?,");
				values.add(field.getValue());
			}
		}
		
		//Replace last comma with closing brace
		columnNames.deleteCharAt(columnNames.length()-1).append(")");
		placeholders.deleteCharAt(placeholders.length()-1).append(")");
		
		query.append("INSERT INTO "+object.getTableName()+" "+columnNames
				  + " VALUES "+placeholders+";");
		
		return this.execute();
	}
	
	/****************************************************************
	 * Creates an update statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean update() {
		return update(fields.keySet().toArray(new String[] {}));
	}
	
	/****************************************************************
	 * Creates an update statement including the specified fields.
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public boolean update(String ...fieldnames) {
		
		StringBuilder columnNames = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();
		
		for(CFWField field : fields.values()) {
			if(!field.equals(object.getPrimaryField())) {
				columnNames.append(field.getName()).append(",");
				placeholders.append("?,");
				values.add(field.getValue());
			}
		}
		
		//Replace last comma with closing brace
		columnNames.deleteCharAt(columnNames.length()-1);
		placeholders.deleteCharAt(placeholders.length()-1);
		
		values.add(object.getPrimaryField().getValue());
		query.append("UPDATE "+object.getTableName()+" SET ("+columnNames
				  + ") = ("+placeholders+")"
				  +" WHERE "
					+ object.getPrimaryField().getName()+" = ?");
		
		return this.execute();
	}
	
	/****************************************************************
	 * Adds a WHERE clause to the query.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement where(String fieldname, Object value) {
		
		if(isFieldnameValid(fieldname)) {
			query.append(" WHERE "+fieldname).append(" = ?");
			values.add(value);
		}
		return this;
	}
	
	/****************************************************************
	 * Adds an AND clause to the query.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement and(String fieldname, Object value) {
		
		if(isFieldnameValid(fieldname)) {
			query.append(" AND "+fieldname).append(" = ?");
			values.add(value);
		}
		return this;
	}
	
	/****************************************************************
	 * Adds an OR clause to the query.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement or(String fieldname, Object value) {
		
		if(isFieldnameValid(fieldname)) {
			query.append(" OR "+fieldname).append(" = ?");
			values.add(value);
		}
		return this;
	}
	
	/****************************************************************
	 * Adds an ORDER BY clause to the query.
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement orderby(String fieldname) {
		
		if(isFieldnameValid(fieldname)) {
			
			if(fields.get(fieldname).getValueClass() == String.class) {
				query.append(" ORDER BY LOWER("+fieldname+")");
			}else {
				query.append(" ORDER BY "+fieldname+"");
			}
		}
		return this;
	}
	
	/****************************************************************
	 * 
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	public CFWStatement orderbyDesc(String fieldname) {
		
		if(isFieldnameValid(fieldname)) {
			
			if(fields.get(fieldname).getValueClass() == String.class) {
				query.append(" ORDER BY LOWER("+fieldname+") DESC");
			}else {
				query.append(" ORDER BY "+fieldname+" DESC");
			}
		}
		return this;
	}
	
	
	/****************************************************************
	 * Executes the query and saves the results in the global 
	 * variable.
	 * 
	 * @return CFWQuery for method chaining
	 ****************************************************************/
	private boolean execute() {
		
		if(result == null) {
			String statement = query.toString();
			
			if(statement.startsWith("SELECT")) {
				result = CFWDB.preparedExecuteQuery(statement, values.toArray());
				if(result != null) {
					return true;
				}else {
					return false;
				}
			}else {
				return CFWDB.preparedExecute(statement, values.toArray());
			}
		}
		
		return true;
	}
	
	/****************************************************************
	 * Executes the query and returns the result set.
	 * @return ResultSet or null 
	 ****************************************************************/
	public ResultSet getResultSet() {
		
		if(this.execute()) {
			return result;
		}else {
			return null;
		}
	}
	
	/****************************************************************
	 * Executes the query and returns the result set.
	 * @return CFWObject the resulting object
	 ****************************************************************/
	public CFWObject getFirstObject() {
		
		if(this.execute()) {

			try {
				if(result.next()) {
					CFWObject object = this.object.getClass().newInstance();
					object.mapResultSet(result);
					return object;
				}
			} catch (SQLException | InstantiationException | IllegalAccessException e) {
				new CFWLog(logger)
				.method("getFirstObject")
				.severe("Error reading object from database.", e);
				
			}finally {
				CFWDB.close(result);
			}
		}
		
		return null;
	}
	
	/***************************************************************
	 * Execute the Query and gets the result as Objects.
	 ****************************************************************/
	public ArrayList<CFWObject> getObjectList() {
		
		ArrayList<CFWObject> objectArray = new ArrayList<CFWObject>();
		
		if(this.execute()) {
			
			if(result == null) {
				return objectArray;
			}
			
			try {
				while(result.next()) {
					CFWObject current = object.getClass().newInstance();
					current.mapResultSet(result);
					objectArray.add(current);
				}
			} catch (SQLException | InstantiationException | IllegalAccessException e) {
				new CFWLog(logger)
				.method("getObjectList")
				.severe("Error reading object from database.", e);
				
			}finally {
				CFWDB.close(result);
			}
			
		}
		
		return objectArray;
		
	}
	
	/***************************************************************
	 * Execute the Query and gets the result as JSON string.
	 ****************************************************************/
	public String getAsJSON() {
		
		if(this.execute()) {
			return CFWDB.resultSetToJSON(result);
		}
		
		return "[]";
		
	}
	
	
	
}
