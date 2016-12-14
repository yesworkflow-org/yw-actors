package org.yesworkflow.actors.python;

import org.yesworkflow.actors.AugmentedScriptActor;

public class PythonActor extends AugmentedScriptActor {
	
    public static String DEFAULT_PYTHON_COMMAND = "python"; 
    
	public PythonActor() {
		super();
		super.scriptExtension = "py";
		super.runcommand = String.format("%s -", DEFAULT_PYTHON_COMMAND);
	}
	
	@Override
	public PythonScriptAugmenter getNewScriptAugmenter() {
		return new PythonScriptAugmenter();
	}
			
	@Override
	public DataSerializationFormat getOutputSerializationFormat() {
		return DataSerializationFormat.YAML;
	}
}
