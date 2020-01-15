package com.pengtoolbox.cfw.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

abstract class AbstractLinearPipeline {
	protected PipelineAction[] actions;
	protected BlockingQueue<?>[] queues;
	protected int numActions;
	protected CountDownLatch s;
	
	/*************************************************************************************
	 * Constructor
	 *************************************************************************************/
	protected AbstractLinearPipeline(String[] args) {
		actions = getPipelineActions(args);
		queues = getQueues(args);
		numActions = actions.length;
		s = new CountDownLatch(numActions);

		BlockingQueue<?> in = null;
		BlockingQueue<?> out = queues[0];
		for (int i = 0; i != numActions; i++) {
			actions[i].init(in, out, s);
			in = out;
			if (i < numActions - 2)
				out = queues[i + 1];
			else
				out = null;
		}
	}

	
	/*************************************************************************************
	 * override method to create desired array of pipeline action objects
	 * @param args
	 * @return
	 *************************************************************************************/
	abstract PipelineAction[] getPipelineActions(String[] args);

	/*************************************************************************************
	 * override method to create desired array of BlockingQueues
	 * element i of returned array contains queue between stages i and i+1
	 * @param args
	 * @return
	 *************************************************************************************/
	abstract BlockingQueue[] getQueues(String[] args);


	/*************************************************************************************
	 * Start all the actions as separate threads.
	 * @param args
	 * @return
	 *************************************************************************************/
	public void start() {
		for (int i = 0; i != numActions; i++) {
			new Thread(actions[i]).start();
		}
	}
}
