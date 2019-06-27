package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBGroups {

	public static String TABLE_NAME = "CFW_GROUPS";
	
	public static Logger logger = CFWLog.getLogger(CFWDBGroups.class.getName());
	
	enum GroupDBFields{
		PK_ID, 
		NAME,
		DESCRIPTION,
		IS_DELETABLE,
	}

	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + GroupDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + GroupDBFields.NAME + " VARCHAR(255) UNIQUE,"
							  + GroupDBFields.DESCRIPTION + " CLOB,"
							  + GroupDBFields.IS_DELETABLE + " BOOLEAN"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
		
		if(!groupExists("Superuser")) {
			// salt and hash for default password "admin"
			create(new Group().name("Superuser")
				.description("Superusers have all the privileges in the system. They are above administrators. ")
				.isDeletable(false)
			);
		}
		
	}
	
	public static void create(Group group) {
		
		String insertGroupSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				  + ") VALUES (?,?,?);";
		
		CFWDB.preparedExecute(insertGroupSQL, 
				group.name(),
				group.description(),
				group.isDeletable()
				);
	}
	
	/***************************************************************
	 * Returns a user or null if not found or in case of exception.
	 * @param usernameOrMail
	 * @return
	 ****************************************************************/
	public static Group selectByName(String name ) {
		
		String selectByName = 
				"SELECT "
				  + GroupDBFields.PK_ID +", "
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +", "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ GroupDBFields.NAME + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, name);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new Group(result);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectByName")
			.severe("Error reading group from database.", e);;
			
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Returns a user or null if not found or in case of exception.
	 * @param usernameOrMail
	 * @return
	 ****************************************************************/
	public static Group selectByID(int id ) {
		
		String selectByName = 
				"SELECT "
				  + GroupDBFields.PK_ID +", "
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +", "
				+" FROM "+TABLE_NAME
				+" WHERE "
				+ GroupDBFields.PK_ID + " = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, id);
		
		if(result == null) {
			return null;
		}
		
		try {
			if(result.next()) {
				return new Group(result);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectByID")
			.severe("Error reading group from database.", e);;
			
		}
		
		return null;
		
	}
	
	/***************************************************************
	 * Returns true or false
	 * @param usernameOrMail
	 * @return
	 ****************************************************************/
	public static boolean update(Group group) {
		
		String updateByID = 
				"UPDATE "+TABLE_NAME
				+" SET ("
				  + GroupDBFields.NAME +", "
				  + GroupDBFields.DESCRIPTION +", "
				  + GroupDBFields.IS_DELETABLE +" "
				  + ") = (?,?,?) "
				+" WHERE "
					+ GroupDBFields.PK_ID+" = ?";
		
		boolean result = CFWDB.preparedExecute(updateByID, 
				group.name(),
				group.description(),
				group.isDeletable(),
				group.id());
		
		
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
					+ GroupDBFields.PK_ID+" = ? ";
		
		return CFWDB.preparedExecute(deleteByID, id);
			
	}
	
	public static boolean groupExists(Group group) {
		return groupExists(group.name());
	}
	
	public static boolean groupExists(String groupName) {
		String checkExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+GroupDBFields.NAME+" = ?";
		ResultSet result = CFW.DB.preparedExecuteQuery(checkExistsSQL, groupName);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("groupExists")
			.severe("Exception occured while checking of group exists.", e);
			
			return false;
		}
		
		return false; 
	}
	
}
