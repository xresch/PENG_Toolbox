package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
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
		IS_DELETABLE, 
		IS_BLOCKED, 
		IS_LDAP
	}

	public static void initialize() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + UserDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + UserDBFields.USERNAME + " VARCHAR(255),"
							  + UserDBFields.EMAIL + " VARCHAR(255),"
							  + UserDBFields.FIRSTNAME + " VARCHAR(255),"
							  + UserDBFields.LASTNAME + " VARCHAR(255),"
							  + UserDBFields.PASSWORD_HASH + " VARCHAR(127),"
							  + UserDBFields.PASSWORD_SALT + " VARCHAR(31),"
							  + UserDBFields.AVATAR_IMAGE + " IMAGE(1M),"
							  + UserDBFields.DATE_CREATED + " DATE,"
							  + UserDBFields.IS_BLOCKED + " BOOLEAN,"
							  + UserDBFields.IS_DELETABLE + " BOOLEAN,"
							  + UserDBFields.IS_LDAP + " BOOLEAN"
							  + ");";
		
		System.out.println(createTableSQL);
		CFWDB.preparedExecute(createTableSQL);
	
	}
	
	public static void createUser(User user) {
		
		String insertUserSQL = "CREATE TABLE IF NOT EXISTS cfw_users("
				  + UserDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
				  + UserDBFields.EMAIL + " VARCHAR(255),"
				  + UserDBFields.FIRSTNAME + " VARCHAR(255),"
				  + UserDBFields.LASTNAME + " VARCHAR(255),"
				  + UserDBFields.PASSWORD_HASH + " VARCHAR(127),"
				  + UserDBFields.PASSWORD_SALT + " VARCHAR(31),"
				  + UserDBFields.AVATAR_IMAGE + " IMAGE(1M),"
				  + UserDBFields.DATE_CREATED + " DATE,"
				  + UserDBFields.IS_BLOCKED + " BOOLEAN,"
				  + UserDBFields.IS_DELETABLE + " BOOLEAN,"
				  + UserDBFields.IS_LDAP + " BOOLEAN"
				  + ");";
	}
	
	public static void checkUsernameUsed(User user) {
		String checkUserExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+UserDBFields.USERNAME+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkUserExistsSQL, user.getUsername());
	}
	
	public static void checkEmailInUse(User user) {
		String checkUserExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+UserDBFields.EMAIL+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkUserExistsSQL, user.getEmail());
	}
}
