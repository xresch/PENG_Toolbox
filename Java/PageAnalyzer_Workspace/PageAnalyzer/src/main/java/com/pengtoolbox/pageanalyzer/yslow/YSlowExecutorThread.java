package com.pengtoolbox.pageanalyzer.yslow;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, � 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class YSlowExecutorThread extends Thread{
	
	@Override
	public void run() {
		YSlowExecutorJavaFX.launch(YSlowExecutorJavaFX.class);
	}
}
