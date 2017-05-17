package org.yesworkflow.actors;

import java.io.File;
import java.util.Map;

public abstract class ScriptActor extends Actor {
	
    protected String initializeScript;
    protected String startScript;
    protected String stepScript;
    protected String wrapupScript;
    protected String disposeScript;

    protected String scriptExtension;
    protected OutputStreamMode stdoutMode;
    protected OutputStreamMode stderrMode;
	
	public ScriptActor() {
		synchronized(this) {
			stdoutMode = OutputStreamMode.DELAYED;
		    stderrMode = OutputStreamMode.DELAYED;
		    scriptExtension = "txt";
		}
	}
	
    public synchronized void setStart(String startScript) { this.startScript = startScript; }
    public synchronized void setInitialize(String initializeScript) { this.initializeScript = initializeScript; }
    public synchronized void setStep(String stepScript) { this.stepScript = stepScript; }
	public synchronized void setWrapup(String wrapupScript) { this.wrapupScript = wrapupScript; }
	public synchronized void setDispose(String disposeScript) { this.disposeScript = disposeScript; }

	public synchronized void setStdoutMode(String mode) throws Exception { this.stdoutMode = parseOutputMode(mode); }
	public synchronized void setStderrMode(String mode) throws Exception { this.stderrMode = parseOutputMode(mode); }

	private OutputStreamMode parseOutputMode(String mode) throws Exception {
		if (mode.equals("discard")) {
			return OutputStreamMode.DISCARD;			
		} else  if (mode.equals("delayed")) {
			return OutputStreamMode.DELAYED;			
		} else if (mode.equals("immediate")) {
			return OutputStreamMode.IMMEDIATE;	
		} else {
			throw new Exception("Parse mode string must be one of discard, delayed, or immediate.");
		}
	}
		
	protected synchronized void updateStateVariables(Map<String,Object> variables) {
		for (String label : stateVariables.keySet()) {
			Object value = variables.get(label);
			stateVariables.put(label, value);
		}
	}
		
	protected synchronized void updateOutputVariables(Map<String,Object> variables) throws Exception {
		for (String label : outputSignature.keySet()) {
			if (actorStatus.getOutputEnable(label)) {
//				if (!variables.containsKey(label)) {
//					throw new Exception("Actor " + name + " did not output a value for " + label);
//				}	
				Object value = variables.get(label);				
				if (value instanceof File && ! ((File)value).exists()) {
					value = null;	
				}
				storeOutputValue(label, value);
			}
		}
	}
	
	@Override
	protected synchronized void storeOutputValue(String label, Object value) throws Exception {	
//		if (value == null && !outputSignature.get(label).isNullable()) {
//			throw new Exception("Null data output by actor " + label);
//		}
		outputValues.put(label, value);
	}
}
