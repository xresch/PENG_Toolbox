package com.pengtoolbox.cfw._main;

public interface CFWAppInterface {

	public void register();
	
	public void initializeDB();
	public void startApp(CFWApplication app);
	
	public void stopApp();
}
