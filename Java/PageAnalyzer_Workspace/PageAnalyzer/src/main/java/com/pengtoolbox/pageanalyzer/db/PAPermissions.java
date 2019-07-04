package com.pengtoolbox.pageanalyzer.db;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Permission;

public class PAPermissions {

	public static String MANAGE_RESULTS = "Manage Results";
	
	public static void initializePermissions() {
		
		Permission manageResults = 
				new Permission(MANAGE_RESULTS)
					.description("Manage results, even from other users.")
					.isDeletable(false);
		
		CFW.DB.Permissions.create(manageResults);
	}
	
	
}
