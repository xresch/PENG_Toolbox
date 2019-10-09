package com.pengtoolbox.cfw.db.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.config.Configuration.ConfigFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBConfig {
	
	public static Logger logger = CFWLog.getLogger(CFWDBConfig.class.getName());
	
	//name/value pairs of configuration elements
	public static LinkedHashMap<String, String> configCache = new LinkedHashMap<String, String>();
	
	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
		new Configuration().createTable();
	}
	
	
	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static boolean updateCache() {
		ResultSet result = new Configuration()
			.select(ConfigFields.NAME.toString(), ConfigFields.VALUE.toString())
			.getResultSet();
		
		if(result == null) {
			return false;
		}
		
		try {
			LinkedHashMap<String, String> newCache = new LinkedHashMap<String, String>();
			while(result.next()) {
				newCache.put(
					result.getString(ConfigFields.NAME.toString()),
					result.getString(ConfigFields.VALUE.toString())
				);
			}
			configCache = newCache;
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("updateCache")
			.severe("Error updating configuration cache.", e);
			return false;
		}finally {
			CFWDB.close(result);
		}
		
		return true;
	}
	
	/********************************************************************************************
	 * Returns a config value from cache as String
	 * 
	 ********************************************************************************************/
	public static String getConfigAsString(String configName) {
		return configCache.get(configName);
	}
	
	/********************************************************************************************
	 * Returns a config value from cache as boolean
	 * 
	 ********************************************************************************************/
	public static boolean getConfigAsBoolean(String configName) {
		//System.out.println("===== Key: "+configName+", Value: "+configCache.get(configName));
		return Boolean.parseBoolean(configCache.get(configName));
	}
	
	/********************************************************************************************
	 * Returns a config value from cache as integer
	 * 
	 ********************************************************************************************/
	public static int getConfigAsInt(String configName) {
		return Integer.parseInt(configCache.get(configName));
	}
	
	
	/********************************************************************************************
	 * Creates multiple configs in the DB.
	 * @param Configs with the values that should be inserted. ID will be set by the Database.
	 * @return nothing
	 * 
	 ********************************************************************************************/
	public static void create(Configuration... configs) {
		
		for(Configuration config : configs) {
			create(config);
		}
	}
	
	/********************************************************************************************
	 * Creates a new config in the DB.
	 * @param Configuration with the values that should be inserted. ID will be set by the Database.
	 * @return true if successful, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean create(Configuration config) {
		
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
		
		boolean insertResult =  config
				.queryCache(CFWDBConfig.class, "create")
				.insert();
		
		updateCache();
		
		return insertResult;
	}
	
	/***************************************************************
	 * Select a config by it's name.
	 * @param id of the config
	 * @return Returns a config or null if not found or in case of exception.
	 ****************************************************************/
	public static Configuration selectByName(String name) {
		
		return (Configuration)new Configuration()
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
	public static Configuration selectByID(int id ) {

		return (Configuration)new Configuration()
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
		
		return new Configuration()
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
		
		return new Configuration()
				.queryCache(CFWDBConfig.class, "getConfigList")
				.select()
				.orderby(ConfigFields.NAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all configs
	 * 
	 * @return Returns a resultSet with all configs or null.
	 ****************************************************************/
	public static ArrayList<CFWObject> getConfigObjectList() {
		
		return new Configuration()
				.queryCache(CFWDBConfig.class, "getConfigList")
				.select()
				.orderby(ConfigFields.NAME.toString())
				.getObjectList();
		
	}
	
	/***************************************************************
	 * Return a list of all users as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getConfigListAsJSON() {
		return new Configuration()
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
	public static boolean update(Configuration config) {
		
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
				
		boolean updateResult =  config
				.queryCache(CFWDBConfig.class, "update")
				.update();
		
		updateCache();
		
		return updateResult;
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * you have to call the updateCache(method manually after using 
	 * this method.
	 * 
	 * @param config
	 * @return true or false
	 ****************************************************************/
	public static boolean updateValue(int id, String value) {
		
		return new Configuration()
				.id(id)
				.value(value)
				.queryCache(CFWDBConfig.class, "updateValue")
				.update(ConfigFields.VALUE.toString());
		
	}
	

	/****************************************************************
	 * Deletes the config by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
		
		Configuration config = selectByID(id);
		if(config == null ) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The config with id '"+id+"'+could not be found.");
			return false;
		}
		
		return new Configuration()
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

		return new Configuration()
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
		
		Configuration config = selectByName(name);
		if(config == null ) {
			new CFWLog(logger)
			.method("deleteByID")
			.severe("The config with name '"+name+"'+could not be found.");
			return false;
		}
		
		return new Configuration()
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
	public static boolean checkConfigExists(Configuration config) {
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
		
		int count = new Configuration()
				.queryCache(CFWDBConfig.class, "checkConfigExists")
				.selectCount()
				.where(ConfigFields.NAME.toString(), configName)
				.getCount();
		
		return (count > 0);
		
	}
	
}
