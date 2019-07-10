package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class BTFooter extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private UserMenuItem userMenuItem = null;
	
	public BTFooter() {
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public void createHTML(StringBuilder html) {

		html.append("<div id=\"cfw-footer\" class=\"flex-default flex-column\">");
		
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
		
		html.append("</div>");
	}

	public String getLabel() {
		return label;
	}

	public BTFooter setLabel(String label) {
		fireChange();
		this.label = label;
		return this;
	}

	public UserMenuItem getUserMenuItem() {
		return userMenuItem;
	}

	public BTFooter setUserMenuItem(UserMenuItem userMenuItem) {
		fireChange();
		this.userMenuItem = userMenuItem;
		return this;
	}
	
}
