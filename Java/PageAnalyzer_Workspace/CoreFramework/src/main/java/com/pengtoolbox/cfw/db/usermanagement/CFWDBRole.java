package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.CFWDBDefaultOperations;
import com.pengtoolbox.cfw.db.PrecheckHandler;
import com.pengtoolbox.cfw.db.usermanagement.Role.RoleFields;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDBRole extends CFWDBDefaultOperations<Role> {

	
	public static String CFW_ROLE_SUPERUSER = "Superuser";
	public static String CFW_ROLE_ADMIN = "Administrator";
	public static String CFW_ROLE_USER = "User";
	
	public static Logger logger = CFWLog.getLogger(CFWDBRole.class.getName());
	
	static {
		precheckForCreate(new Role(), new PrecheckHandler() {

			public boolean doCheck(CFWObject object) {
				Role role = (Role)object;
				
				System.out.println("doCheck() Executed!!!");
				if(role.name() == null || role.name().isEmpty()) {
					new CFWLog(logger)
						.method("create")
						.warn("Please specify a name for the role to create.");
					return false;
				}
				
				if(checkRoleExists(role)) {
					new CFWLog(logger)
						.method("create")
						.warn("The role '"+role.name()+"' cannot be created as a role with this name already exists.");
					return false;
				}
				
				return false;
			}
			
		});
	}

	/***************************************************************
	 * Select a role by it's name.
	 * @param id of the role
	 * @return Returns a role or null if not found or in case of exception.
	 ****************************************************************/
	public static Role selectByName(String name) {
		
		return (Role)new Role()
				.queryCache(CFWDBRole.class, "selectByName")
				.select()
				.where(RoleFields.NAME.toString(), name)
				.getFirstObject();

	}
	
	/***************************************************************
	 * Select a role by it's ID.
	 * @param id of the role
	 * @return Returns a role or null if not found or in case of exception.
	 ****************************************************************/
	public static Role selectByID(int id ) {

		return (Role)new Role()
				.queryCache(CFWDBRole.class, "selectByID")
				.select()
				.where(RoleFields.PK_ID.toString(), id)
				.getFirstObject();
		
	}
	
	/***************************************************************
	 * Select a role by it's ID and return it as JSON string.
	 * @param id of the role
	 * @return Returns a role or null if not found or in case of exception.
	 ****************************************************************/
	public static String getUserRolesAsJSON(String id) {
		
		return new Role()
				.queryCache(CFWDBRole.class, "getUserRolesAsJSON")
				.select()
				.where(RoleFields.PK_ID.toString(), Integer.parseInt(id))
				.and(RoleFields.CATEGORY.toString(), "user")
				.getAsJSON();
		
	}
	
	/***************************************************************
	 * Return a list of all user roles
	 * 
	 * @return Returns a resultSet with all roles or null.
	 ****************************************************************/
	public static ResultSet getUserRoleList() {
		
		return new Role()
				.queryCache(CFWDBRole.class, "getUserRoleList")
				.select()
				.where(RoleFields.CATEGORY.toString(), "user")
				.orderby(RoleFields.NAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all user roles as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getUserRoleListAsJSON() {
		return new Role()
				.queryCache(CFWDBRole.class, "getUserRoleListAsJSON")
				.select()
				.where(RoleFields.CATEGORY.toString(), "user")
				.orderby(RoleFields.NAME.toString())
				.getAsJSON();
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param role
	 * @return true or false
	 ****************************************************************/
	public static boolean update(Role role) {
		
		if(role == null) {
			new CFWLog(logger)
				.method("update")
				.warn("The role that should be updated cannot be null");
			return false;
		}
		
		if(role.name() == null || role.name().isEmpty()) {
			new CFWLog(logger)
				.method("update")
				.warn("Please specify a name for the role.");
			return false;
		}
				
		return role
				.queryCache(CFWDBRole.class, "update")
				.update();
		
	}
	
	/***************************************************************
	 * Retrieve the permissions for the specified role.
	 * @param role
	 * @return Hashmap with roles(key=role name, value=role object), or null on exception
	 ****************************************************************/
	public static HashMap<String, Permission> selectPermissionsForRole(Role role) {
		return CFW.DB.RolePermissionMap.selectPermissionsForRole(role);
	}
	
	/****************************************************************
	 * Deletes the role by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		Role role = selectByID(id);
		if(role != null && role.isDeletable() == false) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The role '"+role.name()+"' cannot be deleted as it is marked as not deletable.");
			return false;
		}
		
		return new Role()
				.queryCache(CFWDBRole.class, "deleteByID")
				.delete()
				.where(RoleFields.PK_ID.toString(), id)
				.and(RoleFields.IS_DELETABLE.toString(), true)
				.executeDelete();
					
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

		return new Role()
				.queryCache(CFWDBRole.class, "deleteMultipleByID")
				.delete()
				.whereIn(RoleFields.PK_ID.toString(), resultIDs)
				.and(RoleFields.IS_DELETABLE.toString(), true)
				.executeDelete();
					
	}
	
	/****************************************************************
	 * Deletes the role by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByName(String name) {
		
		Role role = selectByName(name);
		if(role != null && role.isDeletable() == false) {
			new CFWLog(logger)
			.method("deleteByName")
			.severe("The role '"+role.name()+"' cannot be deleted as it is marked as not deletable.");
			return false;
		}
		
		return new Role()
				.queryCache(CFWDBRole.class, "deleteByName")
				.delete()
				.where(RoleFields.NAME.toString(), name)
				.and(RoleFields.IS_DELETABLE.toString(), true)
				.executeDelete();
					
	}
	
	
	/****************************************************************
	 * Check if the role exists by name.
	 * 
	 * @param role to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkRoleExists(Role role) {
		if(role != null) {
			return checkRoleExists(role.name());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the role exists by name.
	 * 
	 * @param rolename to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkRoleExists(String roleName) {
		
		int count = new Role()
				.queryCache(CFWDBRole.class, "checkRoleExists")
				.selectCount()
				.where(RoleFields.NAME.toString(), roleName)
				.getCount();
		
		return (count > 0);
		
	}
	
}
