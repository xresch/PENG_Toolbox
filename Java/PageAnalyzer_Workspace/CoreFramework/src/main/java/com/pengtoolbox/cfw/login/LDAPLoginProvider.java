package com.pengtoolbox.cfw.login;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

import com.pengtoolbox.cfw._main.CFWConfig;
import com.pengtoolbox.cfw.logging.CFWLogger;
import com.pengtoolbox.cfw.servlets.LoginServlet;

public class LDAPLoginProvider implements LoginProvider {
	
	private static Logger logger = CFWLogger.getLogger(LoginServlet.class.getName());
		
	public LDAPLoginProvider() {
		
	}
	
	@Override
	public boolean checkCredentials(String username, String password) {
		
		Properties props = new Properties(); 
		InitialDirContext context = null;
		String user = "";
		
		try {
		    props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		    props.put(Context.PROVIDER_URL, CFWConfig.LDAP_URL);
		    props.put(Context.SECURITY_PRINCIPAL, CFWConfig.LDAP_USER);
		    props.put(Context.SECURITY_CREDENTIALS, CFWConfig.LDAP_PASSWORD);
	
		    context = new InitialDirContext(props);
	
		    SearchControls ctrls = new SearchControls();
		    ctrls.setReturningAttributes(new String[] {});
		    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	
		    NamingEnumeration<javax.naming.directory.SearchResult> answers = context.search(CFWConfig.LDAP_SEARCHBASE, "("+CFWConfig.LDAP_USER_ATTRIBUTE+"=" + username + ")", ctrls);
		    javax.naming.directory.SearchResult result = answers.nextElement();
	
		    user = result.getNameInNamespace();
		}catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	    
		
	    try {
	        props = new Properties();
	        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	        props.put(Context.PROVIDER_URL, CFWConfig.LDAP_URL);
	        props.put(Context.SECURITY_PRINCIPAL, user);
	        props.put(Context.SECURITY_CREDENTIALS, password);

	        context = new InitialDirContext(props);
	        
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	

}
