package com.pengtoolbox.cfw._main;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.BTFooter;
import com.pengtoolbox.cfw.response.bootstrap.BTMenu;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;


/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWRegistryComponents {
	private static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	private static LinkedHashMap<String, MenuItem> regularMenuItems = new LinkedHashMap<String, MenuItem>();
	private static LinkedHashMap<String, MenuItem> userMenuItems = new LinkedHashMap<String, MenuItem>();
		
	private static Class<?> defaultFooterClass = null;

	
	/***********************************************************************
	 * Adds a menuItem to the regular menu.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void addRegularMenuItem(MenuItem item, String menuPath)  {
		addMenuItem(regularMenuItems, item, menuPath);
	}
	
	/***********************************************************************
	 * Adds a menuItem to the user menu.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void addUserMenuItem(MenuItem itemToAdd, String menuPath)  {
		addMenuItem(userMenuItems, itemToAdd, menuPath);
	}
	
	/***********************************************************************
	 * Adds a menuItem to one of the menus.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	private static void addMenuItem(LinkedHashMap<String, MenuItem> targetItemList, MenuItem itemToAdd, String menuPath)  {
		//System.out.println("======= Path :"+menuPath+" ======== ");
		//-----------------------
		// Check Argument
		if(menuPath == null || menuPath.trim().length() == 0) {
			targetItemList.put(itemToAdd.getLabel(), itemToAdd);
			//System.out.println("Add "+item.getLabel());
			return;
		}
		
		//-----------------------
		// Handle Path
		String[] pathTokens = menuPath.split("\\Q|\\E");
		MenuItem parentItem = null;
		LinkedHashMap<String, MenuItem> currentSubItems = targetItemList;
		for(int i = 0; i < pathTokens.length; i++) {
			String currentToken = pathTokens[i].trim();
			
			//---------------------------
			// Handle Parent
			if(parentItem == null) {
				parentItem = targetItemList.get(currentToken);
				if(parentItem == null) {
					parentItem = new MenuItem(currentToken);
					targetItemList.put(currentToken, parentItem);
				}
			}else if(parentItem.getSubMenuItems().containsKey(currentToken)) {
				parentItem = parentItem.getSubMenuItems().get(currentToken);
			}
			if(i == pathTokens.length-1) {
				parentItem.addChild(itemToAdd);
				//System.out.println("add "+itemToAdd.getLabel()+" to subitems of: "+currentToken);
			}
		}
	}
	
	/***********************************************************************
	 * Create a instance of the menu.
	 * @return a Bootstrap Menu instance
	 ***********************************************************************/
	public static BTMenu createMenuInstance(boolean withUserMenu)  {
		
		BTMenu menu = new BTMenu();
		
		
		for(MenuItem item : regularMenuItems.values() ) {
			menu.addChild(item.createCopy());
		}
		
		if(withUserMenu) {
			UserMenuItem userMenuItem = new UserMenuItem(CFW.Context.Session.getSessionData());	
			menu.setUserMenuItem(userMenuItem);
			
			for(MenuItem item : userMenuItems.values() ) {
				userMenuItem.addChild(item.createCopy());
			}
			

			if(!CFW.Context.Request.getUser().isForeign()) {
				userMenuItem.addChild(new MenuItem("Change Password").faicon("fas fa-key").href("./changepassword"));
			}
			

			userMenuItem.addChild(new MenuItem("Logout").faicon("fas fa-sign-out-alt").href("./logout"));
			
		}
		return menu;
	}
	

	/***********************************************************************
	 * Set the class to be used as the default footer for your application.
	 * @param Class that extends from BTFooter
	 ***********************************************************************/
	public static void setDefaultFooter(Class<?> menuClass)  {
		
		if(BTFooter.class.isAssignableFrom(menuClass)) {
			defaultFooterClass = menuClass;
		}else {
			new CFWLog(logger).severe("Class is not a subclass of 'BTFooter': "+menuClass.getName());
		}
	}
	
	
	/***********************************************************************
	 * Create a instance of the footer.
	 * @return a Bootstrap Menu instance
	 ***********************************************************************/
	public static BTFooter createDefaultFooterInstance()  {
		
		if(defaultFooterClass != null) {
			try {
				Object menu = defaultFooterClass.newInstance();
				
				if(menu instanceof BTFooter) {
					return (BTFooter)menu;
				}else {
					throw new InstantiationException("Class not an instance of BTFooter");
				}
			} catch (Exception e) {
				new CFWLog(logger).severe("Issue creating instance for Class '"+defaultFooterClass.getSimpleName()+"': "+e.getMessage(), e);
			}
		}
		
		return new BTFooter().setLabel("Set your custom menu class(extending BTFooter) using CFW.App.setDefaultFooter()! ");
	}
	
	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public static String dumpMenuItemHierarchy() {
		return new StringBuilder()
				.append(dumpMenuItemHierarchy("", regularMenuItems))
				.append(dumpMenuItemHierarchy("", userMenuItems))
				.toString();
	}
	
	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static String dumpMenuItemHierarchy(String currentPrefix, LinkedHashMap<String, MenuItem> menuItems) {
		
		//-----------------------------------
		//Create Prefix
		StringBuilder builder = new StringBuilder();
		
		MenuItem[] items = menuItems.values().toArray(new MenuItem[]{});
		int objectCount = items.length;
		for(int i = 0; i < objectCount; i++) {
			MenuItem current = items[i];
			builder.append(currentPrefix)
				   .append("|--> ")
				   .append(current.getLabel()).append("\n");
			if(objectCount > 1 && (i != objectCount-1)) {
				builder.append(dumpMenuItemHierarchy(currentPrefix+"|  ", current.getSubMenuItems()));
			}else{
				builder.append(dumpMenuItemHierarchy(currentPrefix+"  ", current.getSubMenuItems()));
			}
		}
		
		return builder.toString();
	}
	

}
