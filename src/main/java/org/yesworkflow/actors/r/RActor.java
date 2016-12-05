package org.yesworkflow.actors.r;

import org.yesworkflow.actors.IActorScriptAugmenter;
import org.yesworkflow.actors.AugmentedScriptActor;

public class RActor extends AugmentedScriptActor {

	private static String OS_SPECIFIC_R_COMMAND;
	
	static {
		if (System.getProperty("os.name").startsWith("Windows")) {
			OS_SPECIFIC_R_COMMAND = "rterm --slave"; 
		} else {
			OS_SPECIFIC_R_COMMAND = "R --slave";
		}
	}
	
	public RActor() {
		super();
		_scriptExtension = "r";
	}
	
	@Override
	public IActorScriptAugmenter getNewScriptAugmenter() {
		return new RScriptAugmenter();
	}
	
	@Override
	public synchronized String getScriptRunCommand() {
		return OS_SPECIFIC_R_COMMAND;
	}
	
	@Override
	public DataSerializationFormat getOutputSerializationFormat() {
		return DataSerializationFormat.YAML;
	}
}
