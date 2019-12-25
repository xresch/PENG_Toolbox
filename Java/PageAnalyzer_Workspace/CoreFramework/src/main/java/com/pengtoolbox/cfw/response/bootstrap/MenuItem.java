package com.pengtoolbox.cfw.response.bootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.Permission;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class MenuItem extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private String alignRightClass = "";
	
	// if any permissions match item will be rendered
	// if null item will be rendered
	private ArrayList<String> permissions = new ArrayList<String>();
	private LinkedHashMap<String, MenuItem> childMenuItems = new LinkedHashMap<String, MenuItem>();
	
	public MenuItem(String label) {
		this.label = label;
		this.addAttribute("href", "#");
	}
	
	public MenuItem(String label, ArrayList<String> permissions) {
		this.label = label;
		this.permissions = permissions;
		this.addAttribute("href", "#");
	}
	
	/***********************************************************************************
	 * Add the permission needed to see this menu item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public MenuItem addPermission(String permission) {
		if(permissions == null) {
			permissions = new ArrayList<String>();
		}
		
		permissions.add(permission);
		
		return this;
	}
	/***********************************************************************************
	 * Overrloaded addChild to handle sub menu items.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public MenuItem addChild(MenuItem childItem) {
		super.addChild(childItem);
		if(childItem instanceof MenuItem) {
			childMenuItems.put(childItem.getLabel(), childItem);
		}
		return this;
	}
	
	/***********************************************************************************
	 * Overrride to handle sub menu items.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public LinkedHashMap<String, MenuItem> getSubMenuItems() {
		return childMenuItems;
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public void createHTML(StringBuilder html) {
		
		//----------------------------------
		// Check Permissions
		if(permissions != null) {
			boolean hasPermission = false;
			HashMap<String, Permission> usersPermissions = CFW.Context.Request.getUserPermissions();
			for(String permission : permissions) {
				if(usersPermissions.containsKey(permission)) {
					hasPermission = true;
					break;
				}
			}
			
			if(!hasPermission) {
				return;
			}
			
		}
		
		//----------------------------------
		// Render Menu Item
		String cssClass = this.popAttributeValue("class");

		if(!this.hasChildren() && !this.hasOneTimeChildren()) {
			html.append("\n<li class=\""+cssClass+"\">");
			html.append("<a class=\"dropdown-item\" "+this.getAttributesString()+">"+label+"</a></li>");   
		}else {
			
			html.append("\n<li class=\"dropdown "+cssClass+"\">");
			html.append("\n<a "+this.getAttributesString()+"class=\"dropdown-item dropdown-toggle\" id=\"cfwMenuDropdown\" data-toggle=\"dropdown\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">"+label+"<span class=\"caret\"></span></a>");   
			html.append("\n<ul class=\"dropdown-menu "+alignRightClass+"\" aria-labelledby=\"cfwMenuDropdown\">");

			for(HierarchicalHTMLItem child : children) {
				if(child instanceof MenuItem) {

					html.append("\t"+((MenuItem)child).getHTML());
				}
			}
			
			for(HierarchicalHTMLItem child : oneTimeChildren) {
				if(child instanceof MenuItem) {
					html.append("\t"+((MenuItem)child).getHTML());
				}
			}
			html.append("\n</ul></li>");
		}
		
	}
	
	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public String getLabel() {
		return label;
	}
	
	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public MenuItem setLabel(String label) {
		fireChange();
		this.label = label;
		return this;
	}
	
	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public HierarchicalHTMLItem href(String href) {
		return addAttribute("href", href);
	}

	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public void alignDropdownRight(boolean alignRight) {
		if(alignRight) {
			alignRightClass = "dropdown-menu-right";
		}else {
			alignRightClass = "";
		}
	}

}
