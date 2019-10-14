package com.pengtoolbox.cfw._main;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.pengtoolbox.cfw.datahandling.CFWForm;
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
	private static LinkedHashMap<String,CFWForm> formMap = new LinkedHashMap<String,CFWForm>();
	
	private BTMenu menu;
	private BTFooter footer;
	
	public SessionData() {
		menu = CFW.Registry.Components.createDefaultMenuInstance();
		footer = CFW.Registry.Components.createDefaultFooterInstance();
	}
	
	public void triggerLogin() {
		isLoggedIn = true;
		
		menu = CFW.Registry.Components.createDefaultMenuInstance();
		menu.setUserMenuItem(CFW.Registry.Components.createUserMenuItemInstance(this));
		footer = CFW.Registry.Components.createDefaultFooterInstance();

	}
	
	public void triggerLogout() {
		isLoggedIn = false;
		
		menu = CFW.Registry.Components.createDefaultMenuInstance();
		footer = CFW.Registry.Components.createDefaultFooterInstance();
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
	
	public void resetUser() {
		user = null;
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
	
	public void addForm(CFWForm form){
		
		//keep cached forms below 7 to prevent memory leaks
		while(formMap.size() > 7) {
			formMap.remove(formMap.keySet().toArray()[0]);
		}
		
		formMap.put(form.getFormID(), form);	
	}
	
	public void removeForm(CFWForm form){
		formMap.remove(form.getFormID(), form);	
	}
	
	public CFWForm getForm(String formID) {
		return formMap.get(formID);
	}
	
	public Collection<CFWForm> getForms() {
		return formMap.values();
	}
}
