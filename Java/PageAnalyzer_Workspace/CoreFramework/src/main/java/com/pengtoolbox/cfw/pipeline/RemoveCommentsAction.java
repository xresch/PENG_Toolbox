package com.pengtoolbox.cfw.pipeline;

public class RemoveCommentsAction extends PipelineAction<String, String>{

	@Override
	void execute() throws Exception {
		boolean isBlockComment = false;
		while(!inQueue.isEmpty()) {
			String line = getInQueue().poll();
			
			if(!line.trim().startsWith("//")) {
				outQueue.add(line);
			}
		}
		
		if(getPreviousAction() == null) {
			this.setDone(true);
		}else if(getPreviousAction().isDone()) {
			this.setDone(true);
		}
	}

}
