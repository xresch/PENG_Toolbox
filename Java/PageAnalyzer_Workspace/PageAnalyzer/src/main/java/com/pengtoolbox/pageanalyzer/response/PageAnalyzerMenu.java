package com.pengtoolbox.pageanalyzer.response;

import com.pengtoolbox.cfw.response.bootstrap.BootstrapMenu;
import com.pengtoolbox.cfw.response.bootstrap.MenuItem;



public class PageAnalyzerMenu extends BootstrapMenu {

	public PageAnalyzerMenu() {
		
		this.setLabel("PAGE ANALYZER");
		
		this.addChild(new MenuItem("HAR Upload").href("./harupload"))
		.addChild(new MenuItem("Analyze URL").href("./analyzeurl"))
		.addChild(new MenuItem("History").href("./resultlist"))
		.addChild(new MenuItem("Docu").href("./docu"))
		.addChild(
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

				)
		.addChild(
				new MenuItem("Chart")
					.cssClass("result-view-tabs")
					.addChild(new MenuItem("Gantt Chart").onclick("draw({data: 'har', info: 'ganttchart', view: ''})"))
		);
		
	}

}
