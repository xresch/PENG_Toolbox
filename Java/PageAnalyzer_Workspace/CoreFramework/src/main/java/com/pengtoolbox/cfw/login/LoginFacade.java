package com.pengtoolbox.cfw.login;

import com.pengtoolbox.cfw._main.CFWConfig;

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
	
	public boolean checkCredentials(String username, String password) {
		return provider.checkCredentials(username, password);
	}
	
	
	
	
}
