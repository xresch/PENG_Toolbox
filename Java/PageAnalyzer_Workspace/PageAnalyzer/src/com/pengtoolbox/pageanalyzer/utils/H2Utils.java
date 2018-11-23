package com.pengtoolbox.pageanalyzer.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

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
		

		
		try {
			
			server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "8889").start();
			
			dataSource = new JdbcDataSource();
			dataSource.setURL("jdbc:h2:./datastore/h2database");
			dataSource.setUser("sa");
			dataSource.setPassword("sa");
			
			H2Utils.cleanupDatabase();
			
			Connection connection = dataSource.getConnection();
			
			String createTableSQL = "CREATE TABLE IF NOT EXISTS results(result_id INT PRIMARY KEY AUTO_INCREMENT, "
								  + "user_id VARCHAR(255),"
								  + "page_url VARCHAR(4096),"
								  + "json_result CLOB,"
								  + "time TIMESTAMP);";
			
			// ALTER TABLE IF EXISTS results ADD COLUMN IF NOT EXISTS (page_url VARCHAR(4096))
			PreparedStatement prepared = connection.prepareStatement(createTableSQL);
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
	public static void saveResults(String userID, String jsonResults) {
		
		
		Pattern pattern = Pattern.compile(".*?\"u\":\"([^\"]+)\".*");
		Matcher matcher = pattern.matcher(jsonResults);

		String page_url = "N/A";
		if(matcher.matches()) {
			page_url = matcher.group(1);
			
			if(page_url == null) {
				page_url = "N/A";
			}
			
		}


		try {

			Connection connection = dataSource.getConnection();
			
			String saveResult = "INSERT INTO results(user_id, page_url, json_result, time) values(?, ?, ?, CURRENT_TIMESTAMP() );";
			
			PreparedStatement prepared = connection.prepareStatement(saveResult);
			prepared.setString(1, userID);
			prepared.setString(2, page_url);
			prepared.setString(3, jsonResults);
			
			prepared.execute();
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("saveResults")
				.severe("Issue saving results to H2 Database.", e);
		}
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static ResultSet getResultsForUser(String userID) {
		
		ResultSet resultSet = null;
		
		Connection connection = null;
		try {

			connection = dataSource.getConnection();
			
			String selectResults = "SELECT * FROM results WHERE user_id = '?'";
			
			PreparedStatement prepared = connection.prepareStatement(selectResults);
			prepared.setString(1, userID);
			
			resultSet = prepared.executeQuery();
			
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("saveResults")
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
		
		return resultSet;
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void cleanupDatabase() {
		
		try {

			Connection connection = dataSource.getConnection();
			
			String deleteTable = "DROP TABLE results;";
			
			PreparedStatement prepared = connection.prepareStatement(deleteTable);
			
		} catch (SQLException e) {
			new PALogger(logger)
				.method("cleanupDatabase")
				.severe("Issue cleaningup H2 Database.", e);
		}
		
	}
}
