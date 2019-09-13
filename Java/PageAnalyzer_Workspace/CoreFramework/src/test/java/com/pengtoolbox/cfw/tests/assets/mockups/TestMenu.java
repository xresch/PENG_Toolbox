package com.pengtoolbox.cfw.tests.assets.mockups;

import com.pengtoolbox.cfw.response.bootstrap.BTMenu;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;

public class TestMenu extends BTMenu {
	
	public TestMenu() {
		this.setLabel("TEST MENU");
		
		this.addChild(
			new MenuItem("Dropdown")
				.cssClass("some-class")
				.addChild(new MenuItem("General Tests").href("./general"))
				.addChild(new MenuItem("Form Tests").href("./form"))
			);
	}

}
