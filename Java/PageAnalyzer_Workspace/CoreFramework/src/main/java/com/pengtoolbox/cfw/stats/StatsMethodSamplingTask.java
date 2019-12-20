package com.pengtoolbox.cfw.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

public class StatsMethodSamplingTask extends TimerTask {

	// Contains the stack element signature and the number of occurences.
	private static HashMap<String, Integer> counterMap = new HashMap<String, Integer>();
	
	@Override
	public void run() {

		Map<Thread, StackTraceElement[]> traceMap = Thread.getAllStackTraces();

		for(StackTraceElement[] elements : traceMap.values()) {
			
			for(StackTraceElement element : elements) {
				String signature = element.toString();
				signature = signature.substring(signature.lastIndexOf('/')+1);
				if(counterMap.containsKey(signature)) {
					counterMap.put(signature, counterMap.get(signature)+1);
				}else {
					counterMap.put(signature, 1);
				}
			}
		}

		dumpCounters();
	}
	
	public static String dumpCounters() {
		
		StringBuilder builder = new StringBuilder();
		for(Entry<String, Integer> entry : counterMap.entrySet()) {
			builder.append(entry.getKey()).append(": ").append(entry.getValue());
		}
		
		return builder.toString();
		
	}

}
