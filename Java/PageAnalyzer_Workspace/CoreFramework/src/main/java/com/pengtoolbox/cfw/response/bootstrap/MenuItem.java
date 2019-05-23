package com.pengtoolbox.cfw.response.bootstrap;

/**********************************************************************************
 * Class for creating a menu for the web application.
 * @author Reto Scheiwiller
 * 
 **********************************************************************************/
public class MenuItem extends HierarchicalHTMLItem {
	
	private String label = "&nbsp;";
	private String href = "#";
	private String cssClass = "";
	private String onclick = null;
	
	public MenuItem(String label) {
		this.label = label;
	}
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * @return String html for this item. 
	 ***********************************************************************************/
	public void createHTML(StringBuilder html) {
		
		if(!this.hasChildren() && !this.hasOneTimeChildren()) {
			String hrefString = (href == null) ? "" : "href=\""+href+"\"";
			String onclickString = (onclick == null) ? "" : "onclick=\""+onclick+"\"";
			html.append("\n<li class=\"nav-item "+cssClass+"\">");
			html.append("<a "+hrefString+" "+onclickString+">"+label+"</a></li>");   
		}else {
			
			String hrefString = (href == null) ? "" : "href=\""+href+"\"";
			String onclickString = (onclick == null) ? "" : "onclick=\""+onclick+"\"";
			html.append("\n<li class=\"dropdown "+cssClass+"\">");
			html.append("\n<a "+hrefString+" "+onclickString+" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">"+label+"<span class=\"caret\"></span></a>");   
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

	public String getHref() {
		return href;
	}

	public MenuItem href(String href) {
		fireChange();
		this.href = href;
		return this;
	}

	/***********************************************************************************
	 * Get the CSS Class attribute.
	 * @return String 
	 ***********************************************************************************/
	public String getCSSClass() {
		return cssClass;
	}

	/***********************************************************************************
	 * Set the CSS Class attribute.
	 * @param cssClass the css classes you want to set on this item.
	 ***********************************************************************************/
	public MenuItem cssClass(String cssClass) {
		fireChange();
		this.cssClass = cssClass;
		return this;
	}

	public String getOnclick() {
		return onclick;
	}

	public MenuItem onclick(String onclick) {
		fireChange();
		this.onclick = onclick;
		return this;
	}


	
	

	
	
	

}
