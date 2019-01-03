package com.pengtoolbox.pageanalyzer.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer._main.SessionData;
import com.pengtoolbox.pageanalyzer.logging.PALogger;

public class H2Utils {

	private static Logger logger = PALogger.getLogger(H2Utils.class.getName());
	private static boolean isInitialized = false;
	
	private static JdbcDataSource dataSource;
	private static Server server;
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initialize() {
		
		String storePath 	= PA.config("pa_h2_store_path");
		String port 		= PA.config("pa_h2_store_port");
		String username		= PA.config("pa_h2_username");
		String password		= PA.config("pa_h2_password");
		
		String h2_url 		= "jdbc:h2:tcp://localhost:"+port+"/"+storePath;
		try {
			
			server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", port).start();
			
			dataSource = new JdbcDataSource();
			dataSource.setURL(h2_url);
			dataSource.setUser(username);
			dataSource.setPassword(password);
			
			//H2Utils.cleanupDatabase();
			
			Connection connection = dataSource.getConnection();
			
			String createTableSQL = "CREATE TABLE IF NOT EXISTS results(result_id INT PRIMARY KEY AUTO_INCREMENT, "
								  + "user_id VARCHAR(255),"
								  + "page_url VARCHAR(4096),"
								  + "json_result CLOB,"
								  + "time TIMESTAMP);";
			
			PreparedStatement prepared = connection.prepareStatement(createTableSQL);
			prepared.execute();
			 
			String addColumnHARFile = "ALTER TABLE results ADD COLUMN IF NOT EXISTS har_file CLOB";
			prepared = connection.prepareStatement(addColumnHARFile);
			prepared.execute();
			
			isInitialized = true;
		} catch (SQLException e) {
			isInitialized = false;
			new PALogger(logger)
				.method("initialize")
				.severe("Issue initializing H2 Database.", e);
			e.printStackTrace();
		}
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void saveResults(HttpServletRequest request, String jsonResults, String harString) {
		
		//-------------------------------
		// Get UserID
		String userID = "";
		if(PA.CONFIG_AUTHENTICATION_ENABLED) {
			SessionData data = (SessionData) request.getSession().getAttribute(PA.SESSION_DATA); 
			if(data.isLoggedIn()) {
				userID = data.getUsername();
			}
		}else {
			userID = "anonymous";
		}
		
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
		
		try {

			Connection connection = dataSource.getConnection();
			
			String saveResult = "INSERT INTO results(user_id, page_url, json_result,har_file, time) values(?, ?, ?, ?, CURRENT_TIMESTAMP() );";
			
			PreparedStatement prepared = connection.prepareStatement(saveResult);
			prepared.setString(1, userID);
			prepared.setString(2, page_url);
			prepared.setString(3, jsonResults);
			prepared.setString(4, harString);
			
			prepared.execute();
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("saveResults")
				.severe("Issue saving results to H2 Database.", e);
		}
		
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getResultListForUser(String userID) {
		
		ResultSet resultSet = null;
		String jsonString = null;
		
		Connection connection = null;
		try {

			connection = dataSource.getConnection();
			
			String selectResults = "SELECT result_id, page_url, time FROM results WHERE user_id = ? ORDER BY time DESC";
			
			PreparedStatement prepared = connection.prepareStatement(selectResults);
			prepared.setString(1, userID);
			
			resultSet = prepared.executeQuery();
			
			jsonString = H2Utils.resultSetToJSON(resultSet);
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("getResultListForUser")
				.severe("Issue fetching results from H2 Database.", e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return jsonString;
		
	}
	
	/********************************************************************************************
	 * Returns a result as a json array.
	 * If the result is null, the method returns an empty array.
	 * 
	 ********************************************************************************************/
	public static String getResultListForComparison(String resultIDArray) {
		
		ResultSet resultSet = null;
		String jsonString = null;
		Connection connection = null;	
		
		if(!resultIDArray.matches("(\\d,?)+")) {
			return null;
		}
		
		try {

			connection = dataSource.getConnection();
			
			String selectResults = "SELECT result_id, page_url, time, json_result FROM results WHERE result_id in ("+resultIDArray+") ORDER BY time";
			
			PreparedStatement prepared = connection.prepareStatement(selectResults);
			
			resultSet = prepared.executeQuery();
			
			jsonString = H2Utils.resultSetToJSON(resultSet);
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("getResultListForComparison")
				.severe("Issue fetching results from H2 Database.", e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return jsonString;
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static String getResultByID(int id) {
		
		ResultSet resultSet = null;
		String jsonResult = null;
		
		Connection connection = null;
		try {

			connection = dataSource.getConnection();
			
			String selectResults = "SELECT json_result FROM results WHERE result_id = ?";
			
			PreparedStatement prepared = connection.prepareStatement(selectResults);
			prepared.setInt(1, id);
			
			resultSet = prepared.executeQuery();
			
			if(resultSet.next()) {
				jsonResult = resultSet.getString(1);
			}
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("getResultByID")
				.severe("Issue saving results to H2 Database.", e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return jsonResult;
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static boolean deleteResults(String resultIDArray) {
		
		boolean result = false;
		
		Connection connection = null;	
		
		if(!resultIDArray.matches("(\\d,?)+")) {
			return false;
		}
		
		try {

			connection = dataSource.getConnection();
			
			String selectResults = "DELETE FROM results WHERE result_id in ("+resultIDArray+");";
			
			PreparedStatement prepared = connection.prepareStatement(selectResults);
			
			result = prepared.execute();
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("deleteResults")
				.severe("Issue fetching results from H2 Database.", e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		return result;
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static String resultSetToJSON(ResultSet resultSet) {
		
		StringBuffer json = new StringBuffer();
		json.append("[");
		
		try {
			ResultSetMetaData metadata = resultSet.getMetaData();
	
			int columnCount = metadata.getColumnCount();
	
			while(resultSet.next()) {
				json.append("{");
				for(int i = 1 ; i <= columnCount; i++) {
					String column = metadata.getColumnName(i);
					json.append("\"").append(column).append("\": ");
					
					String value = resultSet.getString(i);
					if(column.equals("JSON_RESULT")) {
						json.append(value).append(",");
					}else {
						json.append("\"").append(value).append("\",");
					}
				}
				json.deleteCharAt(json.length()-1); //remove last comma
				json.append("},");
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.deleteCharAt(json.length()-1); //remove last comma
		json.append("]");
		
		return json.toString();
	}
		
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void cleanupDatabase() {
		
		try {

			Connection connection = dataSource.getConnection();
			
			String deleteTable = "DROP TABLE results; ";
			
			PreparedStatement prepared = connection.prepareStatement(deleteTable);
			
			prepared.execute();
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("cleanupDatabase")
				.severe("Issue cleaningup H2 Database.", e);
		}
		
	}
}
