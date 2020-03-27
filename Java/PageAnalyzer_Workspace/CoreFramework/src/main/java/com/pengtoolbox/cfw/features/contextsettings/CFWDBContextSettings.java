package com.pengtoolbox.cfw.features.contextsettings;

import java.sql.ResultSet;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWSQL;
import com.pengtoolbox.cfw.db.CFWDBDefaultOperations;
import com.pengtoolbox.cfw.db.PrecheckHandler;
import com.pengtoolbox.cfw.features.contextsettings.ContextSettings.ContextSettingsFields;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDBContextSettings {
	
	private static Class<ContextSettings> cfwObjectClass = ContextSettings.class;
	
	public static Logger logger = CFWLog.getLogger(CFWDBContextSettings.class.getName());
		
	//####################################################################################################
	// Preckeck Initialization
	//####################################################################################################
	private static PrecheckHandler prechecksCreateUpdate =  new PrecheckHandler() {
		public boolean doCheck(CFWObject object) {
			
			ContextSettings environment = (ContextSettings)object;
			
			if(environment == null || environment.name().isEmpty()) {
				new CFWLog(logger)
					.method("doCheck")
					.warn("Please specify a name for the environment.", new Throwable());
				return false;
			}

			return true;
		}
	};
	
	
	private static PrecheckHandler prechecksDelete =  new PrecheckHandler() {
		public boolean doCheck(CFWObject object) {
			ContextSettings environment = (ContextSettings)object;
			
			if(environment == null ) {
				return false;
			}
			
			return true;
		}
	};
		
	//####################################################################################################
	// CREATE
	//####################################################################################################
	public static boolean	create(ContextSettings... items) 	{ return CFWDBDefaultOperations.create(prechecksCreateUpdate, items); }
	public static boolean 	create(ContextSettings item) 		{ return CFWDBDefaultOperations.create(prechecksCreateUpdate, item);}
	
	//####################################################################################################
	// UPDATE
	//####################################################################################################
	public static boolean 	update(ContextSettings... items) 	{ return CFWDBDefaultOperations.update(prechecksCreateUpdate, items); }
	public static boolean 	update(ContextSettings item) 		{ return CFWDBDefaultOperations.update(prechecksCreateUpdate, item); }
	
	//####################################################################################################
	// DELETE
	//####################################################################################################
	public static boolean 	deleteByID(int id) 					{ return CFWDBDefaultOperations.deleteFirstBy(prechecksDelete, cfwObjectClass, ContextSettingsFields.PK_ID.toString(), id); }
	public static boolean 	deleteMultipleByID(String itemIDs) 	{ return CFWDBDefaultOperations.deleteMultipleByID(cfwObjectClass, itemIDs); }
		
	public static boolean 	deleteByName(String name) 		{ 
		return CFWDBDefaultOperations.deleteFirstBy(prechecksDelete, cfwObjectClass, ContextSettingsFields.CFW_CTXSETTINGS_NAME.toString(), name); 
	}
		
	//####################################################################################################
	// SELECT
	//####################################################################################################
	public static ContextSettings selectByID(String id ) {
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, ContextSettingsFields.PK_ID.toString(), id);
	}
	
	public static ContextSettings selectByID(int id ) {
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, ContextSettingsFields.PK_ID.toString(), id);
	}
	
	public static ContextSettings selectFirstByName(String name) { 
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, ContextSettingsFields.CFW_CTXSETTINGS_NAME.toString(), name);
	}
	
	/***************************************************************
	 * Select a dashboard by it's ID and return it as JSON string.
	 * @param id of the dashboard
	 * @return Returns a dashboard or null if not found or in case of exception.
	 ****************************************************************/
	public static String getContextSettingsAsJSON(String id) {
		
		return new ContextSettings()
				.queryCache(CFWDBContextSettings.class, "getContextSettingsAsJSON")
				.select()
				.where(ContextSettingsFields.PK_ID.toString(), Integer.parseInt(id))
				.getAsJSON();
		
	}
	
	/***************************************************************
	 * Return a list of all user dashboards
	 * 
	 * @return Returns a resultSet with all dashboards or null.
	 ****************************************************************/
	public static ResultSet getContextSettingsList() {
		
		return new ContextSettings()
				.queryCache(CFWDBContextSettings.class, "getUserContextSettingsList")
				.select()
				.orderby(ContextSettingsFields.CFW_CTXSETTINGS_NAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all user dashboards
	 * 
	 * @return Returns a resultSet with all dashboards or null.
	 ****************************************************************/
//	public static ResultSet getSharedContextSettingsList() {
//		// SELECT (SELECT USERNAME FROM CFW_USER WHERE PK_ID = FK_ID_USER ) AS USERNAME, * FROM CFW_DASHBOARD WHERE IS_SHARED = TRUE ORDER BY LOWER(NAME)
//		return new ContextSettings()
//				.queryCache(CFWDBContextSettings.class, "getSharedContextSettingsList")
//				.columnSubquery("OWNER", "SELECT USERNAME FROM CFW_USER WHERE PK_ID = FK_ID_USER")
//				.select()
//				.where(ContextSettingsFields.IS_SHARED.toString(), true)
//				.orderby(ContextSettingsFields.NAME.toString())
//				.getResultSet();
//		
//	}
	
	/***************************************************************
	 * Return a list of all user dashboards as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getContextSettingsListAsJSON() {
		
		return new ContextSettings()
				.queryCache(CFWDBContextSettings.class, "getUserContextSettingsListAsJSON")
				.select()
				.orderby(ContextSettingsFields.CFW_CTXSETTINGS_TYPE.toString())
				.getAsJSON();
	}
	
	
	
	//####################################################################################################
	// CHECKS
	//####################################################################################################
	public static boolean checkExistsByName(String itemName) {	return CFWDBDefaultOperations.checkExistsBy(cfwObjectClass, ContextSettingsFields.CFW_CTXSETTINGS_NAME.toString(), itemName); }
	public static boolean checkExistsByName(ContextSettings item) {
		if(item != null) {
			return checkExistsByName(item.name());
		}
		return false;
	}
		
}
