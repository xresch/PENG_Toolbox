package com.pengtoolbox.cfw.login;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.User;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class DBLoginProvider implements LoginProvider {
	
	@Override
	public User checkCredentials(String username, String password) {

		User user = CFW.DB.Users.selectByUsernameOrMail(username);
		
		if(user != null && user.passwordValidation(password)) {
			return user;
		}
		
		return null;
	}
	
}
