package com.pengtoolbox.cfw.response;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;

public abstract class AbstractTemplateHTML extends AbstractTemplate {

	protected String pageTitle;
	
	protected StringBuffer head = new StringBuffer();
	protected StringBuffer menu = new StringBuffer();
	protected StringBuffer messages = new StringBuffer();
	protected StringBuffer footer = new StringBuffer();
	protected StringBuffer supportInfo = new StringBuffer();
	protected StringBuffer javascript = new StringBuffer();
	protected StringBuffer javascriptData = new StringBuffer();
	
	public AbstractTemplateHTML(HttpServletRequest request){
		super(request);
		
		String requestID = (String)request.getAttribute(CFW.REQUEST_ATTR_ID);
		long startNanos = (long)request.getAttribute(CFW.REQUEST_ATTR_STARTNANOS);
		
		this.addJavascriptData(CFW.REQUEST_ATTR_ID, requestID );
		this.addJavascriptData(CFW.REQUEST_ATTR_STARTNANOS, String.valueOf(startNanos) );
		
		this.addSupportInfo("Timestamp:", CFW.currentTimestamp());
		this.addSupportInfo("RequestID:", requestID);
		this.addSupportInfo("SessionID:", (String)this.request.getSession().getId());
		
	}
	
	//##############################################################################
	// Class Methods
	//##############################################################################
	
	public void addJSFileHead(String path){
		
		this.head.append("<script src=\"");
		this.head.append(path);
		this.head.append("\"></script>\n");

	}
	
	public void addJSFileBottom(String path){
		
		this.javascript.append("<script src=\"");
		this.javascript.append(path);
		this.javascript.append("\"></script>\n");

	}
	
	public void addJavascriptData(String key, String value){
		
		this.javascriptData.append("<p id=\"jsdata_");
		this.javascriptData.append(key);
		this.javascriptData.append("\">");
		this.javascriptData.append(value);
		this.javascriptData.append("</p>\n");

	}
	
	public void addCSSFile(String path){
		
		//"<link href="/css/custom.css" rel="stylesheet" />"
		this.head.append("<link href=\"");
		this.head.append(path);
		this.head.append("\" rel=\"stylesheet\" />\n");
	}
	
	public void addSupportInfo(String key, String value){
	
		this.supportInfo.append("<div class=\"row\">");
		
			this.supportInfo.append("<div class=\"col-md-3\"><label>");
			this.supportInfo.append(key);
			this.supportInfo.append("</label></div>");
			
			this.supportInfo.append("<div class=\"col-md-9\">");
			this.supportInfo.append(value);
			this.supportInfo.append("</div>");
			
		this.supportInfo.append("</div>");
	}
	
	/****************************************************************
	 * Adds a message to the message div of the template.
	 * Use the "ALERT_*" keys from OMKeys class for the alertType.
	 *   
	 * @param alertType alert type from OMKeys
	 *   
	 ****************************************************************/
	public void addAlert(int alertType, String message){
		
//		<div class=\"alert alert-success\" role=\"alert\">...</div>

		String clazz = "";
		switch(alertType){
			
			case CFW.ALERT_SUCCESS: 	clazz = "alert-success"; break;
			case CFW.ALERT_INFO: 	clazz = "alert-info"; break;
			case CFW.ALERT_WARN: 	clazz = "alert-warning"; break;
			case CFW.ALERT_ERROR: 	clazz = "alert-danger"; break;
			default:	 			clazz = "alert-info"; break;
			
		}
		
		this.messages.append("<div class=\"alert alert-dismissible ").append(clazz).append("\" role=\"alert\">");
		this.messages.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>");
		this.messages.append(message);
		this.messages.append("</div>\n");

	}
	
	protected void appendSectionTitle(StringBuffer buildedPage, String title){ 
		buildedPage.append("<!--\n");
		buildedPage.append("==================================================\n");
		buildedPage.append(title);
		buildedPage.append("\n==================================================\n");
		buildedPage.append("-->\n");
	}
	
	@Override
	public int getEstimatedSizeChars() {
		int size = this.head.length();
		size += this.menu.length();
		size += this.messages.length();
		size += this.content.length();
		size += this.footer.length();
		size += this.javascript.length();
		size += this.supportInfo.length();
		
		return size;
	}
	
	//##############################################################################
	// Getters
	//##############################################################################
	public String getPageTitle() { return pageTitle; }
	public StringBuffer getHead() { return head; }
	public StringBuffer getMenu() { return menu; }
	public StringBuffer getMessages() { return messages; }
	public StringBuffer getFooter() {return footer;}
	public StringBuffer getJavascript() {return javascript;}
	public StringBuffer getSupportInfo() {return supportInfo;}

	//##############################################################################
	// Setters
	//##############################################################################
	public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }
	public void setHead(StringBuffer head) { this.head = head; }
	public void setMenu(StringBuffer menu) { this.menu = menu; }
	public void setMessages(StringBuffer messages) { this.messages = messages; }
	public void setFooter(StringBuffer footer) {this.footer = footer;}
	public void setJavascript(StringBuffer javascript) {this.javascript = javascript;}
	public void setSupportInfo(StringBuffer comments) {this.supportInfo = comments;}
	
	

}
