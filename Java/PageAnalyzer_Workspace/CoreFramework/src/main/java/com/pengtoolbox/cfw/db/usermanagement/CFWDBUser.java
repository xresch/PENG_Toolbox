package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.Group.GroupFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBUser {

	public static String TABLE_NAME = "CFW_USER";
	
	public static Logger logger = CFWLog.getLogger(CFWDBUser.class.getName());
	
	public enum UserDBFields{
		PK_ID, 
		USERNAME,
		EMAIL, 
		FIRSTNAME, 
		LASTNAME, 
		PASSWORD_HASH,
		PASSWORD_SALT,
		AVATAR_IMAGE, 
		DATE_CREATED, 
		STATUS, 
		IS_DELETABLE, 
		IS_RENAMABLE, 
		IS_FOREIGN
	}

	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + UserDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + UserDBFields.USERNAME + " VARCHAR(255) UNIQUE,"
							  + UserDBFields.EMAIL + " VARCHAR(255) UNIQUE,"
							  + UserDBFields.FIRSTNAME + " VARCHAR(255),"
							  + UserDBFields.LASTNAME + " VARCHAR(255),"
							  + UserDBFields.PASSWORD_HASH + " VARCHAR(127),"
							  + UserDBFields.PASSWORD_SALT + " VARCHAR(31),"
							  + UserDBFields.AVATAR_IMAGE + " IMAGE(1M),"
							  + UserDBFields.DATE_CREATED + " TIMESTAMP,"
							  + UserDBFields.STATUS + " VARCHAR(31),"
							  + UserDBFields.IS_DELETABLE + " BOOLEAN,"
							  + UserDBFields.IS_RENAMABLE + " BOOLEAN,"
							  + UserDBFields.IS_FOREIGN + " BOOLEAN"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
				
	}
	
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
		
		String insertUserSQL = "INSERT INTO "+TABLE_NAME+"("
				  + UserDBFields.USERNAME +", "
				  + UserDBFields.EMAIL +", "
				  + UserDBFields.FIRSTNAME +", "
				  + UserDBFields.LASTNAME +", "
				  + UserDBFields.PASSWORD_HASH +", "
				  + UserDBFields.PASSWORD_SALT +", "
				  + UserDBFields.AVATAR_IMAGE +", "
				  + UserDBFields.DATE_CREATED +", "
				  + UserDBFields.STATUS +", "
				  + UserDBFields.IS_DELETABLE +", "
				  + UserDBFields.IS_RENAMABLE + ", "
				  + UserDBFields.IS_FOREIGN +", "
				  + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
		
		return CFWDB.preparedExecute(insertUserSQL, 
				user.username(),
				user.email(),
				user.firstname(),
				user.lastname(),
				user.passwordHash(),
				user.passwordSalt(),
				user.avatarImage(),
				user.dateCreated(),
				user.status(),
				user.isDeletable(),
				user.isRenamable(),
				user.isForeign()
				);
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
		
		String selectByUsernameOrMail = 
				"SELECT "
				  + UserDBFields.PK_ID +", "
				  + UserDBFields.USERNAME +", "
				  + UserDBFields.EMAIL +", "
				  + UserDBFields.FIRSTNAME +", "
				  + UserDBFields.LASTNAME +", "
				  + UserDBFields.PASSWORD_HASH +", "
				  + UserDBFields.PASSWORD_SALT +", "
				  + UserDBFields.AVATAR_IMAGE +", "
				  + UserDBFields.DATE_CREATED +", "
				  + UserDBFields.STATUS +", "
				  + UserDBFields.IS_DELETABLE +", "
				  + UserDBFields.IS_RENAMABLE + ", "
				  + UserDBFields.IS_FOREIGN 
				+" FROM "+TABLE_NAME
				+" WHERE "
					+ "LOWER(" + UserDBFields.USERNAME+") = LOWER(?) OR "
					+ "LOWER(" +UserDBFields.EMAIL	+ ") = LOWER(?)";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByUsernameOrMail, usernameOrMail, usernameOrMail);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new User(result);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectByUsernameOrMail")
			.severe("Error reading user from database.", e);;
			
		}finally {
			CFWDB.close(result);
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Select a user by it's ID.
	 * 
	 * @param id of the User
	 * @return Returns a user or null if not found or in case of exception.
	 ****************************************************************/
	public static User selectByID(int id) {
		
		String selectByID = 
				"SELECT "
				  + UserDBFields.PK_ID +", "
				  + UserDBFields.USERNAME +", "
				  + UserDBFields.EMAIL +", "
				  + UserDBFields.FIRSTNAME +", "
				  + UserDBFields.LASTNAME +", "
				  + UserDBFields.PASSWORD_HASH +", "
				  + UserDBFields.PASSWORD_SALT +", "
				  + UserDBFields.AVATAR_IMAGE +", "
				  + UserDBFields.DATE_CREATED +", "
				  + UserDBFields.STATUS +", "
				  + UserDBFields.IS_DELETABLE +", "
				  + UserDBFields.IS_RENAMABLE + ", "
				  + UserDBFields.IS_FOREIGN 
				+" FROM "+TABLE_NAME
				+" WHERE "
					+ UserDBFields.PK_ID	+ " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByID, id);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new User(result);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectByID")
			.severe("Error reading user from database.", e);;
			
		}finally {
			CFWDB.close(result);
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Select a user by it's ID and return it as a JSON string.
	 * 
	 * @param id of the User
	 * @return Returns a user or null if not found or in case of exception.
	 ****************************************************************/
	public static String getUserAsJSON(String userID) {
				
		String selectByID = 
				"SELECT "
				  + UserDBFields.PK_ID +", "
				  + UserDBFields.USERNAME +", "
				  + UserDBFields.EMAIL +", "
				  + UserDBFields.FIRSTNAME +", "
				  + UserDBFields.LASTNAME +", "
				  + UserDBFields.AVATAR_IMAGE +", "
				  + UserDBFields.DATE_CREATED +", "
				  + UserDBFields.STATUS +", "
				  + UserDBFields.IS_DELETABLE +", "
				  + UserDBFields.IS_RENAMABLE + ", "
				  + UserDBFields.IS_FOREIGN 
				+" FROM "+TABLE_NAME
				+" WHERE "
					+ UserDBFields.PK_ID	+ " = ?";
		
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByID, userID);
		String json = CFWDB.resultSetToJSON(result);
		CFWDB.close(result);	
		return json;
		
	}
	
	
	/***************************************************************
	 * Return a list of all users as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getUserListAsJSON() {
		String selectAllUsers = 
				"SELECT "
				  + UserDBFields.PK_ID +", "
				  + UserDBFields.USERNAME +", "
				  + UserDBFields.EMAIL +", "
				  + UserDBFields.FIRSTNAME +", "
				  + UserDBFields.LASTNAME +", "
				  + UserDBFields.DATE_CREATED +", "
				  + UserDBFields.STATUS +", "
				  + UserDBFields.IS_DELETABLE +", "
				  + UserDBFields.IS_RENAMABLE + ", "
				  + UserDBFields.IS_FOREIGN 
				+" FROM "+TABLE_NAME
				+" ORDER BY LOWER("+UserDBFields.USERNAME +") ASC";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectAllUsers);
		String json = CFWDB.resultSetToJSON(result);
		CFWDB.close(result);	
		return json;
	}
	
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
		
		String updateByID = 
				"UPDATE "+TABLE_NAME
				+" SET ("
				  + UserDBFields.EMAIL +", "
				  + UserDBFields.FIRSTNAME +", "
				  + UserDBFields.LASTNAME +", "
				  + UserDBFields.PASSWORD_HASH +", "
				  + UserDBFields.PASSWORD_SALT +", "
				  + UserDBFields.AVATAR_IMAGE +", "
				  + UserDBFields.STATUS +", "
				  + UserDBFields.IS_DELETABLE +", "
				  + UserDBFields.IS_RENAMABLE + ", "
				  + UserDBFields.IS_FOREIGN 
				  + ") = (?,?,?,?,?,?,?,?,?,?) "
				+" WHERE "
					+ UserDBFields.PK_ID+" = ?";
		
		boolean resultUpdate = CFWDB.preparedExecute(updateByID, 
				user.email(),
				user.firstname(),
				user.lastname(),
				user.passwordHash(),
				user.passwordSalt(),
				user.avatarImage(),
				user.status(),
				user.isDeletable(),
				user.isRenamable(),
				user.isForeign(),
				user.id());
		
		boolean resultRename = true;
		
		if(user.hasUsernameChanged()) {
			
			if(!user.isRenamable()) {
				new CFWLog(logger)
				.method("update")
				.severe("The user '"+user.username()+"' cannot be renamed as it is marked as not renamable.");
				return false;
			}

			String updateNameByID = 
				"UPDATE "+TABLE_NAME
				+" SET ("
				  + UserDBFields.USERNAME +""
				  + ") = (?) "
				+" WHERE "
					+ UserDBFields.PK_ID+" = ? AND "
					+ UserDBFields.IS_RENAMABLE+"=TRUE";
		
			resultRename = CFWDB.preparedExecute(updateNameByID, 
				user.username(),
				user.id());
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
		
		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ UserDBFields.PK_ID+" = ? "
					+ "AND "
					+ UserDBFields.IS_DELETABLE+" = TRUE ";
		
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
					+ UserDBFields.PK_ID+" IN(?) "
					+ "AND "
					+ UserDBFields.IS_DELETABLE+" = TRUE ";
		
		return CFWDB.preparedExecute(deleteByID, resultIDs);
			
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
		String checkUserExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE LOWER("+UserDBFields.USERNAME+") = LOWER(?)";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkUserExistsSQL, username);
		
		try {
			if(result != null && result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("checkUsernameExists")
			.severe("Error while checking if the user exists.", e);
			
			return false;
		}finally {
			CFWDB.close(result);
		}
		
		return false; 
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
		String checkEmailExists = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE LOWER("+UserDBFields.EMAIL+") = LOWER(?)";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkEmailExists, email);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("checkEmailInUse")
			.severe("Error while checking the if the email is in use.", e);
			
			return false;
		}finally {
			CFWDB.close(result);
		}
		
		return false;
	}
	
}
