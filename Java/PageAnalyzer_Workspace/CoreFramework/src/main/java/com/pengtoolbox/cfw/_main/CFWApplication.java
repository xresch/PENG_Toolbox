package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.servlet.MultipartConfigElement;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.pengtoolbox.cfw.api.APILoginServlet;
import com.pengtoolbox.cfw.api.CFWAPIServlet;
import com.pengtoolbox.cfw.handlers.AuthenticationHandler;
import com.pengtoolbox.cfw.handlers.HTTPSRedirectHandler;
import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.login.LoginServlet;
import com.pengtoolbox.cfw.servlets.AssemblyServlet;
import com.pengtoolbox.cfw.servlets.ConfigurationServlet;
import com.pengtoolbox.cfw.servlets.FormServlet;
import com.pengtoolbox.cfw.servlets.JARResourceServlet;
import com.pengtoolbox.cfw.servlets.LogoutServlet;
import com.pengtoolbox.cfw.servlets.PermissionsServlet;
import com.pengtoolbox.cfw.servlets.admin.APIUserMgmtSevlet;
import com.pengtoolbox.cfw.servlets.admin.UserManagementServlet;
import com.pengtoolbox.cfw.servlets.userprofile.ChangePasswordServlet;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;

public class CFWApplication {
	
	private Server server;
	private MultipartConfigElement globalMultipartConfig;
	
	private static ArrayList<ContextHandler> unsecureContextArray = new ArrayList<ContextHandler>();
	private static ArrayList<ContextHandler> secureContextArray = new ArrayList<ContextHandler>();
	
	private static ArrayList<ServletContextHandler> servletContextArray = new ArrayList<ServletContextHandler>();	
	
	private String defaultURL = "/";
	static DefaultSessionIdManager idmanager;
	private SessionHandler sessionHandler;	
	
	public static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	public WebAppContext applicationContext;
	
	private static ServletContextHandler cfwContext;
	
	public CFWApplication(String[] args) throws Exception {
		    	       
		sessionHandler = CFWApplication.createSessionHandler();
		
    	//---------------------------------------
    	// Create Server 
        server = CFWApplication.createServer();
        applicationContext = new WebAppContext();
        applicationContext.setContextPath("/");
        applicationContext.setServer(server);
        applicationContext.setSessionHandler(sessionHandler);
        applicationContext.setErrorHandler(CFWApplication.createErrorHandler());

    	//---------------------------------------
    	// Default Multipart Config
        int maxSize = 1024*1024*CFW.Properties.APPLICATION_MAX_UPLOADSIZE;
        globalMultipartConfig = new MultipartConfigElement(null, maxSize, maxSize, maxSize);
         
	}
	
	/**************************************************************************************************
	 * Returns a ServletContextHandler that can be accesses without a user login.
	 * Adds several handlers like gzipHandler, SessionHandler and RequestHandler.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public ServletContextHandler createUnsecureContext(String relativePath){

        //----------------------------------
        // Build Handler Chain
        ContextHandler unsecureContextHandler = new ContextHandler(CFWProperties.BASE_URL+""+relativePath);	
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);   
        servletContext.setSessionHandler(createSessionHandler());
        
        new HandlerChainBuilder(unsecureContextHandler)
	        .chain(new GzipHandler())
	    	.chain(new RequestHandler())
	        .chain(servletContext);
        
        unsecureContextArray.add(unsecureContextHandler);
        servletContextArray.add(servletContext);
        return servletContext;
	}
	
	/**************************************************************************************************
	 * Returns a ServletContextHandler that can be accesses with a prior user login.
	 * Adds several handlers like gzipHandler, SessionHandler, AuthenticationHandler and RequestHandler.
	 * 
	 * @param the relative path of the context, CFWConfig.BASE_URL will be prepended.
	 **************************************************************************************************/
	public ServletContextHandler createSecureContext(String relativePath){

        //-------------------------------
        // Create HandlerChain
        //-------------------------------
        ContextHandler secureContext = new ContextHandler(CFWProperties.BASE_URL+""+relativePath);
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setSessionHandler(createSessionHandler());
        
        new HandlerChainBuilder(secureContext)
        	.chain(new GzipHandler())
	        .chain(new RequestHandler())
	        .chain(new AuthenticationHandler())
	        .chain(servletContext);
       
        secureContextArray.add(secureContext);
        servletContextArray.add(servletContext);
        return servletContext;
	}
	
