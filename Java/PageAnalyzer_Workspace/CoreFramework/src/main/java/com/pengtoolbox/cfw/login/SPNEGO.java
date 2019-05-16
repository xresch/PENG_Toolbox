package com.pengtoolbox.cfw.login;

//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import org.eclipse.jetty.security.ConfigurableSpnegoLoginService;
//import org.eclipse.jetty.security.ConstraintMapping;
//import org.eclipse.jetty.security.ConstraintSecurityHandler;
//import org.eclipse.jetty.security.HashLoginService;
//import org.eclipse.jetty.security.authentication.AuthorizationService;
//import org.eclipse.jetty.security.authentication.ConfigurableSpnegoAuthenticator;
//import org.eclipse.jetty.security.authentication.SpnegoAuthenticator;
//import org.eclipse.jetty.server.Handler;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.session.DefaultSessionIdManager;
//import org.eclipse.jetty.server.session.SessionHandler;
//import org.eclipse.jetty.util.security.Constraint;

//Jetty Test Class:  https://github.com/eclipse/jetty.project/blob/9b7afd8a0341f4712031abd322ab8669f07f5c5b/jetty-client/src/test/java/org/eclipse/jetty/client/util/SPNEGOAuthenticationTest.java

//public class SPNEGO {
//	
//	    private String clientName = "spnego_client";
//	    private String clientPassword = "spnego_client_pwd";
//	    private Path serviceKeyTabPath = testDirPath.resolve("");
//	    private Path clientKeyTabPath = testDirPath.resolve("client.keytab");
//
//	    private ConfigurableSpnegoAuthenticator authenticator;
//	public static void initialize() {
//		 String domainRealm = "MY.COM";
//
//	    Constraint constraint = new Constraint();
//	    constraint.setName(Constraint.__SPNEGO_AUTH);
//	    constraint.setRoles(new String[]{domainRealm});
//	    constraint.setAuthenticate(true);
//
//	    ConstraintMapping cm = new ConstraintMapping();
//	    cm.setConstraint(constraint);
//	    cm.setPathSpec("/*");
//
//	    ConfigurableSpnegoLoginService loginService = new ConfigurableSpnegoLoginService();
//
//	    loginService.setConfig("/path/to/spnego.properties");
//	    loginService.setName(domainRealm);
//
//	    ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
//	    sh.setAuthenticator(new SpnegoAuthenticator());
//	    sh.setLoginService(loginService);
//	    sh.setConstraintMappings(new ConstraintMapping[]{cm});
//	    sh.setRealmName(domainRealm);
//	    
//	    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
//	    System.setProperty("java.security.auth.login.config", "/path/to/spnego.conf");
//	    System.setProperty("java.security.krb5.conf", "/path/to/krb5.ini");
//	    
//	}
//	
//	public static void startSPNEGO(Server server, Handler handler) {
//		 String domainRealm = "EXAMPLE.COM";
//		 
//		server.setSessionIdManager(new DefaultSessionIdManager(server));
//        HashLoginService authorizationService = new HashLoginService(domainRealm, "path/to/realm.properties");
//        ConfigurableSpnegoLoginService loginService = new ConfigurableSpnegoLoginService(domainRealm, AuthorizationService.from(authorizationService, ""));
//        loginService.addBean(authorizationService);
//        loginService.setKeyTabPath(Paths.get("path/to/service.keytab"));
//
//        loginService.setServiceName("srvc");
//        loginService.setHostName("localhost");
//        server.addBean(loginService);
//
//        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
//        Constraint constraint = new Constraint();
//        constraint.setAuthenticate(true);
//        constraint.setRoles(new String[]{"**"}); //allow any authenticated user
//        ConstraintMapping mapping = new ConstraintMapping();
//        mapping.setPathSpec("/secure");
//        mapping.setConstraint(constraint);
//        securityHandler.addConstraintMapping(mapping);
//        ConfigurableSpnegoAuthenticator authenticator = new ConfigurableSpnegoAuthenticator();
//        securityHandler.setAuthenticator(authenticator);
//        securityHandler.setLoginService(loginService);
//        securityHandler.setHandler(handler);
//
//        SessionHandler sessionHandler = new SessionHandler();
//        sessionHandler.setHandler(securityHandler);
//		
//	}
//
//}
