package com.pengtoolbox.cfw._main;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.servlet.SessionTrackingMode;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.pengtoolbox.cfw.handlers.RequestHandler;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.BTFooter;
import com.pengtoolbox.cfw.response.bootstrap.BTMenu;
import com.pengtoolbox.cfw.response.bootstrap.UserMenuItem;
import com.pengtoolbox.cfw.servlets.AssemblyServlet;
import com.pengtoolbox.cfw.servlets.ConfigurationServlet;
import com.pengtoolbox.cfw.servlets.FormServlet;
import com.pengtoolbox.cfw.servlets.JARResourceServlet;
import com.pengtoolbox.cfw.servlets.LoginServlet;
import com.pengtoolbox.cfw.servlets.LogoutServlet;
import com.pengtoolbox.cfw.servlets.PermissionsServlet;
import com.pengtoolbox.cfw.servlets.admin.APIUserMgmtSevlet;
import com.pengtoolbox.cfw.servlets.admin.UserManagementServlet;
import com.pengtoolbox.cfw.servlets.userprofile.ChangePasswordServlet;
import com.pengtoolbox.cfw.utils.HandlerChainBuilder;

/***********************************************************************
 * Setup class for the Core Framework
 ***********************************************************************/
public class CFWApp {
	private static Logger logger = CFWLog.getLogger(CFW.class.getName());
	
	private static Class<?> defaultMenuClass = null;
	private static Class<?> defaultUserMenuItemClass = null;
	private static Class<?> defaultFooterClass = null;
	
	private static DefaultSessionIdManager idmanager;
	
	/***********************************************************************
	 * Set the class to be used as the default menu for your application.
	 * @param Class that extends from BTMenu
	 ***********************************************************************/
	public static void setDefaultMenu(Class<?> menuClass)  {
		
		if(BTMenu.class.isAssignableFrom(menuClass)) {
			defaultMenuClass = menuClass;
		}else {
			new CFWLog(logger).severe("Class is not a subclass of 'BTMenu': "+menuClass.getName());
		}
	}
		
	/***********************************************************************
	 * Create a instance of the menu.
	 * @return a Bootstrap Menu instance
	 ***********************************************************************/
	public static BTMenu createDefaultMenuInstance()  {
		
		if(defaultMenuClass != null) {
			try {
				Object menu = defaultMenuClass.newInstance();
				
				if(menu instanceof BTMenu) {
					return (BTMenu)menu;
				}else {
					throw new InstantiationException("Class not an instance of BootstrapMenu");
				}
			} catch (Exception e) {
				new CFWLog(logger).severe("Issue creating instance for Class '"+defaultMenuClass.getSimpleName()+"': "+e.getMessage(), e);
			}
		}
		
		return new BTMenu().setLabel("Set your custom menu class(extending BootstrapMenu) using CFW.App.setDefaultMenu()! ");
	}
	
	/***********************************************************************
	 * Set the class to be used as the default UserMenuItem for your application.
	 * @param Class that extends from {@link UserMenuItem}
	 ***********************************************************************/
	public static void setDefaultUserMenuItem(Class<?> menuItemClass)  {
		
		if(UserMenuItem.class.isAssignableFrom(menuItemClass)) {
			defaultUserMenuItemClass = menuItemClass;
		}else {
			new CFWLog(logger).severe("Class is not a subclass of 'BootstrapMenu': "+menuItemClass.getName());
		}
	}
	
	/***********************************************************************
	 * Create a instance of the user menu.
	 * @return a UserMenuItem or null.
	 ***********************************************************************/
	public static UserMenuItem createUserMenuItemInstance(SessionData data)  {
		
		if(defaultUserMenuItemClass != null) {
			try {
				Constructor<?> constructor = defaultUserMenuItemClass.getConstructor(SessionData.class);
				Object menuItem = constructor.newInstance(data);
				
				if(menuItem instanceof UserMenuItem) {
					return (UserMenuItem)menuItem;
				}else {
					throw new InstantiationException("Class not an instance of UserMenuItem.");
				}
			} catch (Exception e) {
				new CFWLog(logger).severe("Issue creating instance for Class '"+defaultUserMenuItemClass.getSimpleName()+"': "+e.getMessage(), e);
				e.getCause().printStackTrace();
			}
		}
		
		return null;
	}
	
