package com.pengtoolbox.pageanalyzer.login;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

import com.pengtoolbox.pageanalyzer._main.PA;
import com.pengtoolbox.pageanalyzer.logging.PALogger;
import com.pengtoolbox.pageanalyzer.servlets.LoginServlet;

public class LDAPLoginProvider implements LoginProvider {
	
	private static Logger logger = PALogger.getLogger(LoginServlet.class.getName());
	
	private static final String LDAP_URL = PA.config("authentication_ldap_url");
	private static final String LDAP_USER = PA.config("authentication_ldap_user");
	private static final String LDAP_PASSWORD = PA.config("authentication_ldap_password");
	
	private static final String LDAP_SEARCHBASE = PA.config("authentication_ldap_searchbase");
	private static final String LDAP_USER_ATTRIBUTE = PA.config("authentication_ldap_user_attribute");
	
	public LDAPLoginProvider() {
		
	}
	
	@Override
	public boolean checkCredentials(String username, String password) {
		
		Properties props = new Properties(); 
		InitialDirContext context = null;
		String user = "";
		
		try {
		    props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		    props.put(Context.PROVIDER_URL, LDAP_URL);
		    props.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
		    props.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
	
		    context = new InitialDirContext(props);
	
		    SearchControls ctrls = new SearchControls();
		    ctrls.setReturningAttributes(new String[] {});
		    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	
		    NamingEnumeration<javax.naming.directory.SearchResult> answers = context.search(LDAP_SEARCHBASE, "("+LDAP_USER_ATTRIBUTE+"=" + username + ")", ctrls);
		    javax.naming.directory.SearchResult result = answers.nextElement();
	
		    user = result.getNameInNamespace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	    
		
	    try {
	        props = new Properties();
	        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	        props.put(Context.PROVIDER_URL, LDAP_URL);
	        props.put(Context.SECURITY_PRINCIPAL, user);
	        props.put(Context.SECURITY_CREDENTIALS, password);

	        context = new InitialDirContext(props);
	    } catch (Exception e) {
	        return false;
	    }
	    return true;
	}
	

}
