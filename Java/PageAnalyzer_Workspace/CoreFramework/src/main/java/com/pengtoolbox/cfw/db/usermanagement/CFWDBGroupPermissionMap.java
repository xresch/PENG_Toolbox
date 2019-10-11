package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.GroupPermissionMap.GroupPermissionMapFields;
import com.pengtoolbox.cfw.db.usermanagement.Permission.PermissionFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBGroupPermissionMap {

	public static String TABLE_NAME = "CFW_GROUP_PERMISSION_MAP";
	
	public static Logger logger = CFWLog.getLogger(CFWDBGroupPermissionMap.class.getName());
	


	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + GroupPermissionMapFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + GroupPermissionMapFields.FK_ID_GROUP + " INT, "
							  + GroupPermissionMapFields.FK_ID_PERMISSION + " INT, "
							  + "FOREIGN KEY ("+GroupPermissionMapFields.FK_ID_GROUP+") REFERENCES "+Group.TABLE_NAME+"("+Group.GroupFields.PK_ID+") ON DELETE CASCADE, "
							  + "FOREIGN KEY ("+GroupPermissionMapFields.FK_ID_PERMISSION+") REFERENCES "+CFWDBPermission.TABLE_NAME+"("+PermissionFields.PK_ID+") ON DELETE CASCADE"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
		
		String addColumnSQL = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN IF NOT EXISTS "+GroupPermissionMapFields.IS_DELETABLE+" BOOLEAN NOT NULL DEFAULT TRUE;";
		CFWDB.preparedExecute(addColumnSQL);
	}
	
	/********************************************************************************************
	 * Adds the permission to the specified group.
	 * @param permission
	 * @param group
	 * @return return true if user was added, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean addPermissionToGroup(Permission permission, Group group, boolean isDeletable) {
		
		if(permission == null || group == null ) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("Permission and group cannot be null.");
			return false;
		}
		
		if(permission.id() < 0 || group.id() < 0) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("Permission-ID and group-ID are not set correctly.");
			return false;
		}
		
		if(checkIsPermissionInGroup(permission, group)) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("The permission '"+permission.name()+"' is already part of the group '"+group.name()+"'.");
			return false;
		}
		
		return addPermissionToGroup(permission.id(), group.id(), isDeletable);
	}
	/********************************************************************************************
	 * Adds the permission to the specified group.
	 * @param permissionID
	 * @param groupID
	 * @return return true if permission was added, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean addPermissionToGroup(int permissionID, int groupID, boolean isDeletable) {
		
		
		if(permissionID < 0 || groupID < 0) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("Permission-ID or group-ID are not set correctly.");
			return false;
		}
		
		if(checkIsPermissionInGroup(permissionID, groupID)) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("The permission '"+permissionID+"' is already part of the group '"+groupID+"'.");
			return false;
		}
		
		String insertPermissionSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + GroupPermissionMapFields.FK_ID_PERMISSION +", "
				  + GroupPermissionMapFields.FK_ID_GROUP +", "
				  + GroupPermissionMapFields.IS_DELETABLE +" "
				  + ") VALUES (?,?,?);";
		
		return CFWDB.preparedExecute(insertPermissionSQL, 
				permissionID,
				groupID,
				isDeletable
				);
	}
	
	/********************************************************************************************
	 * Update if the permission can be deleted.
	 * @param user
	 * @param group
	 * @return return true if user was removed, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean updateIsDeletable(int permissionID, int groupID, boolean isDeletable) {
		String removeUserFromGroupSQL = "UPDATE "+TABLE_NAME
				+" SET "+ GroupPermissionMapFields.IS_DELETABLE +" = ? "
				+" WHERE "
				  + GroupPermissionMapFields.FK_ID_PERMISSION +" = ? "
				  + " AND "
				  + GroupPermissionMapFields.FK_ID_GROUP +" = ? "
				  + ";";
		
		return CFWDB.preparedExecute(removeUserFromGroupSQL, 
				isDeletable,
				permissionID,
				groupID
				);
	}
	/********************************************************************************************
	 * Adds the permission to the specified group.
	 * @param permission
	 * @param group
	 * @return return true if user was added, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean removePermissionFromGroup(Permission permission, Group group) {
		
		if(permission == null || group == null ) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("Permission and group cannot be null.");
			return false;
		}
		
		if(permission.id() < 0 || group.id() < 0) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("Permission-ID and group-ID are not set correctly.");
			return false;
		}
		
		if(!checkIsPermissionInGroup(permission, group)) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("The permission '"+permission.name()+"' is not part of the group '"+group.name()+"' and cannot be removed.");
			return false;
		}
		
		return removePermissionFromGroup(permission.id(), group.id());
	}
	/********************************************************************************************
	 * Remove a permission from the group.
	 * @param permission
	 * @param group
	 * @return return true if permission was removed, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean removePermissionFromGroup(int permissionID, int groupID) {
		
		if(!checkIsPermissionInGroup(permissionID, groupID)) {
			new CFWLog(logger)
				.method("removePermissionFromGroup")
				.warn("The permission '"+permissionID+"' is not part of the group '"+ groupID+"' and cannot be removed.");
			return false;
		}
		
		String removePermissionFromGroupSQL = "DELETE FROM "+TABLE_NAME
				+" WHERE "
				  + GroupPermissionMapFields.FK_ID_PERMISSION +" = ? "
				  + " AND "
				  + GroupPermissionMapFields.FK_ID_GROUP +" = ? "
				  + " AND "
				  + GroupPermissionMapFields.IS_DELETABLE +" = TRUE "
				  + ";";
		
		return CFWDB.preparedExecute(removePermissionFromGroupSQL, 
				permissionID,
				groupID
				);
	}
	
	/****************************************************************
	 * Check if the permission is in the given group.
	 * 
	 * @param permission to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkIsPermissionInGroup(Permission permission, Group group) {
		
		if(permission != null && group != null) {
			return checkIsPermissionInGroup(permission.id(), group.id());
		}else {
			new CFWLog(logger)
				.method("checkIsPermissionInGroup")
				.severe("The user and group cannot be null. User: '"+permission+"', Group: '"+group+"'");
			
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the permission exists by name.
	 * 
	 * @param permission to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkIsPermissionInGroup(int permissionid, int groupid) {
		
		String checkIsPermissionInGroup = "SELECT COUNT(*) FROM "+TABLE_NAME
				+" WHERE "+GroupPermissionMapFields.FK_ID_PERMISSION+" = ?"
				+" AND "+GroupPermissionMapFields.FK_ID_GROUP+" = ?";
		
		ResultSet result = CFW.DB.preparedExecuteQuery(checkIsPermissionInGroup, permissionid, groupid);
		
		try {
			if(result != null && result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (Exception e) {
			new CFWLog(logger)
			.method("checkIsPermissionInGroup")
			.severe("Exception occured while checking of group exists.", e);
			
			return false;
		}finally {
			CFWDB.close(result);
		}
		
		
		return false;
	}
	
	/***************************************************************
	 * Retrieve the permissions for the specified group.
	 * @param group
	 * @return Hashmap with groups(key=group name, value=group object), or null on exception
	 ****************************************************************/
	public static HashMap<String, Permission> selectPermissionsForGroup(Group group) {
		
		if( group == null) {
			new CFWLog(logger)
				.method("create")
				.severe("The user cannot be null");
			return null;
		}
		
		String selectPermissionsForGroup = "SELECT * FROM "+CFWDBPermission.TABLE_NAME+" P "
				+ " INNER JOIN "+CFWDBGroupPermissionMap.TABLE_NAME+" M "
				+ " ON M.FK_ID_PERMISSION = P.PK_ID "
				+ " WHERE M.FK_ID_GROUP = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectPermissionsForGroup, 
				group.id());
		
		HashMap<String, Permission> permissionMap = new HashMap<String, Permission>(); 
		
		try {
			while(result != null && result.next()) {
				Permission permission = new Permission(result);
				permissionMap.put(permission.name(), permission);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectGroupsForUser")
			.severe("Error while selecting permissions for the group '"+group.name()+"'.", e);
			return null;
		}finally {
			CFWDB.close(result);
		}
		
		return permissionMap;
	
	}
	
	
	/***************************************************************
	 * Retrieve the permissions for the specified user.
	 * @param group
	 * @return Hashmap with permissions(key=group name), or null on exception
	 ****************************************************************/
	public static HashMap<String, Permission> selectPermissionsForUser(User user) {
		
		if( user == null) {
			new CFWLog(logger)
				.method("create")
				.severe("The user cannot be null.");
			return null;
		}
		
		String selectPermissionsForUser = 
				"SELECT P.* "
				+"FROM CFW_PERMISSION P "
				+"JOIN CFW_GROUP_PERMISSION_MAP AS GP ON GP.FK_ID_PERMISSION = P.PK_ID "
				+"JOIN CFW_USER_GROUP_MAP AS UG ON UG.FK_ID_GROUP = GP.FK_ID_GROUP "
				+"WHERE UG.FK_ID_USER = ?;";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectPermissionsForUser, 
				user.id());
		
		HashMap<String, Permission> permissionMap = new HashMap<String, Permission>(); 
		
		try {
			while(result != null && result.next()) {
				Permission permission = new Permission(result);
				permissionMap.put(permission.name(), permission);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectGroupsForUser")
			.severe("Error while selecting permissions for the group '"+user.username()+"'.", e);
			return null;
		}finally {
			CFWDB.close(result);
		}
		
		return permissionMap;
	
	}
	/***************************************************************
	 * Returns a list of all groups and if the user is part of them 
	 * as a json array.
	 * @param group
	 * @return Hashmap with groups(key=group name, value=group object), or null on exception
	 ****************************************************************/
	public static String getPermissionMapForGroupAsJSON(String groupID) {
		
		//----------------------------------
		// Check input format
		if(groupID == null ^ !groupID.matches("\\d+")) {
			new CFWLog(logger)
			.method("getPermissionMapForGroupAsJSON")
			.severe("The groupID '"+groupID+"' is not a number.");
			return "[]";
		}
		
		String sqlString = "SELECT P.PK_ID, P.NAME, P.DESCRIPTION, M.FK_ID_GROUP AS ITEM_ID, M.IS_DELETABLE FROM "+CFWDBPermission.TABLE_NAME+" P "
				+ " LEFT JOIN "+CFWDBGroupPermissionMap.TABLE_NAME+" M "
				+ " ON M.FK_ID_PERMISSION = P.PK_ID"
				+ " AND M.FK_ID_GROUP = ?"
				+ " ORDER BY LOWER(P.NAME)";;
		
		ResultSet result = CFWDB.preparedExecuteQuery(sqlString, 
				groupID);
		
		String json = CFWDB.resultSetToJSON(result);
		CFWDB.close(result);	
		return json;

	}
	/***************************************************************
	 * Remove the user from the group if it is a member of the group, 
	 * add it otherwise.
	 ****************************************************************/
	public static boolean tooglePermissionInGroup(String permissionID, String groupID) {
		
		//----------------------------------
		// Check input format
		if(permissionID == null ^ !permissionID.matches("\\d+")) {
			new CFWLog(logger)
			.method("toogleUserInGroup")
			.severe("The userID '"+permissionID+"' is not a number.");
			return false;
		}
		
		//----------------------------------
		// Check input format
		if(groupID == null ^ !groupID.matches("\\d+")) {
			new CFWLog(logger)
			.method("toogleUserInGroup")
			.severe("The groupID '"+permissionID+"' is not a number.");
			return false;
		}
		
		return tooglePermissionInGroup(Integer.parseInt(permissionID), Integer.parseInt(groupID));
		
	}
	
	/***************************************************************
	 * Remove the user from the group if it is a member of the group, 
	 * add it otherwise.
	 ****************************************************************/
	public static boolean tooglePermissionInGroup(int userID, int groupID) {
		
		if(checkIsPermissionInGroup(userID, groupID)) {
			return removePermissionFromGroup(userID, groupID);
		}else {
			return addPermissionToGroup(userID, groupID, true);
		}

	}
		
}
