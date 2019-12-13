package com.pengtoolbox.cfw.login;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWProperties;
import com.pengtoolbox.cfw.db.usermanagement.User;
import com.pengtoolbox.cfw.db.usermanagement.User.UserFields;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class LDAPLoginProvider implements LoginProvider {

	public LDAPLoginProvider() {}
	
	@Override
	public User checkCredentials(String username, String password) {

		if(CFW.DB.Users.checkUsernameExists(username)) {
			//--------------------------------
			// Check User in DB			
			User user = CFW.DB.Users.selectByUsernameOrMail(username);
			if(user.isForeign()) {
				if(authenticateAgainstLDAP(username, password) != null) {
					return user;
				}
			}else {
				if(user.passwordValidation(password)) {
					return user;
				}
			}
		}else {
			//--------------------------------
			// Create User if password is correct
			
			return authenticateAgainstLDAP(username, password);

		}
		
		return null;
	}
		
	/**********************************************************************
	 * Authenticate against the LDAP defined in the cfw.properties.
	 * @param username
	 * @param password
	 * @return true if credentials are valid, false otherwise
	 **********************************************************************/
	private User authenticateAgainstLDAP(String username, String password) {
		Properties props = new Properties(); 
		InitialDirContext context = null;
		String userInNamespace = "";
		
		try {
		    props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		    props.put(Context.PROVIDER_URL, CFWProperties.LDAP_URL);
		    props.put(Context.SECURITY_PRINCIPAL, CFWProperties.LDAP_USER);
		    props.put(Context.SECURITY_CREDENTIALS, CFWProperties.LDAP_PASSWORD);
	
		    context = new InitialDirContext(props);
	
		    SearchControls ctrls = new SearchControls();
		    ctrls.setReturningAttributes(new String[] {});
		    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	
		    NamingEnumeration<javax.naming.directory.SearchResult> answers = context.search(CFWProperties.LDAP_SEARCHBASE, "("+CFWProperties.LDAP_USER_ATTRIBUTE+"=" + username + ")", ctrls);
		    if(answers.hasMore()) {
		    	
		    	//------------------------------
		    	// Read LDAP Attributes
		    	javax.naming.directory.SearchResult result = answers.nextElement();
		    	
		    	userInNamespace = result.getNameInNamespace();
		    	Attributes attr = context.getAttributes(userInNamespace);
		    	
		    	Attribute mail = attr.get(CFW.Properties.LDAP_MAIL_ATTRIBUTE);
		    	String emailString = null;
		    	if(mail != null) {
		    		emailString = mail.get(0).toString();
		    	}
		    	//System.out.println("user: "+user);
		    	//System.out.println("MAIL: "+mail);
		    	
		    	//------------------------------
		    	// Create User in DB if not exists
		    	User userFromDB = null;
		    	if(!CFW.DB.Users.checkUsernameExists(username)) {

			    	User newUser = new User(username)
							.isForeign(true)
							.status("Active")
							.email(emailString);

					CFW.DB.Users.create(newUser);
					userFromDB = CFW.DB.Users.selectByUsernameOrMail(username);
					
					CFW.DB.UserRoleMap.addUserToRole(userFromDB, CFW.DB.Roles.CFW_ROLE_USER, true);
		    	}else{
		    		userFromDB = CFW.DB.Users.selectByUsernameOrMail(username);
		    		
		    		//-----------------------------
		    		// Update mail if necessary
		    		if( (emailString != null && userFromDB.email() == null)
		    		 || (emailString != null && !userFromDB.email().equals(mail.get(0)) ) ) {
		    			userFromDB.email(emailString);
		    			userFromDB.update(UserFields.EMAIL.toString());
		    		}
		    	}
		    	
				return userFromDB;
		    }else {
		    	return null;
		    }
	
		}catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    
	}
	
}
