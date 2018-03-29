package com.pageanalyzer.response;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pageanalyzer._main.PA;
import com.pageanalyzer.logging.PALogger;

public class TemplateHTMLDefault extends AbstractTemplateHTML {
	
	public static Logger logger = PALogger.getLogger(TemplateHTMLDefault.class.getName());
	
	public TemplateHTMLDefault(HttpServletRequest request, String pageTitle){
		
		super(request);
		
		this.pageTitle = pageTitle;
		
		this.addCSSFile("/css/bootstrap.min.css");
		this.addCSSFile("/css/bootstrap-theme.css");
		this.addCSSFile("/css/font-awesome.css");
		this.addCSSFile("/css/custom.css");
		
		this.addJSFileBottom("/js/jquery-2.2.3.js");
		//this.addJSFileBottom("/js/jquery-ui.js");
		this.addJSFileBottom("/js/custom/custom.js");
		this.addJSFileBottom("/js/bootstrap.js");
		
	}
		
	@Override
	public StringBuffer buildResponse() {
		
		StringBuffer buildedPage = new StringBuffer();
		
		buildedPage.append("<html>\n");
		
			buildedPage.append("<head>\n");
				buildedPage.append("<title>").append(this.pageTitle).append("</title>");
				buildedPage.append(head);
			buildedPage.append("</head>\n");
			
			buildedPage.append("<body ng-app=\"omApp\">\n");
			
				//--------------------------
				// Menubar
				
				this.appendSectionTitle(buildedPage, "Menubar");
				buildedPage.append("");
				buildedPage.append(this.menu);
				
				String menuTemplate = PA.getFileContent(request,PA.PATH_TEMPLATE_MENU);
				if(menuTemplate != null){
					String customMenuInserted = menuTemplate.replace("{!customMenu!}", this.menu);	
					buildedPage.append(customMenuInserted);
				}else{
					buildedPage.append("<!-- FAILED TO LOAD MENU! -->");
				}
				
				//--------------------------
				// Messages
				this.appendSectionTitle(buildedPage, "Messages");
				buildedPage.append("<div id=\"messages\">");
				buildedPage.append(this.messages);
				buildedPage.append("</div>");
				
				//--------------------------
				// Content
				this.appendSectionTitle(buildedPage, "Content");
				buildedPage.append("<div id=\"pa-content\" class=\"container\">");
				buildedPage.append(this.content);
				buildedPage.append("</div>");
				
				//--------------------------
				// Footer
				this.appendSectionTitle(buildedPage, "Footer");

				String footerTemplate = PA.getFileContent(request,PA.PATH_TEMPLATE_FOOTER);
				if(footerTemplate != null){
					String customFooterInserted = footerTemplate.replace("{!customFooter!}", this.footer);	
					buildedPage.append(customFooterInserted);
				}else{
					buildedPage.append("<!-- FAILED TO LOAD FOOTER! -->");
				}

				//--------------------------
				// Javascript
				this.appendSectionTitle(buildedPage, "Javascript");
				buildedPage.append("<div id=\"javascripts\">");
				buildedPage.append(this.javascript);
				buildedPage.append("</div>");
				

				//--------------------------
				// JavascriptData
				this.appendSectionTitle(buildedPage, "Javascript Data");
				buildedPage.append("<div id=\"javaScriptData\" style=\"display: none;\">");
				buildedPage.append(this.javascriptData);
				buildedPage.append("</div>");
				//--------------------------
				// Support Info
				this.appendSectionTitle(buildedPage, "Support Info");
				
				String supportInfoTemplate = PA.getFileContent(request,PA.PATH_TEMPLATE_SUPPORTINFO);
				
				if(supportInfoTemplate != null){
					String supportInfoInserted = supportInfoTemplate.replace("{!supportInfo!}", this.supportInfo);	
					buildedPage.append(supportInfoInserted);
				}else{
					buildedPage.append("<!-- FAILED TO LOAD SUPPORT INFO! -->");
				}
					
				
				
			buildedPage.append("</body>\n");
			
		buildedPage.append("</html>");
		
		return buildedPage;
	}

}
