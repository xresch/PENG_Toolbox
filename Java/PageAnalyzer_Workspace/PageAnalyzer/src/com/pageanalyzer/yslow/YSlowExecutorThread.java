package com.pageanalyzer.yslow;

public class YSlowExecutorThread extends Thread{
	
	@Override
	public void run() {
		YSlowExecutor.launch(YSlowExecutor.class);
	}
}
