package com.pengtoolbox.cfw.pipeline;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

// http://www.informit.com/articles/article.aspx?p=366887&seqNum=8

abstract class PipelineAction<I, O> implements Runnable {

	private PipelineAction<?, I> previousAction = null;
	private PipelineAction<O, ?> nextAction = null;
	
	protected LinkedBlockingQueue<I> inQueue = new LinkedBlockingQueue<I>();
	protected LinkedBlockingQueue<O> outQueue;
	CountDownLatch latch;

	protected boolean done;
	
	// override to specify compute step
	abstract void execute() throws Exception;
	
	void initializeAction() throws Exception {}

	void terminateAction() throws Exception { }

	
	public void run() {
		try {
			this.initializeAction();

				while (!done) {
					if(!inQueue.isEmpty()) {
						this.execute();
					}
				}
				
			this.terminateAction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
	}

	public PipelineAction<?, I> getPreviousAction() {
		return previousAction;
	}

	public PipelineAction<I, O> setPreviousAction(PipelineAction<?, I> previousAction) {
		this.previousAction = previousAction;
		return this;
	}

	public PipelineAction<O, ?> getNextAction() {
		return nextAction;
	}

	public PipelineAction<I, O> setNextAction(PipelineAction<O, ?> nextAction) {
		this.nextAction = nextAction;
		return this;
	}

	public LinkedBlockingQueue<I> getInQueue() {
		return inQueue;
	}

	public PipelineAction<I, O> setInQueue(LinkedBlockingQueue<I> in) {
		this.inQueue = in;
		return this;
	}

	public LinkedBlockingQueue<O> getOutQueue() {
		return outQueue;
	}

	public PipelineAction<I, O> setOutQueue(LinkedBlockingQueue<O> out) {
		this.outQueue = out;
		return this;
	}

	public boolean isDone() {
		return done;
	}

	protected PipelineAction<I, O> setDone(boolean done) {
		this.done = done;
		return this;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public PipelineAction<I, O> setLatch(CountDownLatch latch) {
		this.latch = latch;
		return this;
	}

	
	
}
