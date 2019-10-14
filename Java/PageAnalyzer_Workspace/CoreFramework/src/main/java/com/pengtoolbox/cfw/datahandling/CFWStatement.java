package com.pengtoolbox.cfw.datahandling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWObject.ForeignKeyDefinition;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWStatement {
	
	private static Logger logger = CFWLog.getLogger(CFWStatement.class.getName());
	private static HashMap<String, String> queryCache = new HashMap<String, String>();
	private String queryName = null;
	
	private CFWObject object;
	private LinkedHashMap<String, CFWField<?>> fields;
	
	private StringBuilder query = new StringBuilder();
	private ArrayList<Object> values = new ArrayList<Object>();
	
	private ResultSet result = null;
	
	public CFWStatement(CFWObject object) {
		this.object = object;
		this.fields = object.getFields();
	} 
	
	/****************************************************************
	 * Reset this object and make it ready for another execution.
	 ****************************************************************/
	public CFWStatement reset() {
		query = new StringBuilder();
		values = new ArrayList<Object>();
		queryName = null;
		return this;
	}
	
	/****************************************************************
	 * Caches the query with the specified name for lower performance
	 * impact.
	 * @param Class of the class using the query.
	 * @param name of the query
	 ****************************************************************/
	public CFWStatement queryCache(Class<?> clazz, String name) {
		this.queryName = clazz.getName()+"."+name;
		return this;
	}
	
	/****************************************************************
	 * Check if the fieldname is valid.
	 * @return true if valid, false otherwise
	 ****************************************************************/
	private boolean isQueryCached() {
		
		if(queryName != null && queryCache.containsKey(queryName)) {
			return true;
		}
		
		return false;
	}
		
	
	/****************************************************************
	 * 
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean createTable() {
		
		//------------------------------------
		// Create Table
		boolean success = true;
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+object.getTableName();
		success &= CFWDB.preparedExecute(createTableSQL);
		
		//------------------------------------
		// Create Columns
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
		
		//------------------------------------
		// Create ForeignKeys
		for(ForeignKeyDefinition fkd : object.getForeignKeys()) {
			String foreignTable;
			try {
				foreignTable = fkd.foreignObject.newInstance().getTableName();
					
				// ALTER TABLE PUBLIC.CORE_USERROLE_TO_PARAMETER ADD CONSTRAINT IF NOT EXISTS PUBLIC.CURTBP_USER_ID FOREIGN KEY(USER_ID) REFERENCES PUBLIC.CORE_USER(ID) NOCHECK;
				String createForeignKeysSQL = "ALTER TABLE "+object.getTableName()
				  + " ADD CONSTRAINT IF NOT EXISTS PUBLIC.FK_"+object.getTableName()+"_"+fkd.fieldname
				  + " FOREIGN KEY ("+fkd.fieldname
				  + ") REFERENCES "+foreignTable+"("+fkd.foreignFieldname+") ON DELETE "+fkd.ondelete;
			
				success &= CFWDB.preparedExecute(createForeignKeysSQL);
				
			} catch (Exception e) {
				new CFWLog(logger)
				.method("createTable")
				.severe("An error occured trying to create foreign keys for table: "+object.getTableName(), e);
			} 
			
		}
		
		return success;
		
	}
		
	/****************************************************************
	 * Begins a SELECT * statement.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement select() {
		if(!isQueryCached()) {
			query.append("SELECT * FROM "+object.getTableName());
		}
		return this;
	}
	
	/****************************************************************
	 * Begins a SELECT statement including the specified fields.
	 * @param field names
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement select(String ...fieldnames) {
		if(!isQueryCached()) {
			query.append("SELECT");
			for(String fieldname : fieldnames) {
					query.append(" ").append(fieldname).append(",");
			}
			query.deleteCharAt(query.length()-1);
			query.append(" FROM "+object.getTableName());
		}
		return this;
	}
	
	/****************************************************************
	 * Begins a SELECT statement including all fields except the 
	 * ones specified by the parameter.
	 * @param fieldnames
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement selectWithout(String ...fieldnames) {
		if(!isQueryCached()) {
			query.append("SELECT");
			Arrays.sort(fieldnames);
			for(String name : fields.keySet()) {
				//add if name is not in fieldnames
				if(Arrays.binarySearch(fieldnames, name) < 0) {
					query.append(" ").append(name).append(",");
				}
			}
			query.deleteCharAt(query.length()-1);
			query.append(" FROM "+object.getTableName());
		}
		return this;
	}
	
	/****************************************************************
	 * Creates an insert statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean insert() {

		return insert(fields.keySet().toArray(new String[] {}));
	}
	
	/****************************************************************
	 * Creates an insert statement including the specified fields
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean insert(String ...fieldnames) {
		
			StringBuilder columnNames = new StringBuilder("(");
			StringBuilder placeholders = new StringBuilder("(");
			
			for(String fieldname : fieldnames) {
				CFWField field = fields.get(fieldname);
				if(field != object.getPrimaryField()) {
					if(!isQueryCached()) {
						columnNames.append(field.getName()).append(",");
						placeholders.append("?,");
					}
					values.add(field.getValue());
				}
			}
			
			//Replace last comma with closing brace
			columnNames.deleteCharAt(columnNames.length()-1).append(")");
			placeholders.deleteCharAt(placeholders.length()-1).append(")");
			if(!isQueryCached()) {	
				query.append("INSERT INTO "+object.getTableName()+" "+columnNames
					  + " VALUES "+placeholders+";");
			}

		return this.execute();
	}
	
	/****************************************************************
	 * Creates an update statement including all fields and executes
	 * the statement with the values assigned to the fields of the
	 * object.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean update() {
		return update(fields.keySet().toArray(new String[] {}));
	}
	
	/****************************************************************
	 * Creates an update statement including the specified fields.
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean update(String ...fieldnames) {
		
		StringBuilder columnNames = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();
		
		for(String fieldname : fieldnames) {
			CFWField field = fields.get(fieldname);
			if(!field.equals(object.getPrimaryField())) {
				
				if(!isQueryCached()) {
					columnNames.append(field.getName()).append(",");
					placeholders.append("?,");
				}
				values.add(field.getValue());
			}
		}
		
		if(!isQueryCached()) {
			//Replace last comma with closing brace
			columnNames.deleteCharAt(columnNames.length()-1);
			placeholders.deleteCharAt(placeholders.length()-1);
		}
		
		values.add(object.getPrimaryField().getValue());
		
		if(!isQueryCached()) {
			query.append("UPDATE "+object.getTableName()+" SET ("+columnNames
					  + ") = ("+placeholders+")"
					  +" WHERE "
					  + object.getPrimaryField().getName()+" = ?");
		}
		return this.execute();
	}
	
	/****************************************************************
	 * Creates an update statement including the specified fields.
	 * and executes it with the values assigned to the fields of the
	 * object.
	 * @param fieldnames
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean updateWithout(String ...fieldnames) {
		
		StringBuilder columnNames = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();
		Arrays.sort(fieldnames);
		for(String name : fields.keySet()) {
			//add if name is not in fieldnames
			if(Arrays.binarySearch(fieldnames, name) < 0) {
				CFWField field = fields.get(name);
				if(!field.equals(object.getPrimaryField())) {
					
					
					if(!isQueryCached()) {
						columnNames.append(field.getName()).append(",");
						placeholders.append("?,");
					}
					values.add(field.getValue());
				}
			}
		}
		for(String fieldname : fieldnames) {
			
		}
		
		if(!isQueryCached()) {
			//Replace last comma with closing brace
			columnNames.deleteCharAt(columnNames.length()-1);
			placeholders.deleteCharAt(placeholders.length()-1);
		}
		
		values.add(object.getPrimaryField().getValue());
		
		if(!isQueryCached()) {
			query.append("UPDATE "+object.getTableName()+" SET ("+columnNames
					  + ") = ("+placeholders+")"
					  +" WHERE "
					  + object.getPrimaryField().getName()+" = ?");
		}
		return this.execute();
	}
	
	/****************************************************************
	 * Begins a DELETE statement.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement delete() {
		if(!isQueryCached()) {		
			query.append("DELETE FROM "+object.getTableName());
		}
		return this;
	}
	
	/****************************************************************
	 * Adds a WHERE clause to the query.
	 * This method is case sensitive.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement where(String fieldname, Object value) {
		return where(fieldname, value, true);
	}
	
	/****************************************************************
	 * Adds a WHERE clause to the query.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement where(String fieldname, Object value, boolean isCaseSensitive) {
		if(!isQueryCached()) {
			if(isCaseSensitive) {
				query.append(" WHERE "+fieldname).append(" = ?");	
			}else {
				query.append(" WHERE LOWER("+fieldname).append(") = LOWER(?)");	
			}
		}
		values.add(value);
		return this;
	}
	
	/****************************************************************
	 * Adds a WHERE <fieldname> IN(?) clause to the query.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement whereIn(String fieldname, Object value) {
		if(!isQueryCached()) {
			query.append(" WHERE "+fieldname).append(" IN(?)");
		}
		values.add(value);
		return this;
	}

	/****************************************************************
	 * Adds a WHERE <fieldname> IN(?,?...) clause to the query.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement whereIn(String fieldname, Object ...values) {
			
		StringBuilder placeholders = new StringBuilder();
		for(Object value : values) {
			placeholders.append("?,");
			this.values.add(value);
		}
		placeholders.deleteCharAt(placeholders.length()-1);
		
		if(!isQueryCached()) {
			query.append(" WHERE "+fieldname).append(" IN(").append(placeholders).append(")");
		}
		
		return this;
	}
	
	/****************************************************************
	 * Begins a SELECT COUNT(*) statement.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement selectCount() {
		if(!isQueryCached()) {
			query.append("SELECT COUNT(*) FROM "+object.getTableName());
		}
		return this;
	}
		
	/****************************************************************
	 * Adds a AND clause to the query.
	 * This method is case sensitive.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement and(String fieldname, Object value) {
		return and(fieldname, value, true);
	}
	
	/****************************************************************
	 * Adds a WHERE clause to the query.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement and(String fieldname, Object value, boolean isCaseSensitive) {
		if(!isQueryCached()) {
			if(isCaseSensitive) {
				query.append(" AND "+fieldname).append(" = ?");	
			}else {
				query.append(" AND LOWER("+fieldname).append(") = LOWER(?)");	
			}
		}
		values.add(value);
		return this;
	}
	
	/****************************************************************
	 * Adds a OR clause to the query.
	 * This method is case sensitive.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement or(String fieldname, Object value) {
		return or(fieldname, value, true);
	}
	
	/****************************************************************
	 * Adds a WHERE clause to the query.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement or(String fieldname, Object value, boolean isCaseSensitive) {
		if(!isQueryCached()) {
			if(isCaseSensitive) {
				query.append(" OR "+fieldname).append(" = ?");	
			}else {
				query.append(" OR LOWER("+fieldname).append(") = LOWER(?)");	
			}
		}
		values.add(value);
		return this;
	}
	
	/****************************************************************
	 * Adds an ORDER BY clause to the query. Is case sensitive for
	 * strings.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement orderby(String fieldname) {
		if(!isQueryCached()) {
			if(fields.get(fieldname).getValueClass() == String.class) {
				query.append(" ORDER BY LOWER("+fieldname+")");
			}else {
				query.append(" ORDER BY "+fieldname+"");
			}
		}
		return this;
	}
	
	/****************************************************************
	 * Adds an ORDER BY clause to the query. Is case sensitive for
	 * strings. Sort order is descending.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement orderbyDesc(String fieldname) {
		if(!isQueryCached()) {				
			if(fields.get(fieldname).getValueClass() == String.class) {
				query.append(" ORDER BY LOWER("+fieldname+") DESC");
			}else {
				query.append(" ORDER BY "+fieldname+" DESC");
			}
		}
		return this;
	}
	
	
	/****************************************************************
	 * Adds a custom part to the query and values for the binding.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement custom(String queryPart, Object value) {
		if(!isQueryCached()) {
			query.append(queryPart);
		}
		values.add(value);
		return this;
	}
	
	/****************************************************************
	 * Adds a custom part to the query.
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public CFWStatement custom(String queryPart) {
		if(!isQueryCached()) {
			query.append(queryPart);
		}
		return this;
	}
	
	
	/****************************************************************
	 * Executes the query and saves the results in the global 
	 * variable.
	 * 
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	private boolean execute() {
		
		//----------------------------
		// Handle Caching
		String statement;
		
		if(isQueryCached()) {
			statement = queryCache.get(queryName);
		}else {
			statement = query.toString();
			queryCache.put(queryName, statement);
		}
		
		//----------------------------
		// Execute Statement 
		if(statement.trim().startsWith("SELECT")) {
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
	
	/****************************************************************
	 * Executes the query and saves the results in the global 
	 * variable.
	 * 
	 * @return CFWStatement for method chaining
	 ****************************************************************/
	public boolean executeDelete() {
		
		boolean success = this.execute();
		CFWDB.close(result);
		
		return success;
	}
	
	/****************************************************************
	 * Executes the query and saves the results in the global 
	 * variable.
	 * 
	 * @return int count or -1 on error
	 ****************************************************************/
	public int getCount() {
		
		try {
			
			this.execute();
			if(result != null) {	
				//----------------------------
				//Get Query
				String statement;
				
				if(isQueryCached()) {
					statement = queryCache.get(queryName);
				}else {
					statement = query.toString();
					queryCache.put(queryName, statement);
				}
				
				//----------------------------
				//Get Count
				if(statement.toString().trim().contains("SELECT COUNT(*)")) {
					if(result.next()) {
						return result.getInt(1);
					}
				}else {
					  result.last();    // moves cursor to the last row
					  return result.getRow();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			CFWDB.close(result);
		}
		return -1;
	}
	
	/****************************************************************
	 * Executes the query and returns the result set.
	 * Don't forget to close the connection using CFW.DB.close();
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
		
		try {
			if(this.execute()) {
				if(result.next()) {
					CFWObject object = this.object.getClass().newInstance();
					object.mapResultSet(result);
					return object;
				}
			}
		}catch (SQLException | InstantiationException | IllegalAccessException e) {
			new CFWLog(logger)
			.method("getFirstObject")
			.severe("Error reading object from database.", e);
			
		}finally {
			CFWDB.close(result);
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
		
		String	string = CFWDB.resultSetToJSON(result);
		CFWDB.close(result);
		
		return string;
		
	}
	
	/***************************************************************
	 * Execute the Query and gets the result as JSON string.
	 ****************************************************************/
	public String getAsCSV() {
		
		String string = CFWDB.resultSetToCSV(result, ";");
		CFWDB.close(result);
		
		return string;
		
	}
	
	/***************************************************************
	 * Execute the Query and gets the result as XML string.
	 ****************************************************************/
	public String getAsXML() {
		
		String	string = CFWDB.resultSetToXML(result);
		CFWDB.close(result);
		
		return string;
		
	}
	
	
}
