package com.pengtoolbox.cfw.stats;

import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.stats.StatsMethod.StatsMethodFields;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDBStatsMethod {
	
	public static Logger logger = CFWLog.getLogger(CFWDBStatsMethod.class.getName());
		
	/********************************************************************************************
	 * Creates a new statsMethod in the DB and returns it's primary key.
	 * @param statsMethod to create
	 * @return id or null if not successful
	 * 
	 ********************************************************************************************/
	public static Integer insertGetID(StatsMethod statsMethod) {
		
		if(statsMethod == null) {
			new CFWLog(logger)
				.method("create")
				.warn("The statsMethod cannot be null");
			return null;
		}

		return statsMethod
				.queryCache(CFWDBStatsMethod.class, "create")
				.insertGetPrimaryKey();
	}
	
	
	/***************************************************************
	 * Select a statsMethod by it's ID.
	 * @param id of the statsMethod
	 * @return Returns a statsMethod or null if not found or in case of exception.
	 ****************************************************************/
	public static StatsMethod selectByID(int id ) {

		return (StatsMethod)new StatsMethod()
				.queryCache(CFWDBStatsMethod.class, "selectByID")
				.select()
				.where(StatsMethodFields.PK_ID.toString(), id)
				.getFirstObject();
		
	}
	

	/***************************************************************
	 * Return a list of all user statsMethods as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getLatestAsJSON() {
		return new StatsMethod()
				.queryCache(CFWDBStatsMethod.class, "getLatestAsJSON")
				.select()
				.custom(" WHERE time = (SELECT MAX(time) FROM "+StatsMethod.TABLE_NAME+" )")
				.getAsJSON();
	}
	
		
	/****************************************************************
	 * Deletes the statsMethod by id.
	 * @param id of the user
	 * @return true if successful, false otherwise.
	 ****************************************************************/
	public static boolean deleteByID(int id) {
				
		return new StatsMethod()
				.queryCache(CFWDBStatsMethod.class, "deleteByID")
				.delete()
				.where(StatsMethodFields.PK_ID.toString(), id)
				.executeDelete();
					
	}	
	
}
