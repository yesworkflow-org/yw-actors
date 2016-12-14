package org.yesworkflow.actors.python;

import org.yesworkflow.actors.AugmentedScriptActor;

public class PythonActor extends AugmentedScriptActor {
	
    public static String DEFAULT_PYTHON_COMMAND = "python";
    
	public PythonActor() {
		super();
		super.scriptExtension = "py";
		
		String ywPythonCommand = System.getenv("YW_PYTHON_COMMAND");
		if (ywPythonCommand == null) {
		    ywPythonCommand = DEFAULT_PYTHON_COMMAND;
		}
		super.runcommand = String.format("%s -", ywPythonCommand);
		System.out.println("PYTHON RUN COMMAND = " + super.runcommand);
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
