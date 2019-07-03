package com.pengtoolbox.cfw._main;

import java.util.HashMap;

import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.bootstrap.BootstrapMenu;

public class SessionData {
	
	private boolean isLoggedIn = false;
	private User user = null;
	private HashMap<String, Group> userGroups = new HashMap<String, Group>();
	private HashMap<String, Permission> userPermissions = new HashMap<String, Permission>();
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
		if(user != null) {
			this.user = user;
			this.userGroups = CFW.DB.Users.selectGroupsForUser(user);
			this.userPermissions = CFW.DB.Users.selectPermissionsForUser(user);
		}
	}
	
	
	
	public HashMap<String, Group> getUserGroups() {
		return userGroups;
	}

	public HashMap<String, Permission> getUserPermissions() {
		return userPermissions;
	}

	public BootstrapMenu getMenu() {
		return menu;
	}
	
	

	
}
