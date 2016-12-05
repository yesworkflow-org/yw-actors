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

public abstract class AugmentedScriptActor extends ScriptActor implements IAugmentedScriptActor {

	static final protected String  _scriptOutputDelimiter = "__END_OF_SCRIPT_OUTPUT__";

	public enum DataSerializationFormat { YAML, JSON };
	
	@Override
	public abstract IActorScriptAugmenter getNewScriptAugmenter();
	@Override
	public abstract String getScriptRunCommand();
	@Override
	public abstract DataSerializationFormat getOutputSerializationFormat();

	@Override
	public synchronized void configure() throws Exception {
		
		if (_configureScript != null && !_configureScript.trim().isEmpty()) {

			// augment the configure script
			String augmentedConfigureScript = getAugmentedConfigureScript();
			
			// run the augmented configure script
			String serializedOutput = _runAugmentedScript(augmentedConfigureScript);
			
			// update the actor state based on the augmented script output
			Map<String,Object> binding = _parseSerializedOutput(serializedOutput);
			_updateStateVariables(binding);
		}
	}
	
	
	protected String getAugmentedConfigureScript() throws IOException {
		
		IActorScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "configure");
		_appendOriginalScript(augmentedScriptBuilder, _configureScript);
		
		return augmentedScriptBuilder.toString();
	}
	
	
	@Override
	public synchronized void initialize() throws Exception {
		
		if (_initializeScript != null && !_initializeScript.trim().isEmpty()) {
			
			// augment the initialize script
			String augmentedInitializeScript = _getAugmentedInitializeScript();
			
			// run the augmented initialize script
			String serializedOutput = _runAugmentedScript(augmentedInitializeScript);
			
			// update the actor state based on the augmented script output
			Map<String,Object> scriptOutputs = _parseSerializedOutput(serializedOutput);
			_updateInputOutputControlVariables(scriptOutputs);
			_updateStateVariables(scriptOutputs);
		}
	}
	
	protected String _getAugmentedInitializeScript() throws Exception {
		
		IActorScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "initialize");
		_appendInputControlFunctions(augmentedScriptBuilder);
		_appendOutputControlFunctions(augmentedScriptBuilder);
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, true);
		_appendActorInputVariableInitializers(augmentedScriptBuilder);
		_appendOriginalScript(augmentedScriptBuilder, _initializeScript);
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
	
		if (_stepScript != null && !_stepScript.trim().isEmpty()) {

			// augment the step script
			String augmentedStepScript = getAugmentedStepScript();
			
			// save the step script if the actor uses a step directory
			if (this.usesStepDirectory()) {
				File scriptFile = new File(_stepDirectory + "/" + "step." + _scriptExtension);
				FileUtils.writeStringToFile(scriptFile, augmentedStepScript);
			}

			// run the augmented step script
			String serializedOutput = _runAugmentedScript(augmentedStepScript);
			
			// update the actor state based on the augmented script output
			Map<String,Object> scriptOutputs = _parseSerializedOutput(serializedOutput);
			_updateInputOutputControlVariables(scriptOutputs);
			_updateOutputVariables(scriptOutputs);			
			_updateStateVariables(scriptOutputs);
		}
	}
	
	@Override
	public String getAugmentedStepScript() throws Exception {
		
		IActorScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "step");
		_appendInputControlFunctions(augmentedScriptBuilder);
		_appendOutputControlFunctions(augmentedScriptBuilder);
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, true);
		_appendActorInputVariableInitializers(augmentedScriptBuilder);
		_appendStepDirectoryEntryCommand(augmentedScriptBuilder);
		_appendOriginalScript(augmentedScriptBuilder, _stepScript);
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
		
		if (_wrapupScript != null && !_wrapupScript.trim().isEmpty()) {
			
			// augment the wrapup script
			String augmentedWrapupScript = _getAugmentedWrapupScript();
			
			// run the augmented wrapup script
			_runAugmentedScript(augmentedWrapupScript);
		}
	}

	protected String _getAugmentedWrapupScript() throws Exception {
		
		IActorScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "wrapup");
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, false);
		_appendOriginalScript(augmentedScriptBuilder, _wrapupScript);
		_appendScriptSuffix(augmentedScriptBuilder);
		
		return augmentedScriptBuilder.toString();
	}
	
	@Override
	public synchronized void dispose() throws Exception {
		
		if (_disposeScript != null && !_disposeScript.trim().isEmpty()) {
			
			// augment the dispose script
			String augmentedDisposeScript = _getAugmentedDisposeScript();
			
			// run the augmented dispose script
			_runAugmentedScript(augmentedDisposeScript);
		}
	}
	
	protected String _getAugmentedDisposeScript() throws Exception {
		
		IActorScriptAugmenter augmentedScriptBuilder = getNewScriptAugmenter();
		
		_appendScriptHeader(augmentedScriptBuilder, "dispose");
		_appendActorSettingInitializers(augmentedScriptBuilder);
		_appendActorStateVariableInitializers(augmentedScriptBuilder, false);
		_appendOriginalScript(augmentedScriptBuilder, _disposeScript);
		_appendScriptSuffix(augmentedScriptBuilder);

		return augmentedScriptBuilder.toString();
	}
	
	protected void _appendScriptHeader(IActorScriptAugmenter script, String scriptType) throws IOException {
		script.appendComment("AUGMENTED " + scriptType.toUpperCase() + " SCRIPT FOR ACTOR " + this._name)
		  	  .appendBlankLine()
		  	  .appendScriptHeader(script, scriptType);
	}

	protected void _appendScriptSuffix(IActorScriptAugmenter script) {
		script.appendScriptExitCommand();
	}

	protected void _appendInputControlFunctions(IActorScriptAugmenter script) {
		if (!_inputSignature.isEmpty()) {
			script.appendInputControlFunctions()
			  	  .appendBlankLine();
		}
	}
	
	protected void _appendOutputControlFunctions(IActorScriptAugmenter script) {
		if (!_outputSignature.isEmpty()) {
			script.appendOutputControlFunctions()
			  	  .appendBlankLine();
		}
	}

	protected void _appendOutputVariableInitializers(IActorScriptAugmenter script) throws Exception {
		if (!_outputSignature.isEmpty()) {
			script.appendComment("initialize actor outputs to null");
			for (String name : _outputSignature.keySet()) {
				script.appendLiteralAssignment(name, null, _variableTypes.get(name), false, _outputSignature.get(name).isNullable());
			}
			script.appendBlankLine();
		}
	}

	protected void _appendActorStateVariableInitializers(IActorScriptAugmenter script, boolean hideInputs) throws Exception {
		if (!_stateVariables.isEmpty()) {
			script.appendComment("initialize actor state variables");
			Set<String> stateNames = new HashSet<String>(_stateVariables.keySet());
			if (hideInputs) {
				stateNames.removeAll(_inputSignature.keySet());
			}
			for (String key : stateNames) {
				InputSignatureElement input = (InputSignatureElement)(_inputSignature.get(key));
				boolean nullable = input != null && input.isNullable();
				script.appendLiteralAssignment(key, _stateVariables.get(key), _variableTypes.get(key), true, nullable);
			}
			script.appendBlankLine();
		}
	}
	
	protected void _appendActorInputVariableInitializers(IActorScriptAugmenter script) throws Exception {
		if (!_inputSignature.isEmpty()) {
			script.appendComment("initialize actor input variables");			
			Set<String> inputNames = _inputSignature.keySet();
			for (String key : inputNames) {
				script.appendLiteralAssignment(key, _inputValues.get(key), _variableTypes.get(key), false, _inputSignature.get(key).isNullable());
			}
			script.appendBlankLine();
		}
	}
	
	protected void _appendActorSettingInitializers(IActorScriptAugmenter script) throws Exception {
		if (!_constants.isEmpty()) {
			script.appendComment("initialize actor setting");
			Set<String> settingNames = _constants.keySet();
			for (String key : settingNames) {
				script.appendLiteralAssignment(key, _constants.get(key), _variableTypes.get(key), false, false);
			}
			script.appendBlankLine();
		}
	}

	protected void _appendStepDirectoryEntryCommand(IActorScriptAugmenter script) {
		if (_actorStatus.getStepDirectory() != null) {
			script.appendComment("change working directory to actor step directory")
				  .appendChangeDirectory(_actorStatus.getStepDirectory().toString())
				  .appendBlankLine();
		}
	}
	
	protected void _appendOriginalScript(IActorScriptAugmenter script,String originalScript) {
		script.appendComment("BEGINNING OF ORIGINAL SCRIPT")
			  .appendBlankLine()
			  .appendCode(originalScript)
			  .appendBlankLine()
			  .appendComment("END OF ORIGINAL SCRIPT")
			  .appendBlankLine();
	}
	
	protected void _appendOriginalScriptOutputDelimiter(IActorScriptAugmenter script) {
		script.appendComment("signal end of output from original script")
			  .appendPrintStringStatement(_scriptOutputDelimiter)
			  .appendBlankLine();
	}
	
	protected void appendSerializationBeginStatement(IActorScriptAugmenter sb) {
		sb.appendComment("Serialization of actor outputs");
		sb.appendSerializationBeginStatement();
	}
	
	protected void appendSerializationEndStatement(IActorScriptAugmenter sb) {
		sb.appendSerializationEndStatement();
		sb.appendBlankLine();
	}
	
	protected void _appendOutputVariableSerializationStatements(IActorScriptAugmenter script) {
		if (! _outputSignature.isEmpty()) {
			Set<String> outputNames = new HashSet<String>(_outputSignature.keySet());
			outputNames.removeAll(_stateVariables.keySet());
			for (String name : outputNames) {
			    script.appendOutputVariableSerializationStatement(name, _variableTypes.get(name));
			}
			script.appendBlankLine();
		}
	}

	protected void _appendStateVariableSerializationStatements(IActorScriptAugmenter script) {
		if (!_stateVariables.isEmpty()) {
			for (String name : _stateVariables.keySet()) {
				script.appendVariableSerializationStatement(name, _variableTypes.get(name));
			}
			script.appendBlankLine();
		}
	}

	protected void _appendInputControlVariableSerializationStatements(IActorScriptAugmenter script) {
		if (!_inputSignature.isEmpty()) {
			for (String name : new String[]{ "enabledInputs", "disabledInputs"}) {
			    script.appendNonNullStringVariableSerializationPrintStatement(name);
			}
			script.appendBlankLine();
		}
	}
	
	protected void _appendOutputControlVariableSerializationStatements(IActorScriptAugmenter script) {
		if (! _outputSignature.isEmpty()) {
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

		String runcommand = getScriptRunCommand();
		
		StreamSink[] outputs = PortableIO.runProcess(
									runcommand, 
					  				augmentedScript,
					  				null, 
					  				_actorStatus.getStepDirectory()
		  						 );
		
		// capture the standard output from the run of the script
		String completeStdout = outputs[0].toString();
		  
		String completeStderr = outputs[1].toString();
		
		String adjustedStderr = adjustStderr(completeStderr);
		
		if (!adjustedStderr.isEmpty() && _stderrMode == OutputStreamMode.IMMEDIATE) {
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
		
		if (_stderrMode == OutputStreamMode.DELAYED) {
			System.out.print(adjustedStderr);
		}

		if (_stdoutMode == OutputStreamMode.DELAYED) {
			System.out.print(scriptStdout);
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
					Object variableType = _variableTypes.get(key);
					if (value != null && value.equals("null")) {
						binding.put(key, null);
					} else if (variableType != null && variableType.equals("File")) {
				    	binding.put(key, new File(_actorStatus.getStepDirectory(), value.toString()));
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
				_actorStatus.enableInput(name);
			}
		}
		  
		String enabledOutputs = (String) binding.get("enabledOutputs");
		if (enabledOutputs != null) {
			for (String name : enabledOutputs.split(" ")) {
				_actorStatus.enableOutput(name);
			}
		}
		  
		String disabledInputs = (String) binding.get("disabledInputs");
		if (disabledInputs != null) {
			for (String name : disabledInputs.split(" ")) {
				_actorStatus.disableInput(name);
			}
		}
		  
		String disabledOutputs = (String) binding.get("disabledOutputs");
		if (disabledOutputs != null) {
			for (String name : disabledOutputs.split(" ")) {
				_actorStatus.disableOutput(name);
			}
		}
	}
}
