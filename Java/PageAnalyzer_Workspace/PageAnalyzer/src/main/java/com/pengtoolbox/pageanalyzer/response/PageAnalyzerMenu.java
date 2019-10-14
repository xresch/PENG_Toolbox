package com.pengtoolbox.pageanalyzer.response;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.db.config.Configuration;
import com.pengtoolbox.cfw.response.bootstrap.BTMenu;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;
import com.pengtoolbox.pageanalyzer.db.PAPermissions;



public class PageAnalyzerMenu extends BTMenu {

	public PageAnalyzerMenu() {
		
		String name = CFW.DB.Config.getConfigAsString(Configuration.MENU_TITLE);
		if(name == null) {
			name = "";
		}
		this.setLabel(name);
		SessionData session = CFW.Context.Request.getSessionData();
		if( CFW.Properties.AUTHENTICATION_ENABLED == false ||
			( session != null && session.isLoggedIn()) ) {
			
			if(CFW.Context.Request.hasPermission(PAPermissions.ANALYZE_HAR)) {
				this.addChild(new MenuItem("HAR Upload").href("./harupload"));
			}
			
			if(CFW.Context.Request.hasPermission(PAPermissions.ANALYZE_URL)) {
				this.addChild(new MenuItem("Analyze URL").href("./analyzeurl"));
			}
			
			if(CFW.Context.Request.hasPermission(PAPermissions.VIEW_HISTORY)) {
				this.addChild(new MenuItem("History").href("./resultlist"));
			}
			
			if(CFW.Context.Request.hasPermission(PAPermissions.VIEW_DOCU)) {
				this.addChild(new MenuItem("Docu").href("./docu"));
			}
			
			this.addChild(
				new MenuItem("Summary")
					.cssClass("result-view-tabs")
					.onclick("draw({data: 'yslowresult', info: 'overview', view: ''})")
				)
			.addChild(
					new MenuItem("Grade")
						.cssClass("result-view-tabs")
						.addChild(new MenuItem("Panels")		.onclick("draw({data: 'yslowresult', info: 'grade', view: 'panels'})"))
						.addChild(new MenuItem("Table")			.onclick("draw({data: 'yslowresult', info: 'grade', view: 'table'})"))
						.addChild(new MenuItem("Plain Text")	.onclick("draw({data: 'yslowresult', info: 'grade', view: 'plaintext'})"))
						.addChild(new MenuItem("JIRA Ticket")	.onclick("draw({data: 'yslowresult', info: 'grade', view: 'jira'})"))
						.addChild(new MenuItem("CSV")			.onclick("draw({data: 'yslowresult', info: 'grade', view: 'csv'})"))
						.addChild(new MenuItem("JSON")			.onclick("draw({data: 'yslowresult', info: 'grade', view: 'json'})"))
	
					)
			.addChild(
					new MenuItem("Statistics")
						.cssClass("result-view-tabs")
						.addChild(new MenuItem("Table: Statistics by Type")						.onclick("draw({data: 'yslowresult', info: 'stats', view: 'table', stats: 'type'})"))
						.addChild(new MenuItem("Table: Statistics by Type with primed Cache")	.onclick("draw({data: 'yslowresult', info: 'stats', view: 'table', stats: 'type_cached'})"))
						.addChild(new MenuItem("Table: Components")								.onclick("draw({data: 'yslowresult', info: 'stats', view: 'table', stats: 'components'})"))
	
					);
//			.addChild(
//					new MenuItem("Chart")
//						.cssClass("result-view-tabs")
//						.addChild(new MenuItem("Gantt Chart").onclick("draw({data: 'har', info: 'ganttchart', view: ''})"))
//			);
		}
	}

}
