package org.yesworkflow.actors;

import org.yesworkflow.actors.AugmentedScriptActor.DataSerializationFormat;

public interface IAugmentedScriptActor {
	IActorScriptAugmenter getNewScriptAugmenter();
	String getScriptRunCommand();
	DataSerializationFormat getOutputSerializationFormat();
	String getAugmentedStepScript() throws Exception;
}