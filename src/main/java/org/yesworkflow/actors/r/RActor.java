package org.yesworkflow.actors.r;

import org.yesworkflow.actors.IActorScriptAugmenter;
import org.yesworkflow.actors.AugmentedScriptActor;

public class RActor extends AugmentedScriptActor {

	private static String DEFAULT_R_COMMAND;
	
	static {
	    if (System.getProperty("os.name").startsWith("Windows")) {
	        DEFAULT_R_COMMAND = "rterm"; 
		} else {
		    DEFAULT_R_COMMAND = "R";
		}
	}
	
	public RActor() {
		super();
		super.scriptExtension = "r";
		super.runcommand = String.format("%s --slave", DEFAULT_R_COMMAND);
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
