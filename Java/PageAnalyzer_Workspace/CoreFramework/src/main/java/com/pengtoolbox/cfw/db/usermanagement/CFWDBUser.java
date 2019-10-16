package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Permission.PermissionFields;
import com.pengtoolbox.cfw.db.usermanagement.User.UserFields;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDBUser {

	public static Logger logger = CFWLog.getLogger(CFWDBUser.class.getName());
	
	
	/********************************************************************************************
	 * Creates multiple users in the DB.
	 * @param Users with the values that should be inserted. ID will be set by the Database.
	 * @return nothing
	 * 
	 ********************************************************************************************/
	public static void create(User... users) {
		
		for(User user : users) {
			create(user);
		}
	}
	
	/********************************************************************************************
	 * Creates a new user in the DB.
	 * @param user with the values that should be inserted. ID will be set by the Database.
	 * @return return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean create(User user) {
		
		if( user == null) {
			new CFWLog(logger)
				.method("create")
				.severe("The user cannot be null");
			return false;
		}
		
		if( user.username() == null || user.username().isEmpty() ) {
			new CFWLog(logger)
				.method("create")
				.severe("Please provide at least one character for the username.");
			return false;
		}
		
		if( checkUsernameExists(user.username())) {
			new CFWLog(logger)
				.method("create")
				.warn("The user '"+user.username()+"' cannot be created as a user with this name already exists.");
			return false;
		}
		
		if( user.email() != null
		&& !user.email().isEmpty()
		&& checkEmailExists(user.email())) {
			
			new CFWLog(logger)
				.method("create")
				.warn("The user '"+user.username()+"' cannot be created as the email '"+user.email()+"' is already used by another account.");
			return false;
		}
		
		return user
				.queryCache(CFWDBUser.class, "create")
				.insert();
	}
	
	/***************************************************************
	 * Select a user by it's username or email address.
	 * This method is useful for login forms.
	 * 
	 * @param username or eMail address
	 * @return Returns a user or null if not found or in case of exception.
	 ****************************************************************/
	public static User selectByUsernameOrMail(String usernameOrMail) {
		
		if( usernameOrMail == null) {
			new CFWLog(logger)
				.method("selectByUsernameOrMail")
				.severe("The user or eMail cannot be null.");
			return null;
		}
		
		return (User)new User()
				.queryCache(CFWDBUser.class, "selectByUsernameOrMail")
				.select()
				.where(UserFields.USERNAME.toString(), usernameOrMail, false)
				.or(UserFields.EMAIL.toString(), usernameOrMail, false)
				.getFirstObject();
			
	}
	
	/***************************************************************
	 * Select a user by it's ID.
	 * 
	 * @param id of the User
	 * @return Returns a user or null if not found or in case of exception.
	 ****************************************************************/
	public static User selectByID(int id) {
		
		
		return (User)new User()
				.queryCache(CFWDBUser.class, "selectByID")
				.select()
				.where(UserFields.PK_ID.toString(), id)
				.getFirstObject();
		
	}
	
	/***************************************************************
	 * Select a user by it's ID and return it as a JSON string.
	 * 
	 * @param id of the User
	 * @return Returns a user or null if not found or in case of exception.
	 ****************************************************************/
	public static String getUserAsJSON(String userID) {
		
		return new User()
				.queryCache(CFWDBUser.class, "getUserAsJSON")
				.selectWithout(UserFields.PASSWORD_HASH.toString(), 
						       UserFields.PASSWORD_SALT.toString())
				.where(UserFields.PK_ID.toString(), userID)
				.getAsJSON();
		
	}
	
	/***************************************************************
	 * Return a list of all users.
	 * Don't forget to close the db connection using CFWDB.close().
	 * 
	 * @return Returns a resultSet with all groups or null.
	 ****************************************************************/
	public static ResultSet getUserList() {
		
		return new User()
				.queryCache(CFWDBUser.class, "getUserList")
				.select()
				.orderby(UserFields.USERNAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all users.
	 * Don't forget to close the db connection using CFWDB.close().
	 * 
	 * @return Returns a resultSet with all groups or null.
	 ****************************************************************/
	public static String getUserListAsJSON() {
		
		return new User()
				.queryCache(CFWDBUser.class, "getUserList")
				.select()
				.orderby(UserFields.USERNAME.toString())
				.getAsJSON();
		
	}
	
	
//	/***************************************************************
//	 * Return a list of all users as json string.
//	 * 
//	 * @return Returns a result set with all users or null.
//	 ****************************************************************/
//	public static String getUserListAsJSON() {
//		String selectAllUsers = 
//				"SELECT "
//				  + UserFields.PK_ID +", "
//				  + UserFields.USERNAME +", "
//				  + UserFields.EMAIL +", "
//				  + UserFields.FIRSTNAME +", "
//				  + UserFields.LASTNAME +", "
//				  + UserFields.DATE_CREATED +", "
//				  + UserFields.STATUS +", "
//				  + UserFields.IS_DELETABLE +", "
//				  + UserFields.IS_RENAMABLE + ", "
//				  + UserFields.IS_FOREIGN 
//				+" FROM "+User.TABLE_NAME
//				+" ORDER BY LOWER("+UserFields.USERNAME +") ASC";
//		
//		ResultSet result = CFWDB.preparedExecuteQuery(selectAllUsers);
//		String json = CFWDB.resultSetToJSON(result);
//		CFWDB.close(result);	
//		return json;
//	}
	
	/***************************************************************
	 * Retrieve the groups for the specified user.
	 * @param group
	 * @return Hashmap with groups(key=group name, value=group object), or null on exception
	 ****************************************************************/
	public static HashMap<String, Group> selectGroupsForUser(int userID) {
		
		return CFW.DB.UserGroupMap.selectGroupsForUser(userID);
	
	}
	/***************************************************************
	 * Retrieve the groups for the specified user.
	 * @param group
	 * @return Hashmap with groups(key=group name, value=group object), or null on exception
	 ****************************************************************/
	public static HashMap<String, Group> selectGroupsForUser(User user) {
		
		return CFW.DB.UserGroupMap.selectGroupsForUser(user);
	
	}
	
	/***************************************************************
	 * Retrieve the permissions for the specified user.
	 * @param group
	 * @return Hashmap with permissions(key=group name), or null on exception
	 ****************************************************************/
	public static HashMap<String, Permission> selectPermissionsForUser(User user) {
		return CFW.DB.GroupPermissionMap.selectPermissionsForUser(user);
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param group
	 * @return true or false
	 ****************************************************************/
	public static boolean update(User user) {
		
		if(user == null) {
			new CFWLog(logger)
			.method("update")
			.severe("The user cannot be null.");
			return false;
		}
		
		boolean resultUpdate = 
			   user.queryCache(CFWDBUser.class, "update")
					.updateWithout(UserFields.USERNAME.toString());

		
		boolean resultRename = true;
		
		if(user.hasUsernameChanged()) {
			
			if(!user.isRenamable()) {
				new CFWLog(logger)
				.method("update")
				.severe("The user '"+user.username()+"' cannot be renamed as it is marked as not renamable.");
				return false;
			}
			
			resultRename = 
					   user.queryCache(CFWDBUser.class, "updateNameOnly")
							.update(UserFields.USERNAME.toString());
		}
		
		return resultUpdate && resultRename;
		
	}
	
	/****************************************************************
	 * Deletes the User by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		User user = selectByID(id);
		
		if(user != null && user.isDeletable() == false) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The user '"+user.username()+"' cannot be deleted as it is marked as not deletable.");
			return false;
		}
		
		return new User()
				.queryCache(CFWDBUser.class, "deleteByID")
				.delete()
				.where(UserFields.PK_ID.toString(), id)
				.and(PermissionFields.IS_DELETABLE.toString(), true)
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
		
		return new User()
				.queryCache(CFWDBUser.class, "deleteMultipleByID")
				.delete()
				.whereIn(UserFields.PK_ID.toString(), resultIDs)
				.and(UserFields.IS_DELETABLE.toString(), true)
				.executeDelete();
			
	}
	
	
	
	
	/****************************************************************
	 * Check if the user exists by it's username.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkUsernameExists(User user) {
		if(user != null) {
			return checkUsernameExists(user.username());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the user exists by it's username.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkUsernameExists(String username) {
		
		int count = new User()
				.queryCache(CFWDBUser.class, "checkUsernameExists")
				.selectCount()
				.where(UserFields.USERNAME.toString(), username, false)
				.getCount();
		
		return (count > 0);
		
	}
	
	/****************************************************************
	 * Check if the email of the user is already in use.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkEmailExists(User user) {
		if(user != null) {
			return checkEmailExists(user.email());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the email of the user is already in use.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkEmailExists(String email) {
		
		int count = new User()
				.queryCache(CFWDBUser.class, "checkEmailExists")
				.selectCount()
				.where(UserFields.EMAIL.toString(), email, false)
				.getCount();
		
		return (count > 0);

	}
	
}
