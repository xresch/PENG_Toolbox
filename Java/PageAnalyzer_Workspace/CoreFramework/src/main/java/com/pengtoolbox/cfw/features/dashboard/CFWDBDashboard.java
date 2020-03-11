package com.pengtoolbox.cfw.features.dashboard;

import java.sql.ResultSet;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.datahandling.CFWSQL;
import com.pengtoolbox.cfw.db.CFWDBDefaultOperations;
import com.pengtoolbox.cfw.db.PrecheckHandler;
import com.pengtoolbox.cfw.features.dashboard.Dashboard.DashboardFields;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDBDashboard {
	
	private static Class<Dashboard> cfwObjectClass = Dashboard.class;
	
	public static Logger logger = CFWLog.getLogger(CFWDBDashboard.class.getName());
		
	//####################################################################################################
	// Preckeck Initialization
	//####################################################################################################
	private static PrecheckHandler prechecksCreateUpdate =  new PrecheckHandler() {
		public boolean doCheck(CFWObject object) {
			
			Dashboard dashboard = (Dashboard)object;
			
			if(dashboard == null || dashboard.name().isEmpty()) {
				new CFWLog(logger)
					.method("doCheck")
					.warn("Please specify a name for the dashboard.", new Throwable());
				return false;
			}

			return true;
		}
	};
	
	
	private static PrecheckHandler prechecksDelete =  new PrecheckHandler() {
		public boolean doCheck(CFWObject object) {
			Dashboard dashboard = (Dashboard)object;
			
			if(dashboard != null && dashboard.isDeletable() == false) {
				new CFWLog(logger)
				.method("doCheck")
				.severe("The dashboard '"+dashboard.name()+"' cannot be deleted as it is marked as not deletable.", new Throwable());
				return false;
			}
			
			return true;
		}
	};
		
	//####################################################################################################
	// CREATE
	//####################################################################################################
	public static boolean	create(Dashboard... items) 	{ return CFWDBDefaultOperations.create(prechecksCreateUpdate, items); }
	public static boolean 	create(Dashboard item) 		{ return CFWDBDefaultOperations.create(prechecksCreateUpdate, item);}
	
	//####################################################################################################
	// UPDATE
	//####################################################################################################
	public static boolean 	update(Dashboard... items) 	{ return CFWDBDefaultOperations.update(prechecksCreateUpdate, items); }
	public static boolean 	update(Dashboard item) 		{ return CFWDBDefaultOperations.update(prechecksCreateUpdate, item); }
	
	//####################################################################################################
	// DELETE
	//####################################################################################################
	public static boolean 	deleteByID(int id) 					{ return CFWDBDefaultOperations.deleteFirstBy(prechecksDelete, cfwObjectClass, DashboardFields.PK_ID.toString(), id); }
	public static boolean 	deleteMultipleByID(String itemIDs) 	{ return CFWDBDefaultOperations.deleteMultipleByID(cfwObjectClass, itemIDs); }
	
	public static boolean 	deleteByName(String name) 		{ 
		return CFWDBDefaultOperations.deleteFirstBy(prechecksDelete, cfwObjectClass, DashboardFields.NAME.toString(), name); 
	}
	
	//####################################################################################################
	// SELECT
	//####################################################################################################
	public static Dashboard selectByID(String id ) {
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, DashboardFields.PK_ID.toString(), id);
	}
	
	public static Dashboard selectByID(int id ) {
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, DashboardFields.PK_ID.toString(), id);
	}
	
	public static Dashboard selectFirstByName(String name) { 
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, DashboardFields.NAME.toString(), name);
	}
	
	/***************************************************************
	 * Select a dashboard by it's ID and return it as JSON string.
	 * @param id of the dashboard
	 * @return Returns a dashboard or null if not found or in case of exception.
	 ****************************************************************/
	public static String getDashboardAsJSON(String id) {
		
		return new Dashboard()
				.queryCache(CFWDBDashboard.class, "getDashboardAsJSON")
				.select()
				.where(DashboardFields.FK_ID_USER.toString(), CFW.Context.Request.getUser().id())
				.or(DashboardFields.IS_SHARED.toString(), true)
				.where(DashboardFields.PK_ID.toString(), Integer.parseInt(id))
				.getAsJSON();
		
	}
	
	/***************************************************************
	 * Return a list of all user dashboards
	 * 
	 * @return Returns a resultSet with all dashboards or null.
	 ****************************************************************/
	public static ResultSet getUserDashboardList() {
		
		return new Dashboard()
				.queryCache(CFWDBDashboard.class, "getUserDashboardList")
				.select()
				.where(DashboardFields.FK_ID_USER.toString(), CFW.Context.Request.getUser().id())
				.orderby(DashboardFields.NAME.toString())
				.getResultSet();
		
	}
	
	/***************************************************************
	 * Return a list of all user dashboards
	 * 
	 * @return Returns a resultSet with all dashboards or null.
	 ****************************************************************/
