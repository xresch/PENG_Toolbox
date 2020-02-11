package com.pengtoolbox.cfw.features.manual;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplicationExecutor;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.features.usermgmt.Permission;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
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
		// Manual Menu is handled by CFW.Registry.Components.createMenuInstance()
		
		//---------------------------
		// Admin Manuals
		CFW.Registry.Manual.addManualPage(null, new ManualPage("Administration").faicon("fa fa-cog").addPermission(PERMISSION_ADMIN_MANUAL));
		
		//---------------------------
		// Developer Manuals
		ManualPage dev = CFW.Registry.Manual.addManualPage(null, new ManualPage("Development").faicon("fa fa-code").addPermission(PERMISSION_ADMIN_MANUAL));
		ManualPage quickstart = new ManualPage("Quickstart").faicon("fas fa-fighter-jet").addPermission(PERMISSION_ADMIN_MANUAL);
		dev.addChild(quickstart);
		
		
		
		quickstart.addChild(new ManualPage("Setup, Run and Export")
				.faicon("fas fa-star")
				.addPermission(PERMISSION_ADMIN_MANUAL)
				.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_setup_run_export.html")
			);
		
		quickstart.addChild(new ManualPage("Overview")
				.faicon("fas fa-eye")
				.addPermission(PERMISSION_ADMIN_MANUAL)
				.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_overview.html")
			);
		
		quickstart.addChild(new ManualPage("Create an Application")
				.faicon("fas fa-server")
				.addPermission(PERMISSION_ADMIN_MANUAL)
				.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_create_application.html")
			);
		
		quickstart.addChild(new ManualPage("Create a Feature")
				.faicon("fas fa-plug")
				.addPermission(PERMISSION_ADMIN_MANUAL)
				.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_create_feature.html")
			);
		
		quickstart.addChild(new ManualPage("Create a Servlet")
				.faicon("fas fa-server")
				.addPermission(PERMISSION_ADMIN_MANUAL)
				.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_create_servlet.html")
			);
		
		quickstart.addChild(new ManualPage("Add Configuration Items")
					.faicon("fa fa-cog")
					.addPermission(PERMISSION_ADMIN_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_configuration.html")
				);
		
		quickstart.addChild(
				new ManualPage("Create Permissions")
					.faicon("fa fa-lock")
					.addPermission(PERMISSION_ADMIN_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_permissions.html")
				);
		
		quickstart.addChild(
				new ManualPage("Create Manual Pages")
					.faicon("fa fa-book")
					.addPermission(PERMISSION_ADMIN_MANUAL)
					.content(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "manual_dev_registry_manualpages.html")
				);
	}

	@Override
	public void initializeDB() {
		
		//-----------------------------------
		// 
		CFW.DB.Permissions.oneTimeCreate(
				new Permission(PERMISSION_MANUAL, "user")
					.description("Can access the manual pages. Adds the manual menu item to the menu bar.")
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
	public void addFeature(CFWApplicationExecutor app) {	
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
