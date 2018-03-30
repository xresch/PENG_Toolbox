package com.pageanalyzer.yslow;

import java.io.StringWriter;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.pageanalyzer._main.PA;
import com.sun.javafx.webkit.WebConsoleListener;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class YSlowExecutor extends Application {
	
	private WebView view;
	private WebEngine engine;
	
	private static Logger logger = LogManager.getLogManager().getLogger(YSlowExecutor.class.getName());
    
	
	
	@Override
	public void init() throws Exception {
		YSlow.setExecutor(this);
		super.init();
	}
	
	public void start(Stage stage){
		
		String yslowJS = PA.getFileContent(null, "./resources/js/custom/custom_yslow.js");

		view = new WebView();
		stage.setScene(new Scene(view, 900, 600));
		
		engine = view.getEngine();
		
		WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
		    System.out.println("[JS at line "+ lineNumber + "]"+ message);
		});

		engine.setJavaScriptEnabled(true);
		engine.loadContent("<html><head></head><body><script language=\"javascript\">"+yslowJS+"</script>Hello World</body></html>");
		
//		engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
//		    if (newState == State.SUCCEEDED) {
//		        //Document doc = engine.getDocument();
//		        //System.out.println(debugGetStringFromDocument(doc));
//		        
//		        JSObject window = (JSObject)engine.executeScript("window");
//				String result = (String) window.call("analyzeHARString", harString);
//				YSlow.instance().setResult(result);
//		    }
//		});
				
	}
	
	public void analyzeHARString(String harString){
		
		JSObject window = (JSObject)engine.executeScript("window");
		String result = (String) window.call("analyzeHARString", harString);
		YSlow.instance().setResult(result);
				
	}

	private String debugGetStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	} 
}
