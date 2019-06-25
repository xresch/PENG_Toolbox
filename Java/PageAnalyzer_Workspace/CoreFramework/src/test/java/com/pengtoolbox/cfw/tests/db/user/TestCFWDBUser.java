package com.pengtoolbox.cfw.tests.db.user;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.utils.CFWSecurity;

public class TestCFWDBUser {

	@Test
	public void testCreatePasswordHash() {
		
		String salt = CFW.Security.createPasswordSalt(31);
		String hashtext = CFWSecurity.createPasswordHash("myTestPasswordsdfsf", salt);
		
		System.out.println("Salt: "+salt);
        System.out.println("Hashtext: "+hashtext);

        Assertions.assertTrue(salt.length() == 31);
        Assertions.assertTrue(hashtext.length() == 127);
	}
}
