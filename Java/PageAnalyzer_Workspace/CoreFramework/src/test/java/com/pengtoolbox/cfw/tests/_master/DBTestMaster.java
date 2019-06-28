package com.pengtoolbox.cfw.tests._master;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.Group;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.logging.CFWLog;

public class DBTestMaster {
	
	private static Logger logger = CFWLog.getLogger(DBTestMaster.class.getName());
	
	protected static Group testgroup;
	protected static User testuser;
	protected static User testuser2;
	protected static User testuser3;
	
	
	@BeforeClass
	public static void startDefaultApplication() throws Exception {
		
		CFW.initialize("./config/cfw.properties");
		CFWDB.initialize();
		fillWithTestData();
	}
	
	public static void fillWithTestData() {
		
		CFW.DB.Groups.create(new Group().name("Testgroup"));
		testgroup = CFW.DB.Groups.selectByName("Testgroup");
		
		CFW.DB.Users.create(new User().username("testuser").setInitialPassword("testuser"));
		testuser = CFW.DB.Users.selectByUsernameOrMail("testuser");
		CFW.DB.UserGroupMap.addUserToGroup(testuser, testgroup);
		
		CFW.DB.Users.create(new User().username("testuser2").setInitialPassword("testuser2"));
		testuser2 = CFW.DB.Users.selectByUsernameOrMail("testuser2");
		CFW.DB.UserGroupMap.addUserToGroup(testuser2, testgroup);
		
		CFW.DB.Users.create(new User().username("testuser2").setInitialPassword("testuser3"));	
		testuser2 = CFW.DB.Users.selectByUsernameOrMail("testuser2");
		CFW.DB.UserGroupMap.addUserToGroup(testuser3, testgroup);
	}
	
	@AfterClass
	public static void stopDefaultApplication() throws Exception {

	}
}
