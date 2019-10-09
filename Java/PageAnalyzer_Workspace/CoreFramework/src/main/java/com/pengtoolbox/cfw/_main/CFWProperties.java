package com.pengtoolbox.cfw._main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

public class CFWProperties {

	public static final Properties configProperties = new Properties();
	
	/** Application name, will be used for creating the base url (Property=cfw_application_name, Default="myapp") */
	public static String APPLICATION_NAME = "myapp";
	
	/** Application ID, used to make your application more secure. (Property=cfw_application_id, Default="change_me_now") */
	public static String APPLICATION_ID = "change_me_now";
	
	/** Bootstrap Theme for the application. (Property=cfw_application_theme, Default="superhero") */
	public static String APPLICATION_THEME = "slate";
	
	/** Default maximum upload size for files in megabyte. (Property=cfw_application_max_uploadsize, Default=200) */
	public static int APPLICATION_MAX_UPLOADSIZE = 200;
	
	/** Reset the admin password on the next restart. (Property=cfw_reset_admin_pw, Default=false) */
	public static boolean RESET_ADMIN_PW = false;
	
	
	/** Enables or disables the HTTP connector. (Property=cfw_http_enabled, Default=true) */
	public static boolean HTTP_ENABLED = true;
	
	/** The port for the HTTP connector. (Property=cfw_http_port, Default=80) */
	public static int HTTP_PORT = 80;
	
	/** The port for the HTTP connector. (Property=cfw_http_redirect_to_https, Default=true) */
	public static boolean HTTP_REDIRECT_TO_HTTPS = true;
	
	/** Enables or disables the HTTPS connector. (Property=cfw_https_enabled, Default=true) */
	public static boolean HTTPS_ENABLED = true;
	
	/**  (Property=cfw_https_port, Default=443) */
	public static int HTTPS_PORT = 443;
	
	/**  (Property=cfw_https_keystore_path, Default=./config/keystore.jks) */
	public static String HTTPS_KEYSTORE_PATH = "./config/keystore.jks";
	
	/**  (Property=cfw_https_keystore_password, Default="") */
	public static String HTTPS_KEYSTORE_PASSWORD  = "";
	
	/**  (Property=cfw_https_keymanager_password, Default="") */
	public static String HTTPS_KEYMANAGER_PASSWORD = "";
	
	/** the base url of the application. */
	public static String BASE_URL = "/";
		
	/** Session Time in Seconds. (Property=cfw_session_timeout, Default=36000) */
	public static int SESSION_TIMEOUT = 36000;
	
	/** Time in seconds to cache resources. (Property=cfw_browser_resource_maxage, Default="36000") */
	public static int BROWSER_RESOURCE_MAXAGE = 36000;
	
	/** Enables or disables the Authentication. (Property=cfw_authentication_enabled, Default="false") */
	public static boolean AUTHENTICATION_ENABLED = false;
	
	/** The authentication method which should be used. (Property=authentication_method, Default="csv") */
	public static String AUTHENTICATION_METHOD = "csv";
	
