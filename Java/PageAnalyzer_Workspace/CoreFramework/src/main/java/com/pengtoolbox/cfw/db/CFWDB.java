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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFW.Properties;
import com.pengtoolbox.cfw._main.CFWProperties;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBRole;
import com.pengtoolbox.cfw.db.usermanagement.Role;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWDB {

	private static JdbcDataSource dataSource;
	private static JdbcConnectionPool connectionPool;
	private static Server server;
	private static boolean isInitialized = false;

	private static Logger logger = CFWLog.getLogger(CFWDB.class.getName());
	
	private static InheritableThreadLocal<ArrayList<Connection>> myOpenConnections = new InheritableThreadLocal<ArrayList<Connection>>();
	private static InheritableThreadLocal<Connection> transactionConnection = new InheritableThreadLocal<Connection>();

	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void startDatabase() {
    	
    	//---------------------------------------
    	// Get variables
		String server 		= CFWProperties.DB_SERVER;
		String storePath 	= CFWProperties.DB_STORE_PATH;
		String databaseName	= CFWProperties.DB_NAME;
		int port 			= CFWProperties.DB_PORT;
		String username		= CFWProperties.DB_USERNAME;
		String password		= CFWProperties.DB_PASSWORD;
		
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
			
			//initializeTables();
			//createDefaultEntries();
			
		} catch (SQLException e) {
			CFWDB.isInitialized = false;
			new CFWLog(CFWDB.logger)
				.method("initialize")
				.severe("Issue initializing H2 Database.", e);
			e.printStackTrace();
		}
	}
	
	public static void resetAdminPW() {
		
		if(Properties.RESET_ADMIN_PW) {
			User admin = CFW.DB.Users.selectByUsernameOrMail("admin");
			admin.setNewPassword("admin", "admin");
			
			if(CFW.DB.Users.update(admin)) {
				new CFWLog(logger)
				.method("resetAdminPW")
				.info("Admin password was reset to default!");
			}else{
				new CFWLog(logger)
				.method("resetAdminPW")
				.warn("Admin password was reset failed!");
			};
		}
	}
			
	/********************************************************************************************
	 * Roles have to be existing.
	 * 
	 ********************************************************************************************/
	private static void createDefaultUsers() {
		
		//-----------------------------------------
		// Create anonymous user 
		//-----------------------------------------
		if(!CFW.Properties.AUTHENTICATION_ENABLED) {
			String initialPassword = CFW.Encryption.createPasswordSalt(32);
			if(!CFW.DB.Users.checkUsernameExists("anonymous")) {
			    CFW.DB.Users.create(
					new User("anonymous")
					.setNewPassword(initialPassword, initialPassword)
					.isDeletable(true)
					.isRenamable(false)
					.status("ACTIVE")
					.isForeign(false)
				);
			}
		
			User anonUser = CFW.DB.Users.selectByUsernameOrMail("anonymous");
			
			if(anonUser == null) {
				new CFWLog(logger)
				.method("createDefaultUsers")
				.severe("User 'anonymous' was not found in the database.");
			}
		}
		//-----------------------------------------
		// Create default admin user
		//-----------------------------------------
		
		if(!CFW.DB.Users.checkUsernameExists("admin")) {
			
		    CFW.DB.Users.create(
				new User("admin")
				.isDeletable(false)
				.isRenamable(false)
				.setNewPassword("admin", "admin")
				.status("ACTIVE")
				.isForeign(false)
			);
		}
		
		User adminUser = CFW.DB.Users.selectByUsernameOrMail("admin");
		
		if(adminUser == null) {
			new CFWLog(logger)
			.method("createDefaultUsers")
			.severe("User 'admin' was not found in the database.");
		}
		
		
		
		//-----------------------------------------
		// Add Admin to role Superuser
		//-----------------------------------------
		Role superuserRole = CFW.DB.Roles.selectByName(CFWDBRole.CFW_ROLE_SUPERUSER);
		
		if(!CFW.DB.UserRoleMap.checkIsUserInRole(adminUser, superuserRole)) {
			CFW.DB.UserRoleMap.addUserToRole(adminUser, superuserRole, false);
		}
		//Needed for Upgrade
		CFW.DB.UserRoleMap.updateIsDeletable(adminUser.id(), superuserRole.id(), false);

		if(!CFW.DB.UserRoleMap.checkIsUserInRole(adminUser, superuserRole)) {
			new CFWLog(logger)
			.method("createDefaultUsers")
			.severe("User 'admin' is not assigned to role 'Superuser'.");
		}
		
		//-----------------------------------------
		// Upgrade Step: Superuser permissions undeletable
		//-----------------------------------------
		HashMap<String, Permission> permissions = CFW.DB.RolePermissionMap.selectPermissionsForRole(superuserRole);
		
		for(Permission p : permissions.values()) {
			CFW.DB.RolePermissionMap.updateIsDeletable(p.id(), superuserRole.id(), false);
		}
		
	}
	
	/********************************************************************************************
	 * Add a connection that was openend to the list of open connections.
	 * When connections remain after the Servlet returns, they will be closed 
	 * by the RequestHandler using hardCloseRemainingConnections().
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static void forceCloseRemainingConnections() {	
		
		if(myOpenConnections.get() == null) {
			//all good, return
			return;
		}
		
		ArrayList<Connection> connArray = myOpenConnections.get();
		
		int counter = 0;
		
		//Create new array to avoid ConcurrentModificationException
		for(Connection con : connArray.toArray(new Connection[] {})) {
			

			try {
				if(!con.isClosed()) {
					counter++;			
					System.out.println("ForceClose: "+con.getClass());
					con.close();
				}
				connArray.remove(con);
			} catch (SQLException e) {
				new CFWLog(logger)
					.method("forceCloseRemainingConnections")
					.severe("Error on forced closing of DB connection.", e);
			}
		}
		
		if(counter > 0) {
			new CFWLog(logger)
			.method("forceCloseRemainingConnections")
			.warn(""+counter+" database connection(s) not closed properly.");
		}
	}
	
	/********************************************************************************************
	 * Add a connection that was openend to the list of open connections.
	 * When connections remain after the Servlet returns, they will be closed 
	 * by the RequestHandler using forceCloseRemainingConnections().
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	private static void addOpenConnection(Connection connection) {	
		if(myOpenConnections.get() == null) {
			myOpenConnections.set(new ArrayList<Connection>());
		}
		
		myOpenConnections.get().add(connection);
	}
	
	/********************************************************************************************
	 * Removes a connection that was openend from the list of open connections.
	 * When connections remain after the Servlet returns, they will be closed 
	 * by the RequestHandler using hardCloseRemainingConnections().
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	private static void removeOpenConnection(Connection connection) {	
		
		if(myOpenConnections.get() == null) {
			return;
		}
		myOpenConnections.get().remove(connection);
	}
	
	/********************************************************************************************
	 * Get a connection from the connection pool or returns the current connection used for the 
	 * transaction.
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static Connection getConnection() throws SQLException {	
		
		if(isInitialized) {
			new CFWLog(logger)
				.method("getConnection")
				.finer("DB Connections Active: "+connectionPool.getActiveConnections());
			
			if(transactionConnection.get() != null) {
				return transactionConnection.get();
			}else {
				synchronized (connectionPool) {
					Connection connection = connectionPool.getConnection();
					addOpenConnection(connection);
					return connection;
				}
			}
		}else {
			throw new SQLException("DB not initialized, call CFWDB.initialize() first.");
		}
	}
	
	/********************************************************************************************
	 * Starts a new transaction.
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static void beginTransaction() {	
		
		if(transactionConnection.get() != null) {
			new CFWLog(logger)
				.method("beginTransaction")
				.severe("A transaction was already started for this thread. Use commitTransaction() before starting another one.");
			return;
		}
		
		try {
			Connection con = CFWDB.getConnection();
			con.setAutoCommit(false);
			transactionConnection.set(con);
			addOpenConnection(con);
			new CFWLog(logger).method("beginTransaction").finer("DB transaction started.");
			
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("beginTransaction")
				.severe("Error while retrieving DB connection.", e);
		}
		
	}
	
	/********************************************************************************************
	 * Starts a new transaction.
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static void commitTransaction() {	
		
		
		if(transactionConnection.get() == null) {
			new CFWLog(logger)
				.method("commitTransaction")
				.severe("There is no running transaction. Use beginTransaction() before using commit.");
			return;
		}
		
		Connection con = null;
		
		try {
			con = transactionConnection.get();
			con.commit();
			new CFWLog(logger).method("commitTransaction").finer("DB transaction committed.");
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("commitTransaction")
				.severe("Error occured on commit transaction.", e);
		} finally {
			transactionConnection.set(null);
			if(con != null) { 
				try {
					removeOpenConnection(con);
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					new CFWLog(logger)
						.method("commitTransaction")
						.severe("Error occured closing DB resources.", e);
				}
				
			}
		}
		
	}
	
	/********************************************************************************************
	 * Rollbacks the transaction.
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static void rollbackTransaction() {	
		
		
		if(transactionConnection.get() == null) {
			new CFWLog(logger)
				.method("rollbackTransaction")
				.severe("There is no running transaction. Use beginTransaction() before using commit.");
			return;
		}
		
		Connection con = null;
		
		try {
			con = transactionConnection.get();
			con.rollback();
			new CFWLog(logger).method("rollbackTransaction").finer("DB transaction rolled back.");
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("rollbackTransaction")
				.severe("Error occured on rollback transaction.", e);
		} finally {
			transactionConnection.set(null);
			if(con != null) { 
				try {
					con.setAutoCommit(true);
					con.close();
					removeOpenConnection(con);
				} catch (SQLException e) {
					new CFWLog(logger)
						.method("rollbackTransaction")
						.severe("Error occured closing DB resources.", e);
				}
				
			}
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
			log.severe("Database Error: "+e.getMessage(), e);
		} finally {
			try {
				if(conn != null && transactionConnection.get() == null) { 
					removeOpenConnection(conn);
					conn.close(); 
				}
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e) {
				log.severe("Issue closing resources.", e);
			}
			
		}
		
		log.custom("sql", sql).end();
		return result;
	}
	
	/********************************************************************************************
	 * 
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @return true if update count is > 0, false otherwise
	 ********************************************************************************************/
	public static boolean preparedExecuteBatch(String sql, Object... values){	
        
		CFWLog log = new CFWLog(logger).method("preparedExecuteBatch").start();
		Connection conn = null;
		PreparedStatement prepared = null;

		boolean result = true;
		try {
			//-----------------------------------------
			// Initialize Variables
			conn = CFWDB.getConnection();
			prepared = conn.prepareStatement(sql);
			
			//-----------------------------------------
			// Prepare Statement
			CFWDB.prepareStatement(prepared, values);
			prepared.addBatch();
			
			//-----------------------------------------
			// Execute
			int[] resultCounts = prepared.executeBatch();

			for(int i : resultCounts) {
				if(i < 0) {
					result = false;
					break;
				}
			}
		} catch (SQLException e) {
			result = false;
			log.severe("Database Error: "+e.getMessage(), e);
		} finally {
			try {
				if(conn != null && transactionConnection.get() == null) { 
					removeOpenConnection(conn);
					conn.close(); 
				}
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e) {
				log.severe("Issue closing resources.", e);
			}
			
		}
		
		log.custom("sql", sql).end();
		return result;
	}
	
	/********************************************************************************************
	 * Executes the insert and returns the generated Key of the new record. (what is a
	 * primary key in most cases)
	 * 
	 * @param sql string with placeholders
	 * @param generatedKeyName name of the column of the key to retrieve
	 * @param values the values to be placed in the prepared statement
	 * @return generated key, null if not successful
	 ********************************************************************************************/
	public static Integer preparedInsertGetKey(String sql, String generatedKeyName, Object... values){	
        
		CFWLog log = new CFWLog(logger).method("preparedInsert").start();
		Connection conn = null;
		PreparedStatement prepared = null;

		Integer generatedID = null;
		try {
			//-----------------------------------------
			// Initialize Variables
			conn = CFWDB.getConnection();
			prepared = conn.prepareStatement(sql, new String[] {generatedKeyName});
			
			//-----------------------------------------
			// Prepare Statement
			CFWDB.prepareStatement(prepared, values);
			
			//-----------------------------------------
			// Execute
			int affectedRows = prepared.executeUpdate();

			if(affectedRows > 0) {
				ResultSet result = prepared.getGeneratedKeys();
				result.next();
				generatedID = result.getInt(generatedKeyName);
			}
		} catch (SQLException e) {
			log.severe("Database Error: "+e.getMessage(), e);
		} finally {
			try {
				if(conn != null && transactionConnection.get() == null) { 
					removeOpenConnection(conn);
					conn.close(); 
				}
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e) {
				log.severe("Issue closing resources.", e);
			}
			
		}
		
		log.custom("sql", sql).end();
		return generatedID;
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
				if(conn != null && transactionConnection == null) { 
					removeOpenConnection(conn);
					conn.close(); 
				}
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e2) {
				log.severe("Issue closing resources.", e2);
			}
		} 
		
		log.custom("sql", sql).end();
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
				// Could be a better solution
				//prepared.setObject(i+1, currentValue);
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
				else if (currentValue instanceof Object[]) 	{ prepared.setArray(i+1, prepared.getConnection().createArrayOf("VARCHAR", (Object[])currentValue)); }
				else if (currentValue == null) 				{ prepared.setNull(i+1, Types.NULL); }
				else { throw new RuntimeException("Unsupported database field type: "+ currentValue.getClass().getName());}
			}
		}
		new CFWLog(logger).custom("preparedSQL", prepared.toString()).finest("Prepared SQL");
		//System.out.println(prepared);
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param resultSet which should be closed.
	 ********************************************************************************************/
	public static void close(Connection conn){
		
		try {
			if(!conn.isClosed()) {
				removeOpenConnection(conn);
				conn.close();
			}
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("close")
				.severe("Exception occured while closing connection. ", e);
		}
	}
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param resultSet which should be closed.
	 ********************************************************************************************/
	public static void close(ResultSet resultSet){
		
		try {
			if(resultSet != null 
			&& transactionConnection.get() == null
			&& !resultSet.getStatement().isClosed()) {
				
				removeOpenConnection(resultSet.getStatement().getConnection());
				
				if(!resultSet.getStatement().getConnection().isClosed()) {
					resultSet.getStatement().getConnection().close();
					resultSet.close();
				}
			}
		} catch (SQLException e) {
			new CFWLog(logger)
				.method("close")
				.severe("Exception occured while closing ResultSet. ", e);
		}
	}


	/********************************************************************************************
	 * Returns a jsonString with an array containing a json object for each row.
	 * Returns an empty array in case of error.
	 * 
	 ********************************************************************************************/
	public static String resultSetToJSON(ResultSet resultSet) {
		StringBuffer json = new StringBuffer();
		
		try {
			
			if(resultSet == null) {
				return "[]";
			}
			//--------------------------------------
			// Check has results
			resultSet.beforeFirst();
			if(!resultSet.isBeforeFirst()) {
				return "[]";
			}
			
			//--------------------------------------
			// Iterate results
			ResultSetMetaData metadata = resultSet.getMetaData();
			int columnCount = metadata.getColumnCount();
	
			json.append("[");
			while(resultSet.next()) {
				json.append("{");
				for(int i = 1 ; i <= columnCount; i++) {
					String column = metadata.getColumnLabel(i);
					json.append("\"").append(column).append("\": ");
					
					Object value = resultSet.getObject(i);
					if(column.startsWith("JSON")) {
						json.append(value).append(",");
					}else {
						if(value == null
						|| value instanceof Number
						|| value instanceof Boolean ) {
							json.append(value).append(",");
						}else {
							json.append("\"").append(resultSet.getString(i)).append("\",");
						}
					}
				}
				json.deleteCharAt(json.length()-1); //remove last comma
				json.append("},");
			}
			
			json.deleteCharAt(json.length()-1); //remove last comma
			json.append("]");
			
		} catch (SQLException e) {
				new CFWLog(logger)
					.method("resultSetToJSON")
					.severe("Exception occured while converting ResultSet to JSON.", e);
				
				return "[]";
		}

		return json.toString();
	}
	
	/********************************************************************************************
	 * Returns a jsonString with an array containing a json object for each row.
	 * Returns an empty array in case of error.
	 * 
	 ********************************************************************************************/
	public static String resultSetToCSV(ResultSet resultSet, String delimiter) {
		StringBuffer csv = new StringBuffer();
		
		try {
			
			if(resultSet == null) {
				return "";
			}
			//--------------------------------------
			// Check has results
			resultSet.beforeFirst();
			if(!resultSet.isBeforeFirst()) {
				return "";
			}
			
			//--------------------------------------
			// Iterate results
			ResultSetMetaData metadata = resultSet.getMetaData();
			int columnCount = metadata.getColumnCount();
			
			for(int i = 1 ; i <= columnCount; i++) {
				csv.append("\"")
				   .append(metadata.getColumnLabel(i))
				   .append("\"")
				   .append(delimiter);
			}
			csv.deleteCharAt(csv.length()-1); //remove last comma
			csv.append("\r\n");
			while(resultSet.next()) {
				for(int i = 1 ; i <= columnCount; i++) {
					
					String value = resultSet.getString(i);
					csv.append("\"")
					   .append(CFW.JSON.escapeString(value))
					   .append("\"")
					   .append(delimiter);
				}
				csv.deleteCharAt(csv.length()-1); //remove last comma
				csv.append("\r\n");
			}
			csv.deleteCharAt(csv.length()-1); //remove last comma

			
		} catch (SQLException e) {
				new CFWLog(logger)
					.method("resultSetToCSV")
					.severe("Exception occured while converting ResultSet to CSV.", e);
				
				return "";
		}

		return csv.toString();
	}
	
	/********************************************************************************************
	 * Returns a jsonString with an array containing a json object for each row.
	 * Returns an empty array in case of error.
	 * 
	 ********************************************************************************************/
	public static String resultSetToXML(ResultSet resultSet) {
		StringBuffer json = new StringBuffer();
		
		try {
			
			if(resultSet == null) {
				return "<data></data>";
			}
			//--------------------------------------
			// Check has results
			resultSet.beforeFirst();
			if(!resultSet.isBeforeFirst()) {
				return "<data></data>";
			}
			
			//--------------------------------------
			// Iterate results
			ResultSetMetaData metadata = resultSet.getMetaData();
			int columnCount = metadata.getColumnCount();
	
			json.append("<data>\n");
			while(resultSet.next()) {
				json.append("\t<record>\n");
				for(int i = 1 ; i <= columnCount; i++) {
					String column = metadata.getColumnLabel(i);
					json.append("\t\t<").append(column).append(">");
					
					String value = resultSet.getString(i);
					json.append(value);
					json.append("</").append(column).append(">\n");
				}
				json.append("\t</record>\n");
			}
			json.append("</data>");
			
		} catch (SQLException e) {
				new CFWLog(logger)
					.method("resultSetToXML")
					.severe("Exception occured while converting ResultSet to XML.", e);
				
				return "<data></data>";
		}

		return json.toString();
	}
	
	/********************************************************************************************
	 * Create Testdata for testing purposes
	 ********************************************************************************************/
	public static void createTestData() {
		
		Role testroleA, testroleB, testroleC;
		User testuserA, testuserB, testuserC;
		
		Permission  permissionA, permissionAA, permissionAAA, 
					permissionB, permissionBB,
					permissionC;
		//------------------------------
		// Roles
		CFW.DB.Roles.create(new Role("TestroleA", "user").description("This is the testrole A."));
		testroleA = CFW.DB.Roles.selectByName("TestroleA");
		
		CFW.DB.Roles.create(new Role("TestroleB", "user").description("This is the testrole B."));
		testroleB = CFW.DB.Roles.selectByName("TestroleB");
		
		CFW.DB.Roles.create(new Role("TestroleC", "user").description("This is the testrole C."));
		testroleC = CFW.DB.Roles.selectByName("TestroleC");
		
		//------------------------------
		// Users
		CFW.DB.Users.create(new User("TestuserA")
				.setNewPassword("TestuserA", "TestuserA")
				.email("testuserA@cfwtest.com")
				.firstname("Testika")
				.lastname("Testonia"));
		testuserA = CFW.DB.Users.selectByUsernameOrMail("TestuserA");
		CFW.DB.UserRoleMap.addUserToRole(testuserA, testroleA, true);
		CFW.DB.UserRoleMap.addUserToRole(testuserA, testroleB, true);
		CFW.DB.UserRoleMap.addUserToRole(testuserA, testroleC, true);
		
		CFW.DB.Users.create(new User("TestuserB")
				.setNewPassword("TestuserB", "TestuserB")
				.email("testuserB@cfwtest.com")
				.firstname("Jane")
				.lastname("Doe"));
		testuserB = CFW.DB.Users.selectByUsernameOrMail("TestuserB");
		CFW.DB.UserRoleMap.addUserToRole(testuserB, testroleA, true);
		CFW.DB.UserRoleMap.addUserToRole(testuserB, testroleB, true);
		
		CFW.DB.Users.create(new User("TestuserC")
				.setNewPassword("TestuserC", "TestuserC")
				.email("testuserC@cfwtest.com")
				.firstname("Paola")
				.lastname("Pernandez"));	
		testuserC = CFW.DB.Users.selectByUsernameOrMail("TestuserC");
		CFW.DB.UserRoleMap.addUserToRole(testuserC, testroleC, true);
		
		//------------------------------
		// Permissions
		CFW.DB.Permissions.create(new Permission("PermissionA", "user").description("This is the permission A."));
		permissionA = CFW.DB.Permissions.selectByName("PermissionA");
		CFW.DB.RolePermissionMap.addPermissionToRole(permissionA, testroleA, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionAA", "user").description("This is the permission AA."));
		permissionAA = CFW.DB.Permissions.selectByName("PermissionAA");
		CFW.DB.RolePermissionMap.addPermissionToRole(permissionAA, testroleA, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionAAA", "user").description("This is the permission AAA."));
		permissionAAA = CFW.DB.Permissions.selectByName("PermissionAAA");
		CFW.DB.RolePermissionMap.addPermissionToRole(permissionAAA, testroleA, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionB", "user").description("This is the permission B."));
		permissionB = CFW.DB.Permissions.selectByName("PermissionB");
		CFW.DB.RolePermissionMap.addPermissionToRole(permissionB, testroleB, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionBB", "user").description("This is the permission BB."));
		permissionBB = CFW.DB.Permissions.selectByName("PermissionBB");
		CFW.DB.RolePermissionMap.addPermissionToRole(permissionBB, testroleB, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionC", "user").description("This is the permission C."));
		permissionC = CFW.DB.Permissions.selectByName("PermissionC");
		CFW.DB.RolePermissionMap.addPermissionToRole(permissionC, testroleC, true);
	}

}