	/**************************************************************************************************
	 * @throws Exception
	 **************************************************************************************************/
	public void start() throws Exception {
		
        //-------------------------------
        // Create Rewrite Handler
        //-------------------------------
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        rewriteHandler.setOriginalPathAttribute("requestedPath");

        RedirectRegexRule mainRedirect = new RedirectRegexRule();
        mainRedirect.setRegex("^/$"+
        					 "|"+CFWProperties.BASE_URL+"/?$"+
        					 "|"+CFWProperties.BASE_URL+"/app/?$");
        mainRedirect.setReplacement(CFWProperties.BASE_URL+defaultURL);
        rewriteHandler.addRule(mainRedirect);	
        
		// TODO Auto-generated method stub
        //###################################################################
        // Create Handler Collection
        //###################################################################
        
        //Connect all relevant Handlers
        ArrayList<Handler> handlerArray = new ArrayList<Handler>();
        handlerArray.add(new ShutdownHandler(CFW.Properties.APPLICATION_ID, true, true));
        handlerArray.add(new HTTPSRedirectHandler());
        handlerArray.addAll(unsecureContextArray);
        handlerArray.add(rewriteHandler);
        handlerArray.addAll(secureContextArray);
        handlerArray.add(CFWApplication.createResourceHandler());
        handlerArray.add(createCFWHandler());
        
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(handlerArray.toArray(new Handler[] {}));
        server.setHandler(handlerCollection);
        
        //###################################################################
        // Startup
        //###################################################################
        server.start();
        server.join();
	}
	
	/**************************************************************************************************
	 * 
	 **************************************************************************************************/
	public static void stop() {
		
		System.out.println("Try to stop running application instance.");
		
		//----------------------------------
		// Resolve Port to use
		String protocol = "http";
		int port = CFW.Properties.HTTP_PORT;
		if(!CFW.Properties.HTTP_ENABLED && CFW.Properties.HTTPS_ENABLED) {
			protocol = "https";
			port = CFW.Properties.HTTPS_PORT;
		}
		
		//----------------------------------
		// Try Stop 
        try {
        	URL url = new URL(protocol, "localhost", port, "/shutdown?token="+CFW.Properties.APPLICATION_ID);
        	 HttpURLConnection connection = (HttpURLConnection)url.openConnection();
             connection.setRequestMethod("POST");

             if(connection.getResponseCode() == 200) {
            	 System.out.println("Shutdown successful.");
             }else {
            	 System.err.println("Jetty returned response code HTTP "+connection.getResponseCode());
             }
             
        } catch (IOException ex) {
            System.err.println("Stop Jetty failed: " + ex.getMessage());
        }
	}

	public Server getServer() {
		return server;
	}

	public MultipartConfigElement getGlobalMultipartConfig() {
		return globalMultipartConfig;
	}

	public String getDefaultURL() {
		return defaultURL;
	}