//	public static ResultSet getSharedDashboardList() {
//		// SELECT (SELECT USERNAME FROM CFW_USER WHERE PK_ID = FK_ID_USER ) AS USERNAME, * FROM CFW_DASHBOARD WHERE IS_SHARED = TRUE ORDER BY LOWER(NAME)
//		return new Dashboard()
//				.queryCache(CFWDBDashboard.class, "getSharedDashboardList")
//				.columnSubquery("OWNER", "SELECT USERNAME FROM CFW_USER WHERE PK_ID = FK_ID_USER")
//				.select()
//				.where(DashboardFields.IS_SHARED.toString(), true)
//				.orderby(DashboardFields.NAME.toString())
//				.getResultSet();
//		
//	}
	
	/***************************************************************
	 * Return a list of all user dashboards as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getUserDashboardListAsJSON() {
		
		return new Dashboard()
				.queryCache(CFWDBDashboard.class, "getUserDashboardListAsJSON")
				.select()
				.where(DashboardFields.FK_ID_USER.toString(), CFW.Context.Request.getUser().id())
				.orderby(DashboardFields.NAME.toString())
				.getAsJSON();
	}
	
	/***************************************************************
	 * Return a list of all user dashboards as json string.
	 * 
	 * @return Returns a result set with all users or null.
	 ****************************************************************/
	public static String getSharedDashboardListAsJSON() {
		
//		SELECT *, (SELECT USERNAME FROM CFW_USER WHERE PK_ID = FK_ID_USER ) AS USERNAME 
//		FROM CFW_DASHBOARD 
//		WHERE ( IS_SHARED = TRUE AND ARRAY_LENGTH(SHARED_WITH_USERS) IS NULL )
//		OR ( IS_SHARED = TRUE AND ARRAY_CONTAINS(SHARED_WITH_USERS, 'admin') )
//		ORDER BY LOWER(NAME)
		return new Dashboard()
				.queryCache(CFWDBDashboard.class, "getSharedDashboardListAsJSON")
				.columnSubquery("OWNER", "SELECT USERNAME FROM CFW_USER WHERE PK_ID = FK_ID_USER")
				.select()
				.where("("+DashboardFields.IS_SHARED.toString(), true)
					.and()
					.arrayIsNull(DashboardFields.SHARED_WITH_USERS.toString())
					.custom(")")
				.or("("+DashboardFields.IS_SHARED.toString(), true)
					.and()
					.arrayContains(DashboardFields.SHARED_WITH_USERS.toString(), CFW.Context.Request.getUser().username())
					.custom(")")
				.orderby(DashboardFields.NAME.toString())
				.getAsJSON();
	}
	
	public static boolean isDashboardOfCurrentUser(String dashboardID) {
		
		int count = new CFWSQL(new Dashboard())
			.selectCount()
			.where(DashboardFields.PK_ID.toString(), dashboardID)
			.and(DashboardFields.FK_ID_USER.toString(), CFW.Context.Request.getUser().id())
			.getCount();
		
		return count > 0;
	}
	
	//####################################################################################################
	// CHECKS
	//####################################################################################################
	public static boolean checkExistsByName(String itemName) {	return CFWDBDefaultOperations.checkExistsBy(cfwObjectClass, DashboardFields.NAME.toString(), itemName); }
	public static boolean checkExistsByName(Dashboard item) {
		if(item != null) {
			return checkExistsByName(item.name());
		}
		return false;
	}
		
}
