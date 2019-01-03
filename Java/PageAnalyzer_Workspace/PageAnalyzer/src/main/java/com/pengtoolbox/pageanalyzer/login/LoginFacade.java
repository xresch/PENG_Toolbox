package com.pengtoolbox.pageanalyzer.login;

import com.pengtoolbox.pageanalyzer._main.PA;

public class LoginFacade {

	private static LoginFacade INSTANCE;
	
	private static LoginProvider provider;
	
	private static final String AUTHENTICATION_METHOD = PA.config("authentication_method");
	
	private LoginFacade() {
		
		switch(AUTHENTICATION_METHOD.trim().toUpperCase()) {
		
			case "CSV": 	provider = new CSVLoginProvider();
				 			break;
				 			
			case "LDAP": 	provider = new LDAPLoginProvider();
 							break;
 							
			default:		throw new RuntimeException("Unknown authentication method'"+AUTHENTICATION_METHOD+"', please review the config file.");
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
