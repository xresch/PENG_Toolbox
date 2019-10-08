package com.pengtoolbox.cfw.db.config;

import java.sql.ResultSet;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.db.config.Config.ConfigFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBConfig {
	
	public static Logger logger = CFWLog.getLogger(CFWDBConfig.class.getName());
	
	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
		new Config().createTable();
	}
	
	/********************************************************************************************
	 * Creates multiple configs in the DB.
	 * @param Configs with the values that should be inserted. ID will be set by the Database.
	 * @return nothing
	 * 
	 ********************************************************************************************/
	public static void create(Config... configs) {
		
		for(Config config : configs) {
			create(config);
		}
	}
	
	/********************************************************************************************
	 * Creates a new config in the DB.
	 * @param Config with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean create(Config config) {
		
		if(config == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The config cannot be null");
			return false;
		}
		
		if(config.name() == null || config.name().isEmpty()) {
			new CFWLog(logger)
				.method("create")
				.warn("Please specify a name for the config to create.");
			return false;
		}
		
		if(checkConfigExists(config)) {
			new CFWLog(logger)
				.method("create")
				.warn("The config '"+config.name()+"' cannot be created as a config with this name already exists.");
			return false;
		}
		
		return config
				.queryCache(CFWDBConfig.class, "create")
				.insert();
	}
	
	/***************************************************************
	 * Select a config by it's name.
	 * @param id of the config
	 * @return Returns a config or null if not found or in case of exception.
	 ****************************************************************/
	public static Config selectByName(String name) {
		
		return (Config)new Config()
				.queryCache(CFWDBConfig.class, "selectByName")
				.select()
				.where(ConfigFields.NAME.toString(), name)
				.getFirstObject();

	}
	
	/***************************************************************
	 * Select a config by it's ID.
	 * @param id of the config
	 * @return Returns a config or null if not found or in case of exception.
	 ****************************************************************/
	public static Config selectByID(int id ) {

		return (Config)new Config()
				.queryCache(CFWDBConfig.class, "selectByID")
				.select()
				.where(ConfigFields.PK_ID.toString(), id)
				.getFirstObject();
		
	}
	
	/***************************************************************
	 * Select a config by it's ID and return it as JSON string.
	 * @param id of the config
	 * @return Returns a config or null if not found or in case of exception.
	 ****************************************************************/
	public static String getConfigAsJSON(String id) {
		
		return new Config()
				.queryCache(CFWDBConfig.class, "getConfigAsJSON")
				.select()
				.where(ConfigFields.PK_ID.toString(), Integer.parseInt(id))
				.getAsJSON();
		
	}
	
	/***************************************************************
	 * Return a list of all configs
	 * 
	 * @return Returns a resultSet with all configs or null.
	 ****************************************************************/
	public static ResultSet getConfigList() {
		
		return new Config()
				.queryCache(CFWDBConfig.class, "getConfigList")
				.select()
				.orderby(ConfigFields.NAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all users as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getConfigListAsJSON() {
		return new Config()
				.queryCache(CFWDBConfig.class, "getConfigListAsJSON")
				.select()
				.orderby(ConfigFields.NAME.toString())
				.getAsJSON();
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param config
	 * @return true or false
	 ****************************************************************/
	public static boolean update(Config config) {
		
		if(config == null) {
			new CFWLog(logger)
				.method("update")
				.warn("The config that should be updated cannot be null");
			return false;
		}
		
		if(config.name() == null || config.name().isEmpty()) {
			new CFWLog(logger)
				.method("update")
				.warn("Please specify a name for the config.");
			return false;
		}
				
		return config
				.queryCache(CFWDBConfig.class, "update")
				.update();
		
	}
	

	/****************************************************************
	 * Deletes the config by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		Config config = selectByID(id);
		if(config == null ) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The config with id '"+id+"'+could not be found.");
			return false;
		}
		
		return new Config()
				.queryCache(CFWDBConfig.class, "deleteByID")
				.delete()
				.where(ConfigFields.PK_ID.toString(), id)
				.executeDelete();
					
	}
	
	/****************************************************************
	 * Deletes multiple users by id.
	 * @param ids of the users separated by comma
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteMultipleByID(String resultIDs) {
		
		//----------------------------------
		// Check input format
		if(resultIDs == null ^ !resultIDs.matches("(\\d,?)+")) {
			new CFWLog(logger)
			.method("deleteMultipleByID")
			.severe("The userID's '"+resultIDs+"' are not a comma separated list of strings.");
			return false;
		}

		return new Config()
				.queryCache(CFWDBConfig.class, "deleteMultipleByID")
				.delete()
				.whereIn(ConfigFields.PK_ID.toString(), resultIDs)
				.executeDelete();
					
	}
	
	/****************************************************************
	 * Deletes the config by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByName(String name) {
		
		Config config = selectByName(name);
		if(config == null ) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The config with name '"+name+"'+could not be found.");
			return false;
		}
		
		return new Config()
				.queryCache(CFWDBConfig.class, "deleteByName")
				.delete()
				.where(ConfigFields.NAME.toString(), name)
				.executeDelete();
					
	}
	
	
	/****************************************************************
	 * Check if the config exists by name.
	 * 
	 * @param config to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkConfigExists(Config config) {
		if(config != null) {
			return checkConfigExists(config.name());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the config exists by name.
	 * 
	 * @param configname to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkConfigExists(String configName) {
		
		int count = new Config()
				.queryCache(CFWDBConfig.class, "checkConfigExists")
				.selectCount()
				.where(ConfigFields.NAME.toString(), configName)
				.getCount();
		
		return (count > 0);
		
	}
	
}
