package com.pengtoolbox.cfw._main;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.BTFooter;
import com.pengtoolbox.cfw.response.bootstrap.BTMenu;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;


/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWRegistryComponents {
	private static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
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

}
