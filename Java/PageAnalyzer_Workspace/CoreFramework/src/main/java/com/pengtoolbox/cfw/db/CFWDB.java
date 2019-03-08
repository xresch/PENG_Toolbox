package com.pengtoolbox.cfw.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLogger;

public class CFWDB {

	private static JdbcDataSource dataSource;
	private static JdbcConnectionPool connectionPool;
	public static Server server;
	public static boolean isInitialized = false;
	public static Logger logger = CFWLogger.getLogger(CFWDB.class.getName());
		
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initialize() {
    	
    	//---------------------------------------
    	// Get variables
		String storePath 	= CFWConfig.DB_STORE_PATH;
		String databaseName	= CFWConfig.DB_NAME;
		int port 			= CFWConfig.DB_PORT;
		String username		= CFWConfig.DB_USERNAME;
		String password		= CFWConfig.DB_PASSWORD;
		
		//---------------------------------------
    	// Create Folder  
		File datastoreFolder = new File(storePath);
    	if(!datastoreFolder.isDirectory()) {
    		datastoreFolder.mkdir();
    	}
    	
		String h2_url 		= "jdbc:h2:tcp://localhost:"+port+"/"+storePath+"/"+databaseName;
		try {
			
			CFWDB.server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "" +port).start();
			
			connectionPool = JdbcConnectionPool.create(h2_url, username, password);
			
			CFWDB.dataSource = new JdbcDataSource();
			CFWDB.dataSource.setURL(h2_url);
			CFWDB.dataSource.setUser(username);
			CFWDB.dataSource.setPassword(password);
			
			CFWDB.isInitialized = true;
		} catch (SQLException e) {
			CFWDB.isInitialized = false;
			new CFWLogger(CFWDB.logger)
				.method("initialize")
				.severe("Issue initializing H2 Database.", e);
			e.printStackTrace();
		}
	}
	
	
	/********************************************************************************************
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static Connection getConnection() throws SQLException {	
         return connectionPool.getConnection();
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @throws SQLException 
	 ********************************************************************************************/
	public static boolean preparedExecute(HttpServletRequest request, String sql, Object... values){	
        
		CFWLogger log = new CFWLogger(logger).method("preparedExecute").start();
		Connection conn = null;
		PreparedStatement prepared = null;
		boolean result = false;
		try {
			//-----------------------------------------
			// Initialize Variables
			conn = CFWDB.getConnection();
			prepared = conn.prepareStatement(sql);
			
			//-----------------------------------------
			// Prepare Statement
			if(values != null) {
				for(int i = 0; i < values.length ; i++) {
					Object currentValue = values[i];
					if		(currentValue instanceof String) 	{ prepared.setString(i+1, (String)currentValue); }
					else if (currentValue instanceof Integer) 	{ prepared.setInt(i+1, (Integer)currentValue); }
					else if (currentValue instanceof Boolean) 	{ prepared.setBoolean(i+1, (Boolean)currentValue); }
				}
			}
			
			//-----------------------------------------
			// Execute
			result = prepared.execute();
			
		} catch (SQLException e) {
			log.severe("Issue executing prepared statement: "+sql, e);
		} finally {
			try {
				if(conn != null) { conn.close(); }
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e) {
				log.severe("Issue closing resources.", e);
			}
			
		}
		
		log.end("Duration SQL Statement: "+sql);
		return result;
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @throws SQLException 
	 ********************************************************************************************/
	public static ResultSet preparedExecuteQuery(HttpServletRequest request, String sql, Object... values){	
        
		CFWLogger log = new CFWLogger(logger, request)
				.method("preparedExecuteQuery")
				.start();
		
		Connection conn = null;
		PreparedStatement prepared = null;
		ResultSet result = null;
		try {
			//-----------------------------------------
			// Initialize Variables
			conn = CFWDB.getConnection();
			prepared = conn.prepareStatement(sql);
			
			//-----------------------------------------
			// Prepare Statement
			if(values != null) {
				for(int i = 0; i < values.length ; i++) {
					Object currentValue = values[i];
					if		(currentValue instanceof String) 	{ prepared.setString(i+1, (String)currentValue); }
					else if (currentValue instanceof Integer) 	{ prepared.setInt(i+1, (Integer)currentValue); }
					else if (currentValue instanceof Boolean) 	{ prepared.setBoolean(i+1, (Boolean)currentValue); }
				}
			}
			
			//-----------------------------------------
			// Execute
			result = prepared.executeQuery();
			
		} catch (SQLException e) {
			log.severe("Issue executing prepared statement.", e);
		} 
		
		log.end("Duration SQL Statement: "+sql);
		return result;
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @throws SQLException 
	 ********************************************************************************************/
	public static void close(HttpServletRequest request, ResultSet resultSet){
		
		try {
			resultSet.close();
		} catch (SQLException e) {
			new CFWLogger(logger, request)
				.method("close")
				.severe("Exception occured while closing ResultSet. ", e);
		}
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

}
