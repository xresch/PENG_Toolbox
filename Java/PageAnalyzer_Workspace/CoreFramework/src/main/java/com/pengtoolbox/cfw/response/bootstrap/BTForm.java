package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class BTForm extends HierarchicalHTMLItem {
	
	private String title = "";
	private String submitLabel = "";
	public BTForm(String title, String submitLabel) {
		this.title = title;
		this.submitLabel = submitLabel;
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected void createHTML(StringBuilder html) {

		html.append("<form id=\"cfw-form\" class=\"form\" method=\"post\">");
		
		if(this.hasChildren()) {
				
			for(HierarchicalHTMLItem child : children) {
				html.append("\n\t"+child.getHTML());
			}
		}
		
		if(this.hasOneTimeChildren()) {
			
			for(HierarchicalHTMLItem child : oneTimeChildren) {
				html.append("\n\t"+child.getHTML());
			}
		}
		
		html.append("<input type=\"submit\" class=\"form-control\" value=\"Sign In\">");
		html.append("</form>");
	}

	public String getLabel() {
		return title;
	}

	public BTForm setLabel(String label) {
		fireChange();
		this.title = label;
		return this;
	}

}
