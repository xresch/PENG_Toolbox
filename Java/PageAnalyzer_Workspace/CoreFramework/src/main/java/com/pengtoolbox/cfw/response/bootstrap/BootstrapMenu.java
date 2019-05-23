package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class BootstrapMenu extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private UserMenuItem userMenuItem = null;
	
	public BootstrapMenu() {
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public void createHTML(StringBuilder html) {

		html.append("\n<div id=\"menubar\">");
		html.append("\n<nav class=\"navbar navbar-inverse navbar-fixed-top\">");
		html.append("\n	  <div class=\"container\">");
		html.append("\n		<div class=\"navbar-header\">");
		html.append("\n		  <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\">");
		html.append("\n			<span class=\"sr-only\">Toggle navigation</span>");
		html.append("\n			<span class=\"icon-bar\"></span>");
		html.append("\n			<span class=\"icon-bar\"></span>");
		html.append("\n			<span class=\"icon-bar\"></span>");
		html.append("\n		  </button>");
		html.append("\n		  <a class=\"navbar-brand\" href=\"#\">"+this.label+"</a>");
		html.append("\n		</div>");
		html.append("\n	  ");
		html.append("\n		<div class=\"collapse navbar-collapse\" id=\"navbarsDefault\">");
		html.append("\n			<ul class=\"nav navbar-nav\">");
		
		
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

	public BootstrapMenu setLabel(String label) {
		fireChange();
		this.label = label;
		return this;
	}

	public UserMenuItem getUserMenuItem() {
		return userMenuItem;
	}

	public BootstrapMenu setUserMenuItem(UserMenuItem userMenuItem) {
		fireChange();
		this.userMenuItem = userMenuItem;
		return this;
	}
	
	

}
