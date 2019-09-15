package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission.PermissionDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBGroup {

	public static String TABLE_NAME = "CFW_GROUP";
	public static String CFW_GROUP_SUPERUSER = "Superuser";
	public static String CFW_GROUP_ADMIN = "Administrator";
	public static String CFW_GROUP_FOREIGN_USER = "Foreign User";
	public static String CFW_GROUP_USER = "User";
	
	public static Logger logger = CFWLog.getLogger(CFWDBGroup.class.getName());
	
	enum GroupDBFields{
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
							  + GroupDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + GroupDBFields.NAME + " VARCHAR(255) UNIQUE,"
							  + GroupDBFields.DESCRIPTION + " CLOB,"
							  + GroupDBFields.IS_DELETABLE + " BOOLEAN"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
		
	}
	
	/********************************************************************************************
	 * Creates multiple groups in the DB.
	 * @param Groups with the values that should be inserted. ID will be set by the Database.
	 * @return nothing
	 * 
	 ********************************************************************************************/
	public static void create(Group... groups) {
		
		for(Group group : groups) {
			create(group);
		}
	}
	/********************************************************************************************
	 * Creates a new group in the DB.
	 * @param Group with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean create(Group group) {
		
		if(group == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The group cannot be null");
			return false;
		}
		
		if(group.name() == null || group.name().isEmpty()) {
			new CFWLog(logger)
				.method("create")
				.warn("Please specify a name for the group to create.");
			return false;
		}
		
		if(checkGroupExists(group)) {
			new CFWLog(logger)
				.method("create")
				.warn("The group '"+group.name()+"' cannot be created as a group with this name already exists.");
			return false;
		}
		
		String insertGroupSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				  + ") VALUES (?,?,?);";
		
		return CFWDB.preparedExecute(insertGroupSQL, 
				group.name(),
				group.description(),
				group.isDeletable()
				);
	}
	
	/***************************************************************
	 * Select a group by it's name.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static Group selectByName(String name ) {
		
		String selectByName = 
				"SELECT "
				  + GroupDBFields.PK_ID +", "
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ GroupDBFields.NAME + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, name);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new Group(result);
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
	public static Group selectByID(int id ) {
		
		String selectByName = 
				"SELECT "
				  + GroupDBFields.PK_ID +", "
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ GroupDBFields.PK_ID + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, id);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new Group(result);
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
	 * Select a group by it's ID and return it as JSON string.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static String getGroupAsJSON(String id) {
		
		String selectByName = 
				"SELECT "
				  + GroupDBFields.PK_ID +", "
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ GroupDBFields.PK_ID + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, id);
		
		String json = CFWDB.resultSetToJSON(result);
		CFWDB.close(result);	
		return json;
		
	}
	
	/***************************************************************
	 * Return a list of all groups
	 * 
	 * @return Returns a resultSet with all groups or null.
	 ****************************************************************/
	public static ResultSet getGroupList() {
		
		String selectByName = 
				"SELECT "
				  + GroupDBFields.PK_ID +", "
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				+" FROM "+TABLE_NAME
				+" ORDER BY LOWER("+GroupDBFields.NAME+")";
		
		return CFWDB.preparedExecuteQuery(selectByName);
		
	}
	
	/***************************************************************
	 * Return a list of all users as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getGroupListAsJSON() {
		ResultSet result = CFW.DB.Groups.getGroupList();
		return CFWDB.resultSetToJSON(result);
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param group
	 * @return true or false
	 ****************************************************************/
	public static boolean update(Group group) {
		
		String updateByID = 
				"UPDATE "+TABLE_NAME
				+" SET ("
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				  + ") = (?,?,?) "
				+" WHERE "
					+ GroupDBFields.PK_ID+" = ?";
		
		boolean result = CFWDB.preparedExecute(updateByID, 
				group.name(),
				group.description(),
				group.isDeletable(),
				group.id());
		
		
		return result;
		
	}
	
	/***************************************************************
	 * Retrieve the permissions for the specified group.
	 * @param group
	 * @return Hashmap with groups(key=group name, value=group object), or null on exception
	 ****************************************************************/
	public static HashMap<String, Permission> selectPermissionsForGroup(Group group) {
		return CFW.DB.GroupPermissionMap.selectPermissionsForGroup(group);
	}
	
	/****************************************************************
	 * Deletes the group by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		Group group = selectByID(id);
		if(group != null && group.isDeletable() == false) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The group '"+group.name()+"' cannot be deleted as it is marked as not deletable.");
			return false;
		}
		
		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ GroupDBFields.PK_ID+" = ? "
					+ "AND "
					+ PermissionDBFields.IS_DELETABLE+" = TRUE ";
		
		return CFWDB.preparedExecute(deleteByID, id);
			
	}
	
	/****************************************************************
	 * Deletes multiple users by id.
	 * @param ids of the users separated by comma
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteMultipleByID(String resultIDs) {
		
		//----------------------------------
		// Check input format
		if(resultIDs == null ^ !resultIDs.matches("(\\d,?)+")) {
			new CFWLog(logger)
			.method("deleteMultipleByID")
			.severe("The userID's '"+resultIDs+"' are not a comma separated list of strings.");
			return false;
		}

		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ GroupDBFields.PK_ID+" IN(?) "
					+ "AND "
					+ GroupDBFields.IS_DELETABLE+" = TRUE ";
		
		return CFWDB.preparedExecute(deleteByID, resultIDs);
			
	}
	
	/****************************************************************
	 * Deletes the group by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByName(String name) {
		
		Group group = selectByName(name);
		if(group != null && group.isDeletable() == false) {
			new CFWLog(logger)
			.method("deleteByName")
			.severe("The group '"+group.name()+"' cannot be deleted as it is marked as not deletable.");
			return false;
		}
		
		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ GroupDBFields.NAME+" = ? "
					+ "AND "
					+ PermissionDBFields.IS_DELETABLE+" = TRUE ";
		
		return CFWDB.preparedExecute(deleteByID, name);
			
	}
	
	
	/****************************************************************
	 * Check if the group exists by name.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkGroupExists(Group group) {
		if(group != null) {
			return checkGroupExists(group.name());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the group exists by name.
	 * 
	 * @param groupname to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkGroupExists(String groupName) {
		String checkExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+GroupDBFields.NAME+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkExistsSQL, groupName);
		
		try {
			if(result != null && result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("groupExists")
			.severe("Exception occured while checking of group exists.", e);
			
			return false;
		}finally {
			CFWDB.close(result);
		}
		
		
		return false; 
	}
	
}
