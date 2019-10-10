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
import java.util.HashMap;
import java.util.logging.Logger;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFW.Properties;
import com.pengtoolbox.cfw._main.CFWProperties;
import com.pengtoolbox.cfw.datahandling.CFWField.FormFieldType;
import com.pengtoolbox.cfw.db.config.Configuration;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBPermission;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.Permission;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDB {

	private static JdbcDataSource dataSource;
	private static JdbcConnectionPool connectionPool;
	private static Server server;
	private static boolean isInitialized = false;

	private static Logger logger = CFWLog.getLogger(CFWDB.class.getName());
	
	private static InheritableThreadLocal<Connection> transactionConnection = new InheritableThreadLocal<Connection>();

	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initialize() {
    	
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
			
			initializeTables();
			createDefaultEntries();
			
			resetAdminPW();
			
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
			admin.setInitialPassword("admin", "admin");
			
			if(CFW.DB.Users.update(admin)) {
				new CFWLog(logger)
				.method("resetAdminPW")
				.info("Admin password was reset to default!");
			}else{
				new CFWLog(logger)
				.method("resetAdminPW")
				.warn("Admin password was reset failed!");
			};
		}else {
			new CFWLog(logger)
			.method("resetAdminPW")
			.warn("Admin password was reset failed!");
		}
	}
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public static void initializeTables() {
		
		CFW.DB.Config.initializeTable();
		CFW.DB.Users.initializeTable();
		CFW.DB.Groups.initializeTable();
		CFW.DB.UserGroupMap.initializeTable();
		CFW.DB.Permissions.initializeTable();
		CFW.DB.GroupPermissionMap.initializeTable();
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	private static void createDefaultEntries() {
		
		CFWDB.createDefaultConfigs();
		CFWDB.createDefaultGroups();
		CFWDB.createDefaultPermissions();
		CFWDB.createDefaultUsers();
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	private static void createDefaultConfigs() {
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Config.checkConfigExists(Configuration.FILE_CACHING)) {
			CFW.DB.Config.create(
				new Configuration("Core Framework", Configuration.FILE_CACHING)
					.description("Enables the caching of files read from the disk.")
					.type(FormFieldType.BOOLEAN)
					.value("true")
			);
		}
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Config.checkConfigExists(Configuration.THEME)) {
			CFW.DB.Config.create(
				new Configuration("Core Framework", Configuration.THEME)
					.description("Set the application look and feel. 'Slate' is the default and recommended theme, all others are not 100% tested.")
					.type(FormFieldType.SELECT)
					.options(new String[]{"flatly", "lumen", "materia", "minty", "pulse", "sandstone", "simplex", "sketchy", "slate", "spacelab", "superhero", "united"})
					.value("slate")
			);
		}
				
				
		CFW.DB.Config.updateCache();
		
		
		
	}
	
	/********************************************************************************************
	 *
	 ********************************************************************************************/
	private static void createDefaultGroups() {
		//-----------------------------------------
		// Create Group Superuser
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.CFW_GROUP_SUPERUSER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.CFW_GROUP_SUPERUSER)
				.description("Superusers have all the privileges in the system. They are above administrators. ")
				.isDeletable(false)
			);
		}
		
		Group superuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_SUPERUSER);
		
		if(superuserGroup == null) {
			new CFWLog(logger)
			.method("createDefaultGroups")
			.severe("User group '"+CFWDBGroup.CFW_GROUP_SUPERUSER+"' was not found in the database.");
		}
		
		superuserGroup.isRenamable(false);
		CFW.DB.Groups.update(superuserGroup);
		
		//-----------------------------------------
		// Create Group Admin
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.CFW_GROUP_ADMIN)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.CFW_GROUP_ADMIN)
				.description("Administrators have the privileges to manage the application.")
				.isDeletable(false)
			);
		}
		
		Group adminGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_ADMIN);
		
		if(adminGroup == null) {
			new CFWLog(logger)
			.method("createDefaultGroups")
			.severe("User group '"+CFWDBGroup.CFW_GROUP_ADMIN+"' was not found in the database.");
		}
		
		adminGroup.isRenamable(false);
		CFW.DB.Groups.update(adminGroup);
		//-----------------------------------------
		// Create Group Foreign
		//-----------------------------------------
		if(!CFW.DB.Groups.checkGroupExists(CFWDBGroup.CFW_GROUP_USER)) {
			CFW.DB.Groups.create(new Group(CFWDBGroup.CFW_GROUP_USER)
				.description("Default User group. New users will automatically be added to this group if they are not managed by a foreign source.")
				.isDeletable(false)
			);
		}
		
		Group userGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_USER);
		
		if(userGroup == null) {
			new CFWLog(logger)
			.method("createDefaultGroups")
			.severe("User group '"+CFWDBGroup.CFW_GROUP_USER+"' was not found in the database.");
		}
		
		userGroup.isRenamable(false);
		CFW.DB.Groups.update(userGroup);
		
		//-----------------------------------------
		// Create Group Foreign
		//-----------------------------------------
		Group foreignuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_FOREIGN_USER);
		
		if(!(foreignuserGroup == null)) {
			foreignuserGroup.isRenamable(true);
			foreignuserGroup.isDeletable(true);
			CFW.DB.Groups.update(foreignuserGroup);
		}

	}
	
	/********************************************************************************************
	 * Groups have to be existing before calling this method.
	 * 
	 ********************************************************************************************/
	private static void createDefaultPermissions() {
		
		//-----------------------------------------
		//
		//-----------------------------------------
		if(!CFW.DB.Permissions.checkPermissionExists(CFWDBPermission.CFW_USER_MANAGEMENT)) {
			CFW.DB.Permissions.create(new Permission(CFWDBPermission.CFW_USER_MANAGEMENT)
				.description("Gives the user the ability to view, create, update and delete users.")
				.isDeletable(false)
			);
			
			Permission userManagement = CFW.DB.Permissions.selectByName(CFWDBPermission.CFW_USER_MANAGEMENT);
			
			if(userManagement == null) {
				new CFWLog(logger)
				.method("createDefaultPermissions")
				.severe("User permission '"+CFWDBPermission.CFW_USER_MANAGEMENT+"' was not found in the database.");
			}
		}
		
		//-----------------------------------------
		// 
		//-----------------------------------------
		if(!CFW.DB.Permissions.checkPermissionExists(CFWDBPermission.CFW_CONFIG_MANAGEMENT)) {
			CFW.DB.Permissions.create(new Permission(CFWDBPermission.CFW_CONFIG_MANAGEMENT)
				.description("Gives the user the ability to view and update the configurations in the database.")
				.isDeletable(false)
			);
			
			Permission userManagement = CFW.DB.Permissions.selectByName(CFWDBPermission.CFW_CONFIG_MANAGEMENT);
			
			if(userManagement == null) {
				new CFWLog(logger)
				.method("createDefaultPermissions")
				.severe("User permission '"+CFWDBPermission.CFW_CONFIG_MANAGEMENT+"' was not found in the database.");
			}
		}
		

	}
	/********************************************************************************************
	 * Groups have to be existing.
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
					.setInitialPassword(initialPassword, initialPassword)
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
				.setInitialPassword("admin", "admin")
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
		// Add Admin to group Superuser
		//-----------------------------------------
		Group superuserGroup = CFW.DB.Groups.selectByName(CFWDBGroup.CFW_GROUP_SUPERUSER);
		
		if(!CFW.DB.UserGroupMap.checkIsUserInGroup(adminUser, superuserGroup)) {
			CFW.DB.UserGroupMap.addUserToGroup(adminUser, superuserGroup, false);
		}
		//Needed for Upgrade
		CFW.DB.UserGroupMap.updateIsDeletable(adminUser.id(), superuserGroup.id(), false);

		if(!CFW.DB.UserGroupMap.checkIsUserInGroup(adminUser, superuserGroup)) {
			new CFWLog(logger)
			.method("createDefaultUsers")
			.severe("User 'admin' is not assigned to group 'Superuser'.");
		}
		
		//-----------------------------------------
		// Upgrade Step: Superuser permissions undeletable
		//-----------------------------------------
		HashMap<String, Permission> permissions = CFW.DB.GroupPermissionMap.selectPermissionsForGroup(superuserGroup);
		
		for(Permission p : permissions.values()) {
			CFW.DB.GroupPermissionMap.updateIsDeletable(p.id(), superuserGroup.id(), false);
		}
		
	}
	
	
	/********************************************************************************************
	 * Get a connection from the connection pool or returns the current connection used for the 
	 * transaction.
	 * 
	 * @throws SQLException 
	 ********************************************************************************************/
	public static Connection getConnection() throws SQLException {	
		if(isInitialized) {
			CFWLog log = new CFWLog(logger).method("getConnection");
			log.finer("DB Connections Active: "+connectionPool.getActiveConnections());
			
			if(transactionConnection.get() != null) {
				return transactionConnection.get();
			}else {
				return connectionPool.getConnection();
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
				.severe("A transaction was already started. Use commitTransaction() before starting another one.");
			return;
		}
		
		try {
			Connection con = CFWDB.getConnection();
			con.setAutoCommit(false);
			transactionConnection.set(con);
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
				if(conn != null && transactionConnection.get() == null) { conn.close(); }
				if(prepared != null) { prepared.close(); }
			} catch (SQLException e) {
				log.severe("Issue closing resources.", e);
			}
			
		}
		
		log.custom("sql", sql).end();
		return result;
	}
	
	/********************************************************************************************
	 * Returns a ResultSet includin generated keys.
	 * 
	 * @param sql string with placeholders
	 * @param values the values to be placed in the prepared statement
	 * @return int first generated key, -1 otherwise
	 ********************************************************************************************/
	public static int preparedInsert(String sql, Object... values){	
        
		CFWLog log = new CFWLog(logger).method("preparedInsert").start();
		Connection conn = null;
		PreparedStatement prepared = null;

		int generatedID = -1;
		try {
			//-----------------------------------------
			// Initialize Variables
			conn = CFWDB.getConnection();
			prepared = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			
			//-----------------------------------------
			// Prepare Statement
			CFWDB.prepareStatement(prepared, values);
			
			//-----------------------------------------
			// Execute
			int affectedRows = prepared.executeUpdate();

			if(affectedRows > 0) {
				ResultSet result = prepared.getGeneratedKeys();
				result.next();
				generatedID = result.getInt(1);
			}
		} catch (SQLException e) {
			log.severe("Database Error: "+e.getMessage(), e);
		} finally {
			try {
				if(conn != null && transactionConnection.get() == null) { conn.close(); }
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
				if(conn != null && transactionConnection == null) { conn.close(); }
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
	}
	
	/********************************************************************************************
	 * 
	 * @param request HttpServletRequest containing session data used for logging information(null allowed).
	 * @param resultSet which should be closed.
	 ********************************************************************************************/
	public static void close(ResultSet resultSet){
		
		try {
			if(resultSet != null && transactionConnection.get() == null) {
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
	 * Returns a jsonString with an array containing a json object for eeach row.
	 * Returns an empty array 
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
					
					String value = resultSet.getString(i);
					if(column.startsWith("JSON")) {
						json.append(value).append(",");
					}else {
						json.append("\"").append(value).append("\",");
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
	 * Create Testdata for testing purposes
	 ********************************************************************************************/
	public static void createTestData() {
		
		Group testgroupA, testgroupB, testgroupC;
		User testuserA, testuserB, testuserC;
		
		Permission  permissionA, permissionAA, permissionAAA, 
					permissionB, permissionBB,
					permissionC;
		//------------------------------
		// Groups
		CFW.DB.Groups.create(new Group("TestgroupA").description("This is the testgroup A."));
		testgroupA = CFW.DB.Groups.selectByName("TestgroupA");
		
		CFW.DB.Groups.create(new Group("TestgroupB").description("This is the testgroup B."));
		testgroupB = CFW.DB.Groups.selectByName("TestgroupB");
		
		CFW.DB.Groups.create(new Group("TestgroupC").description("This is the testgroup C."));
		testgroupC = CFW.DB.Groups.selectByName("TestgroupC");
		
		//------------------------------
		// Users
		CFW.DB.Users.create(new User("TestuserA")
				.setInitialPassword("TestuserA", "TestuserA")
				.email("testuserA@cfwtest.com")
				.firstname("Testika")
				.lastname("Testonia"));
		testuserA = CFW.DB.Users.selectByUsernameOrMail("TestuserA");
		CFW.DB.UserGroupMap.addUserToGroup(testuserA, testgroupA, true);
		CFW.DB.UserGroupMap.addUserToGroup(testuserA, testgroupB, true);
		CFW.DB.UserGroupMap.addUserToGroup(testuserA, testgroupC, true);
		
		CFW.DB.Users.create(new User("TestuserB")
				.setInitialPassword("TestuserB", "TestuserB")
				.email("testuserB@cfwtest.com")
				.firstname("Jane")
				.lastname("Doe"));
		testuserB = CFW.DB.Users.selectByUsernameOrMail("TestuserB");
		CFW.DB.UserGroupMap.addUserToGroup(testuserB, testgroupA, true);
		CFW.DB.UserGroupMap.addUserToGroup(testuserB, testgroupB, true);
		
		CFW.DB.Users.create(new User("TestuserC")
				.setInitialPassword("TestuserC", "TestuserC")
				.email("testuserC@cfwtest.com")
				.firstname("Paola")
				.lastname("Pernandez"));	
		testuserC = CFW.DB.Users.selectByUsernameOrMail("TestuserC");
		CFW.DB.UserGroupMap.addUserToGroup(testuserC, testgroupC, true);
		
		//------------------------------
		// Permissions
		CFW.DB.Permissions.create(new Permission("PermissionA").description("This is the permission A."));
		permissionA = CFW.DB.Permissions.selectByName("PermissionA");
		CFW.DB.GroupPermissionMap.addPermissionToGroup(permissionA, testgroupA, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionAA").description("This is the permission AA."));
		permissionAA = CFW.DB.Permissions.selectByName("PermissionAA");
		CFW.DB.GroupPermissionMap.addPermissionToGroup(permissionAA, testgroupA, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionAAA").description("This is the permission AAA."));
		permissionAAA = CFW.DB.Permissions.selectByName("PermissionAAA");
		CFW.DB.GroupPermissionMap.addPermissionToGroup(permissionAAA, testgroupA, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionB").description("This is the permission B."));
		permissionB = CFW.DB.Permissions.selectByName("PermissionB");
		CFW.DB.GroupPermissionMap.addPermissionToGroup(permissionB, testgroupB, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionBB").description("This is the permission BB."));
		permissionBB = CFW.DB.Permissions.selectByName("PermissionBB");
		CFW.DB.GroupPermissionMap.addPermissionToGroup(permissionBB, testgroupB, true);
		
		CFW.DB.Permissions.create(new Permission("PermissionC").description("This is the permission C."));
		permissionC = CFW.DB.Permissions.selectByName("PermissionC");
		CFW.DB.GroupPermissionMap.addPermissionToGroup(permissionC, testgroupC, true);
	}

}
