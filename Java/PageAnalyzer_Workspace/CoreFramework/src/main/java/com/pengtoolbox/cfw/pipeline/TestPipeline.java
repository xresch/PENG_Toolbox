package com.pengtoolbox.cfw.pipeline;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.caching.FileDefinition;

class TestPipeline extends Pipeline<String, String> {
	/*******************************************************************************
	 * Constructor
	 *******************************************************************************/
	public TestPipeline() {
		super();
		this.add(new RemoveCommentsAction());
	}

	public static void main(String... args) throws InterruptedException {
		TestPipeline pipe = new TestPipeline();
		CFW.Files.addAllowedPackage(FileDefinition.CFW_JAR_RESOURCES_PATH);
		System.out.println(
			pipe.data(CFW.Files.readPackageResource(FileDefinition.CFW_JAR_RESOURCES_PATH + ".test", "cfwjs_test.js").split("\\r\\n|\\n"))
				.execute()
				.resultToString()
		);

		System.out.println("All threads terminated");
	   
	}
}
