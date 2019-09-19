package com.pengtoolbox.pageanalyzer.db;

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
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser.UserDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class PageAnalyzerDB {

	public static Logger logger = CFWLog.getLogger(PageAnalyzerDB.class.getName());
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initialize() {
		
		String createTableSQL = "CREATE TABLE IF NOT EXISTS results(result_id INT PRIMARY KEY AUTO_INCREMENT, "
							  + "user_id VARCHAR(255),"
							  + "page_url VARCHAR(4096),"
							  + "json_result CLOB,"
							  + "time TIMESTAMP);";
		CFWDB.preparedExecute(createTableSQL);
	
		
		String addColumnHARFile = "ALTER TABLE results ADD COLUMN IF NOT EXISTS har_file CLOB";
		
		CFWDB.preparedExecute(addColumnHARFile);
		
		String addColumnName = "ALTER TABLE results ADD COLUMN IF NOT EXISTS name VARCHAR(255)";
		CFWDB.preparedExecute(addColumnName);	
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void saveResults(HttpServletRequest request, String resultName, String jsonResults, String harString) {
		
		//-------------------------------
		// Get UserID
		String username = CFW.Context.Request.getUser().username();
		
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
		String saveResult = "INSERT INTO results(user_id, page_url, name, json_result,har_file, time) values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP() );";
		
		CFWDB.preparedExecute(saveResult, 
				username,
				page_url,
				resultName,
				jsonResults,
				harString);
			
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getResultListForUser(String userID) {
					
		String selectResults = "SELECT result_id, name, page_url, time FROM results WHERE user_id = ? ORDER BY time DESC";
		
		ResultSet resultSet = CFWDB.preparedExecuteQuery(selectResults, userID);
		String jsonString = CFWDB.resultSetToJSON(resultSet);
		
		CFWDB.close(resultSet);
		
		return jsonString;
		
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getAllResults() {
		
		if(CFW.Context.Request.hasPermission(PAPermissions.MANAGE_RESULTS)) {
			String selectResults = "SELECT user_id, result_id, name, page_url, time FROM results ORDER BY time DESC";
			
			ResultSet resultSet = CFWDB.preparedExecuteQuery(selectResults);
			String jsonString = CFWDB.resultSetToJSON(resultSet);
			
			CFWDB.close(resultSet);
			
			return jsonString;
		}else {
			CFW.Context.Request.addAlertMessage(MessageType.ERROR, "Access Denied");
		}
		
		return null;
		
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getResultListForComparison(String resultIDArray) {
		
		String userID = CFW.Context.Request.getUser().username();
		
		//----------------------------------
		// Check input format
		if(resultIDArray == null ^ !resultIDArray.matches("(\\d,?)+")) {
			return null;
		}
		
		//----------------------------------
		// Execute
		String selectResults;
		ResultSet resultSet;
		if( CFW.Context.Request.getUserPermissions() != null
		 && CFW.Context.Request.getUserPermissions().containsKey(PAPermissions.MANAGE_RESULTS)) {
			selectResults = "SELECT result_id, page_url, time, json_result FROM results WHERE result_id in ("+resultIDArray+") ORDER BY time";
			resultSet = CFWDB.preparedExecuteQuery(selectResults);
		}else {
			selectResults = "SELECT result_id, page_url, time, json_result FROM results WHERE result_id in ("+resultIDArray+") AND user_id = ? ORDER BY time";
			resultSet = CFWDB.preparedExecuteQuery(selectResults, userID);
		}
			
	    
		String jsonString = CFWDB.resultSetToJSON(resultSet);
		
		CFWDB.close(resultSet);
		
		return jsonString;
		
	}
	
	/********************************************************************************************
	 * Returns the YSlow results as json string for the given resultID.
	 * 
	 * @param request
	 * @param resultId the ID of the result
	 ********************************************************************************************/
	public static String getResultByID(int resultID) {
		
		//----------------------------------
		// Initialize
		String jsonResult = null;
		String userID = CFW.Context.Request.getUser().username();
		
		//----------------------------------
		// Execute
		ResultSet resultSet = null;
		
		if( CFW.Context.Request.getUserPermissions() != null
			&& CFW.Context.Request.getUserPermissions().containsKey(PAPermissions.MANAGE_RESULTS)) {
			resultSet = CFWDB.preparedExecuteQuery(
				"SELECT json_result FROM results WHERE result_id = ?",
				resultID);
			
		}else {
			resultSet = CFWDB.preparedExecuteQuery(
					"SELECT json_result FROM results WHERE result_id = ?  AND user_id = ?",
					resultID, 
					userID);
		}
		
		//----------------------------------
		// Get First Result
		try {
			if(resultSet!=null && resultSet.next()) {
				jsonResult = resultSet.getString(1);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("getResultByID")
				.severe("Exception occured while reading results.", e);
		}
		
		//----------------------------------
		// Close and return
		CFWDB.close(resultSet);
		
		return jsonResult;

	}
	
	/********************************************************************************************
	 * Returns the YSlow results as json string for the given resultID.
	 * 
	 * @param request
	 * @param resultId the ID of the result
	 ********************************************************************************************/
	public static String getHARFileByID(int resultID) {
		
		//----------------------------------
		// Initialize
		String jsonResult = null;
		String userID = CFW.Context.Request.getUser().username();
		
		//----------------------------------
		// Execute
		ResultSet resultSet = null;
		if( CFW.Context.Request.getUserPermissions() != null
		 && CFW.Context.Request.getUserPermissions().containsKey(PAPermissions.MANAGE_RESULTS)) {
			resultSet = CFWDB.preparedExecuteQuery(
				"SELECT har_file FROM results WHERE result_id = ?",
				resultID);
		}else {
			resultSet = CFWDB.preparedExecuteQuery(
					"SELECT har_file FROM results WHERE result_id = ? AND user_id = ?",
					resultID, 
					userID);
		}
		//----------------------------------
		// Get First Result
		try {
			if(resultSet != null && resultSet.next()) {
				jsonResult = resultSet.getString(1);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("getHARFileByID")
				.severe("Exception occured while reading results.", e);
		}
		
		//----------------------------------
		// Close and return
		CFWDB.close(resultSet);
		
		return jsonResult;

	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static boolean deleteResults(String resultIDArray) {
		
		boolean result = false;
				
		//----------------------------------
		// Check input format
		if(!resultIDArray.matches("(\\d,?)+")) {
			return false;
		}
		
		//----------------------------------
		// Execute
		String deleteResults = "DELETE FROM results WHERE result_id in ("+resultIDArray+");";
		result = CFWDB.preparedExecute(deleteResults);
		
		return result;
		
	}
	
//	/********************************************************************************************
//	 * Test code only
//	 ********************************************************************************************/
//	public static void cleanupDatabase() {
//		
//		CFWDB.preparedExecute("DROP TABLE results;");
//		
//	}
}
