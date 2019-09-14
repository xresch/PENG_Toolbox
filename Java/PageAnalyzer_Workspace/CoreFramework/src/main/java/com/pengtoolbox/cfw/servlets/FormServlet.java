package com.pengtoolbox.cfw.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
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

	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
		doPost(request, response);
    }
	
    protected void doPost( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException,
                                          IOException
	{
    	String formID = request.getParameter(BTForm.FORM_ID);
    	BTForm form = CFW.Context.Session.getForm(formID);
    	
    	if(form == null) {
    		JSONResponse json = new JSONResponse();
    		CFW.Context.Request.addAlertMessage(MessageType.ERROR, "The form with ID '"+formID+"' could not be found.");
    		return;
    	}
    	
    	form.getFormHandler().handleForm(request, response, form);
	}
}