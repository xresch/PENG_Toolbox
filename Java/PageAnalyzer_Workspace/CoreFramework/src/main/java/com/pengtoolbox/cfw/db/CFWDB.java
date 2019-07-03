package com.pengtoolbox.cfw.db;

import java.io.File;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Logger;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDB {

	private static JdbcDataSource dataSource;
	private static JdbcConnectionPool connectionPool;
	private static Server server;
	private static boolean isInitialized = false;

	private static Logger logger = CFWLog.getLogger(CFWDB.class.getName());

		
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initialize() {
    	
    	//---------------------------------------
    	// Get variables
		String server 		= CFWConfig.DB_SERVER;
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
    	
		String h2_url 		= "jdbc:h2:tcp://"+server+":"+port+"/"+storePath+"/"+databaseName;
		new CFWLog(logger).info("H2 DB URL: "+ h2_url);
		
		try {
			
			CFWDB.server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "" +port).start();
			
			connectionPool = JdbcConnectionPool.create(h2_url, username, password);
			connectionPool.setMaxConnections(90);
			
			CFWDB.dataSource = new JdbcDataSource();
			CFWDB.dataSource.setURL(h2_url);
			CFWDB.dataSource.setUser(username);
			CFWDB.dataSource.setPassword(password);
			
			CFWDB.isInitialized = true;
			
			initializeTables();
			createDefaultEntries();
			
		} catch (SQLException e) {
			CFWDB.isInitialized = false;
			new CFWLog(CFWDB.logger)
				.method("initialize")
				.severe("Issue initializing H2 Database.", e);
			e.printStackTrace();
		}
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initializeTables() {
		
		CFW.DB.Users.initializeTable();
		CFW.DB.Groups.initializeTable();
		CFW.DB.UserGroupMap.initializeTable();
		CFW.DB.Permissions.initializeTable();
		CFW.DB.GroupPermissionMap.initializeTable();
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void createDefaultEntries() {
		
		//-----------------------------------------
		// Create default admin user
		//-----------------------------------------
		
		if(!CFW.DB.Users.checkUsernameExists("admin")) {
			
		    CFW.DB.Users.create(
				new User("admin")
				.isDeletable(false)
				.isRenamable(false)
				.setInitialPassword("admin", "admin")
				.status("ACTIVE")
				.isForeign(false)
			);
		}
		
		User adminUser = CFW.DB.Users.selectByUsernameOrMail("admin");
		
		if(adminUser == null) {
			new CFWLog(logger)
			.method("createDefaultEntries")
			.severe("User 'admin' was not found in the database.");
		}
		
		//-----------------------------------------
		// Create Group Superuser
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.DEFAULT_GROUP_SUPERUSER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.DEFAULT_GROUP_SUPERUSER)
				.description("Superusers have all the privileges in the system. They are above administrators. ")
				.isDeletable(false)
			);
		}
		
		Group superuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.DEFAULT_GROUP_SUPERUSER);
		
		if(superuserGroup == null) {
			new CFWLog(logger)
			.method("createDefaultEntries")
			.severe("User group '"+CFWDBGroup.DEFAULT_GROUP_SUPERUSER+"' was not found in the database.");
		}
		
		//-----------------------------------------
		// Create Group Foreign
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.DEFAULT_GROUP_USER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.DEFAULT_GROUP_USER)
				.description("Default User group. New users will automatically be added to this group if they are not managed by a foreign source.")
				.isDeletable(false)
			);
		}
		
		Group userGroup = CFW.DB.Groups.selectByName(CFWDBGroup.DEFAULT_GROUP_USER);
		
		if(userGroup == null) {
			new CFWLog(logger)
			.method("createDefaultEntries")
			.severe("User group '"+CFWDBGroup.DEFAULT_GROUP_USER+"' was not found in the database.");
		}
		
		//-----------------------------------------
		// Create Group Foreign
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.DEFAULT_GROUP_FOREIGN_USER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.DEFAULT_GROUP_FOREIGN_USER)
				.description("Foreign users are accounts which are managed by other sources like LDAP or .csv-Files. They normally don't have certain perissions like changing the password, as this is managed by the foreign account source.")
				.isDeletable(false)
			);
		}
		
		Group foreignuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.DEFAULT_GROUP_FOREIGN_USER);
		
		if(foreignuserGroup == null) {
			new CFWLog(logger)
			.method("createDefaultEntries")
			.severe("User group '"+CFWDBGroup.DEFAULT_GROUP_FOREIGN_USER+"' was not found in the database.");
		}
		
		
		//-----------------------------------------
		// Add Admin to group Superuser
		//-----------------------------------------
		if(!CFW.DB.UserGroupMap.checkIsUserInGroup(adminUser, superuserGroup)) {
			CFW.DB.UserGroupMap.addUserToGroup(adminUser, superuserGroup);
		}

		if(!CFW.DB.UserGroupMap.checkIsUserInGroup(adminUser, superuserGroup)) {
			new CFWLog(logger)
			.method("createDefaultEntries")
			.severe("User 'admin' is not assigned to group 'Superuser'.");
		}
		
	}
	
	
	/********************************************************************************************
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static Connection getConnection() throws SQLException {	
		if(isInitialized) {
			return connectionPool.getConnection();
		}else {
			throw new SQLException("DB not initialized, call CFWDB.initialize() first.");
		}
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @return true if update count is > 0, false otherwise
	 ********************************************************************************************/
	public static boolean preparedExecute(String sql, Object... values){	
        
		CFWLog log = new CFWLog(logger).method("preparedExecute").start();
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
			CFWDB.prepareStatement(prepared, values);
			
			//-----------------------------------------
			// Execute
			boolean isResultSet = prepared.execute();

			if(!isResultSet) {
				if(prepared.getUpdateCount() > 0) {
					result = true;
				}
			}
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
	 * Returns the result or null if there was any issue.
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @throws SQLException 
	 ********************************************************************************************/
	public static ResultSet preparedExecuteQuery(String sql, Object... values){	
        
		CFWLog log = new CFWLog(logger)
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
			CFWDB.prepareStatement(prepared, values);
			
			//-----------------------------------------
			// Execute
			result = prepared.executeQuery();
			
		} catch (SQLException e) {
			log.severe("Issue executing prepared statement.", e);
			try {
				if(conn != null) { conn.close(); }
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e2) {
				log.severe("Issue closing resources.", e2);
			}
		} 
		
		log.end("Duration SQL Statement: "+sql);
		return result;
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement. Supports String, Integer,
	 *               Boolean, Float, Date, Timestamp, Blob, Clob, Byte
	 * @throws SQLException 
	 ********************************************************************************************/
	public static void prepareStatement(PreparedStatement prepared, Object... values) throws SQLException{

		if(values != null) {
			for(int i = 0; i < values.length ; i++) {
				Object currentValue = values[i];
				if		(currentValue instanceof String) 	{ prepared.setString(i+1, (String)currentValue); }
				else if	(currentValue instanceof char[]) 	{ prepared.setString(i+1, new String((char[])currentValue)); }
				else if (currentValue instanceof Integer) 	{ prepared.setInt(i+1, (Integer)currentValue); }
				else if (currentValue instanceof Boolean) 	{ prepared.setBoolean(i+1, (Boolean)currentValue); }
				else if (currentValue instanceof Float) 	{ prepared.setFloat(i+1, (Float)currentValue); }
				else if (currentValue instanceof Date) 		{ prepared.setDate(i+1, (Date)currentValue); }
				else if (currentValue instanceof Timestamp) { prepared.setTimestamp(i+1, (Timestamp)currentValue); }
				else if (currentValue instanceof Blob) 		{ prepared.setBlob(i+1, (Blob)currentValue); }
				else if (currentValue instanceof Clob) 		{ prepared.setClob(i+1, (Clob)currentValue); }
				else if (currentValue instanceof Byte) 		{ prepared.setByte(i+1, (Byte)currentValue); }
				else if (currentValue == null) 				{ prepared.setNull(i+1, Types.NULL); }
				else { throw new RuntimeException("Unsupported database field type: "+ currentValue.getClass().getName());}
			}
		}
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param resultSet which should be closed.
	 ********************************************************************************************/
	public static void close(ResultSet resultSet){
		
		try {
			if(resultSet != null) {
				resultSet.getStatement().getConnection().close();
				resultSet.close();
			}
		} catch (SQLException e) {
			new CFWLog(logger)
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
		
		if(resultSet != null) {
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
		}
		
		json.append("]");
		return json.toString();
	}

}
