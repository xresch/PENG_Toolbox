package com.pengtoolbox.cfw.login;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.db.usermanagement.User;

public class LoginFacade {

	private static LoginFacade INSTANCE;
	
	private static LoginProvider provider;
	
	private LoginFacade() {
		
		switch(CFWConfig.AUTHENTICATION_METHOD.trim().toUpperCase()) {
		
			case "CSV": 	provider = new CSVLoginProvider();
				 			break;
				 			
			case "LDAP": 	provider = new LDAPLoginProvider();
 							break;
 			
			case "DB": 		provider = new DBLoginProvider();
				break;
 							
			default:		throw new RuntimeException("Unknown authentication method'"+CFWConfig.AUTHENTICATION_METHOD+"', please review the config file.");
		}
	}
	
	public static LoginFacade getInstance() {
		
		if(INSTANCE == null) {
			INSTANCE = new LoginFacade();
		}
		
		return INSTANCE;
	}
	
	/******************************************************************************
	 * Check if the username password exists and has to return a user object which
	 * can be found in the Database.
	 * In case of foreign login providers like LDAP, users that do not exist in the
	 * DB have to be created by this method.
	 * 
	 * @param username
	 * @param password
	 * @return user object fetched from the database with CFW.DB.Users.select*(),
	 *         or null if the login failed.
	 ******************************************************************************/
	public User checkCredentials(String username, String password) {
		return provider.checkCredentials(username, password);
	}
	
	
	
	
}
