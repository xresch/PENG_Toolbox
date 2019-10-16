package com.pengtoolbox.cfw._main;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public interface CFWAppInterface {

	public void register();
	
	public void initializeDB();
	public void startApp(CFWApplication app);
	
	public void stopApp();
}
