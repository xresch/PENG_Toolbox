package com.pengtoolbox.pageanalyzer.yslow;

import java.util.Stack;
import java.util.logging.Logger;

import com.pengtoolbox.pageanalyzer.logging.PALogger;

public class ExecutionContextPool {
	
	private static Stack<ExecutionContext> freeExecutorPool = new Stack<ExecutionContext>();
	private static Stack<ExecutionContext> lockedExecutorPool = new Stack<ExecutionContext>();

	private static Logger logger = Logger.getLogger(ExecutionContextPool.class.getName());
	
	/***********************************************************************
	 * 
	 ***********************************************************************/
	public static void initializeExecutors(int count) {
		
		for(int i = 0; i < count; i++) {

		}
		
		//wait for first executor to initialize, max 50 seconds
		for(int i = 0; freeExecutorPool.isEmpty() && i < 100; i++){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/***********************************************************************
	 * Retrieve and lock a free context.
	 ***********************************************************************/
	public static ExecutionContext lockContext() {
		
		new PALogger(logger).method("releaseExecutor")
		.info("Before Lock: lockedExecutorPool["+lockedExecutorPool.size()+"], freeExecutorPool["+freeExecutorPool.size()+"]");
		
		ExecutionContext executor = null;
		while(executor == null) {
			
			synchronized(freeExecutorPool) {
				if(!freeExecutorPool.isEmpty()) {
					executor =  freeExecutorPool.pop();
					
					synchronized(lockedExecutorPool) {
						lockedExecutorPool.push(executor);
					}
				}
			}
			
			if(executor == null) {
				try {
					Thread.sleep(200);
					new PALogger(logger).method("lockExecutor").fine("Thread waiting for free executor.");
				} catch (InterruptedException e) {
					new PALogger(logger).method("lockExecutor").warn("Thread interrupted while taking a nap.", e);
					e.printStackTrace();
				}
			}
		}
		
		new PALogger(logger).method("releaseExecutor")
		.info("After Lock: lockedExecutorPool["+lockedExecutorPool.size()+"], freeExecutorPool["+freeExecutorPool.size()+"]");
		
		return executor;
	}
	
	/***********************************************************************
	 * Release a context by unlocking it.
	 ***********************************************************************/
	public static void releaseContext(ExecutionContext executor) {
		synchronized(lockedExecutorPool) {
			lockedExecutorPool.remove(executor);
		}
		synchronized(freeExecutorPool) {
			freeExecutorPool.push(executor);
		}
		new PALogger(logger).method("releaseExecutor")
		.info("After Release: lockedExecutorPool["+lockedExecutorPool.size()+"], freeExecutorPool["+freeExecutorPool.size()+"]");
	}

	/***********************************************************************
	 * 
	 ***********************************************************************/
	public static void addExecutor(ExecutionContext executor) {
		synchronized(freeExecutorPool) {
			freeExecutorPool.add(executor);
		}
	}
}
