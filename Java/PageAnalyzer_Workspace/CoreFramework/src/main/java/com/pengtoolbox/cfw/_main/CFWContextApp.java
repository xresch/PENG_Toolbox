package com.pengtoolbox.cfw._main;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWContextApp {
	
	private static CFWApplication app;

	public static CFWApplication getApp() {
		return app;
	}

	public static void setApp(CFWApplication app) {
		CFWContextApp.app = app;
	}
	
	

}
