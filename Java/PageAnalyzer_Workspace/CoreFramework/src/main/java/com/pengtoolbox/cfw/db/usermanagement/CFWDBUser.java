package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBUser {

	public static String TABLE_NAME = "CFW_USER";
	
	public static Logger logger = CFWLog.getLogger(CFWDBUser.class.getName());
	
	enum UserDBFields{
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
	 * Creates a new user in the DB.
	 * @param user with the values that should be inserted. ID will be set by the Database.
	 * @return return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean create(User user) {
		
		if( checkUsernameExists(user.username())) {
			new CFWLog(logger)
				.method("create")
				.warn("The user '"+user.username()+"' cannot be created as a user with this name already exists.");
			return false;
		}
		
		if( checkEmailExists(user.email())) {
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
					+ UserDBFields.USERNAME+" = ? OR "
					+ UserDBFields.EMAIL	+ " = ?";
		
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
					+ UserDBFields.PK_ID	+ " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByUsernameOrMail, id);
		
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
			
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param group
	 * @return true or false
	 ****************************************************************/
	public static boolean update(User user) {
		
		String updateByID = 
				"UPDATE "+TABLE_NAME
				+" SET ("
				  + UserDBFields.USERNAME +", "
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
				  + ") = (?,?,?,?,?,?,?,?,?,?,?) "
				+" WHERE "
					+ UserDBFields.PK_ID+" = ?";
		
		boolean result = CFWDB.preparedExecute(updateByID, 
				user.username(),
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
		
		
		return result;
		
	}
	
	/****************************************************************
	 * Deletes the User by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ UserDBFields.PK_ID+" = ? ";
		
		return CFWDB.preparedExecute(deleteByID, id);
			
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
		String checkUserExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+UserDBFields.USERNAME+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkUserExistsSQL, username);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("checkUsernameUsed")
			.severe("The user '"+username+"' already exists. Please choose another name.", e);
			
			return false;
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
		String checkEmailExists = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+UserDBFields.EMAIL+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkEmailExists, email);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("checkEmailInUse")
			.severe("The email '"+email+"' is already used by another account.", e);
			
			return false;
		}
		
		return false;
	}
	
}
