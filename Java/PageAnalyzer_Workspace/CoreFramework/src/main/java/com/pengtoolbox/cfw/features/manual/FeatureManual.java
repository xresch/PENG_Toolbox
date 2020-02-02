package com.pengtoolbox.cfw.features.manual;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplication;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.features.usermgmt.Permission;
import com.pengtoolbox.cfw.features.usermgmt.Role;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class FeatureManual extends CFWAppFeature {

	public static final String PERMISSION_MANUAL = "Manual";
	public static final String PERMISSION_ADMIN_MANUAL = "Manual for Admins";

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
		CFW.Registry.Manual.addManualPage(null, 			new ManualPage("Administration").faicon("fa fa-cog").addPermission(PERMISSION_ADMIN_MANUAL));
		CFW.Registry.Manual.addManualPage("Administration", new ManualPage("Development").faicon("fa fa-code").addPermission(PERMISSION_ADMIN_MANUAL));
		CFW.Registry.Manual.addManualPage("Administration | Development", 
				new ManualPage("Configuration")
					.faicon("fa fa-cog")
					.addPermission(PERMISSION_ADMIN_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_configuration.html"));
		
		CFW.Registry.Manual.addManualPage("Administration | Development", 
				new ManualPage("Permissions")
					.faicon("fa fa-lock")
					.addPermission(PERMISSION_ADMIN_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_permissions.html"));
		
		//---------------------------
		// Test Menu Hierarchy 2
		CFW.Registry.Manual.addManualPage(null, new ManualPage("Top Item 2").faicon("fa fa-book"));
		CFW.Registry.Manual.addManualPage("Top Item 2", new ManualPage("Sub Item"));
		CFW.Registry.Manual.addManualPage(" Top Item 2 | Sub Item ", new ManualPage("Sub Sub Item"));
		CFW.Registry.Manual.addManualPage("Top Item 2 | Sub Item ", new ManualPage("Sub Sub Item 2"));
	}

	@Override
	public void initializeDB() {
		
		//-----------------------------------
		// 
		CFW.DB.Permissions.oneTimeCreate(
				new Permission(PERMISSION_MANUAL, "user")
					.description("Can access the manual pages. Adds the ")
					.isDeletable(false),
					true,
					true
				);
			
		CFW.DB.Permissions.oneTimeCreate(
				new Permission(PERMISSION_ADMIN_MANUAL, "user")
					.description("Can access the manual pages for admins and developers.")
					.isDeletable(false),
					true,
					false
				);
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
