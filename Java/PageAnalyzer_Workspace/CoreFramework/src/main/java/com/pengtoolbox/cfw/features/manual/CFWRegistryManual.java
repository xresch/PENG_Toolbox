package com.pengtoolbox.cfw.features.manual;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pengtoolbox.cfw.logging.CFWLog;


/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWRegistryManual {
	private static Logger logger = CFWLog.getLogger(CFWRegistryManual.class.getName());
	
	private static LinkedHashMap<String, ManualPage> manualPages = new LinkedHashMap<String, ManualPage>();
		
	/***********************************************************************
	 * Adds a menuItem to the regular menu.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void addManualPage(ManualPage item, String menuPath)  {
		addManualPage(manualPages, item, menuPath);
	}
	
	

	/***********************************************************************
	 * Adds a menuItem to one of the menus.
	 * Define the position of in the menu with the menuPath parameter. Use
	 * "|" to separate multiple menu labels.
	 * @param menuitem to add
	 * @param menuPath were the menu should be added, or null for root
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	private static void addManualPage(LinkedHashMap<String, ManualPage> targetPageList, ManualPage itemToAdd, String menuPath)  {
		//System.out.println("======= Path :"+menuPath+" ======== ");
		//-----------------------
		// Check Argument
		if(menuPath == null || menuPath.trim().length() == 0) {
			targetPageList.put(itemToAdd.getLabel(), itemToAdd);
			//System.out.println("Add "+item.getLabel());
			return;
		}
		
		//-----------------------
		// Handle Path
		String[] pathTokens = menuPath.split("\\Q|\\E");
		ManualPage parentItem = null;
		LinkedHashMap<String, ManualPage> currentSubPage = targetPageList;
		for(int i = 0; i < pathTokens.length; i++) {
			String currentToken = pathTokens[i].trim();
			
			//---------------------------
			// Handle Parent
			if(parentItem == null) {
				parentItem = targetPageList.get(currentToken);
				if(parentItem == null) {
					parentItem = new ManualPage(currentToken);
					targetPageList.put(currentToken, parentItem);
				}
			}else if(parentItem.getSubManualPages().containsKey(currentToken)) {
				parentItem = parentItem.getSubManualPages().get(currentToken);
			}
			if(i == pathTokens.length-1) {
				parentItem.addChild(itemToAdd);
				//System.out.println("add "+itemToAdd.getLabel()+" to subitems of: "+currentToken);
			}
		}
	}
	
	/***********************************************************************
	 * Returns the manual pages the user has permissions for.
	 ***********************************************************************/
	public static JsonArray getManualPagesForUserAsJSON()  {
		
		JsonArray pages = new JsonArray();

		for(ManualPage page : manualPages.values()) {
			JsonObject object = page.toJSONObjectForUser();
			if(object != null) {
				pages.add(object);
			}
		}

		return pages;
	}
	


	/*****************************************************************************
	 *  
	 *****************************************************************************/
	public static String dumpManualPageHierarchy() {
		return new StringBuilder()
				.append(dumpManualHierarchy("", manualPages))
				.toString();
	}
	
	/*****************************************************************************
	 * 
	 *****************************************************************************/
	public static String dumpManualHierarchy(String currentPrefix, LinkedHashMap<String, ManualPage> manualPages) {
		
		//-----------------------------------
		//Create Prefix
		StringBuilder builder = new StringBuilder();
		
		ManualPage[] items = manualPages.values().toArray(new ManualPage[]{});
		int objectCount = items.length;
		for(int i = 0; i < objectCount; i++) {
			ManualPage current = items[i];
			builder.append(currentPrefix)
				   .append("|--> ")
				   .append(current.getLabel()).append("\n");
			if(objectCount > 1 && (i != objectCount-1)) {
				builder.append(dumpManualHierarchy(currentPrefix+"|  ", current.getSubManualPages()));
			}else{
				builder.append(dumpManualHierarchy(currentPrefix+"  ", current.getSubManualPages()));
			}
		}
		
		return builder.toString();
	}
	

}
