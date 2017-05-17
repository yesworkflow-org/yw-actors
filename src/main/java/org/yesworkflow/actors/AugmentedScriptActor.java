package org.yesworkflow.actors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yesworkflow.actors.util.PortableIO;
import org.yesworkflow.actors.util.PortableIO.StreamSink;

public abstract class AugmentedScriptActor extends ScriptActor {

	static final protected String  _scriptOutputDelimiter = "__END_OF_SCRIPT_OUTPUT__";

	public enum DataSerializationFormat { YAML, JSON };
	
    protected String runcommand;
	
	public abstract ScriptAugmenter getNewScriptAugmenter();
	
	public abstract DataSerializationFormat getOutputSerializationFormat();

	@Override
	public synchronized void configure() throws Exception {
		
		if (configureScript != null && !configureScript.trim().isEmpty()) {

			// augment the configure script
			String augmentedConfigureScript = getAugmentedConfigureScript();
			
			// run the augmented configure script
			String serializedOutput = _runAugmentedScript(augmentedConfigureScript);
			
			// update the actor state based on the augmented script output
			Map<String,Object> binding = _parseSerializedOutput(serializedOutput);
			updateStateVariables(binding);
		}
	}
	
	
	protected String getAugmentedConfigureScript() throws IOException {
		
		ScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "configure");
		_appendOriginalScript(augmentedScriptBuilder, configureScript);
		
