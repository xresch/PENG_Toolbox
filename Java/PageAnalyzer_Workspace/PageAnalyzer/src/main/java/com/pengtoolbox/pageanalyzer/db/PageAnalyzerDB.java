package com.pengtoolbox.pageanalyzer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw._main.SessionData;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLogger;

public class PageAnalyzerDB {

	public static Logger logger = CFWLogger.getLogger(PageAnalyzerDB.class.getName());
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initialize() {
		
		String createTableSQL = "CREATE TABLE IF NOT EXISTS results(result_id INT PRIMARY KEY AUTO_INCREMENT, "
							  + "user_id VARCHAR(255),"
							  + "page_url VARCHAR(4096),"
							  + "json_result CLOB,"
							  + "time TIMESTAMP);";
		CFWDB.preparedExecute(null, createTableSQL);
	
		
		String addColumnHARFile = "ALTER TABLE results ADD COLUMN IF NOT EXISTS har_file CLOB";
		CFWDB.preparedExecute(null, addColumnHARFile);
			
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	private static String getUserIDForDBAccess(HttpServletRequest request) {
		//-------------------------------
		// Get UserID
		String userID = "";
		if(CFWConfig.AUTHENTICATION_ENABLED) {
			SessionData data = (SessionData) request.getSession().getAttribute(CFW.SESSION_DATA); 
			if(data.isLoggedIn()) {
				userID = data.getUsername();
			}
		}else {
			userID = "anonymous";
		}
		
		return userID;
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void saveResults(HttpServletRequest request, String jsonResults, String harString) {
		
		//-------------------------------
		// Get UserID
		String userID = getUserIDForDBAccess(request);
		
		//-------------------------------
		// Extract URL
		Pattern pattern = Pattern.compile(".*?\"u\":\"([^\"]+)\".*");
		Matcher matcher = pattern.matcher(jsonResults);

		String page_url = "N/A";
		if(matcher.matches()) {
			page_url = matcher.group(1);
			
			if(page_url == null) {
				page_url = "N/A";
			}
			
		}

		//-------------------------------
		// Insert into DB
		String saveResult = "INSERT INTO results(user_id, page_url, json_result,har_file, time) values(?, ?, ?, ?, CURRENT_TIMESTAMP() );";
		
		CFWDB.preparedExecute(request, saveResult, 
				userID,
				page_url,
				jsonResults,
				harString);
						
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getResultListForUser(HttpServletRequest request, String userID) {
					
		String selectResults = "SELECT result_id, page_url, time FROM results WHERE user_id = ? ORDER BY time DESC";
		
		ResultSet resultSet = CFWDB.preparedExecuteQuery(request, selectResults, userID);
		String jsonString = CFWDB.resultSetToJSON(resultSet);
		
		CFWDB.close(request, resultSet);
		
		return jsonString;
		
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getResultListForComparison(HttpServletRequest request, String resultIDArray) {
		
		String userID = getUserIDForDBAccess(request);
		
		//----------------------------------
		// Check input format
		if(!resultIDArray.matches("(\\d,?)+")) {
			return null;
		}
		
		//----------------------------------
		// Execute
		String selectResults = "SELECT result_id, page_url, time, json_result FROM results WHERE result_id in ("+resultIDArray+") AND user_id = ? ORDER BY time";
		ResultSet resultSet = CFWDB.preparedExecuteQuery(request, selectResults, userID);
		String jsonString = CFWDB.resultSetToJSON(resultSet);
		
		CFWDB.close(request, resultSet);
		
		return jsonString;
		
	}
	
	/********************************************************************************************
	 * Returns the YSlow results as json string for the given resultID.
	 * 
	 * @param request
	 * @param resultId the ID of the result
	 ********************************************************************************************/
	public static String getResultByID(HttpServletRequest request, int resultID) {
		
		//----------------------------------
		// Initialize
		String jsonResult = null;
		String userID = getUserIDForDBAccess(request);
		
		//----------------------------------
		// Execute
		ResultSet resultSet = CFWDB.preparedExecuteQuery(request,
				"SELECT json_result FROM results WHERE result_id = ?  AND user_id = ?",
				resultID, 
				userID);
		
		//----------------------------------
		// Get First Result
		try {
			if(resultSet.next()) {
				jsonResult = resultSet.getString(1);
			}
		} catch (SQLException e) {
			new CFWLogger(logger, request).method("getResultByID")
			.severe("Exception occured while reading results.", e);
		}
		
		//----------------------------------
		// Close and return
		CFWDB.close(request, resultSet);
		
		return jsonResult;

	}
	
	/********************************************************************************************
	 * Returns the YSlow results as json string for the given resultID.
	 * 
	 * @param request
	 * @param resultId the ID of the result
	 ********************************************************************************************/
	public static String getHARFileByID(HttpServletRequest request, int resultID) {
		
		//----------------------------------
		// Initialize
		String jsonResult = null;
		String userID = getUserIDForDBAccess(request);
		
		//----------------------------------
		// Execute
		ResultSet resultSet = CFWDB.preparedExecuteQuery(request,
				"SELECT har_file FROM results WHERE result_id = ? AND user_id = ?",
				resultID, 
				userID);
		
		//----------------------------------
		// Get First Result
		try {
			if(resultSet.next()) {
				jsonResult = resultSet.getString(1);
			}
		} catch (SQLException e) {
			new CFWLogger(logger, request).method("getResultByID")
			.severe("Exception occured while reading results.", e);
		}
		
		//----------------------------------
		// Close and return
		CFWDB.close(request, resultSet);
		
		return jsonResult;

	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static boolean deleteResults(HttpServletRequest request, String resultIDArray) {
		
		boolean result = false;
		String userID = getUserIDForDBAccess(request);
				
		//----------------------------------
		// Check input format
		if(!resultIDArray.matches("(\\d,?)+")) {
			return false;
		}
		
		//----------------------------------
		// Execute
		String deleteResults = "DELETE FROM results WHERE result_id in ("+resultIDArray+");";
		result = CFWDB.preparedExecute(request, deleteResults);
		
		return result;
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void cleanupDatabase() {
		
		CFWDB.preparedExecute(null, "DROP TABLE results;");
		
	}
}