	/** The path to the csv file with credentials. (Property=authentication_csv_file, Default="./config/credentials.csv") */
	public static String AUTHENTICATION_CSV_FILE = "./config/credentials.csv"; 
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_url, Default="") */
	public static String LDAP_URL = "";
	
	/** The User used for LDAP authentication. (Property=authentication_ldap_user, Default="") */
	public static String LDAP_USER = "";
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_password, Default="") */
	public static String LDAP_PASSWORD = "";
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_searchbase, Default="") */
	public static String LDAP_SEARCHBASE = "";
	
	/** The URL used for LDAP authentication. (Property=authentication_ldap_user_attribute, Default="") */
	public static String LDAP_USER_ATTRIBUTE = "";
	
	/** The name of the database server. (Property=cfw_h2_server, Default="localhost") */
	public static String DB_SERVER = "localhost";
	
	/** The directory where the database should be stored. (Property=cfw_h2_path, Default="false") */
	public static String DB_STORE_PATH = "./datastore";
	
	/** The name of the database. (Property=cfw_h2_db_name, Default="h2database") */
	public static String DB_NAME = "h2database";
	
	/** The port of the database. (Property=cfw_h2_port, Default="8081") */
	public static int DB_PORT = 8081;

	/** The username for the database. (Property=cfw_h2_username, Default="sa") */
	public static String DB_USERNAME = "sa";
	
	/** The password for the database. (Property=cfw_h2_password, Default="sa") */
	public static String DB_PASSWORD = "sa";


	
	/******************************************************************************
	 * Initialize the configuration with the given properties file.
	 * @param key
	 * @return
	 *******************************************************************************/
	public static void loadConfiguration(String configFilePath) throws IOException {
		
		CFWProperties.configProperties.load(new FileReader(new File(configFilePath)));
		printConfiguration();
		
		APPLICATION_ID					= CFWProperties.configAsString("cfw_application_id", APPLICATION_ID);
		APPLICATION_NAME				= CFWProperties.configAsString("cfw_application_name", APPLICATION_NAME);
		APPLICATION_THEME				= CFWProperties.configAsString("cfw_application_theme", APPLICATION_THEME);
		APPLICATION_MAX_UPLOADSIZE		= CFWProperties.configAsInt("cfw_application_max_uploadsize", APPLICATION_MAX_UPLOADSIZE);
		RESET_ADMIN_PW 					= CFWProperties.configAsBoolean("cfw_reset_admin_pw", RESET_ADMIN_PW);
				
		BASE_URL 						= "/"+APPLICATION_NAME;
		
		HTTP_ENABLED 					= CFWProperties.configAsBoolean("cfw_http_enabled", HTTP_ENABLED);
		HTTP_PORT 						= CFWProperties.configAsInt("cfw_http_port", HTTP_PORT);
		HTTP_REDIRECT_TO_HTTPS			= CFWProperties.configAsBoolean("cfw_http_redirect_to_https", HTTP_REDIRECT_TO_HTTPS);

		HTTPS_ENABLED 					= CFWProperties.configAsBoolean("cfw_https_enabled", HTTPS_ENABLED);
		HTTPS_PORT 						= CFWProperties.configAsInt("cfw_https_port", HTTPS_PORT);
		
		HTTPS_KEYSTORE_PATH 			= CFWProperties.configAsString("cfw_https_keystore_path", HTTPS_KEYSTORE_PATH);
		HTTPS_KEYSTORE_PASSWORD			= CFWProperties.configAsString("cfw_https_keystore_password", HTTPS_KEYSTORE_PASSWORD);
		HTTPS_KEYMANAGER_PASSWORD		= CFWProperties.configAsString("cfw_https_keymanager_password", HTTPS_KEYMANAGER_PASSWORD);
		
		SESSION_TIMEOUT					= CFWProperties.configAsInt("cfw_session_timeout", SESSION_TIMEOUT);
		BROWSER_RESOURCE_MAXAGE 		= CFWProperties.configAsInt("cfw_browser_resource_maxage", BROWSER_RESOURCE_MAXAGE);
		
		AUTHENTICATION_METHOD 			= CFWProperties.configAsString("authentication_method", AUTHENTICATION_METHOD);
		AUTHENTICATION_ENABLED 			= CFWProperties.configAsBoolean("cfw_authentication_enabled", AUTHENTICATION_ENABLED);
		AUTHENTICATION_CSV_FILE			= CFWProperties.configAsString("authentication_csv_file", AUTHENTICATION_CSV_FILE);
		
		LDAP_URL 						= CFWProperties.configAsString("authentication_ldap_url", LDAP_URL);
		LDAP_USER 						= CFWProperties.configAsString("authentication_ldap_user", LDAP_USER);
		LDAP_PASSWORD 					= CFWProperties.configAsString("authentication_ldap_password", LDAP_PASSWORD);
		LDAP_SEARCHBASE 				= CFWProperties.configAsString("authentication_ldap_searchbase", LDAP_SEARCHBASE);
		LDAP_USER_ATTRIBUTE 			= CFWProperties.configAsString("authentication_ldap_user_attribute", LDAP_USER_ATTRIBUTE);

		DB_SERVER					= CFWProperties.configAsString("cfw_h2_server", DB_SERVER);
		DB_PORT						= CFWProperties.configAsInt("cfw_h2_port", DB_PORT);
		DB_STORE_PATH				= CFWProperties.configAsString("cfw_h2_path", DB_STORE_PATH);
		DB_NAME						= CFWProperties.configAsString("cfw_h2_db_name", DB_NAME);
		DB_USERNAME					= CFWProperties.configAsString("cfw_h2_username", DB_USERNAME);
		DB_PASSWORD					= CFWProperties.configAsString("cfw_h2_password", DB_PASSWORD);
		
	}
	
	
	/******************************************************************************
	 * Initialize the configuration with the given properties file.
	 * @param key
	 * @return
	 *******************************************************************************/
	public static void printConfiguration() {
		
		System.out.println("################################################");
		System.out.println("##            LOADED CONFIGURATION            ##");
		System.out.println("################################################");
		for (Entry<Object,Object> e : configProperties.entrySet()) {
			System.out.println(e.getKey()+"='"+e.getValue()+"'");
		}
		System.out.println("################################################");
	}

	
	/******************************************************************************
	 * Retrieve
	 * @param key
	 * @return
	 *******************************************************************************/
	public static boolean configAsBoolean(String key, boolean defaultValue){
		
		return configProperties.getOrDefault(key, defaultValue).toString().toLowerCase().equals("true") ? true : false;
	}

	public static int configAsInt(String key, int defaultValue){
		
		return (int)Integer.parseInt((String)configProperties.getOrDefault(key, ""+defaultValue));
	}

	public static String configAsString(String key, String defaultValue){
		
		return (String)configProperties.getOrDefault(key, defaultValue);
	}


}
