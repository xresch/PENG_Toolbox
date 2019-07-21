package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class BTInputText extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private String fieldID = "&nbsp;";
	
	public BTInputText(String label, String fieldID) {
		this.label = label;
		this.fieldID = fieldID;
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected void createHTML(StringBuilder html) {

		html.append("<div class=\"form-group\">");
		html.append("<for=\""+fieldID+"\" >"+label+":</label> ");
		html.append("<input type=\"text\" placeholder=\""+label+"\" class=\"form-control\" name=\""+fieldID+"\" id=\""+fieldID+"\" />");
		
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

	public BTInputText setLabel(String label) {
		fireChange();
		this.label = label;
		return this;
	}
	
}
