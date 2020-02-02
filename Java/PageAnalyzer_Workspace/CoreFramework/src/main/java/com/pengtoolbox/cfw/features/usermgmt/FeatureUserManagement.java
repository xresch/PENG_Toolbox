package com.pengtoolbox.cfw.features.usermgmt;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplication;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class FeatureUserManagement extends CFWAppFeature {

	public static final String PERMISSION_CPU_SAMPlING = "CPU Sampling";
	

	public static final String RESOURCE_PACKAGE = "com.pengtoolbox.cfw.features.usermgmt.resources";
	
	@Override
	public void register() {
		//----------------------------------
		// Register Package
		CFW.Files.addAllowedPackage(RESOURCE_PACKAGE);
		
		//----------------------------------
		// Register Objects
		CFW.Registry.Objects.addCFWObject(User.class);
		CFW.Registry.Objects.addCFWObject(Role.class);
		CFW.Registry.Objects.addCFWObject(Permission.class);
		CFW.Registry.Objects.addCFWObject(UserRoleMap.class);
		CFW.Registry.Objects.addCFWObject(RolePermissionMap.class);
    	
    	//----------------------------------
    	// Register Regular Menu
				
		CFW.Registry.Components.addAdminCFWMenuItem(
				(MenuItem)new MenuItem("Manage Users")
					.faicon("fas fa-users")
					.addPermission(Permission.CFW_USER_MANAGEMENT)
					.href("./usermanagement")	
				, null);
	}

	@Override
	public void initializeDB() {
		
	}

	@Override
	public void addFeature(CFWApplication app) {	
		app.addAppServlet(ServletUserManagement.class,  "/usermanagement");
		app.addAppServlet(ServletPermissions.class,  "/usermanagement/permissions");
		app.addAppServlet(SevletUserManagementAPI.class, "/usermanagement/data"); 
	}

	@Override
	public void startTasks() {}

	@Override
	public void stopFeature() {}

}
