package com.pengtoolbox.pageanalyzer.servlets;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.TemplateHTMLDefault;
import com.pengtoolbox.cfw.response.PlaintextResponse;
import com.pengtoolbox.cfw.utils.CFWFiles;
import com.pengtoolbox.pageanalyzer.db.PageAnalyzerDB;
import com.pengtoolbox.pageanalyzer.yslow.YSlow;

/*************************************************************************
 * 
 * @author Reto Scheiwiller, 2018
 * 
 * Distributed under the MIT license
 *************************************************************************/
public class DataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogManager().getLogger(DataServlet.class.getName());
       
	/*****************************************************************
	 *
	 ******************************************************************/
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//-------------------------------------------
		// Initialize
		//-------------------------------------------
		CFWLog log = new CFWLog(logger).method("doGet");
		log.info(request.getRequestURL().toString());
		
		String type = request.getParameter("type");
		String resultID = request.getParameter("resultid");

		//-------------------------------------------
		// Resolve User ID
		//-------------------------------------------
		String username = "";
		
		if(CFWConfig.AUTHENTICATION_ENABLED) {
			SessionData data = CFW.Context.Request.getSessionData(); 
			if(data.isLoggedIn()) {
				username = data.getUser().username();
			}
		}else {
			username = "anonymous";
		}
		
		//-------------------------------------------
		// Fetch Data
		//-------------------------------------------
		PlaintextResponse plain = new PlaintextResponse();
		StringBuffer content = plain.getContent();

		if (type == null) {
			content.append("{\"error\": \"Type was not specified.\"}");
		}else {

			switch(type.toLowerCase()) {
				case "yslowresult": 	content.append(PageAnalyzerDB.getResultByID(Integer.parseInt(resultID)));
										break;
										
				case "resultlist": 		content.append(PageAnalyzerDB.getResultListForUser(username));
										break;
				
				case "allresults": 		content.append(PageAnalyzerDB.getAllResults());
										break;
										
				case "har": 			content.append(PageAnalyzerDB.getHARFileByID(Integer.parseInt(resultID)));
										break;
				
				case "compareyslow": 	String resultIDs = request.getParameter("resultids");
										content.append(PageAnalyzerDB.getResultListForComparison(resultIDs));
										break;
										
			}
						
		}
		
		response.setContentType("application/json");
	}
}