	/**************************************************************************************************
	 * Set the Default URL to which an incomplete URL should be redirected to.
	 **************************************************************************************************/
	public void setDefaultURL(String defaultURL) {
		this.defaultURL = defaultURL;
	}

	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  LoginServlet on /login
	 *  LogoutServlet on /logout
	 ***********************************************************************/
	public void addCFWServlets(ServletContextHandler servletContextHandler) {
		
		cfwContext = servletContextHandler;
		//-----------------------------------------
		// Authentication Servlets
	    if(CFWProperties.AUTHENTICATION_ENABLED) {
	        servletContextHandler.addServlet(LoginServlet.class, "/login");
	        servletContextHandler.addServlet(LogoutServlet.class,  "/logout");
	    }
	    
	    
		//-----------------------------------------
		// User Profile Servlets
	    servletContextHandler.addServlet(CFWAPIServlet.class,  "/api");
	    
		//-----------------------------------------
		// User Profile Servlets
	    servletContextHandler.addServlet(ChangePasswordServlet.class,  "/changepassword");
	    
		//-----------------------------------------
		// User Management Servlets
	    servletContextHandler.addServlet(ConfigurationServlet.class,  "/configuration");
	    servletContextHandler.addServlet(UserManagementServlet.class,  "/usermanagement");
	    servletContextHandler.addServlet(PermissionsServlet.class,  "/usermanagement/permissions");
		servletContextHandler.addServlet(APIUserMgmtSevlet.class, "/usermanagement/data"); 
	    
	}
	
	/**************************************************************************************************
	 * Propagates all sessions and session data to all available session context handlers.
	 * @throws Exception
	 **************************************************************************************************/
	public static String propagateSessionDataToOtherContexts(HttpServletRequest request, SessionData data) {
		
		String sessionID = "";
		for(ServletContextHandler handler : servletContextArray) {
			HttpSession session = handler.getSessionHandler().getSession(request.getSession().getId());
			if(session == null) {
				session = handler.getSessionHandler().newHttpSession(request);
			}
			session.setAttribute(CFW.SESSION_DATA, data);
			sessionID = session.getId();
			System.out.println("SessionID: "+sessionID);
		}
		
		return sessionID;
	}

	/**************************************************************************************************
	 * Create an error handler.
	 * @throws Exception
	 **************************************************************************************************/
	public static ErrorHandler createErrorHandler() throws Exception {
//	    ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
//	    errorHandler.addErrorPage(404, "/missing.html");
//	    context.setErrorHandler(errorHandler);
	    
		// Extend ErrorHandler and overwrite methods to create custom error page
	    ErrorHandler handler = new ErrorHandler();
	    return handler;
	}

	/***********************************************************************
	 * Setup and returns a SessionHandler
	 ***********************************************************************/
	public static SessionHandler createSessionHandler() {
	
	    SessionHandler sessionHandler = new SessionHandler();
	    
	    
	    sessionHandler.setSessionIdManager(CFWApplication.idmanager);
	    // workaround maxInactiveInterval=-1 issue
	    // set inactive interval in RequestHandler
	    sessionHandler.setMaxInactiveInterval(CFW.Properties.SESSION_TIMEOUT);
	    sessionHandler.setHttpOnly(false);
	    sessionHandler.setUsingCookies(true);
	    sessionHandler.setSecureRequestOnly(false);
	    
	    HashSet<SessionTrackingMode> trackingModes = new HashSet<SessionTrackingMode>();
	    trackingModes.add(SessionTrackingMode.COOKIE);
	    sessionHandler.setSessionTrackingModes(trackingModes);
	    sessionHandler.getSessionCookieConfig().setPath("/");
	    
	    //prevent URL rewrite
	    sessionHandler.setSessionIdPathParameterName("none");
	    
	    // Explicitly set Session Cache and null Datastore.
	    // This is normally done by default,
	    // but is done explicitly here for demonstration.
	    // If more than one context is to be deployed, it is
	    // simpler to use SessionCacheFactory and/or
	    // SessionDataStoreFactory instances set as beans on 
	    // the server.
	    SessionCache cache = new DefaultSessionCache(sessionHandler);
	    cache.setSessionDataStore(new NullSessionDataStore());
	    sessionHandler.setSessionCache(cache);
	
	    return sessionHandler;
	}

