package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class MenuItem extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private String alignRightClass = "";
	
	public MenuItem(String label) {
		this.label = label;
		this.addAttribute("href", "#");
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public void createHTML(StringBuilder html) {
		
		String cssClass = this.popAttributeValue("class");
		
//		html.append("      <li class=\"nav-item dropdown\">");
//		html.append("        <a class=\"nav-link dropdown-toggle\" href=\"#\" id=\"dropdown01\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">Dropdown</a>");
//		html.append("        <div class=\"dropdown-menu\" aria-labelledby=\"dropdown01\">");
//		html.append("          <a class=\"dropdown-item\" href=\"#\">Action</a>");
//		html.append("          <a class=\"dropdown-item\" href=\"#\">Another action</a>");
//		html.append("          <a class=\"dropdown-item\" href=\"#\">Something else here</a>");
//		html.append("        </div>");
//		html.append("      </li>");
		
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

	public String getLabel() {
		return label;
	}

	public MenuItem setLabel(String label) {
		fireChange();
		this.label = label;
		return this;
	}
	
	public HierarchicalHTMLItem href(String href) {
		return addAttribute("href", href);
	}

	public void alignDropdownRight(boolean alignRight) {
		if(alignRight) {
			alignRightClass = "dropdown-menu-right";
		}else {
			alignRightClass = "";
		}
	}

	
	

	
	
	

}
