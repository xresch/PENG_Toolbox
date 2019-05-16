package com.pengtoolbox.cfw.login;

import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.servlets.LoginServlet;

public class CSVLoginProvider implements LoginProvider {
	
	private static Logger logger = CFWLog.getLogger(LoginServlet.class.getName());
	private static HashMap<String, String> userCredentials = null;
	
	public CSVLoginProvider() {
		
		if(userCredentials == null) {
			this.loadCredentials();
		}
	}
	
	@Override
	public boolean checkCredentials(String username, String password) {
		String passwordFromFile = userCredentials.get(username);
		return password.equals(passwordFromFile);
	}
	
	private void loadCredentials() {
		CFWLog log = new CFWLog(logger, null).method("loadCredentials");
		
		//------------------------------
		// Load File
		String credentials = CFW.Files.getFileContent(null, CFW.Config.AUTHENTICATION_CSV_FILE);
		
		if(credentials == null) {
			log.severe("Credential file could not be loaded: "+CFW.Config.AUTHENTICATION_CSV_FILE);
			return;
		}
		
		//------------------------------
		// Read Credentials
		String[] lines = credentials.split("\n|\r\n");
		
		userCredentials = new HashMap<String, String>();
		for(String line : lines) {
			String[] userAndPW = line.split(";");
			if(userAndPW.length == 2) {
				userCredentials.put(userAndPW[0], userAndPW[1]);
			}else {
				log.severe("Error loading user credentials.");
			}
		}
	}

}
