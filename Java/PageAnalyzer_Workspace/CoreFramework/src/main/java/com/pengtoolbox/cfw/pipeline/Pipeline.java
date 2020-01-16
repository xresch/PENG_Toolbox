package com.pengtoolbox.cfw.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.stats.StatsCPUSamplingTask;

public class Pipeline<I, O> {
	private static Logger logger = CFWLog.getLogger(StatsCPUSamplingTask.class.getName());
	
	protected ArrayList<PipelineAction> actionArray = new ArrayList<PipelineAction>();
	protected ArrayList<LinkedBlockingQueue<?>> queues = new ArrayList<LinkedBlockingQueue<?>>();
	protected CountDownLatch latch;
	
	protected LinkedBlockingQueue<I> firstQueue = null;
	protected LinkedBlockingQueue<O> lastQueue = new LinkedBlockingQueue<O>();
	
	/*************************************************************************************
	 * Constructor
	 *************************************************************************************/
	protected Pipeline() {
		
	}

	/*************************************************************************************
	 * Start all the actions as separate threads.
	 * @param args
	 * @return
	 *************************************************************************************/
	public Pipeline<I, O> execute() {

		//-----------------------------------
		// Check has Actions
		if(actionArray.size() == 0) {
			new CFWLog(logger)
				.method("execute")
				.warn("No actions in pipeline.", new Throwable());
			
			return null;
		}

		//-----------------------------------
		// Initialize
		latch = new CountDownLatch(actionArray.size());

		actionArray.get(actionArray.size()-1).setOutQueue(lastQueue);
		
		//-----------------------------------
		// Initialize
		for (PipelineAction action : actionArray) {
			action.setLatch(latch);
			new Thread(action).start();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			new CFWLog(logger)
				.method("execute")
				.warn("Pipeline execution was interupted.", e);
			
			return null;
		}	
		
		return this;
		
	}
	
	/*************************************************************************************
	 * Start all the actions as separate threads.
	 * @param args
	 * @return
	 *************************************************************************************/
	public void add(PipelineAction nextAction) {
		
		if(actionArray.size() > 0) {
			PipelineAction previousAction = actionArray.get(actionArray.size());
			previousAction.setOutQueue(nextAction.getInQueue());
			previousAction.setNextAction(nextAction);
			
			nextAction.setPreviousAction(previousAction);
			
		}else {
			this.firstQueue = nextAction.getInQueue();
		}
		
		actionArray.add(nextAction);
		queues.add(nextAction.getInQueue());
				
	}
	
	public  Pipeline<I, O> data(I[] data) {
		if(firstQueue != null) {
			firstQueue.addAll(Arrays.asList(data));
		}
		return this;
	}
	
	/*************************************************************************************
	 * 
	 * @param args
	 * @return
	 *************************************************************************************/
	public String resultToString() {
		
		StringBuilder builder = new StringBuilder();
		
		while(!lastQueue.isEmpty()) {
			builder.append(lastQueue.poll().toString()).append("\n");
		}
		
		return builder.toString();
	}
}
