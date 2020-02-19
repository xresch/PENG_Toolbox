package com.pengtoolbox.cfw.features.dashboard;

import java.sql.ResultSet;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.datahandling.CFWObject;
import com.pengtoolbox.cfw.db.CFWDBDefaultOperations;
import com.pengtoolbox.cfw.db.PrecheckHandler;
import com.pengtoolbox.cfw.features.dashboard.DashboardWidget.DashboardWidgetFields;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDBDashboardWidget {
	
	private static Class<DashboardWidget> cfwObjectClass = DashboardWidget.class;
	
	public static Logger logger = CFWLog.getLogger(CFWDBDashboardWidget.class.getName());
		
	//####################################################################################################
	// Preckeck Initialization
	//####################################################################################################
	private static PrecheckHandler prechecksCreateUpdate =  new PrecheckHandler() {
		public boolean doCheck(CFWObject object) {
			
			DashboardWidget dashboard = (DashboardWidget)object;
			
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
			DashboardWidget dashboard = (DashboardWidget)object;
			
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
	public static boolean	create(DashboardWidget... items) 	{ return CFWDBDefaultOperations.create(prechecksCreateUpdate, items); }
	public static boolean 	create(DashboardWidget item) 		{ return CFWDBDefaultOperations.create(prechecksCreateUpdate, item);}
	
	//####################################################################################################
	// UPDATE
	//####################################################################################################
	public static boolean 	update(DashboardWidget... items) 	{ return CFWDBDefaultOperations.update(prechecksCreateUpdate, items); }
	public static boolean 	update(DashboardWidget item) 		{ return CFWDBDefaultOperations.update(prechecksCreateUpdate, item); }
	
	//####################################################################################################
	// DELETE
	//####################################################################################################
	public static boolean 	deleteByID(int id) 					{ return CFWDBDefaultOperations.deleteFirstBy(prechecksDelete, cfwObjectClass, DashboardWidgetFields.PK_ID.toString(), id); }
	public static boolean 	deleteMultipleByID(String itemIDs) 	{ return CFWDBDefaultOperations.deleteMultipleByID(cfwObjectClass, itemIDs); }
	
	public static boolean 	deleteByName(String name) 		{ 
		return CFWDBDefaultOperations.deleteFirstBy(prechecksDelete, cfwObjectClass, DashboardWidgetFields.NAME.toString(), name); 
	}
	
	//####################################################################################################
	// SELECT
	//####################################################################################################
	public static DashboardWidget selectByID(int id ) {
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, DashboardWidgetFields.PK_ID.toString(), id);
	}
	
	public static DashboardWidget selectFirstByName(String name) { 
		return CFWDBDefaultOperations.selectFirstBy(cfwObjectClass, DashboardWidgetFields.NAME.toString(), name);
	}
	
	
	/***************************************************************
	 * Return a list of all user dashboards
	 * 
	 * @return Returns a resultSet with all dashboards or null.
	 ****************************************************************/
	public static String getWidgetsForDashboardAsJSON(String dashboardID) {
		
		return new DashboardWidget()
				.queryCache(CFWDBDashboardWidget.class, "getWidgetsForDashboardAsJSON")
				.select()
				.where(DashboardWidgetFields.PK_ID.toString(), dashboardID)
				.getAsJSON();
		
	}


	

		
}
