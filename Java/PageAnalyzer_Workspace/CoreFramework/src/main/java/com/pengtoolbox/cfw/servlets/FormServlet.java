package com.pengtoolbox.cfw.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;

/*************************************************************************************
 * This servlet is used to handle forms that have a BTFormHandler defined.
 * 
 * @author Reto
 *
 *************************************************************************************/
public class FormServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger logger = CFWLog.getLogger(FormServlet.class.getName());
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
		String id = request.getParameter("id");
		
		BTForm form = CFW.Context.Session.getForm(id);
		
    	JSONResponse json = new JSONResponse();
    	if(form == null) {
    		json.setSuccess(false);
    		new CFWLog(logger)
	    		.method("doGet")
	    		.severe("The form with ID '"+id+"' could not be found.");
    		return;
    	}
		
    	JsonObject payload = new JsonObject();
    	payload.addProperty("html", form.getHTML());
    	
    	json.getContent().append(payload.toString());
    }
	
	
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
    	String formID = request.getParameter(BTForm.FORM_ID);
    	BTForm form = CFW.Context.Session.getForm(formID);
    	
    	JSONResponse json = new JSONResponse();
    	if(form == null) {
    		json.setSuccess(false);
    		new CFWLog(logger)
	    		.method("doGet")
	    		.severe("The form with ID '"+formID+"' could not be found.");
    		return;
    	}
    	    	
    	form.getFormHandler().handleForm(request, response, form);
	}
}