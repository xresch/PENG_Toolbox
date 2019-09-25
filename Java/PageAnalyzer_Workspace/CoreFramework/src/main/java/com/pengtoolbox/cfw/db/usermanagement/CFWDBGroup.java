package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission.PermissionDBFields;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBGroup {

	public static String TABLE_NAME = "CFW_GROUP";
	public static String CFW_GROUP_SUPERUSER = "Superuser";
	public static String CFW_GROUP_ADMIN = "Administrator";
	public static String CFW_GROUP_FOREIGN_USER = "Foreign User";
	public static String CFW_GROUP_USER = "User";
	
	public static Logger logger = CFWLog.getLogger(CFWDBGroup.class.getName());
	
	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + GroupFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + GroupFields.NAME + " VARCHAR(255) UNIQUE,"
							  + GroupFields.DESCRIPTION + " CLOB,"
							  + GroupFields.IS_DELETABLE + " BOOLEAN"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
		
		//--------------------------------------
		// Add Column IS_RENAMABLE
		String addColumnIsRenamable = "ALTER TABLE "+TABLE_NAME
									 +" ADD COLUMN IF NOT EXISTS "+GroupFields.IS_RENAMABLE+" BOOLEAN DEFAULT TRUE;";
		
		CFWDB.preparedExecute(addColumnIsRenamable);
				
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
		
		return group.insert();
	}
	
	/***************************************************************
	 * Select a group by it's name.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static Group selectByName(String name ) {
		
		return (Group)new Group()
				.select()
				.where(GroupFields.NAME.toString(), name)
				.getFirstObject();

	}
	
	/***************************************************************
	 * Select a group by it's ID.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static Group selectByID(int id ) {

		return (Group)new Group()
				.select()
				.where(GroupFields.PK_ID.toString(), id)
				.getFirstObject();
		
	}
	
	/***************************************************************
	 * Select a group by it's ID and return it as JSON string.
	 * @param id of the group
	 * @return Returns a group or null if not found or in case of exception.
	 ****************************************************************/
	public static String getGroupAsJSON(String id) {
		
		return new Group()
				.select()
				.where(GroupFields.PK_ID.toString(), Integer.parseInt(id))
				.getAsJSON();
		
	}
	
	/***************************************************************
	 * Return a list of all groups
	 * 
	 * @return Returns a resultSet with all groups or null.
	 ****************************************************************/
	public static ResultSet getGroupList() {
		
		return new Group()
				.select()
				.orderby(GroupFields.NAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all users as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getGroupListAsJSON() {
		return new Group("")
				.select()
				.orderby(GroupFields.NAME.toString())
				.getAsJSON();
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param group
	 * @return true or false
	 ****************************************************************/
	public static boolean update(Group group) {
		
		if(group == null) {
			new CFWLog(logger)
				.method("update")
				.warn("The group cannot be null");
			return false;
		}
		
		if(group.name() == null || group.name().isEmpty()) {
			new CFWLog(logger)
				.method("update")
				.warn("Please specify a name for the group to create.");
			return false;
		}
				
		return group.update();
		
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
					+ GroupFields.PK_ID+" = ? "
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
					+ GroupFields.PK_ID+" IN(?) "
					+ "AND "
					+ GroupFields.IS_DELETABLE+" = TRUE ";
		
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
					+ GroupFields.NAME+" = ? "
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
		String checkExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+GroupFields.NAME+" = ?";
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
