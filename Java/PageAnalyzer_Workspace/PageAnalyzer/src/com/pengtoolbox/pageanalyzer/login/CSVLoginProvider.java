package com.pengtoolbox.pageanalyzer.login;

import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.servlets.LoginServlet;
import com.pengtoolbox.pageanalyzer.utils.FileUtils;

public class CSVLoginProvider implements LoginProvider {
	
	private static Logger logger = PALogger.getLogger(LoginServlet.class.getName());
	private static HashMap<String, String> userCredentials = null;
	private static final String CREDENTIAL_FILE_PATH = PA.config("authentication_csv_file");
	
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
		PALogger log = new PALogger(logger, null).method("loadCredentials");
		
		//------------------------------
		// Load File
		String credentials = FileUtils.getFileContent(null, CREDENTIAL_FILE_PATH);
		
		if(credentials == null) {
			log.severe("Credential file could not be loaded: "+CREDENTIAL_FILE_PATH);
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
