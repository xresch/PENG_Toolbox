package com.pengtoolbox.pageanalyzer.db;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;

public class PAPermissions {

	public static String MANAGE_RESULTS 		= "Manage Results";
	public static String ANALYZE_HAR 			= "Analyze HAR";
	public static String DOWNLOAD_HAR 			= "Download HAR";
	public static String DELETE_RESULT			= "Delete Result";
	public static String ANALYZE_URL 			= "Analyze URL";
	public static String VIEW_HISTORY 			= "View History";
	public static String VIEW_DOCU				= "View Documentation";
	
	public static void initializePermissions() {
		
		Group adminGroup = CFW.DB.Groups.selectByName(CFW.DB.Groups.CFW_GROUP_ADMIN);
		Group userGroup = CFW.DB.Groups.selectByName(CFW.DB.Groups.CFW_GROUP_USER);
		Group foreignGroup = CFW.DB.Groups.selectByName(CFW.DB.Groups.CFW_GROUP_FOREIGN_USER);
		
		//-----------------------------------
		// Manage Results
		if(!CFW.DB.Permissions.checkPermissionExists(MANAGE_RESULTS)) {
			Permission manageResults = 
					new Permission(MANAGE_RESULTS)
						.description("Manage all Page Analyzer results results in the DB.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(manageResults);
			
			manageResults = CFW.DB.Permissions.selectByName(MANAGE_RESULTS);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(manageResults, adminGroup, true);
		}
		
		//-----------------------------------
		// Analyze HAR
		if(!CFW.DB.Permissions.checkPermissionExists(ANALYZE_HAR)) {
			Permission analyzeHAR = 
					new Permission(ANALYZE_HAR)
						.description("Upload and analyze HAR files.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(analyzeHAR);
			analyzeHAR = CFW.DB.Permissions.selectByName(ANALYZE_HAR);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(analyzeHAR, adminGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(analyzeHAR, userGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(analyzeHAR, foreignGroup, true);
		}
		//-----------------------------------
		// Download HAR
		if(!CFW.DB.Permissions.checkPermissionExists(DOWNLOAD_HAR)) {
			Permission downloadHAR = 
					new Permission(DOWNLOAD_HAR)
						.description("Download HAR files from the result history.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(downloadHAR);
			downloadHAR = CFW.DB.Permissions.selectByName(DOWNLOAD_HAR);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(downloadHAR, adminGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(downloadHAR, userGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(downloadHAR, foreignGroup, true);
		}
		
		//-----------------------------------
		// Analyze URL
		if(!CFW.DB.Permissions.checkPermissionExists(ANALYZE_URL)) {
			Permission analyzeURL = 
					new Permission(ANALYZE_URL)
						.description("Analyze a web application by using a URL.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(analyzeURL);
			analyzeURL = CFW.DB.Permissions.selectByName(ANALYZE_URL);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(analyzeURL, adminGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(analyzeURL, userGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(analyzeURL, foreignGroup, true);
		}
		
		//-----------------------------------
		// View History
		if(!CFW.DB.Permissions.checkPermissionExists(VIEW_HISTORY)) {
			Permission viewHistory = 
					new Permission(VIEW_HISTORY)
						.description("View the history of the saved results.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(viewHistory);
			viewHistory = CFW.DB.Permissions.selectByName(VIEW_HISTORY);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(viewHistory, adminGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(viewHistory, userGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(viewHistory, foreignGroup, true);
		}
		
		//-----------------------------------
		// Delete Result
		if(!CFW.DB.Permissions.checkPermissionExists(DELETE_RESULT)) {
			Permission deleteResult = 
					new Permission(DELETE_RESULT)
						.description("Delete results from the result history.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(deleteResult);
			deleteResult = CFW.DB.Permissions.selectByName(DELETE_RESULT);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(deleteResult, adminGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(deleteResult, userGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(deleteResult, foreignGroup, true);
		}
		//-----------------------------------
		// View Documentation
		if(!CFW.DB.Permissions.checkPermissionExists(VIEW_DOCU)) {
			Permission viewDocu = 
					new Permission(VIEW_DOCU)
						.description("View the documentation page of the page analyzer.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(viewDocu);
			viewDocu = CFW.DB.Permissions.selectByName(VIEW_DOCU);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(viewDocu, adminGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(viewDocu, userGroup, true);
			CFW.DB.GroupPermissionMap.addPermissionToGroup(viewDocu, foreignGroup, true);
		}
	}
	
	
}
