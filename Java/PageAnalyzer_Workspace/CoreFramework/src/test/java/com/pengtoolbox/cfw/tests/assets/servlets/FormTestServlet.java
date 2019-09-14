package com.pengtoolbox.cfw.tests.assets.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw._main.CFWContextRequest;
import com.pengtoolbox.cfw.caching.FileDefinition;
import com.pengtoolbox.cfw.caching.FileDefinition.HandlingType;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.HTMLResponse;
import com.pengtoolbox.cfw.response.JSONResponse;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;
import com.pengtoolbox.cfw.response.bootstrap.BTForm;
import com.pengtoolbox.cfw.response.bootstrap.BTFormHandler;
import com.pengtoolbox.cfw.response.bootstrap.CFWField;
import com.pengtoolbox.cfw.response.bootstrap.CFWField.FormFieldType;
import com.pengtoolbox.cfw.tests.assets.mockups.CFWObjectMockup;

public class FormTestServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = CFWLog.getLogger(FormTestServlet.class.getName());

	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		
		CFWLog log = new CFWLog(logger).method("doGet");
		
		HTMLResponse html = new HTMLResponse("Test Page");
		StringBuffer content = html.getContent();
		  
		//------------------------------
		// Test Form
		//------------------------------
        BTForm form = new BTForm("directForm", "Save");
        form.addChild(new CFWField(FormFieldType.TEXT, "Firstname", "firstname"));
        form.addChild(new CFWField(FormFieldType.TEXT, "Lastname", "lastname"));
        
        content.append("<h2>Direct use of BTForm</h2>");
        content.append(form.getHTML());
        
		//------------------------------
		// Test Form
		//------------------------------
        content.append("<h2>Form Created Through CFWObject</h2>");
        content.append(new CFWObjectMockup().toForm("myForm", "Submit!!!").getHTML());
        
		//------------------------------
		// Form with Handler
		//------------------------------
        
        content.append("<h2>Form with BTFormHandler</h2>");
        BTForm handledForm = new CFWObjectMockup().toForm("handlerForm", "Handle!!!");
        
        handledForm.setFormHandler(new BTFormHandler() {
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form) {
				// TODO Auto-generated method stub
				String formID = request.getParameter(BTForm.FORM_ID);
				
				JSONResponse json = new JSONResponse();
		    	json.addAlert(MessageType.SUCCESS, "BTFormHandler: Post recieved from "+formID+"!!!");
			}
		});
        content.append(handledForm.getHTML());
        
		//------------------------------
		// Form with Handler
		//------------------------------
        content.append("<h2>Map Requests and Validate</h2>");
        BTForm handledForm2 = new CFWObjectMockup().toForm("handlerForm2", "Handle Again!!!");
        
        handledForm2.setFormHandler(new BTFormHandler() {
			@Override
			public void handleForm(HttpServletRequest request, HttpServletResponse response, BTForm form) {
				// TODO Auto-generated method stub
				String formID = request.getParameter(BTForm.FORM_ID);
				
				JSONResponse json = new JSONResponse();
		    	json.addAlert(MessageType.SUCCESS, "BTFormHandler: Post recieved from "+formID+"!!!");
		    	
		    	CFWObjectMockup mockup = (CFWObjectMockup)new CFWObjectMockup().mapRequestParameters(request);
			}
		});
        content.append(handledForm2.getHTML());
        

    }
	
	
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
    	    	
    	String formID = request.getParameter(BTForm.FORM_ID);
    	
    	JSONResponse json = new JSONResponse();
    	json.addAlert(MessageType.SUCCESS, "Post recieved from "+formID+"!!!");
    	
    	
    	
    }
	
}