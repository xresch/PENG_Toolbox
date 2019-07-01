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
	
	protected static Group testgroupA;
	protected static Group testgroupB;
	protected static Group testgroupC;
	
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
		
		CFW.DB.Groups.create(new Group().name("TestgroupA"));
		testgroupA = CFW.DB.Groups.selectByName("TestgroupA");
		
		CFW.DB.Groups.create(new Group().name("TestgroupB"));
		testgroupB = CFW.DB.Groups.selectByName("TestgroupB");
		
		CFW.DB.Groups.create(new Group().name("TestgroupC"));
		testgroupC = CFW.DB.Groups.selectByName("TestgroupC");
		
		CFW.DB.Users.create(new User().username("testuser").setInitialPassword("testuser", "testuser"));
		testuser = CFW.DB.Users.selectByUsernameOrMail("testuser");
		CFW.DB.UserGroupMap.addUserToGroup(testuser, testgroupA);
		CFW.DB.UserGroupMap.addUserToGroup(testuser, testgroupB);
		CFW.DB.UserGroupMap.addUserToGroup(testuser, testgroupC);
		
		CFW.DB.Users.create(new User().username("testuser2").setInitialPassword("testuser2", "testuser2"));
		testuser2 = CFW.DB.Users.selectByUsernameOrMail("testuser2");
		CFW.DB.UserGroupMap.addUserToGroup(testuser2, testgroupA);
		CFW.DB.UserGroupMap.addUserToGroup(testuser2, testgroupB);
		
		CFW.DB.Users.create(new User().username("testuser3").setInitialPassword("testuser3", "testuser3"));	
		testuser3 = CFW.DB.Users.selectByUsernameOrMail("testuser3");
		CFW.DB.UserGroupMap.addUserToGroup(testuser3, testgroupA);
	}
	
	@AfterClass
	public static void stopDefaultApplication() throws Exception {

	}
}
