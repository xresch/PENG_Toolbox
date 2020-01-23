package com.pengtoolbox.cfw.feature.cpusampling;

import java.util.concurrent.ScheduledFuture;

import org.eclipse.jetty.servlet.ServletContextHandler;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplication;
import com.pengtoolbox.cfw.config.Configuration;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.Role;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CPUSamplingFeature extends CFWAppFeature {

	public static final String PERMISSION_CPU_SAMPlING = "CPU Sampling";
	

	public static final String RESOURCE_PACKAGE = "com.pengtoolbox.cfw.feature.cpusampling.resources";
	
	@Override
	public void register() {
		//----------------------------------
		// Register Package
		CFW.Files.addAllowedPackage(RESOURCE_PACKAGE);
		//----------------------------------
		// Register Objects
		CFW.Registry.Objects.addCFWObject(StatsCPUSampleSignature.class);
		CFW.Registry.Objects.addCFWObject(StatsCPUSample.class);
    	
    	//----------------------------------
    	// Register Regular Menu
		
		CFW.Registry.Components.addAdminCFWMenuItem(
				(MenuItem)new MenuItem("CPU Sampling")
					.faicon("fas fa-microchip")
					.addPermission(PERMISSION_CPU_SAMPlING)
					.href("./cpusampling")	
				, null);
	}

	@Override
	public void initializeDB() {
		
		Role adminRole = CFW.DB.Roles.selectFirstByName(CFW.DB.Roles.CFW_ROLE_ADMIN);
		//Role userRole = CFW.DB.Roles.selectFirstByName(CFW.DB.Roles.CFW_ROLE_USER);
		
		//-----------------------------------
		// 
		if(!CFW.DB.Permissions.checkExistsByName(PERMISSION_CPU_SAMPlING)) {
			Permission permission = 
					new Permission(PERMISSION_CPU_SAMPlING, "user")
						.description("Analyze CPU sampling data with the CPU smapling feature.")
						.isDeletable(false);
			
			CFW.DB.Permissions.create(permission);
			
			permission = CFW.DB.Permissions.selectByName(PERMISSION_CPU_SAMPlING);
			
			CFW.DB.RolePermissionMap.addPermissionToRole(permission, adminRole, true);
			//CFW.DB.RolePermissionMap.addPermissionToRole(permission, userRole, true);
		}

	}

	@Override
	public void addFeature(CFWApplication app) {	
    	ServletContextHandler appContext = app.getSecureContext();
        appContext.addServlet(StatsCPUSamplingServlet.class,  "/cpusampling");
	}

	@Override
	public void startTasks() {

		int seconds = CFW.DB.Config.getConfigAsInt(Configuration.CPU_SAMPLING_SECONDS);
		ScheduledFuture<?> sampling = CFW.Schedule.runPeriodically(0, seconds, new StatsCPUSamplingTask());
		ScheduledFuture<?> aggregation = CFW.Schedule.runPeriodically(0, 600, new StatsCPUSamplingAggregationTask());
	}

	@Override
	public void stopFeature() {
		// TODO Auto-generated method stub
		
	}

}
