package com.pengtoolbox.cfw.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

// http://www.informit.com/articles/article.aspx?p=366887&seqNum=8

abstract class PipelineAction implements Runnable {

	BlockingQueue in;
	BlockingQueue out;
	CountDownLatch s;

	boolean done;

	// override to specify initialization step
	abstract void firstStep() throws Exception;

	// override to specify compute step
	abstract void step() throws Exception;

	// override to specify finalization step
	abstract void lastStep() throws Exception;

	void handleComputeException(Exception e) {
		e.printStackTrace();
	}

	public void run() {
		try {
			firstStep();
			while (!done) {
				step();
			}
			lastStep();
		} catch (Exception e) {
			handleComputeException(e);
		} finally {
			s.countDown();
		}
	}

	public void init(BlockingQueue in, BlockingQueue out, CountDownLatch s) {
		this.in = in;
		this.out = out;
		this.s = s;
	}

}
