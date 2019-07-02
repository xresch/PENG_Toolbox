package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup.GroupDBFields;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission.PermissionDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBGroupPermissionMap {

	public static String TABLE_NAME = "CFW_GROUP_PERMISSION_MAP";
	
	public static Logger logger = CFWLog.getLogger(CFWDBGroupPermissionMap.class.getName());
	
	enum GroupPermissionMapDBFields{
		PK_ID, 
		FK_ID_PERMISSION,
		FK_ID_GROUP,
	}

	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + GroupPermissionMapDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + GroupPermissionMapDBFields.FK_ID_GROUP + " INT, "
							  + GroupPermissionMapDBFields.FK_ID_PERMISSION + " INT, "
							  + "FOREIGN KEY ("+GroupPermissionMapDBFields.FK_ID_GROUP+") REFERENCES "+CFWDBGroup.TABLE_NAME+"("+GroupDBFields.PK_ID+") ON DELETE CASCADE, "
							  + "FOREIGN KEY ("+GroupPermissionMapDBFields.FK_ID_PERMISSION+") REFERENCES "+CFWDBPermission.TABLE_NAME+"("+PermissionDBFields.PK_ID+") ON DELETE CASCADE"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
		
	}
	
	/********************************************************************************************
	 * Adds the permission to the specified group.
	 * @param permission
	 * @param group
	 * @return return true if permission was added, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean addPermissionToGroup(Permission permission, Group group) {
		
		if(permission == null || group == null ) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("User and group cannot be null.");
			return false;
		}
		
		if(permission.id() < 0 || group.id() < 0) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("User-ID and group-ID are not set correctly.");
			return false;
		}
		
		if(checkIsPermissionInGroup(permission, group)) {
			new CFWLog(logger)
				.method("addPermissionToGroup")
				.warn("The permission '"+permission.name()+"' is already part of the group '"+group.name()+"'.");
			return false;
		}
		
		String insertPermissionSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + GroupPermissionMapDBFields.FK_ID_PERMISSION +", "
				  + GroupPermissionMapDBFields.FK_ID_GROUP +" "
				  + ") VALUES (?,?);";
		
		return CFWDB.preparedExecute(insertPermissionSQL, 
				permission.id(),
				group.id()
				);
	}
	
	/********************************************************************************************
	 * Remove a permission from the group.
	 * @param permission
	 * @param group
	 * @return return true if permission was removed, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean removePermissionFromGroup(Permission permission, Group group) {
		
		if(permission == null || group == null ) {
			new CFWLog(logger)
				.method("removePermissionFromGroup")
				.warn("User and group cannot be null.");
			return false;
		}
		
		if(!checkIsPermissionInGroup(permission, group)) {
			new CFWLog(logger)
				.method("removePermissionFromGroup")
				.warn("The user '"+permission.name()+"' is not part of the group '"+group.name()+"' and cannot be removed.");
			return false;
		}
		
		String removePermissionFromGroupSQL = "DELETE FROM "+TABLE_NAME
				+" WHERE "
				  + GroupPermissionMapDBFields.FK_ID_PERMISSION +" = ? "
				  + " AND "
				  + GroupPermissionMapDBFields.FK_ID_GROUP +" = ? "
				  + ";";
		
		return CFWDB.preparedExecute(removePermissionFromGroupSQL, 
				permission.id(),
				group.id()
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
				+" WHERE "+GroupPermissionMapDBFields.FK_ID_PERMISSION+" = ?"
				+" AND "+GroupPermissionMapDBFields.FK_ID_GROUP+" = ?";
		
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
	
	
		
}
