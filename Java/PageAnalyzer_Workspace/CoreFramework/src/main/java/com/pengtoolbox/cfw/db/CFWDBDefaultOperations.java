package com.pengtoolbox.cfw.db;

import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public abstract class CFWDBDefaultOperations<O extends CFWObject> {
	
	public static Logger logger = CFWLog.getLogger(CFWDBDefaultOperations.class.getName());
	
	public static HashMap<String, PrecheckHandler> precheckMap = new HashMap<String, PrecheckHandler>();
	
	private static <O extends CFWObject> void addPrecheckHandler(O object, String method, PrecheckHandler handler) {
		precheckMap.put(object.getClass().getName()+"."+method, handler);
	}
	
	private static <O extends CFWObject> PrecheckHandler getPrecheckHandler(O object, String method) {
		return precheckMap.get(object.getClass().getName()+"."+method);
	}
	
	public static <O extends CFWObject> void precheckForCreate(CFWObject object, PrecheckHandler handler) {
		addPrecheckHandler(object, "create", handler);
	}
	
	public static <O extends CFWObject> void precheckForUpdate(CFWObject object, PrecheckHandler handler) {
		addPrecheckHandler(object, "update", handler);
	}
	
	/********************************************************************************************
	 * Creates multiple objects in the DB.
	 * @param Roles with the values that should be inserted. ID will be set by the Database.
	 * @return nothing
	 * 
	 ********************************************************************************************/
	public static <O extends CFWObject> void create(O... cfwObjects) {
		
		for(O object : cfwObjects) {
			create(object);
		}
	}
	
	/********************************************************************************************
	 * Creates a new role in the DB.
	 * @param CFWSpace with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static <O extends CFWObject> boolean create(O object) {
		
		if(object == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The object cannot be null", new Throwable());
			return false;
		}
		
		PrecheckHandler precheck = getPrecheckHandler(object, "create");
		if(precheck != null && !precheck.doCheck(object)) {
			return false;
		}
		
		return object
			.queryCache(object.getClass(), "create")
			.insert();

	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param object
	 * @return true or false
	 ****************************************************************/
	public static <O extends CFWObject> boolean update(O object) {
		
		if(object == null) {
			new CFWLog(logger)
				.method("update")
				.warn("The role that should be updated cannot be null");
			return false;
		}
		
		PrecheckHandler precheck = getPrecheckHandler(object, "update");
		if(precheck != null && !precheck.doCheck(object)) {
			return false;
		}
				
		return object
				.queryCache(CFWDBDefaultOperations.class, "update")
				.update();
		
	}
	
//	/***************************************************************
//	 * Select a role by it's name.
//	 * @param id of the role
//	 * @return Returns a role or null if not found or in case of exception.
//	 ****************************************************************/
//	public static Role selectByName(String name) {
//		
//		return (Role)new Role()
//				.queryCache(CFWDBDefaultOperations.class, "selectByName")
//				.select()
//				.where(RoleFields.NAME.toString(), name)
//				.getFirstObject();
//
//	}
//	
//	/***************************************************************
//	 * Select a role by it's ID.
//	 * @param id of the role
//	 * @return Returns a role or null if not found or in case of exception.
//	 ****************************************************************/
//	public static Role selectByID(int id ) {
//
//		return (Role)new Role()
//				.queryCache(CFWDBDefaultOperations.class, "selectByID")
//				.select()
//				.where(RoleFields.PK_ID.toString(), id)
//				.getFirstObject();
//		
//	}
//	
//	/***************************************************************
//	 * Select a role by it's ID and return it as JSON string.
//	 * @param id of the role
//	 * @return Returns a role or null if not found or in case of exception.
//	 ****************************************************************/
//	public static String getUserRolesAsJSON(String id) {
//		
//		return new Role()
//				.queryCache(CFWDBDefaultOperations.class, "getUserRolesAsJSON")
//				.select()
//				.where(RoleFields.PK_ID.toString(), Integer.parseInt(id))
//				.and(RoleFields.CATEGORY.toString(), "user")
//				.getAsJSON();
//		
//	}
//	
//	/***************************************************************
//	 * Return a list of all user roles
//	 * 
//	 * @return Returns a resultSet with all roles or null.
//	 ****************************************************************/
//	public static ResultSet getUserRoleList() {
//		
//		return new Role()
//				.queryCache(CFWDBDefaultOperations.class, "getUserRoleList")
//				.select()
//				.where(RoleFields.CATEGORY.toString(), "user")
//				.orderby(RoleFields.NAME.toString())
//				.getResultSet();
//		
//	}
//	
//	/***************************************************************
//	 * Return a list of all user roles as json string.
//	 * 
//	 * @return Returns a result set with all users or null.
//	 ****************************************************************/
//	public static String getUserRoleListAsJSON() {
//		return new Role()
//				.queryCache(CFWDBDefaultOperations.class, "getUserRoleListAsJSON")
//				.select()
//				.where(RoleFields.CATEGORY.toString(), "user")
//				.orderby(RoleFields.NAME.toString())
//				.getAsJSON();
//	}
//	
//	
//	/***************************************************************
//	 * Retrieve the permissions for the specified role.
//	 * @param role
//	 * @return Hashmap with roles(key=role name, value=role object), or null on exception
//	 ****************************************************************/
//	public static HashMap<String, Permission> selectPermissionsForRole(Role role) {
//		return CFW.DB.RolePermissionMap.selectPermissionsForRole(role);
//	}
//	
//	/****************************************************************
//	 * Deletes the role by id.
//	 * @param id of the user
//	 * @return true if successful, false otherwise.
//	 ****************************************************************/
//	public static boolean deleteByID(int id) {
//		
//		Role role = selectByID(id);
//		if(role != null && role.isDeletable() == false) {
//			new CFWLog(logger)
//			.method("deleteByID")
//			.severe("The role '"+role.name()+"' cannot be deleted as it is marked as not deletable.");
//			return false;
//		}
//		
//		return new Role()
//				.queryCache(CFWDBDefaultOperations.class, "deleteByID")
//				.delete()
//				.where(RoleFields.PK_ID.toString(), id)
//				.and(RoleFields.IS_DELETABLE.toString(), true)
//				.executeDelete();
//					
//	}
//	
//	/****************************************************************
//	 * Deletes multiple users by id.
//	 * @param ids of the users separated by comma
//	 * @return true if successful, false otherwise.
//	 ****************************************************************/
//	public static boolean deleteMultipleByID(String resultIDs) {
//		
//		//----------------------------------
//		// Check input format
//		if(resultIDs == null ^ !resultIDs.matches("(\\d,?)+")) {
//			new CFWLog(logger)
//			.method("deleteMultipleByID")
//			.severe("The userID's '"+resultIDs+"' are not a comma separated list of strings.");
//			return false;
//		}
//
//		return new Role()
//				.queryCache(CFWDBDefaultOperations.class, "deleteMultipleByID")
//				.delete()
//				.whereIn(RoleFields.PK_ID.toString(), resultIDs)
//				.and(RoleFields.IS_DELETABLE.toString(), true)
//				.executeDelete();
//					
//	}
//	
//	/****************************************************************
//	 * Deletes the role by id.
//	 * @param id of the user
//	 * @return true if successful, false otherwise.
//	 ****************************************************************/
//	public static boolean deleteByName(String name) {
//		
//		Role role = selectByName(name);
//		if(role != null && role.isDeletable() == false) {
//			new CFWLog(logger)
//			.method("deleteByName")
//			.severe("The role '"+role.name()+"' cannot be deleted as it is marked as not deletable.");
//			return false;
//		}
//		
//		return new Role()
//				.queryCache(CFWDBDefaultOperations.class, "deleteByName")
//				.delete()
//				.where(RoleFields.NAME.toString(), name)
//				.and(RoleFields.IS_DELETABLE.toString(), true)
//				.executeDelete();
//					
//	}
//	
//	
//	/****************************************************************
//	 * Check if the role exists by name.
//	 * 
//	 * @param role to check
//	 * @return true if exists, false otherwise or in case of exception.
//	 ****************************************************************/
//	public static boolean checkRoleExists(Role role) {
//		if(role != null) {
//			return checkRoleExists(role.name());
//		}
//		return false;
//	}
//	
//	/****************************************************************
//	 * Check if the role exists by name.
//	 * 
//	 * @param rolename to check
//	 * @return true if exists, false otherwise or in case of exception.
//	 ****************************************************************/
//	public static boolean checkRoleExists(String roleName) {
//		
//		int count = new Role()
//				.queryCache(CFWDBDefaultOperations.class, "checkRoleExists")
//				.selectCount()
//				.where(RoleFields.NAME.toString(), roleName)
//				.getCount();
//		
//		return (count > 0);
//		
//	}
	
}
