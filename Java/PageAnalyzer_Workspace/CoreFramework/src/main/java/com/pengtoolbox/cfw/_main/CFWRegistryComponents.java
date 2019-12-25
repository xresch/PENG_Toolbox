package com.pengtoolbox.cfw._main;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.datahandling.CFWObject;
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
	
	private static LinkedHashMap<String, MenuItem> rootMenuItems = new LinkedHashMap<String, MenuItem>();
	private static LinkedHashMap<String, MenuItem> userMenuItems = new LinkedHashMap<String, MenuItem>();
	
	
	/***********************************************************************
	 * Adds a menuItem to the regular menu.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void addRegularMenuItem(MenuItem item, String menuPath)  {
		addMenuItem(rootMenuItems, item, menuPath);
	}
	
	/***********************************************************************
	 * Adds a menuItem to the user menu.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void addUserMenuItem(MenuItem item, String menuPath)  {
		addMenuItem(userMenuItems, item, menuPath);
	}
	
	/***********************************************************************
	 * Adds a menuItem to one of the menus.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	private static void addMenuItem(LinkedHashMap<String, MenuItem> targetItemList, MenuItem item, String menuPath)  {
		System.out.println("======= Path :"+menuPath+" ======== ");
		//-----------------------
		// Check Argument
		if(menuPath == null || menuPath.trim().length() == 0) {
			targetItemList.put(item.getLabel(), item);
			//System.out.println("Add "+item.getLabel());
			return;
		}
		
		//-----------------------
		// Handle Path
		String[] pathTokens = menuPath.split("\\Q|\\E");
		
		LinkedHashMap<String, MenuItem> currentSubItems = targetItemList;
		for(int i = 0; i < pathTokens.length; i++) {
			String currentToken = pathTokens[i].trim();
			if(!currentSubItems.containsKey(currentToken)) {
				
				// Create Item
				MenuItem newParent = new MenuItem(currentToken);
				currentSubItems.put(currentToken, newParent);
				currentSubItems = newParent.getSubMenuItems();
				System.out.println("Set subitems to: "+currentToken+" >> "+currentSubItems);
			}else {
				currentSubItems = currentSubItems.get(currentToken).getSubMenuItems();
				System.out.println("Set subitems to: "+currentToken+" >> "+currentSubItems);
			}
			
			if(i == pathTokens.length-1) {
				currentSubItems.put(item.getLabel(), item);
				System.out.println("add "+item.getLabel()+" to subitems of: "+currentToken);
				//System.out.println("Add "+item.getLabel()+" to "+currentToken);
			}
		}
	}
	
	
	private static Class<?> defaultMenuClass = null;
	private static Class<?> defaultUserMenuItemClass = null;
	private static Class<?> defaultFooterClass = null;
	 
	/***********************************************************************
	 * Set the class to be used as the default menu for your application.
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void setDefaultMenu(Class<?> menuClass)  {
		
		if(BTMenu.class.isAssignableFrom(menuClass)) {
			defaultMenuClass = menuClass;
		}else {
			new CFWLog(logger).severe("Class is not a subclass of 'BTMenu': "+menuClass.getName());
		}
	}
		
	/***********************************************************************
	 * Create a instance of the menu.
	 * @return a Bootstrap Menu instance
	 ***********************************************************************/
	public static BTMenu createDefaultMenuInstance()  {
		
		if(defaultMenuClass != null) {
			try {
				Object menu = defaultMenuClass.newInstance();
				
				if(menu instanceof BTMenu) {
					return (BTMenu)menu;
				}else {
					throw new InstantiationException("Class not an instance of BootstrapMenu");
				}
			} catch (Exception e) {
				new CFWLog(logger).severe("Issue creating instance for Class '"+defaultMenuClass.getSimpleName()+"': "+e.getMessage(), e);
			}
		}
		
		return new BTMenu().setLabel("Set your custom menu class(extending BootstrapMenu) using CFW.App.setDefaultMenu()! ");
	}
	
	/***********************************************************************
	 * Set the class to be used as the default UserMenuItem for your application.
	 * @param Class that extends from {@link UserMenuItem}
	 ***********************************************************************/
	public static void setDefaultUserMenuItem(Class<?> menuItemClass)  {
		
		if(UserMenuItem.class.isAssignableFrom(menuItemClass)) {
			defaultUserMenuItemClass = menuItemClass;
		}else {
			new CFWLog(logger).severe("Class is not a subclass of 'BootstrapMenu': "+menuItemClass.getName());
		}
	}
	
	/***********************************************************************
	 * Create a instance of the user menu.
	 * @return a UserMenuItem or null.
	 ***********************************************************************/
	public static UserMenuItem createUserMenuItemInstance(SessionData data)  {
		
		if(defaultUserMenuItemClass != null) {
			try {
				Constructor<?> constructor = defaultUserMenuItemClass.getConstructor(SessionData.class);
				Object menuItem = constructor.newInstance(data);
				
				if(menuItem instanceof UserMenuItem) {
					return (UserMenuItem)menuItem;
				}else {
					throw new InstantiationException("Class not an instance of UserMenuItem.");
				}
			} catch (Exception e) {
				new CFWLog(logger).severe("Issue creating instance for Class '"+defaultUserMenuItemClass.getSimpleName()+"': "+e.getMessage(), e);
				e.getCause().printStackTrace();
			}
		}
		
		return null;
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
				.append(dumpMenuItemHierarchy("", rootMenuItems))
				//.append(dumpMenuItemHierarchy("", userMenuItems))
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
