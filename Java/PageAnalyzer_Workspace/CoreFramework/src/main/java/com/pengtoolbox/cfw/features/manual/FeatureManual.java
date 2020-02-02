package com.pengtoolbox.cfw.features.manual;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplication;
import com.pengtoolbox.cfw.features.usermgmt.Permission;
import com.pengtoolbox.cfw.features.usermgmt.Role;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class FeatureManual extends CFWAppFeature {

	public static final String PERMISSION_MANUAL = "Manual";
	

	public static final String RESOURCE_PACKAGE = "com.pengtoolbox.cfw.features.manual.resources";
	
	@Override
	public void register() {
		//----------------------------------
		// Register Package
		CFW.Files.addAllowedPackage(RESOURCE_PACKAGE);

    	//----------------------------------
    	// Register Regular Menu
//		CFW.Registry.Components.addRegularMenuItem(
//				(MenuItem)new MenuItem("Manual")
//					.faicon("fas fa-book")
//					.addPermission(PERMISSION_MANUAL)
//					.href("./manual")
//				, null);
		
		
		//---------------------------
		// Test Menu Hierarchy
		CFW.Registry.Manual.addManualPage(new ManualPage("Top Page").faicon("fa fa-cog"), null);
		CFW.Registry.Manual.addManualPage(new ManualPage("A"), "Top Page");
		CFW.Registry.Manual.addManualPage(new ManualPage("B").content("This is a test B."), "Top Page | A");
		CFW.Registry.Manual.addManualPage(new ManualPage("C").content("This is a test C."), "Top Page | A | B" );
		
		//---------------------------
		// Test Menu Hierarchy 2
		CFW.Registry.Manual.addManualPage(new ManualPage("Top Item 2").faicon("fa fa-book"), null);
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Item"), "Top Item 2");
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Sub Item"), " Top Item 2 | Sub Item ");
		CFW.Registry.Manual.addManualPage(new ManualPage("Sub Sub Item 2"), "Top Item 2 | Sub Item ");
	}

	@Override
	public void initializeDB() {
		
		Role adminRole = CFW.DB.Roles.selectFirstByName(CFW.DB.Roles.CFW_ROLE_ADMIN);
		Role userRole = CFW.DB.Roles.selectFirstByName(CFW.DB.Roles.CFW_ROLE_USER);
		
		//-----------------------------------
		// 
		if(!CFW.DB.Permissions.checkExistsByName(PERMISSION_MANUAL)) {
			Permission permission = 
					new Permission(PERMISSION_MANUAL, "user")
						.description("Can access the manual pages.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(permission);
			
			permission = CFW.DB.Permissions.selectByName(PERMISSION_MANUAL);
			
			CFW.DB.RolePermissionMap.addPermissionToRole(permission, adminRole, true);
			CFW.DB.RolePermissionMap.addPermissionToRole(permission, userRole, true);
		}

	}

	@Override
	public void addFeature(CFWApplication app) {	
    	app.addAppServlet(ServletManual.class,  "/manual");
	}

	@Override
	public void startTasks() {

	}

	@Override
	public void stopFeature() {
		// TODO Auto-generated method stub
		
	}

}
