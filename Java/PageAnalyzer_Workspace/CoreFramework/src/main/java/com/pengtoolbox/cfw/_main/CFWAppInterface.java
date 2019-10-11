package com.pengtoolbox.cfw._main;

public interface CFWAppInterface {

	public void register();
	
	public void startDB();
	public void startApp(CFWDefaultApp app);
	
	public void stopApp();
}
