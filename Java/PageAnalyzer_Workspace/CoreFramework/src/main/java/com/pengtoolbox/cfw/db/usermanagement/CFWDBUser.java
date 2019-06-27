package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.User.UserStatus;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBUser {

	public static String TABLE_NAME = "CFW_USERS";
	
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
		
		if(!isUsernameUsed("admin")) {
			// salt and hash for default password "admin"
			String salt = ";%IYi6:0ls,!8PQac?;o9570kn{NYSb";
			String hash = "12f42860e885448d8bcc02d08188f2e860894ae6aa786112c84e2da567b9935090720dd951be7811d68b098375ed9dbcc8fa042ddfceaa6973a83ab9231732";
			create(new User().username("admin")
								.isDeletable(false)
								.isRenamable(false)
								.passwordHash(hash)
								.passwordSalt(salt)
								.status(UserStatus.ACTIVE)
								.isForeign(false)
								);
		}
		
	}
	
	public static void create(User user) {
		
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
		
		CFWDB.preparedExecute(insertUserSQL, 
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
	 * Returns a user or null if not found or in case of exception.
	 * @param usernameOrMail
	 * @return
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
	 * Returns true or false
	 * @param usernameOrMail
	 * @return
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
	
	/***************************************************************
	 * Returns true or false.
	 * @param id of the user
	 * @return
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		String deleteByID = 
				"DELETE FROM "+TABLE_NAME
				+" WHERE "
					+ UserDBFields.PK_ID+" = ? ";
		
		return CFWDB.preparedExecute(deleteByID, id);
			
	}
	
	public static boolean checkUsernameUsed(User user) {
		return isUsernameUsed(user.username());
	}
	
	public static boolean isUsernameUsed(String username) {
		String checkUserExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+UserDBFields.USERNAME+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkUserExistsSQL, username);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				System.out.println("isUsernameUsed Count: "+1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("checkUsernameUsed")
			.severe("Exception occured.", e);
			
			return false;
		}
		
		return false; 
	}
	
	public static void checkEmailInUse(User user) {
		String checkUserExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+UserDBFields.EMAIL+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkUserExistsSQL, user.email());
	}
	
}
