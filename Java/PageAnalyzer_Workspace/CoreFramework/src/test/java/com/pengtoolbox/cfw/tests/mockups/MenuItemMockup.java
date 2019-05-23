package com.pengtoolbox.cfw.tests.mockups;

import com.pengtoolbox.cfw.response.bootstrap.MenuItem;

public class MenuItemMockup extends MenuItem {

	public MenuItemMockup(String label) {
		super(label);
		
		MenuItem subDropdown = new MenuItem("Sub Dropdown").cssClass("dropdownClass");
		subDropdown.addChild(new MenuItem("Sub Subitem 1"))
				   .addChild(new MenuItem("Sub Subitem 2"));
		
		this.addChild(new MenuItem("href Subitem").href("./test/servlet"))
			.addChild(new MenuItem("onclick Subitem").href(null).onclick("draw('test');"))
			.addChild(new MenuItem("cssClass Subitem").cssClass("mockup-class test-class"))
			.addChild(subDropdown);
	}

}
