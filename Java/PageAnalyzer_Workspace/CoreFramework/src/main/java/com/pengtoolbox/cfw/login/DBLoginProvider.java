package com.pengtoolbox.cfw.login;

import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.servlets.LoginServlet;

public class DBLoginProvider implements LoginProvider {
	
	private static Logger logger = CFWLog.getLogger(LoginServlet.class.getName());
	
	@Override
	public boolean checkCredentials(String username, String password) {

		User user = CFW.DB.Users.selectByUsernameOrMail(username);
		
		return user.passwordValidation(password);
	}
	
}
