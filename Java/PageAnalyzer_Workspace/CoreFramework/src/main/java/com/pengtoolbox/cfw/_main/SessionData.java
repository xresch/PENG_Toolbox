package com.pengtoolbox.cfw._main;

import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.bootstrap.BootstrapMenu;

public class SessionData {
	
	private boolean isLoggedIn = false;
	private User user = null;
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
		user = null;
		isLoggedIn = false;
	}
	
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
		
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public BootstrapMenu getMenu() {
		return menu;
	}

	
}
