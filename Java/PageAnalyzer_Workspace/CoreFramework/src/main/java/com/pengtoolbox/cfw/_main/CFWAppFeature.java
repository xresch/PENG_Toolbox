package com.pengtoolbox.cfw._main;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public interface CFWAppFeature {

	/************************************************************************************
	 * Register components and objects.
	 ************************************************************************************/
	public void register();
	
	/************************************************************************************
	 * Initialize database with data.
	 ************************************************************************************/
	public void initializeDB();
	
	/************************************************************************************
	 * Add servlets to the application.
	 * This is executed before the application is started.
	 ************************************************************************************/
	public void addFeature(CFWApplication app);
	
	/************************************************************************************
	 * Actions that should be executed when the application is stopped.
	 ************************************************************************************/
	public void stopFeature();
}
