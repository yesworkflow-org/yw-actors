package org.yesworkflow.actors;

import java.io.File;
import java.util.Map;

public abstract class ScriptActor extends Actor implements IScriptActor {
	
	protected String _configureScript;
    protected String _initializeScript;
    protected String _stepScript;
    protected String _wrapupScript;
    protected String _disposeScript;
    protected String scriptExtension;
	
    public  enum OutputStreamMode { DISCARD, DELAYED, IMMEDIATE }; 
    
    protected OutputStreamMode _stdoutMode;
    protected OutputStreamMode _stderrMode;
    
	public ScriptActor() {
		synchronized(this) {
			_stdoutMode = OutputStreamMode.DELAYED;
		    _stderrMode = OutputStreamMode.DELAYED;
		    scriptExtension = "txt";
		}
	}
	
	public synchronized void setStep(String script) {
		_stepScript = script;
	}

	public synchronized void setWrapup(String script) {
		_wrapupScript = script;
	}

	public synchronized void setDispose(String script) {
		_disposeScript = script;
	}

	public synchronized void setInitialize(String script) {
		_initializeScript = script;
	}

	public synchronized void setConfigure(String script) {
		_configureScript = script;
	}

	public synchronized void setStdoutMode(String mode) throws Exception {
		_stdoutMode = _parseOutputMode(mode);
	}

	public synchronized void setStderrMode(String mode) throws Exception {
		_stderrMode = _parseOutputMode(mode);
	}

	private OutputStreamMode _parseOutputMode(String mode) throws Exception {
		if (mode.equals("discard")) {
			return OutputStreamMode.DISCARD;			
		} else  if (mode.equals("delayed")) {
			return OutputStreamMode.DELAYED;			
		} else if (mode.equals("immediate")) {
			return OutputStreamMode.IMMEDIATE;	
		} else {
			throw new Exception("Parse mode string must be one of discard, delayed, or immedate.");
		}
	}
		
	protected synchronized void _updateStateVariables(Map<String,Object> variables) {
		for (String label : _stateVariables.keySet()) {
			Object value = variables.get(label);
			_stateVariables.put(label, value);
		}
	}
		
	protected synchronized void _updateOutputVariables(Map<String,Object> variables) throws Exception {
		
		for (String label : _outputSignature.keySet()) {

			if (_actorStatus.getOutputEnable(label)) {
				
				if (!variables.containsKey(label)) {
					throw new Exception("Actor " + _name + " did not output a value for " + label);
				}	
			
				Object value = variables.get(label);				
				if (value instanceof File && ! ((File)value).exists()) {
					value = null;	
				}
				
				_storeOutputValue(label, value);
			}
		}
	}
	
	protected synchronized void _storeOutputValue(String label, Object value) throws Exception {	
		
		if (value == null && !_outputSignature.get(label).isNullable()) {
			throw new Exception("Null data output by actor " + label);
		}
		
		_outputValues.put(label, value);
	}
}