	/***********************************************************************
	 * Setup and returns a ResourceHandler
	 ***********************************************************************/
	public static ContextHandler createResourceHandler() {
	
	    ResourceHandler resourceHandler = new ResourceHandler();
	    // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
	    // In this example it is the current directory but it can be configured to anything that the jvm has access to.
	    resourceHandler.setDirectoriesListed(false);
	    //resource_handler.setWelcomeFiles(new String[]{ "/"+PA.config("pa_application_name")+"/harupload" });
	    resourceHandler.setResourceBase("./resources");
	
	    // Add the ResourceHandler to the server.
	    ContextHandler resourceContextHandler = new ContextHandler();
	    resourceContextHandler.setContextPath("/resources");
	    
	    GzipHandler resourceGzipHandler = new GzipHandler();
	    
	    resourceContextHandler.setHandler(resourceGzipHandler);
	    resourceGzipHandler.setHandler(resourceHandler);
	    
	    return resourceContextHandler;
	}

	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  AssemblyServlet on /assembly
	 *  JARFontServlet on /jarfont
	 *  TestServlet on /test
	 ***********************************************************************/
	public HandlerWrapper createCFWHandler() {
		
		ContextHandler contextHandler = new ContextHandler("/cfw");

		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setSessionHandler(sessionHandler);
	    //-----------------------------------------
	    // Form Servlet
	    servletContextHandler.addServlet(APILoginServlet.class,  "/apilogin");
	    
	    //-----------------------------------------
	    // Form Servlet
	    servletContextHandler.addServlet(FormServlet.class,  "/formhandler");
	    
		//-----------------------------------------
		// Resource Servlets
		servletContextHandler.addServlet(AssemblyServlet.class, "/assembly"); 
		servletContextHandler.addServlet(JARResourceServlet.class, "/jarresource");
		
		//-----------------------------------------
		// Handler Chain
	    GzipHandler servletGzipHandler = new GzipHandler();
	    RequestHandler requestHandler = new RequestHandler();
	
	     new HandlerChainBuilder(contextHandler)
	     	 .chain(servletGzipHandler)
	         .chain(requestHandler)
	         .chain(servletContextHandler);
		
		return contextHandler;
	}

	/***********************************************************************
	 * Create a Server with the defined HTTP and HTTPs settings in the 
	 * cfw.properties.
	 * @return Server instance
	 ***********************************************************************/
	private static Server createServer() {
		Server server = new Server();
		ArrayList<Connector> connectorArray = new ArrayList<Connector>();
		
		CFWApplication.idmanager = new DefaultSessionIdManager(server);
	    server.setSessionIdManager(CFWApplication.idmanager);
	    
		
		if(CFWProperties.HTTP_ENABLED) {
			HttpConfiguration httpConf = new HttpConfiguration();
			httpConf.setSecurePort(CFWProperties.HTTPS_PORT);
			httpConf.setSecureScheme("https");
			
			ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConf));
			httpConnector.setName("unsecured");
			httpConnector.setPort(CFWProperties.HTTP_PORT);
			connectorArray.add(httpConnector);
		}
		
		if(CFWProperties.HTTPS_ENABLED) {
			HttpConfiguration httpsConf = new HttpConfiguration();
			httpsConf.addCustomizer(new SecureRequestCustomizer());
			httpsConf.setSecurePort(CFWProperties.HTTPS_PORT);
			httpsConf.setSecureScheme("https");
			
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(CFWProperties.HTTPS_KEYSTORE_PATH);
			sslContextFactory.setKeyStorePassword(CFWProperties.HTTPS_KEYSTORE_PASSWORD);
			sslContextFactory.setKeyManagerPassword(CFWProperties.HTTPS_KEYMANAGER_PASSWORD);
			
			ServerConnector httpsConnector = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, "http/1.1"),
					new HttpConnectionFactory(httpsConf));
			httpsConnector.setName("secured");
			httpsConnector.setPort(CFWProperties.HTTPS_PORT);
			
			connectorArray.add(httpsConnector);
		}
		
		server.setConnectors(connectorArray.toArray(new Connector[] {}));
		
		return server;
	}


	
	
	
	
	

}
