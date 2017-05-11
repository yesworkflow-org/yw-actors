package org.yesworkflow.actors.r;

import org.yesworkflow.actors.IActorScriptAugmenter;
import org.yesworkflow.actors.AugmentedScriptActor;

public class RActor extends AugmentedScriptActor {

	private static String DEFAULT_R_COMMAND;
	private static String DEFAULT_R_EXTENSION = "r";

	static {
	    if (System.getProperty("os.name").startsWith("Windows")) {
	        DEFAULT_R_COMMAND = "rterm"; 
		} else {
		    DEFAULT_R_COMMAND = "R";
		}
	}
	
	public RActor() {
		super();
	
		String customRCommand = System.getProperty("yw.actors.r.command");
		String rCommand = customRCommand != null ? customRCommand : DEFAULT_R_COMMAND;
		
		String customRExtension = System.getProperty("yw.actors.r.extension");
        String rExtension = customRExtension != null ? customRExtension : DEFAULT_R_EXTENSION;
		
		super.scriptExtension = rExtension;
		super.runcommand = String.format("%s --slave", rCommand);
	}
	
	@Override
	public IActorScriptAugmenter getNewScriptAugmenter() {
		return new RScriptAugmenter();
	}
	
	@Override
	public DataSerializationFormat getOutputSerializationFormat() {
		return DataSerializationFormat.YAML;
	}
}
