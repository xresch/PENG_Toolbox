package com.pageanalyzer.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.pageanalyzer._main.PA;
import com.pageanalyzer.response.AbstractTemplate;
import com.pageanalyzer.response.AbstractTemplateHTML;

public class PALogger {
	
	protected Logger logger;
	
	protected boolean saveLogging = false;
	

	private long tempStartNanos = -1;
	protected long starttimeNanos = -1;
	protected long endtimeNanos = -1;
	protected long durationMillis = -1;
	protected long deltaStartMillis = -1;
	
	protected HttpServletRequest request; 
	protected String webURL;
	protected String queryString;
	protected String requestID;
	protected int estimatedResponseSizeChars;
	
	protected String sessionID;
	
	protected String sourceClass;
	protected String sourceMethod;
	
	protected String exception;

	public PALogger(Logger logger){
		this.logger = logger;
	}
	
	public PALogger(Logger logger, HttpServletRequest request){
		this.logger = logger;
		this.request = request;
	}
	
	public PALogger request(HttpServletRequest request){
		
		this.request = request;
		
		return this;
	}
	
	public PALogger method(String method){
		
		this.sourceMethod = method;
		
		return this;
	}

	/***********************************************************************
	 * Returns a Logger.
	 *  
	 * @return Logger 
	 *   
	 ***********************************************************************/
	public static Logger getLogger(String name){
		
		return Logger.getLogger(name);
	}
	
	/***********************************************************************
	 * Toogle save logging to prevent endless loops. 
	 *   
	 ***********************************************************************/
	public PALogger toogleSaveLogging(boolean isSave){
		
		this.saveLogging = isSave;
		
		return this;
	}
	
	/***********************************************************************
	 * Starts a duration measurement, to end the measurement and write a 
	 * duration log call end().
	 * This measurement can only be nested if you use different Instances of 
	 * OMLogger.
	 *  
	 * @return OMLogger this instance
	 *   
	 ***********************************************************************/
	public PALogger start(){
		
		//save to temp variable to not mess up calls to other log methods 
		//than end()
		tempStartNanos = System.nanoTime();
		return this;
	}
	
	/***********************************************************************
	 * Starts a duration measurement with custom starttime, to end the 
	 * measurement and write a duration log call end().
	 * This measurement can only be nested if you use different Instances of 
	 * OMLogger.
	 *  
	 * @return OMLogger this instance
	 *   
	 ***********************************************************************/
	public PALogger start(long startNanos){
		
		//save to temp variable to not mess up calls to other log methods 
		//than end()
		tempStartNanos = startNanos;
		return this;
	}
	
	/***********************************************************************
	 * Ends a measurement and logs a duration log with level INFO.
	 * 
	 ***********************************************************************/
	public void end(){
		
		//
		starttimeNanos = tempStartNanos;
		this.log(Level.INFO, "Duration Log", null);
				
	}
	
	public void all(String message){this.log(Level.ALL, message, null);}
	public void config(String message){this.log(Level.CONFIG, message, null);}
	public void finest(String message){this.log(Level.FINEST, message, null);}
	public void finer(String message){this.log(Level.FINER, message, null);}
	public void fine(String message){this.log(Level.FINE, message, null);}
	public void info(String message){this.log(Level.INFO, message, null);}
	
	public void warn(String message){this.log(Level.WARNING, message, null);}
	public void warn(String message, Throwable throwable){this.log(Level.WARNING, message, throwable);}
	
	public void severe(String message){this.log(Level.SEVERE, message, null);}
	public void severe(String message, Throwable throwable){this.log(Level.SEVERE, message, throwable);}

	/***********************************************************************
	 * This is the main log method that handles all the logging.
	 * 
	 ***********************************************************************/
	public void log(Level level, String message, Throwable throwable){
		
		//check logging level before proceeding
		if(logger != null && logger.isLoggable(level)){
			
			//-------------------------
			// Calculate Time
			//-------------------------
			endtimeNanos = System.nanoTime();
			
			if(starttimeNanos != -1){
				durationMillis = (endtimeNanos - starttimeNanos) / 1000000;
			}
			
			this.sourceClass = logger.getName();
			
			//-------------------------
			// Handle Throwable
			//-------------------------
			if(throwable != null){
				
				StringBuffer buffer = new StringBuffer();
				buffer.append(throwable.getClass());
				buffer.append(": ");
				buffer.append(throwable.getMessage());
				
				for(StackTraceElement element : throwable.getStackTrace()){
					buffer.append(" <br/>  at ");
					buffer.append(element);
				}
				
				this.exception = buffer.toString();
			
			}
			
			//-------------------------
			// Handle Request
			//-------------------------
			if(request != null){

				this.webURL = request.getRequestURI();
				this.requestID = (String)request.getAttribute(PA.REQUEST_ATTR_ID);
				this.queryString = request.getQueryString();
				this.sessionID = request.getSession().getId();
				
				if(starttimeNanos != -1){
					this.deltaStartMillis = (starttimeNanos - (long)request.getAttribute(PA.REQUEST_ATTR_STARTNANOS) ) / 1000000;
				}else{
					this.deltaStartMillis = (endtimeNanos - (long)request.getAttribute(PA.REQUEST_ATTR_STARTNANOS) ) / 1000000;
				}
				
				AbstractTemplate template = PA.getTemplate(request);
				
				if(template != null){
					
					this.estimatedResponseSizeChars = template.getEstimatedSizeChars();
					
					if(level == Level.SEVERE || level == Level.WARNING){
						
						int alertType = ( level == Level.SEVERE ? PA.ALERT_ERROR : PA.ALERT_WARN );
						
						if(template instanceof AbstractTemplateHTML){
							((AbstractTemplateHTML)template).addAlert(alertType, message);
							
							if(this.exception != null){
								((AbstractTemplateHTML)template).addSupportInfo("Exception: ", this.exception);
							}
						}
					}
				}
			}
			
			//-------------------------
			// Log Message
			//-------------------------
			logger.logp(level, sourceClass, sourceMethod, message, this);
				
		}
		
		//-------------------------
		// Reset
		//-------------------------
		this.exception = null;
		this.starttimeNanos = -1;
		this.durationMillis = -1;
		this.deltaStartMillis = -1;
		
	}
	
	

}