		return augmentedScriptBuilder.toString();
	}
	
	
	@Override
	public synchronized void initialize() throws Exception {
		
		if (initializeScript != null && !initializeScript.trim().isEmpty()) {
			
			// augment the initialize script
			String augmentedInitializeScript = _getAugmentedInitializeScript();
			
			// run the augmented initialize script
			String serializedOutput = _runAugmentedScript(augmentedInitializeScript);
			
			// update the actor state based on the augmented script output
			Map<String,Object> scriptOutputs = _parseSerializedOutput(serializedOutput);
			_updateInputOutputControlVariables(scriptOutputs);
			updateStateVariables(scriptOutputs);
		}
	}
	
	protected String _getAugmentedInitializeScript() throws Exception {
		
		ScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "initialize");
		_appendInputControlFunctions(augmentedScriptBuilder);
		_appendOutputControlFunctions(augmentedScriptBuilder);
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, true);
		_appendActorInputVariableInitializers(augmentedScriptBuilder);
		_appendOriginalScript(augmentedScriptBuilder, initializeScript);
		_appendOriginalScriptOutputDelimiter(augmentedScriptBuilder);
		appendSerializationBeginStatement(augmentedScriptBuilder);
		_appendStateVariableSerializationStatements(augmentedScriptBuilder);
		_appendInputControlVariableSerializationStatements(augmentedScriptBuilder);
		_appendOutputControlVariableSerializationStatements(augmentedScriptBuilder);
		appendSerializationEndStatement(augmentedScriptBuilder);
		_appendScriptSuffix(augmentedScriptBuilder);
		
		return augmentedScriptBuilder.toString();
	}
	
	@Override
	public synchronized void step() throws Exception {
	
		if (stepScript != null && !stepScript.trim().isEmpty()) {

			// augment the step script
			String augmentedStepScript = getAugmentedStepScript();
			
			// save the step script if the actor uses a step directory
			if (this.usesStepDirectory()) {
				File scriptFile = new File(stepDirectory + "/" + "step." + scriptExtension);
				FileUtils.writeStringToFile(scriptFile, augmentedStepScript);
			}

			// run the augmented step script
			String serializedOutput = _runAugmentedScript(augmentedStepScript);
			
			// update the actor state based on the augmented script output
			Map<String,Object> scriptOutputs = _parseSerializedOutput(serializedOutput);
			_updateInputOutputControlVariables(scriptOutputs);
			updateOutputVariables(scriptOutputs);			
			updateStateVariables(scriptOutputs);
		}
	}
	
	public String getAugmentedStepScript() throws Exception {
		
		ScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "step");
		_appendInputControlFunctions(augmentedScriptBuilder);
		_appendOutputControlFunctions(augmentedScriptBuilder);
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, true);
		_appendActorInputVariableInitializers(augmentedScriptBuilder);
		_appendStepDirectoryEntryCommand(augmentedScriptBuilder);
		_appendOriginalScript(augmentedScriptBuilder, stepScript);
		_appendOriginalScriptOutputDelimiter(augmentedScriptBuilder);
		appendSerializationBeginStatement(augmentedScriptBuilder);
		_appendOutputVariableSerializationStatements(augmentedScriptBuilder);
		_appendStateVariableSerializationStatements(augmentedScriptBuilder);
		_appendInputControlVariableSerializationStatements(augmentedScriptBuilder);
		_appendOutputControlVariableSerializationStatements(augmentedScriptBuilder);
		appendSerializationEndStatement(augmentedScriptBuilder);
		_appendScriptSuffix(augmentedScriptBuilder);
		
		return augmentedScriptBuilder.toString();
	}
	
	@Override
	public synchronized void wrapup() throws Exception {
		
		if (wrapupScript != null && !wrapupScript.trim().isEmpty()) {
			
			// augment the wrapup script
			String augmentedWrapupScript = _getAugmentedWrapupScript();
			
			// run the augmented wrapup script
			_runAugmentedScript(augmentedWrapupScript);
		}
	}

	protected String _getAugmentedWrapupScript() throws Exception {
		
		ScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "wrapup");
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, false);
		_appendOriginalScript(augmentedScriptBuilder, wrapupScript);
		_appendScriptSuffix(augmentedScriptBuilder);
		
		return augmentedScriptBuilder.toString();
	}
	
	@Override
	public synchronized void dispose() throws Exception {
		
		if (disposeScript != null && !disposeScript.trim().isEmpty()) {
			
			// augment the dispose script
			String augmentedDisposeScript = _getAugmentedDisposeScript();
			
			// run the augmented dispose script
			_runAugmentedScript(augmentedDisposeScript);
		}
	}
	
	protected String _getAugmentedDisposeScript() throws Exception {
		
		ScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "dispose");
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, false);
		_appendOriginalScript(augmentedScriptBuilder, disposeScript);
		_appendScriptSuffix(augmentedScriptBuilder);

		return augmentedScriptBuilder.toString();
	}
	
	protected void _appendScriptHeader(ScriptAugmenter script, String scriptType) throws IOException {
		script.appendComment("AUGMENTED " + scriptType.toUpperCase() + " SCRIPT FOR ACTOR " + this.name)
		  	  .appendBlankLine()
		  	  .appendHeader(script, scriptType);
	}

	protected void _appendScriptSuffix(ScriptAugmenter script) {
		script.appendScriptExitCommand();
	}

	protected void _appendInputControlFunctions(ScriptAugmenter script) {
		if (!inputSignature.isEmpty()) {
			script.appendInputControlFunctions()
			  	  .appendBlankLine();
		}
	}
	
	protected void _appendOutputControlFunctions(ScriptAugmenter script) {
		if (!outputSignature.isEmpty()) {
			script.appendOutputControlFunctions()
			  	  .appendBlankLine();
		}
	}

	protected void _appendOutputVariableInitializers(ScriptAugmenter script) throws Exception {
		if (!outputSignature.isEmpty()) {
			script.appendComment("initialize actor outputs to null");
			for (String name : outputSignature.keySet()) {
				script.appendLiteralAssignment(name, null, variableTypes.get(name), false, outputSignature.get(name).isNullable());
			}
			script.appendBlankLine();
		}
	}

	protected void _appendActorStateVariableInitializers(ScriptAugmenter script, boolean hideInputs) throws Exception {
		if (!stateVariables.isEmpty()) {
			script.appendComment("initialize actor state variables");
			Set<String> stateNames = new HashSet<String>(stateVariables.keySet());
			if (hideInputs) {
				stateNames.removeAll(inputSignature.keySet());
			}
			for (String key : stateNames) {
				InputSignatureElement input = (InputSignatureElement)(inputSignature.get(key));
				boolean nullable = input != null && input.isNullable();
				script.appendLiteralAssignment(key, stateVariables.get(key), variableTypes.get(key), true, nullable);
			}
			script.appendBlankLine();
		}
	}
	
	protected void _appendActorInputVariableInitializers(ScriptAugmenter script) throws Exception {
		if (!inputSignature.isEmpty()) {
			script.appendComment("initialize actor input variables");			
			Set<String> inputNames = inputSignature.keySet();
			for (String key : inputNames) {
				script.appendLiteralAssignment(key, inputValues.get(key), variableTypes.get(key), false, inputSignature.get(key).isNullable());
			}
			script.appendBlankLine();
		}
	}
	
	protected void _appendActorSettingInitializers(ScriptAugmenter script) throws Exception {
		if (!constants.isEmpty()) {
			script.appendComment("initialize actor setting");
			Set<String> settingNames = constants.keySet();
			for (String key : settingNames) {
				script.appendLiteralAssignment(key, constants.get(key), variableTypes.get(key), false, false);
			}
			script.appendBlankLine();
		}
	}

	protected void _appendStepDirectoryEntryCommand(ScriptAugmenter script) {
		if (actorStatus.getStepDirectory() != null) {
			script.appendComment("change working directory to actor step directory")
				  .appendChangeDirectory(actorStatus.getStepDirectory().toString())
				  .appendBlankLine();
		}
	}
	
	protected void _appendOriginalScript(ScriptAugmenter script,String originalScript) {
		script.appendComment("BEGINNING OF ORIGINAL SCRIPT")
			  .appendBlankLine()
			  .appendCode(originalScript)
			  .appendBlankLine()
			  .appendComment("END OF ORIGINAL SCRIPT")
			  .appendBlankLine();
	}
	
	protected void _appendOriginalScriptOutputDelimiter(ScriptAugmenter script) {
		script.appendComment("signal end of output from original script")
			  .appendPrintStringStatement(_scriptOutputDelimiter)
			  .appendBlankLine();
	}
	
	protected void appendSerializationBeginStatement(ScriptAugmenter sb) {
		sb.appendComment("Serialization of actor outputs");
		sb.appendSerializationBeginStatement();
	}
	
	protected void appendSerializationEndStatement(ScriptAugmenter sb) {
		sb.appendSerializationEndStatement();
		sb.appendBlankLine();
	}
	
	protected void _appendOutputVariableSerializationStatements(ScriptAugmenter script) {
		if (! outputSignature.isEmpty()) {
			Set<String> outputNames = new HashSet<String>(outputSignature.keySet());
			outputNames.removeAll(stateVariables.keySet());
			for (String name : outputNames) {
			    script.appendOutputVariableSerializationStatement(name, variableTypes.get(name));
			}
			script.appendBlankLine();
		}
	}

	protected void _appendStateVariableSerializationStatements(ScriptAugmenter script) {
		if (!stateVariables.isEmpty()) {
			for (String name : stateVariables.keySet()) {
				script.appendVariableSerializationStatement(name, variableTypes.get(name));
			}
			script.appendBlankLine();
		}
	}

	protected void _appendInputControlVariableSerializationStatements(ScriptAugmenter script) {
		if (!inputSignature.isEmpty()) {
			for (String name : new String[]{ "enabledInputs", "disabledInputs"}) {
			    script.appendNonNullStringVariableSerializationPrintStatement(name);
			}
			script.appendBlankLine();
		}
	}
	
	protected void _appendOutputControlVariableSerializationStatements(ScriptAugmenter script) {
		if (! outputSignature.isEmpty()) {
			for (String name : new String[]{ "enabledOutputs", "disabledOutputs" }) {
			    script.appendNonNullStringVariableSerializationPrintStatement(name);
			}
			script.appendBlankLine();
		}
	}
	
	protected String adjustStderr(String completeStderr) {
		return completeStderr;
	}
	
	protected synchronized String _runAugmentedScript(String augmentedScript) throws Exception {
		
		StreamSink[] outputs = PortableIO.runProcess(
									runcommand, 
					  				augmentedScript,
					  				null, 
					  				actorStatus.getStepDirectory()
		  						 );
		
		// capture the standard output from the run of the script
		String completeStdout = outputs[0].toString();
		  
		String completeStderr = outputs[1].toString();
		
		String adjustedStderr = adjustStderr(completeStderr);
		
		if (!adjustedStderr.isEmpty() && stderrMode == OutputStreamMode.IMMEDIATE) {
			System.err.println(	">>>>>>>>>>>>>>>>>>>> Error running augmented actor script >>>>>>>>>>>>>>>>>>>>>>"	);
			System.err.print  (	augmentedScript																		);
			System.err.println(	"-------------------------------- Error message ---------------------------------"	);
			System.err.print  (	completeStderr																		);
			System.err.println(	"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"	);
		}
		
		// find the beginning of the script output delimiter
		int delimiterStart = completeStdout.lastIndexOf(_scriptOutputDelimiter);
		
		String scriptStdout;
		String serializedOutputs;
		
		if (delimiterStart == -1) {
			scriptStdout = completeStdout;
			serializedOutputs = "";
		} else {
			scriptStdout = completeStdout.substring(0, delimiterStart);
			int serializedOutputStart = delimiterStart + _scriptOutputDelimiter.length();
			serializedOutputs = completeStdout.substring(serializedOutputStart);
		}
		
		if (stderrMode == OutputStreamMode.DELAYED) {
			errStream.print(adjustedStderr);
		}

		if (stdoutMode == OutputStreamMode.DELAYED) {
			outStream.print(scriptStdout);
		}

		return serializedOutputs;
	}
	
	private synchronized Map<String,Object> _parseSerializedOutput(String serializedOutput) throws Exception {
		if (this.getOutputSerializationFormat() == DataSerializationFormat.YAML) {
			return _parseYamlOutput(serializedOutput);
		} else if (this.getOutputSerializationFormat() == DataSerializationFormat.JSON) {
			return _parseJsonOutput(serializedOutput);
		} else {
			throw new Exception("Unsupported serialization format");
		}
	}
		
	private synchronized Map<String,Object> _parseJsonOutput(String jsonOutput) {

		Map<String,Object> binding = new HashMap<String,Object>();
		  
//		// parse the yaml block and save output variable values in map
//		Yaml yaml = new Yaml();
//		Map<String,Object> outputMap = (Map<String,Object>) yaml.load(yamlOutput);
//		if (outputMap != null) {
//			for (Map.Entry<String,Object> entry : outputMap.entrySet()) { 
//				String key = entry.getKey();
//				Object value = entry.getValue();
//				Object variableType = _variableTypes.get(key);
//				if (value != null && value.equals("null")) {
//					binding.put(key, null);
//				} else if (variableType != null && variableType.equals("File")) {
//			    	binding.put(key, new File(_actorStatus.getStepDirectory(), value.toString()));
//			    } else {
//			    	binding.put(key, value);
//			    }
//			}
//		}
		
		return binding;
	}

	private synchronized Map<String,Object> _parseYamlOutput(String yamlOutput) {

		Map<String,Object> binding = new HashMap<String,Object>();
		  
		// parse the yaml block and save output variable values in map
		Yaml yaml = new Yaml();
		
		Object yamlParseResult =  yaml.load(yamlOutput);
		
		if (yamlParseResult instanceof Map<?,?>) {
			@SuppressWarnings("unchecked")
			Map<String,Object> outputMap = (Map<String,Object>) yamlParseResult;
			if (outputMap != null) {
				for (Map.Entry<String,Object> entry : outputMap.entrySet()) { 
					String key = entry.getKey();
					Object value = entry.getValue();
					Object variableType = variableTypes.get(key);
					if (value != null && value.equals("null")) {
						binding.put(key, null);
					} else if (variableType != null && variableType.equals("File")) {
				    	binding.put(key, new File(actorStatus.getStepDirectory(), value.toString()));
				    } else {
				    	binding.put(key, value);
				    }
				}
			}
		}

		return binding;
	}
	
	private synchronized void _updateInputOutputControlVariables(Map<String,Object> binding) {
		
		String enabledInputs = (String) binding.get("enabledInputs");
		if (enabledInputs != null) {
			for (String name : enabledInputs.split(" ")) {
				actorStatus.enableInput(name);
			}
		}
		  
		String enabledOutputs = (String) binding.get("enabledOutputs");
		if (enabledOutputs != null) {
			for (String name : enabledOutputs.split(" ")) {
				actorStatus.enableOutput(name);
			}
		}
		  
		String disabledInputs = (String) binding.get("disabledInputs");
		if (disabledInputs != null) {
			for (String name : disabledInputs.split(" ")) {
				actorStatus.disableInput(name);
			}
		}
		  
		String disabledOutputs = (String) binding.get("disabledOutputs");
		if (disabledOutputs != null) {
			for (String name : disabledOutputs.split(" ")) {
				actorStatus.disableOutput(name);
			}
		}
	}
}
