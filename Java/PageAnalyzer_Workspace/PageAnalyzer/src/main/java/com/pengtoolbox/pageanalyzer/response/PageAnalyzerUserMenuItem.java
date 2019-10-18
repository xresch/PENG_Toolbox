package com.pengtoolbox.pageanalyzer.response;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;
import com.pengtoolbox.pageanalyzer.db.PAPermissions;


/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class PageAnalyzerUserMenuItem extends UserMenuItem {

	public PageAnalyzerUserMenuItem(SessionData data) {
		super(data);

		User currentUser = CFW.Context.Request.getUser();

		if(CFW.Context.Request.hasPermission(PAPermissions.MANAGE_RESULTS)) {
			this.addChild(new MenuItem("Manage Results").href("./manageresults"));
		}
		
		if(CFW.Context.Request.hasPermission(Permission.CFW_CONFIG_MANAGEMENT)) {
			this.addChild(new MenuItem("Manage Configuration").href("./configuration"));
		}
		
		if(CFW.Context.Request.hasPermission(Permission.CFW_USER_MANAGEMENT)) {
			this.addChild(new MenuItem("Manage Users").href("./usermanagement"));
		}
		
		if(CFW.Context.Request.hasPermission(Permission.CFW_API)) {
			this.addChild(new MenuItem("API").href("./api"));
		}
		
		if(!currentUser.isForeign()) {
			this.addChild(new MenuItem("Change Password").href("./changepassword"));
		}
		

		this.addChild(new MenuItem("Logout").href("./logout"));
		
	}

}
