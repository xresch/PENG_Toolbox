package com.pengtoolbox.cfw.pipeline;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;

class StringProcessingPipeline extends Pipeline<String, String> {
	/*******************************************************************************
	 * Constructor
	 *******************************************************************************/
	public StringProcessingPipeline() {
		super();
		
	}
	
	/*********************************************************************
	 * 
	 * @return
	 *********************************************************************/
	public StringProcessingPipeline removeBlankLines() {
		
		this.add(new PipelineAction<String, String>(){
			@Override
			void execute() throws Exception {

				while(!inQueue.isEmpty()) {
					String line = inQueue.poll();
					if(!line.trim().isEmpty()) {
						outQueue.add(line);
					}
				}
				this.setDoneIfPreviousDone();
			}
			
		});
		return this;
	}
	/*********************************************************************
	 * 
	 * @return
	 *********************************************************************/
	public StringProcessingPipeline trim() {
		
		this.add(new PipelineAction<String, String>(){
			@Override
			void execute() throws Exception {

				while(!inQueue.isEmpty()) {
					outQueue.add(inQueue.poll().trim());
				}
				this.setDoneIfPreviousDone();
			}
			
		});
		return this;
	}
	
	public StringProcessingPipeline removeComments() {
		this.add(new RemoveCommentsAction());
		return this;
	}

	public static void main(String... args) throws InterruptedException {
		StringProcessingPipeline pipe = new StringProcessingPipeline();
		CFW.Files.addAllowedPackage(FileDefinition.CFW_JAR_RESOURCES_PATH);

		pipe.removeBlankLines()
			.removeComments()
			.trim()
			.data(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".test", "cfwjs_test.js").split("\\r\\n|\\n"))
			.execute(false);
			
		System.out.println(	
			pipe.waitForComplete()
				.resultToString()
		);

		System.out.println("All threads terminated");
	   
	}
}
