package com.pengtoolbox.pageanalyzer.response;

import java.util.HashMap;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFW.DB.Permissions;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;
import com.pengtoolbox.pageanalyzer.db.PAPermissions;



public class PageAnalyzerUserMenuItem extends UserMenuItem {

	public PageAnalyzerUserMenuItem(SessionData data) {
		super(data);

		User currentUser = CFW.Context.Request.getUser();
		HashMap<String, Permission> permissions = CFW.Context.Request.getUserPermissions();
		
		if(permissions.containsKey(PAPermissions.MANAGE_RESULTS)) {
			this.addChild(new MenuItem("Manage Results").href("./manageresults"));
		}
		
		if(!currentUser.isForeign()) {
			this.addChild(new MenuItem("Change Password").href("./changepassword"));
		}
		

		this.addChild(new MenuItem("Logout").href("./logout"));
		
	}

}
