package com.pengtoolbox.cfw.response.bootstrap;

import java.util.ArrayList;


public abstract class HierarchicalHTMLItem {
	
	protected ArrayList<HierarchicalHTMLItem> children = new ArrayList<HierarchicalHTMLItem> ();
	protected ArrayList<HierarchicalHTMLItem> oneTimeChildren = new ArrayList<HierarchicalHTMLItem> ();
	protected boolean hasChanged = true;
	protected StringBuilder html = null;
	
	protected HierarchicalHTMLItem parent = null;
	
	/***********************************************************************************
	 * Create the HTML representation of this item.
	 * Use the method getHTML to retrieve the html.
	 * When implementing this method, make sure that any changes to fields used to create
	 * the html trigger {@link #fireChange()}.
	 * @return String html for this item. 
	 ***********************************************************************************/
	protected abstract void createHTML(StringBuilder builder);
	
	/***********************************************************************************
	 * Get the HTML representation of this item.
	 * If the HTML was already created and no changes occure, this method will read it
	 * from memory.
	 * 
	 * @return String html for this item. 
	 ***********************************************************************************/
	public String getHTML() {
		
		if(hasChanged || html == null) {
			html = new StringBuilder();
			createHTML(html);
			hasChanged = false;
		}
		
		//-----------------------------------------
		// Reset oneTimeChildren
		if(oneTimeChildren.size() > 0) {
			oneTimeChildren = new ArrayList<HierarchicalHTMLItem> ();
			hasChanged = true;
		}
		
		return html.toString();
	}
	
	protected void fireChange() {
		hasChanged = true;
		if(parent != null) parent.fireChange();
	}
	
	public HierarchicalHTMLItem getParent() {
		return parent;
	}

	public void setParent(HierarchicalHTMLItem parent) {
		this.parent = parent;
	}
	
	public HierarchicalHTMLItem setChildren(ArrayList<HierarchicalHTMLItem> children) {
		fireChange();
		this.children = children;
		return this;
	}
	
	public ArrayList<HierarchicalHTMLItem> getChildren() {
		return children;
	}

	public HierarchicalHTMLItem addChild(HierarchicalHTMLItem childItem) {
		fireChange();
		childItem.setParent(this);
		this.children.add(childItem);
		return this;
	}
	
	public boolean removeChild(MenuItem childItem) {
		fireChange();
		return this.children.remove(childItem);
	}
	
	public boolean containsChild(MenuItem childItem) {
		return children.contains(childItem);
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	
	
	public HierarchicalHTMLItem setOneTimeChildren(ArrayList<HierarchicalHTMLItem> children) {
		fireChange();
		this.oneTimeChildren = children;
		return this;
	}
	
	public ArrayList<HierarchicalHTMLItem> getOneTimeChildren() {
		return oneTimeChildren;
	}

	public HierarchicalHTMLItem addOneTimeChild(HierarchicalHTMLItem childItem) {
		fireChange();
		childItem.setParent(this);
		this.oneTimeChildren.add(childItem);
		return this;
	}
	
	public boolean removeOneTimeChild(MenuItem childItem) {
		fireChange();
		return this.oneTimeChildren.remove(childItem);
	}
	
	public boolean containsOneTimeChild(MenuItem childItem) {
		return oneTimeChildren.contains(childItem);
	}
	
	public boolean hasOneTimeChildren() {
		return !oneTimeChildren.isEmpty();
	}
	
}