	/***********************************************************************
	 * Set the class to be used as the default footer for your application.
	 * @param Class that extends from BTFooter
	 ***********************************************************************/
	public static void setDefaultFooter(Class<?> menuClass)  {
		
		if(BTFooter.class.isAssignableFrom(menuClass)) {
			defaultFooterClass = menuClass;
		}else {
			new CFWLog(logger).severe("Class is not a subclass of 'BTFooter': "+menuClass.getName());
		}
	}
	
	
	/***********************************************************************
	 * Create a instance of the footer.
	 * @return a Bootstrap Menu instance
	 ***********************************************************************/
	public static BTFooter createDefaultFooterInstance()  {
		
		if(defaultFooterClass != null) {
			try {
				Object menu = defaultFooterClass.newInstance();
				
				if(menu instanceof BTFooter) {
					return (BTFooter)menu;
				}else {
					throw new InstantiationException("Class not an instance of BTFooter");
				}
			} catch (Exception e) {
				new CFWLog(logger).severe("Issue creating instance for Class '"+defaultFooterClass.getSimpleName()+"': "+e.getMessage(), e);
			}
		}
		
		return new BTFooter().setLabel("Set your custom menu class(extending BTFooter) using CFW.App.setDefaultFooter()! ");
	}
	
	/***********************************************************************
	 * Create an instance of the CFWDefaultApp.
	 * @param args command line arguments
	 * @return CFWDefaultApp instance
	 * @throws Exception 
	 ***********************************************************************/
	public static CFWDefaultApp createApp(String[] args) throws Exception {
		return new CFWDefaultApp(args);
	}
	
	/***********************************************************************
	 * Create a Server with the defined HTTP and HTTPs settings in the 
	 * cfw.properties.
	 * @return Server instance
	 ***********************************************************************/
	public static Server createServer() {
		Server server = new Server();
		ArrayList<Connector> connectorArray = new ArrayList<Connector>();
		
		idmanager = new DefaultSessionIdManager(server);
	    server.setSessionIdManager(idmanager);
	    
		
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

	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  LoginServlet on /login
	 *  LogoutServlet on /logout
	 ***********************************************************************/
	public static void addCFWServlets(ServletContextHandler servletContextHandler) {
		
		//-----------------------------------------
		// Authentication Servlets
	    if(CFWProperties.AUTHENTICATION_ENABLED) {
	        servletContextHandler.addServlet(LoginServlet.class, "/login");
	        servletContextHandler.addServlet(LogoutServlet.class,  "/logout");
	    }
	    
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
	
	/***********************************************************************
	 * Add the servlets provided by CFW to the given context.
	 *  AssemblyServlet on /assembly
	 *  JARFontServlet on /jarfont
	 *  TestServlet on /test
	 ***********************************************************************/
	public static HandlerWrapper createCFWHandler() {
		
		ContextHandler contextHandler = new ContextHandler("/cfw");
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		
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
	 * Setup and returns a SessionHandler
	 ***********************************************************************/
	public static SessionHandler createSessionHandler() {

	    SessionHandler sessionHandler = new SessionHandler();
	    sessionHandler.setSessionIdManager(idmanager);
	    // workaround maxInactiveInterval=-1 issue
	    // set inactive interval in RequestHandler
	    sessionHandler.setMaxInactiveInterval(CFW.Properties.SESSION_TIMEOUT);
	    sessionHandler.setHttpOnly(false);
	    sessionHandler.setUsingCookies(true);
	    sessionHandler.setSecureRequestOnly(false);
	    
	    HashSet<SessionTrackingMode> trackingModes = new HashSet<SessionTrackingMode>();
	    trackingModes.add(SessionTrackingMode.COOKIE);
	    sessionHandler.setSessionTrackingModes(trackingModes);
	    
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

}
