package com.pengtoolbox.cfw._main;

import java.util.HashMap;

import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.bootstrap.BTFooter;
import com.pengtoolbox.cfw.response.bootstrap.BTMenu;

public class SessionData {
	
	private boolean isLoggedIn = false;
	private User user = null;
	private HashMap<String, Group> userGroups = new HashMap<String, Group>();
	private HashMap<String, Permission> userPermissions = new HashMap<String, Permission>();
	private BTMenu menu;
	private BTFooter footer;
	
	public SessionData() {
		menu = CFW.App.createDefaultMenuInstance();
		footer = CFW.App.createDefaultFooterInstance();
	}
	
	public void triggerLogin() {
		isLoggedIn = true;
		
		menu = CFW.App.createDefaultMenuInstance();
		menu.setUserMenuItem(CFW.App.createUserMenuItemInstance(this));
		footer = CFW.App.createDefaultFooterInstance();

	}
	
	public void triggerLogout() {
		isLoggedIn = false;
		
		menu = CFW.App.createDefaultMenuInstance();
		footer = CFW.App.createDefaultFooterInstance();
		user = null;
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

	public BTMenu getMenu() {
		return menu;
	}
	
	public BTFooter getFooter() {
		return footer;
	}
	
	

	
}
