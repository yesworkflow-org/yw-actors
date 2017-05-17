package org.yesworkflow.actors;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ActorStatus implements Cloneable {

	private String 				callType;
	private Map<String,Boolean> inputEnableMap;
	private Map<String,Boolean> outputEnableMap;
	private Map<String,Boolean> outputStreamClosedMap;
    private int                 stepCount;
	private File 				stepDirectory;

	public ActorStatus() {
		synchronized(this) {
			inputEnableMap        = new Hashtable<String, Boolean>();
			outputEnableMap       = new Hashtable<String, Boolean>();
			outputStreamClosedMap = new Hashtable<String,Boolean>();
            stepCount             = 0;
		}
	}
	
	public synchronized Object clone() throws CloneNotSupportedException {
		ActorStatus theClone           = (ActorStatus) super.clone();
		theClone.inputEnableMap        = new Hashtable<String,Boolean>(inputEnableMap);
		theClone.outputEnableMap       = new Hashtable<String,Boolean>(outputEnableMap);
		theClone.outputStreamClosedMap = new Hashtable<String,Boolean>(outputStreamClosedMap);
		return theClone;
	}
	
    public synchronized void setCallType(String callType)                  { this.callType = callType; }
	public synchronized void setStepDirectory(File directory)              { this.stepDirectory = directory; }
    public synchronized void setReadyForInput(String label, Boolean value) { this.inputEnableMap.put(label, value); }
    public synchronized void setStepCount(int count)                       { this.stepCount = count; }
    public synchronized void setOutputEnable(String label, Boolean value)  { this.outputEnableMap.put(label, value); }
    public synchronized void setOutputStreamClosed(String label)           { this.outputStreamClosedMap.put(label, true); }

	public synchronized File getStepDirectory() { return stepDirectory; }
    public synchronized int getStepCount()      { return stepCount; }
    public synchronized int getStepCountIndex() { return stepCount - 1; }
    public synchronized String getCallType()    { return callType; }

	public synchronized void enableInput(String label)   { inputEnableMap.put(label, true); }
	public synchronized void disableInput(String label)  { inputEnableMap.put(label, false); }
	public synchronized void enableOutput(String label)  { outputEnableMap.put(label, true); }
	public synchronized void disableOutput(String label) { outputEnableMap.put(label, false); }	

	public synchronized boolean getInputEnable(String label)     { return isNullOrTrue(inputEnableMap.get(label)); }
    public synchronized boolean getOutputEnable(String label)    { return isNullOrTrue(outputEnableMap.get(label)); }
    public synchronized boolean outputStreamClosed(String label) { return outputStreamClosedMap.get(label); }
	
    public synchronized void enableInputs()   { for (String label : inputEnableMap.keySet())  inputEnableMap.put(label, true);   }
    public synchronized void enableOutputs()  { for (String label : outputEnableMap.keySet()) outputEnableMap.put(label, true);  }
    public synchronized void disableInputs()  { for (String label : inputEnableMap.keySet())  inputEnableMap.put(label, false);  }
    public synchronized void disableOutputs() { for (String label : outputEnableMap.keySet()) outputEnableMap.put(label, false); }

	public synchronized ActorState copyState() {
		ActorState state = new ActorState();
		state.setStepCount( stepCount );
		Map<String,Boolean> inputEnableMap = new HashMap<String,Boolean>();
		for ( String key : inputEnableMap.keySet() ) {
			inputEnableMap.put(key, inputEnableMap.get(key).booleanValue() );
		}
		state.setInEnableMap(inputEnableMap);
		Map<String,Boolean> outputEnableMap = new HashMap<String,Boolean>();
		for (String key : outputEnableMap.keySet() ) {
			outputEnableMap.put(key, outputEnableMap.get(key).booleanValue() );
		}
		state.setOutEnableMap(outputEnableMap);		
		return state;
	}
	
    private Boolean isNullOrTrue(Boolean value) { return (value == null || value); }
}
