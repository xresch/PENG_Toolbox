package com.pengtoolbox.cfw.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class SortingPipeline extends AbstractLinearPipeline {
	/*******************************************************************************
	 * Constructor
	 *******************************************************************************/
	public SortingPipeline(String[] args) {
		super(args);
	}
	
	/*******************************************************************************
	 * Creates an array of pipeline stages with the number of sorting stages given
	 * via args. Input and output stages are also included at the beginning and end
	 * of the array. Details are omitted.
	 *******************************************************************************/
	public PipelineAction[] getPipelineActions(String[] args) { 
		PipelineAction[] actions = new PipelineAction[2];
		actions[0] = new SortingAction();
		return actions;
	}

	/*******************************************************************************
	 * Creates an array of LinkedBlockingQueues to serve as communication channels
	 * between the stages. For this example, the first is restricted to hold
	 * Strings, the rest can hold Comparables.
	 *******************************************************************************/
	public BlockingQueue[] getQueues(String[] args) {
		BlockingQueue[] queues = new BlockingQueue[numActions - 1];
		queues[0] = new LinkedBlockingQueue<String>();
		for (int i = 1; i != numActions - 1; i++) {
			queues[i] = new LinkedBlockingQueue<Comparable>();
		}
		return queues;
	}

	public static void main(String[] args)
	   throws InterruptedException
	   { // create pipeline
	   AbstractLinearPipeline pipe = new SortingPipeline(args);
	   pipe.start(); //start threads associated with stages
	   pipe.s.await(); //terminate thread when all stages terminated
	   System.out.println("All threads terminated");
	   }
}
