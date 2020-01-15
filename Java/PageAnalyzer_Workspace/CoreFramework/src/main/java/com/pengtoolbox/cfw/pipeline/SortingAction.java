package com.pengtoolbox.cfw.pipeline;

class SortingAction extends PipelineAction
{
   Comparable val = null;
   Comparable input = null;

   void firstStep() throws InterruptedException
   { 
	 input = (Comparable)in.take();
     done = (input.equals("DONE"));
     val = input;
     return;
   }

   void step() throws InterruptedException
   { 
	   input = (Comparable)in.take();
       done = (input.equals("DONE"));
       if (!done)
       { 
    	   if(val.compareTo(input)<0)
          { 
    		   out.put(val); val = input; 
    	  }
           else { 
        	   out.put(input); 
           }
       } else out.put(val);
   }

   void lastStep() throws InterruptedException{ 
	   out.put("DONE"); 
   }
}