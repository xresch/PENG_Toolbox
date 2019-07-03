package com.pengtoolbox.pageanalyzer.response;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;



public class PageAnalyzerUserMenuItem extends UserMenuItem {

	public PageAnalyzerUserMenuItem(SessionData data) {
		super(data);

		User currentUser = CFW.Context.Request.getUser();
		
		if(!currentUser.isForeign()) {
			this.addChild(new MenuItem("Change Password").href("./changepassword"));
		}
		this.addChild(new MenuItem("Logout").href("./logout"));
		
	}


}
