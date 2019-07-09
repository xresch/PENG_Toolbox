package com.pengtoolbox.cfw.tests.assets.mockups;

import com.pengtoolbox.cfw.response.bootstrap.BootstrapMenu;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;

public class TestMenu extends BootstrapMenu {
	
	public TestMenu() {
		this.setLabel("TEST MENU");
		
		this.addChild(
			new MenuItem("Dropdown")
				.cssClass("some-class")
				.addChild(new MenuItem("TestPage").href("./test/testpage")
			)
		);
		
	}

}
