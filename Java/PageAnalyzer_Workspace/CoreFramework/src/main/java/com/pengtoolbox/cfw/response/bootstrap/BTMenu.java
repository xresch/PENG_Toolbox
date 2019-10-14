package com.pengtoolbox.cfw.response.bootstrap;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.config.Configuration;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class BTMenu extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private UserMenuItem userMenuItem = null;
	
	public BTMenu() {
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected void createHTML(StringBuilder html) {
		
		html.append("\n<div id=\"menubar\">");
		
		html.append("<nav class=\"navbar navbar-expand-md fixed-top navbar-dark\">");
		html.append("  <a class=\"navbar-brand\" href=\"#\">");
		
			String logopath = CFW.DB.Config.getConfigAsString(Configuration.LOGO_PATH);
			if(logopath != null && !logopath.isEmpty()) {
				html.append("<img id=\"cfw-logo\" src=\""+logopath+"\" />");
			}
			
		html.append(this.label+"</a>");
		html.append("  <button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#cfw-navbar-top\" aria-controls=\"cfw-navbar-top\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">");
		html.append("    <span class=\"navbar-toggler-icon\"></span>");
		html.append("  </button>");

		html.append("  <div class=\"collapse navbar-collapse\" id=\"cfw-navbar-top\">");
		html.append("    <ul class=\"navbar-nav mr-auto\">");
		

		if(this.hasChildren()) {
				
			for(HierarchicalHTMLItem child : children) {
				html.append("\t"+child.getHTML());
			}
		}
		
		if(this.hasOneTimeChildren()) {
			
			for(HierarchicalHTMLItem child : oneTimeChildren) {
				html.append("\t"+child.getHTML());
			}
		}
		html.append("\n</ul>");
		
		if(this.userMenuItem != null) {	
			html.append("\n<ul class=\"nav navbar-nav navbar-right\">");
			html.append(userMenuItem.getHTML());
			html.append("\n</ul>");
		}
		
		html.append("\n</div></div></nav></div>");
	}

	public String getLabel() {
		return label;
	}

	public BTMenu setLabel(String label) {
		fireChange();
		this.label = label;
		return this;
	}

	public UserMenuItem getUserMenuItem() {
		return userMenuItem;
	}

	public BTMenu setUserMenuItem(UserMenuItem userMenuItem) {
		fireChange();
		this.userMenuItem = userMenuItem;
		return this;
	}
	
	

}
