package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class MenuItem extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	
	public MenuItem(String label) {
		this.label = label;
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public void createHTML(StringBuilder html) {
		
		String cssClass = this.popAttributeValue("class");
		
		if(!this.hasChildren() && !this.hasOneTimeChildren()) {
			html.append("\n<li class=\"nav-item "+cssClass+"\">");
			html.append("<a "+this.getAttributesString()+">"+label+"</a></li>");   
		}else {
			
			html.append("\n<li class=\"dropdown "+cssClass+"\">");
			html.append("\n<a "+this.getAttributesString()+" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">"+label+"<span class=\"caret\"></span></a>");   
			html.append("\n<ul class=\"dropdown-menu\">");
			
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



	
	

	
	
	

}
