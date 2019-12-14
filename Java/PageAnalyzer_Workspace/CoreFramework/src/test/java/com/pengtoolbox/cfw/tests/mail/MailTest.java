package com.pengtoolbox.cfw.tests.mail;

import java.io.IOException;

import org.junit.Test;

import com.pengtoolbox.cfw._main.CFW;

public class MailTest {
	
	@Test
	public void testMailNoSMTPAuthentication() throws IOException {
		CFW.Properties.loadProperties(CFW.CLI.getValue(CFW.CLI.CONFIG_FILE));
	    CFW.Mail.sendEmailFromNoReply("reto.scheiwiller5@bluewin.ch", "Testing Subject", "<!DOCTYPE HTML><html>SimpleEmail Testing Body. <strong>STRONG</strong></html>");
	}

}
