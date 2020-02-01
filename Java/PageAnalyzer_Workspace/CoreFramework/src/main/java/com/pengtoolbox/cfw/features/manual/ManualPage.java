package com.pengtoolbox.cfw.features.manual;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.features.usermgmt.Permission;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class ManualPage {
	
	private static Logger logger = CFWLog.getLogger(CFWRegistryManual.class.getName());
	
	private String title = "&nbsp;";
	private String faiconClasses = "";
	
	// if any permissions match page will be accessible by the user
	// if no permission in the list page will be accessible by the user
	private HashSet<String> permissions = new HashSet<String>();
	private LinkedHashMap<String, ManualPage> childPages = new LinkedHashMap<String, ManualPage>();
	
	protected ManualPage parent = null;
	
	public ManualPage(String title) {
		
		if(title.contains("|")) {
			new CFWLog(logger)
			.method("<init>")
			.severe("Title cannot contain '|'.", new Exception());
		}
		
		this.title = title;
	}
	
	public ManualPage(String label, HashSet<String> permissions) {
		this.title = label;
		this.permissions = permissions;
	}
			
	/***********************************************************************************
	 * Overrloaded addChild to handle sub menu items.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public ManualPage addChild(ManualPage childItem) {
		
		childPages.put(((ManualPage)childItem).getLabel(), (ManualPage)childItem);
		this.addPermissions(((ManualPage)childItem).getPermissions());
		
		childItem.setParent(this);

		return this;
	}
		
	/***********************************************************************************
	 * Overrride to handle sub menu items.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public LinkedHashMap<String, ManualPage> getSubManualPages() {
		return childPages;
	}
	
	/***********************************************************************************
	 * Returns the Json data needed to build the navigation if the user has the required 
	 * permissions for the page
	 * @return String html for this item. 
	 ***********************************************************************************/
	public JsonObject toJSONObjectForUser() {
		
		//----------------------------------
		// Check Permissions
		if(permissions.size() > 0) {

			boolean hasPermission = false;
			HashMap<String, Permission> usersPermissions = CFW.Context.Request.getUserPermissions();
			for(String permission : permissions) {
				if(usersPermissions.containsKey(permission)) {
					hasPermission = true;
					break;
				}
			}
			
			if(!hasPermission) {
				return null;
			}
		}

		//----------------------------------
		// Build JSON
		JsonObject result = new JsonObject();
		
		result.addProperty("title", title);
		result.addProperty("faiconClasses", title);
		
		if(childPages.size() > 0) {
			JsonArray children = new JsonArray();
			for(ManualPage page : childPages.values()) {
				JsonObject object = page.toJSONObjectForUser();
				if(object != null) {
					children.add(object);
				}
			}
			
			result.add("children", children);
		}
		
		return result;

	}
	
	public ManualPage getParent() {
		return parent;
	}

	public void setParent(ManualPage parent) {
		this.parent = parent;
	}
	
	/***********************************************************************************
	 * Add the permission needed to see this menu item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public ManualPage addPermission(String permission) {
		if(permissions == null) {
			permissions = new HashSet<String>();
		}
		
		permissions.add(permission);
		
		if(this.parent != null && parent instanceof ManualPage) {
			((ManualPage)parent).addPermission(permission);
		}
		
		return this;
	}
	
	
	/***********************************************************************************
	 * Add the permissions needed to see this menu item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public ManualPage addPermissions(HashSet<String> permissionArray) {
		if(permissions == null) {
			permissions = new HashSet<String>();
		}
		
		permissions.addAll(permissionArray);
		
		if(this.parent != null && parent instanceof ManualPage) {
			((ManualPage)parent).addPermissions(permissionArray);
		}
		
		return this;
	}
	
	/***********************************************************************************
	 * 
	 * @return permissions
	 ***********************************************************************************/
	public HashSet<String> getPermissions( ) {
		return permissions;
	}
	
	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public String getLabel() {
		return title;
	}
	
	
	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public ManualPage faicon(String faiconClasses) {
		this.faiconClasses = faiconClasses;
		return this;
	}
	
	

		
	

}
