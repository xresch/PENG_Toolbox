package com.pengtoolbox.cfw.response;

import java.util.ArrayList;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileAssembly;
import com.pengtoolbox.cfw.caching.FileAssembly.HandlingType;
import com.pengtoolbox.cfw.caching.FileDefinition;

public abstract class AbstractHTMLResponse extends AbstractResponse {

	protected String pageTitle;
	
	protected FileAssembly headjs = new FileAssembly("js_assembly_jshead", "js");
	protected FileAssembly bottomjs = new FileAssembly("js_assembly_bottom", "js");
	protected FileAssembly assemblyCSS = new FileAssembly("css_assembly", "css");
	protected ArrayList<FileDefinition> singleJSBottom = new ArrayList<FileDefinition>();
	
	protected StringBuffer head = new StringBuffer();
	protected StringBuffer menu = new StringBuffer();
	protected StringBuffer footer = new StringBuffer();
	protected StringBuffer supportInfo = new StringBuffer();
	protected StringBuffer javascript = new StringBuffer();
	protected StringBuffer javascriptData = new StringBuffer();
	
	public AbstractHTMLResponse(){
		super();
		
		String requestID = (String)request.getAttribute(CFW.REQUEST_ATTR_ID);
		long startNanos = (long)request.getAttribute(CFW.REQUEST_ATTR_STARTNANOS);
		
		this.addJavascriptData(CFW.REQUEST_ATTR_ID, requestID );
		this.addJavascriptData(CFW.REQUEST_ATTR_STARTNANOS, String.valueOf(startNanos) );
		
		this.addSupportInfo("Timestamp:", CFW.Time.currentTimestamp());
		this.addSupportInfo("RequestID:", requestID);
		this.addSupportInfo("SessionID:", (String)this.request.getSession().getId());
		
	}
	
	//##############################################################################
	// Class Methods
	//##############################################################################
	
	public void addJSFileHeadAssembly(HandlingType type, String path, String filename){
		headjs.addFile(type, path, filename);
	}
	
	public void addJSFileBottomAssembly(HandlingType type, String path, String filename){
		bottomjs.addFile(type, path, filename);
	}
	
	public void addJSFileBottomSingle(HandlingType type, String path, String filename){
		singleJSBottom.add(new FileDefinition(type, path, filename));
	}
	
	/***************************************************************************
	 * Adds the javascript code to the bottom of the page.
	 * @param javascript
	 ***************************************************************************/
	public void addJavascriptCode(String javascript) {
		singleJSBottom.add(new FileDefinition(javascript));
	}
	
	public void addJavascriptData(String key, String value){
		
		this.javascriptData.append("<p id=\"jsdata_");
		this.javascriptData.append(key);
		this.javascriptData.append("\">");
		this.javascriptData.append(value);
		this.javascriptData.append("</p>\n");

	}
	
	public void addCSSFile(HandlingType type, String path, String filename){
		assemblyCSS.addFile(type, path, filename);
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
	public StringBuffer getFooter() {return footer;}
	public StringBuffer getJavascript() {return javascript;}
	public StringBuffer getSupportInfo() {return supportInfo;}

	//##############################################################################
	// Setters
	//##############################################################################
	public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }
	public void setHead(StringBuffer head) { this.head = head; }
	public void setMenu(StringBuffer menu) { this.menu = menu; }
	public void setFooter(StringBuffer footer) {this.footer = footer;}
	public void setSupportInfo(StringBuffer comments) {this.supportInfo = comments;}
	
	

}
