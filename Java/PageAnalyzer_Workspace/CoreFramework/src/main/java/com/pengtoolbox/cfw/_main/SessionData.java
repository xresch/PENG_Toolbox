package com.pengtoolbox.cfw._main;

import com.pengtoolbox.cfw.response.bootstrap.BootstrapMenu;

public class SessionData {
	
	private boolean isLoggedIn = false;
	private String username = null;
	private BootstrapMenu menu;

	
	public SessionData() {
		menu = CFW.App.createDefaultMenuInstance();
	}
	
	public void triggerLogin() {
		menu = CFW.App.createDefaultMenuInstance();
		menu.setUserMenuItem(CFW.App.createUserMenuItemInstance(this));
		isLoggedIn = true;
	}
	
	public void triggerLogout() {
		menu.setUserMenuItem(null);
		isLoggedIn = false;
	}
	
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
		
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public BootstrapMenu getMenu() {
		return menu;
	}

	
}
