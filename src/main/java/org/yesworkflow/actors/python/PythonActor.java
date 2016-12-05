package org.yesworkflow.actors.python;

import org.yesworkflow.actors.AugmentedScriptActor;

public class PythonActor extends AugmentedScriptActor {
	
	public PythonActor() {
		super();
		_scriptExtension = "py";
	}
	
	@Override
	public PythonScriptAugmenter getNewScriptAugmenter() {
		return new PythonScriptAugmenter();
	}
		
	@Override
	public synchronized String getScriptRunCommand() {
		return "python -";
	}
	
	@Override
	public DataSerializationFormat getOutputSerializationFormat() {
		return DataSerializationFormat.YAML;
	}
}
