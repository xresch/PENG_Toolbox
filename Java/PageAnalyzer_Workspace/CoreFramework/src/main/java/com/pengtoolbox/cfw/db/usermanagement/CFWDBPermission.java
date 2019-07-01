package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBPermission {

	public static String TABLE_NAME = "CFW_PERMISSION";
	
	public static Logger logger = CFWLog.getLogger(CFWDBPermission.class.getName());
	
	enum PermissionDBFields{
		PK_ID, 
		NAME,
		DESCRIPTION,
		IS_DELETABLE,
	}

	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + PermissionDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + PermissionDBFields.NAME + " VARCHAR(255) UNIQUE,"
							  + PermissionDBFields.DESCRIPTION + " CLOB,"
							  + PermissionDBFields.IS_DELETABLE + " BOOLEAN"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
		
	}
	
	/********************************************************************************************
	 * Creates multiple groups in the DB.
	 * @param Groups with the values that should be inserted. ID will be set by the Database.
	 * @return nothing
	 * 
	 ********************************************************************************************/
	public static void create(Permission... permissions) {
		
		for(Permission permission : permissions) {
			create(permission);
		}
	}
	/********************************************************************************************
	 * Creates a new group in the DB.
	 * @param Group with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean create(Permission permission) {
		
		if(permission == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The group cannot be null");
			return false;
		}
		
		if(permission.name() == null || permission.name().isEmpty()) {
			new CFWLog(logger)
				.method("create")
				.warn("Please specify a name for the group to create.");
			return false;
		}
		
		if(checkGroupExists(permission)) {
			new CFWLog(logger)
				.method("create")
				.warn("The group '"+permission.name()+"' cannot be created as a group with this name already exists.");
			return false;
		}
		
		String insertGroupSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + PermissionDBFields.NAME +", "
				  + PermissionDBFields.DESCRIPTION +", "
				  + PermissionDBFields.IS_DELETABLE +" "
				  + ") VALUES (?,?,?);";
		
		return CFWDB.preparedExecute(insertGroupSQL, 
				permission.name(),
				permission.description(),
				permission.isDeletable()
				);
	}
	
	/***************************************************************
	 * Select a group by it's name.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static Permission selectByName(String name ) {
		
		String selectByName = 
				"SELECT "
				  + PermissionDBFields.PK_ID +", "
				  + PermissionDBFields.NAME +", "
				  + PermissionDBFields.DESCRIPTION +", "
				  + PermissionDBFields.IS_DELETABLE +" "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ PermissionDBFields.NAME + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, name);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new Permission(result);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectByName")
			.severe("Error reading group from database.", e);;
			
		}finally {
			CFWDB.close(result);
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Select a group by it's ID.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static Permission selectByID(int id ) {
		
		String selectByName = 
				"SELECT "
				  + PermissionDBFields.PK_ID +", "
				  + PermissionDBFields.NAME +", "
				  + PermissionDBFields.DESCRIPTION +", "
				  + PermissionDBFields.IS_DELETABLE +" "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ PermissionDBFields.PK_ID + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, id);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new Permission(result);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectByID")
			.severe("Error reading group from database.", e);;
			
		}finally {
			CFWDB.close(result);
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param permission
	 * @return true or false
	 ****************************************************************/
	public static boolean update(Permission permission) {
		
		String updateByID = 
				"UPDATE "+TABLE_NAME
				+" SET ("
				  + PermissionDBFields.NAME +", "
				  + PermissionDBFields.DESCRIPTION +", "
				  + PermissionDBFields.IS_DELETABLE +" "
				  + ") = (?,?,?) "
				+" WHERE "
					+ PermissionDBFields.PK_ID+" = ?";
		
		boolean result = CFWDB.preparedExecute(updateByID, 
				permission.name(),
				permission.description(),
				permission.isDeletable(),
				permission.id());
		
		
		return result;
		
	}
	
	/****************************************************************
	 * Deletes the group by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		Permission permission = selectByID(id);
		if(permission != null && permission.isDeletable() == false) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The permission '"+permission.name()+"' cannot be deleted as it is marked as not deletable.");
			return false;
		}
		
		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ PermissionDBFields.PK_ID+" = ? "
					+ "AND "
					+ PermissionDBFields.IS_DELETABLE+" = TRUE ";
		
		return CFWDB.preparedExecute(deleteByID, id);
			
	}
	
	
	/****************************************************************
	 * Check if the group exists by name.
	 * 
	 * @param permission to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkGroupExists(Permission permission) {
		if(permission != null) {
			return checkPermissionExists(permission.name());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the group exists by name.
	 * 
	 * @param groupname to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkPermissionExists(String permissionName) {
		String checkExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+PermissionDBFields.NAME+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkExistsSQL, permissionName);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("checkPermissionExists")
			.severe("Exception occured while checking of group exists.", e);
			
			return false;
		}
		
		return false; 
	}
	
}
