package org.yesworkflow.actors;

import org.yesworkflow.actors.AugmentedScriptActor.DataSerializationFormat;

public interface IAugmentedScriptActor {
	IActorScriptAugmenter getNewScriptAugmenter();
	DataSerializationFormat getOutputSerializationFormat();
	String getAugmentedStepScript() throws Exception;
}