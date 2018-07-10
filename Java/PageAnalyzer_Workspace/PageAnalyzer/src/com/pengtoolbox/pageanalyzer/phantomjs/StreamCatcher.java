package com.pengtoolbox.pageanalyzer.phantomjs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamCatcher extends Thread
{
    private InputStream is;
    private String type;
    private StringBuffer catchedData = new StringBuffer();
    
    StreamCatcher(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                //DEBUG System.out.println(type + ">" + line); 
            	catchedData.append(line+"\n");
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
    }
    
    public String getCatchedData(){
    	return catchedData.toString();
    }
}