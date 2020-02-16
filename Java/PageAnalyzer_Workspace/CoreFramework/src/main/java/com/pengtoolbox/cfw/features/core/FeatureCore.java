package com.pengtoolbox.cfw.features.core;

import java.util.Locale;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWAppFeature;
import com.pengtoolbox.cfw._main.CFWApplicationExecutor;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class FeatureCore extends CFWAppFeature {

	public static final String RESOURCE_PACKAGE = "com.pengtoolbox.cfw.features.core.resources";
	
	@Override
	public void register() {
		
		//----------------------------------
		// Register Package
		CFW.Files.addAllowedPackage(RESOURCE_PACKAGE);
		
		//----------------------------------
		// Register Languages
		CFW.Localization.registerLocaleFile(Locale.ENGLISH, "", new FileDefinition(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "lang_en.properties"));
		CFW.Localization.registerLocaleFile(Locale.GERMAN, "", new FileDefinition(HandlingType.JAR_RESOURCE, RESOURCE_PACKAGE, "lang_de.properties"));		
		//----------------------------------
		// Register Objects
		//CFW.Registry.Objects.addCFWObject(Configuration.class);
    	
    	//----------------------------------
    	// Register Regular Menu
		
//		CFW.Registry.Components.addAdminCFWMenuItem(
//				(MenuItem)new MenuItem("Configuration")
//					.faicon("fas fa-cog")
//					.addPermission(PERMISSION_CONFIGURATION)
//					.href("./configuration")	
//				, null);
		
	}

	@Override
	public void initializeDB() {
		
	}

	@Override
	public void addFeature(CFWApplicationExecutor app) {	
		app.addUnsecureServlet(LocalizationServlet.class,  "/cfw/locale");
	}

	@Override
	public void startTasks() {
	}

	@Override
	public void stopFeature() {
	}

}
