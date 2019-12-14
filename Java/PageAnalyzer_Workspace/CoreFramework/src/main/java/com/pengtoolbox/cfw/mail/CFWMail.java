package com.pengtoolbox.cfw.mail;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWMail {
	
	private static Logger logger = CFWLog.getLogger(CFWMail.class.getName());
	
	/***************************************************************
	 * Creates a session for sending mails.
	 * @return session or null if mailing is disabled
	 ***************************************************************/
	public static Session initializeSession() {

		if(CFW.Properties.MAIL_ENABLED) {
			final String authMethod = CFW.Properties.MAIL_SMTP_AUTHENTICATION.trim().toUpperCase();
			final String loginEmail = CFW.Properties.MAIL_SMTP_LOGIN_MAIL;
			final String password = CFW.Properties.MAIL_SMTP_LOGIN_PASSWORD; 
			
			//------------------------------
			//General Properties
			Properties props = new Properties();
			props.put("mail.smtp.host", CFW.Properties.MAIL_SMTP_HOST); 
			
			//------------------------------
			// TLS Authentication
			if(authMethod.equals("TLS")) {
				props.put("mail.smtp.port", CFW.Properties.MAIL_SMTP_PORT); //TLS Port
				props.put("mail.smtp.auth", "true"); //enable authentication
				props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
				
				Authenticator auth = new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(loginEmail, password);
					}
				};
				
				return Session.getInstance(props, auth);
			}
			
			//------------------------------
			// SSL Authentication
			if(authMethod.equals("SSL")){
				System.out.println("SSLEmail Start");
				
				props.put("mail.smtp.socketFactory.port", CFW.Properties.MAIL_SMTP_PORT);
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
				props.put("mail.smtp.auth", "true"); 
				props.put("mail.smtp.port", CFW.Properties.MAIL_SMTP_PORT); 
				
				Authenticator auth = new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(loginEmail, password);
					}
				};
				
				return Session.getInstance(props, auth);
			}
			
			//------------------------------
			// No Authentication
			return Session.getInstance(props, null);
			}
		return null;
	}
	
	
	/***************************************************************
	 * Utility method to send simple HTML email
	 * @param toEmail
	 * @param subject
	 * @param body
	 ***************************************************************/
	public static void sendEmailFromNoReply(String toEmail, String subject, String body){
		Session session = initializeSession();
		
		sendEmailFromNoReply(session, toEmail, subject, body);
	}
	
	/***************************************************************
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 ***************************************************************/
	public static void sendEmailFromNoReply(Session session, String toEmail, String subject, String body){
		
		if(CFW.Properties.MAIL_ENABLED) {
			try
		    {
		     
				//---------------------------
				// Create Message
				MimeMessage msg = new MimeMessage(session);
				
				msg.addHeader("Content-type", "text/html; charset=UTF-8");
				msg.addHeader("Content-Transfer-Encoding", "8bit");
				
				msg.setFrom(new InternetAddress(CFW.Properties.MAIL_SMTP_FROMMAIL_NOREPLY, "no-reply"));
				msg.setReplyTo(InternetAddress.parse(CFW.Properties.MAIL_SMTP_FROMMAIL_NOREPLY, false));
				
				msg.setSubject(subject, "UTF-8");
				msg.setContent(body, "text/html");
				
				msg.setSentDate(new Date());
				
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
				
				//---------------------------
				// Send Message
				Transport.send(msg);  
				

		    }
		    catch (Exception e) {
		    	new CFWLog(logger)
		    		.method("sendEmailFromNoReply")
		    		.severe("Exception occured while sending mail.", e);
		    }
		}
	}
}
