package com.pengtoolbox.cfw.db;

import java.util.ArrayList;
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
	
	
	/********************************************************************************************
	 * Creates multiple items in the DB.
	 * @param Roles with the values that should be inserted. ID will be set by the Database.
	 * @return true if all created successful
	 * 
	 ********************************************************************************************/
	public static <O extends CFWObject> boolean create(PrecheckHandler precheck, O... cfwObjects) {
		
		boolean result = true;
		for(O object : cfwObjects) {
			result &= create(precheck, object);
		}
		
		return result;
	}
	
	/********************************************************************************************
	 * Creates a new item in the DB.
	 * @param Object with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static <O extends CFWObject> boolean create(PrecheckHandler precheck, O object) {
		
		if(object == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The object cannot be null", new Throwable());
			return false;
		}
		
		if(precheck != null && !precheck.doCheck(object)) {
			return false;
		}
		
		return object
			.queryCache(object.getClass(), "CFWDBDefaultOperations.create")
			.insert();

	}
	
	/********************************************************************************************
	 * Creates a new item in the DB.
	 * @param Object with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static <O extends CFWObject> Integer createGetPrimaryKey(PrecheckHandler precheck, O object) {
		
		if(object == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The object cannot be null", new Throwable());
			return null;
		}
		
		if(precheck != null && !precheck.doCheck(object)) {
			return null;
		}
		
		return object
			.queryCache(object.getClass(), "CFWDBDefaultOperations.insertGetPrimaryKey")
			.insertGetPrimaryKey();

	}
	
	/********************************************************************************************
	 * Updates multiple items in the DB.
	 * @param Objects with the values that should be inserted. ID will be set by the Database.
	 * @return true if all updated successful
	 * 
	 ********************************************************************************************/
	public static <O extends CFWObject> boolean update(PrecheckHandler precheck, O... cfwObjects) {
		
		boolean result = true;
		for(O object : cfwObjects) {
			result &= update(precheck, object);
		}
		
		return result;
	}
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param object
	 * @return true or false
	 ****************************************************************/
	public static <O extends CFWObject> boolean update(PrecheckHandler precheck, O object) {
		
		if(object == null) {
			new CFWLog(logger)
				.method("update")
				.warn("The role that should be updated cannot be null");
			return false;
		}
		
		if(precheck != null && !precheck.doCheck(object)) {
			return false;
		}
				
		return object
				.queryCache(object.getClass(), "CFWDBDefaultOperations.update")
				.update();
		
	}
	
	/***************************************************************
	 * Deletes the objects selected where the �recheck returns true.
	 * @param object
	 * @return true or false
	 ****************************************************************/
	public static <O extends CFWObject> boolean deleteBy(PrecheckHandler precheck, Class<? extends CFWObject> cfwObjectClass, String column, Object value) {
		
		ArrayList<CFWObject> objectArray = CFWDBDefaultOperations.selectBy(cfwObjectClass, column, value);
		
		for(CFWObject object : objectArray) {
			if(precheck != null && !precheck.doCheck(object)) {
				return false;
			}
			
			return object
					.queryCache(cfwObjectClass, "CFWDBDefaultOperations.deleteBy"+column)
					.delete()
					.where(column, value)
					.executeDelete();
		}
		
		return false;
	}
	
	/***************************************************************
	 * Deletes the first object selected.
	 * @param object
	 * @return true or false
	 ****************************************************************/
	public static <O extends CFWObject> boolean deleteFirstBy(PrecheckHandler precheck, Class<? extends CFWObject> cfwObjectClass, String column, Object value) {
		
		CFWObject object = CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, column, value);
		
		if(precheck != null && !precheck.doCheck(object)) {
			return false;
		}
		
		if(object != null) {
			return object
				.queryCache(cfwObjectClass, "CFWDBDefaultOperations.deleteFirstBy"+column)
				.deleteTop(1)
				.where(column, value)
				.executeDelete();
		}
		
		return false;
		
	}
	
	/****************************************************************
	 * Deletes multiple items by id.
	 * @param ids separated by comma
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static <O extends CFWObject> boolean deleteMultipleByID(Class<? extends CFWObject> cfwObjectClass, String commaSeparatedIDs) {
		
		//----------------------------------
		// Check input format
		if(commaSeparatedIDs == null ^ !commaSeparatedIDs.matches("(\\d,?)+")) {
			new CFWLog(logger)
			.method("deleteMultipleByID")
			.severe("The input '"+commaSeparatedIDs+"' are not a comma separated list of IDs.");
			return false;
		}

		try {
			CFWObject instance = cfwObjectClass.newInstance();
			return instance
					.queryCache(CFWDBDefaultOperations.class, "CFWDBDefaultOperations.deleteMultipleByID")
					.delete()
					.whereIn(instance.getPrimaryField().getName(), commaSeparatedIDs)
					.executeDelete();
			
		} catch (Exception e) {
			new CFWLog(logger)
				.method("deleteMultipleByID")
				.warn("Error while instanciating object.", e);
			return false;
		}
		
	}
	
	/***************************************************************
	 * Select a role by it's name.
	 * @param id of the role
	 * @return Returns a role or null if not found or in case of exception.
	 ****************************************************************/
	@SuppressWarnings("unchecked")
	public static <O extends CFWObject> O selectFirstBy(Class<? extends CFWObject> cfwObjectClass, String column, Object value ) {
		
		try {
			return (O)cfwObjectClass.newInstance()
					.queryCache(cfwObjectClass, "CFWDBDefaultOperations.selectFirstBy"+column)
					.select()
					.where(column, value)
					.getFirstObject();
		} catch (Exception e) {
			new CFWLog(logger)
				.method("selectFirstBy")
				.warn("Error while instanciating object.", e);
		} 
		
		return null;

	}
	
	/***************************************************************
	 * Select a role by it's name.
	 * @param id of the role
	 * @return Returns a role or null if not found or in case of exception.
	 ****************************************************************/
	@SuppressWarnings("unchecked")
	public static <O extends CFWObject> ArrayList<O> selectBy(Class<? extends CFWObject> cfwObjectClass, String column, Object value ) {
		
		try {
			return (ArrayList<O>)cfwObjectClass.newInstance()
					.queryCache(cfwObjectClass, "CFWDBDefaultOperations.selectBy"+column)
					.select()
					.where(column, value)
					.getObjectList();
			
		} catch (Exception e) {
			new CFWLog(logger)
			.method("selectBy")
			.warn("Error while instanciating object.", e);
		} 
		
		return null;

	}
	
	/***************************************************************
	 * Select a role by it's name.
	 * @param id of the role
	 * @return Returns a role or null if not found or in case of exception.
	 ****************************************************************/
	@SuppressWarnings("unchecked")
	public static <O extends CFWObject> boolean checkExistsBy(Class<? extends CFWObject> cfwObjectClass, String column, Object value ) {
		
		try {
			int count = cfwObjectClass.newInstance()
					.queryCache(cfwObjectClass, "CFWDBDefaultOperations.checkExistsBy"+column)
					.selectCount()
					.where(column, value)
					.limit(1)
					.getCount();
			
			return (count > 0);
			
		} catch (Exception e) {
			new CFWLog(logger)
				.method("checkExistsBy")
				.warn("Error while instanciating object.", e);
		} 
		
		return false;

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
