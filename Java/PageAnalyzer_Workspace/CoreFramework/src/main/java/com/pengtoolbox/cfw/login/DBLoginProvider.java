package com.pengtoolbox.cfw.login;

import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;

public class DBLoginProvider implements LoginProvider {
	
	private static Logger logger = CFWLog.getLogger(LoginServlet.class.getName());
	
	@Override
	public User checkCredentials(String username, String password) {

		User user = CFW.DB.Users.selectByUsernameOrMail(username);
		
		if(user != null && user.passwordValidation(password)) {
			return user;
		}
		
		return null;
	}
	
}
