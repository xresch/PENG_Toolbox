package com.pengtoolbox.cfw.db.usermanagement;

import com.pengtoolbox.cfw.db.CFWDB;

public class CFWDBUser {

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
	}

	
	public static void initialize() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS cfw_users("
							  + UserDBFields.PK_ID + "user_id INT PRIMARY KEY AUTO_INCREMENT, "
							  + UserDBFields.EMAIL + "VARCHAR(255),"
							  + UserDBFields.FIRSTNAME + "VARCHAR(255),"
							  + UserDBFields.LASTNAME + "VARCHAR(255),"
							  + UserDBFields.PASSWORD_HASH + "VARCHAR(127),"
							  + UserDBFields.PASSWORD_SALT + "VARCHAR(31),"
							  + UserDBFields.AVATAR_IMAGE + "CLOB,"
							  + UserDBFields.DATE_CREATED + "DATE,"
							  + UserDBFields.IS_BLOCKED + "BOOLEAN,"
							  + UserDBFields.IS_DELETABLE + "BOOLEAN);";
		
		CFWDB.preparedExecute(null, createTableSQL);
	
	}
}
