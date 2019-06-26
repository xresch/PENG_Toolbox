package com.pengtoolbox.cfw.tests.db.user;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.db.usermanagement.User.UserStatus;
import com.pengtoolbox.cfw.tests._master.DBTestMaster;
import com.pengtoolbox.cfw.utils.CFWSecurity;

public class TestCFWDBUser extends DBTestMaster {

	@Test
	public void testCreatePasswordHash() {
		
		String salt = CFW.Security.createPasswordSalt(31);
		String hashtext = CFWSecurity.createPasswordHash("admin", salt);
		
		System.out.println("Salt: "+salt);
        System.out.println("Hashtext: "+hashtext);

        Assertions.assertTrue(salt.length() == 31);
        Assertions.assertTrue(hashtext.length() == 127);
        
	}
	
	@Test
	public void testCRUDUser() {
		
		String username = "t.testonia";
		String usernameUpdated = "t.testonia2";
		
		//--------------------------------------
		// Cleanup
		User userToDelete = CFW.DB.Users.selectByUsernameOrMail(username);
		CFW.DB.Users.deleteByID(userToDelete.id());
		
		userToDelete = CFW.DB.Users.selectByUsernameOrMail(usernameUpdated);
		CFW.DB.Users.deleteByID(userToDelete.id());
		
		Assertions.assertFalse(CFW.DB.Users.isUsernameUsed(username), "DB is not cleaned up.");
		Assertions.assertFalse(CFW.DB.Users.isUsernameUsed(usernameUpdated), "DB is not cleaned up.");
		
		
		//--------------------------------------
		// CREATE
		CFW.DB.Users.create(new User()
				.username(username)
				.email("t.testonia@cfw.com")
				.firstname("Testika")
				.lastname("Testonia")
				.passwordHash("hash")
				.passwordSalt("salt")
				.status(UserStatus.BLOCKED)
				.isDeletable(true)
				.isRenamable(false)
				.isForeign(true)
				);
		
		Assertions.assertTrue(CFW.DB.Users.isUsernameUsed(username));
		
		//--------------------------------------
		// SELECT
		User user = CFW.DB.Users.selectByUsernameOrMail(username);
		
		System.out.println("===== USER =====");
		System.out.println(user.toString());

		Assertions.assertTrue(user != null);
		Assertions.assertTrue(user.username().equals(username));
		Assertions.assertTrue(user.email().equals("t.testonia@cfw.com"));
		Assertions.assertTrue(user.firstname().equals("Testika"));
		Assertions.assertTrue(user.lastname().equals("Testonia"));
		Assertions.assertTrue(user.passwordHash().equals("hash"));
		Assertions.assertTrue(user.passwordSalt().equals("salt"));
		Assertions.assertTrue(user.status().equals(UserStatus.BLOCKED.toString()));
		Assertions.assertTrue(user.isDeletable() == true);
		Assertions.assertTrue(user.isRenamable() == false);
		Assertions.assertTrue(user.isForeign() == true);
		
		//--------------------------------------
		// UPDATE
		user.username(usernameUpdated)
			.email("t.testonia2@cfw.com")
			.firstname("Testika2")
			.lastname("Testonia2")
			.passwordHash("hash2")
			.passwordSalt("salt2")
			.status(UserStatus.INACTIVE)
			.isDeletable(false)
			.isRenamable(true)
			.isForeign(false);
		
		CFW.DB.Users.update(user);
		
		//--------------------------------------
		// SELECT
		User userUpdated = CFW.DB.Users.selectByUsernameOrMail(usernameUpdated);
		
		System.out.println("===== UPDATED USER =====");
		System.out.println(userUpdated.toString());
		
		Assertions.assertTrue(userUpdated != null);
		Assertions.assertTrue(userUpdated.username().equals(usernameUpdated));
		Assertions.assertTrue(userUpdated.email().equals("t.testonia2@cfw.com"));
		Assertions.assertTrue(userUpdated.firstname().equals("Testika2"));
		Assertions.assertTrue(userUpdated.lastname().equals("Testonia2"));
		Assertions.assertTrue(userUpdated.passwordHash().equals("hash2"));
		Assertions.assertTrue(userUpdated.passwordSalt().equals("salt2"));
		Assertions.assertTrue(userUpdated.status().equals(UserStatus.INACTIVE.toString()));
		Assertions.assertTrue(userUpdated.isDeletable() == false);
		Assertions.assertTrue(userUpdated.isRenamable() == true);
		Assertions.assertTrue(userUpdated.isForeign() == false);

		//--------------------------------------
		// SELECT
		User userbyMail = CFW.DB.Users.selectByUsernameOrMail("t.testonia2@cfw.com");
		
		Assertions.assertTrue( (userbyMail != null), "Select User by Mail works.");
		
		
		//--------------------------------------
		// DELETE
		CFW.DB.Users.deleteByID(userbyMail.id());
		
		Assertions.assertFalse(CFW.DB.Users.isUsernameUsed(username));
		
	}
}
