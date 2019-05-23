package com.pengtoolbox.pageanalyzer.response;

import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;



public class PageAnalyzerUserMenuItem extends UserMenuItem {

	public PageAnalyzerUserMenuItem(SessionData data) {
		super(data);
		this.addChild(new MenuItem("Logout").href("./logout"));
	}


}
