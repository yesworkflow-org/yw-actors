package org.yesworkflow.actors;

import java.util.Hashtable;
import java.util.Map;

public class ActorState {

	private int stepCount = 0;
	private Map<String,Boolean> inputEnableMap;
	private Map<String,Boolean> outputEnableMap;
	private Map<String,Object> stateValues;

   public ActorState() {
        synchronized(this) {
            stepCount = 0;
            inputEnableMap = new Hashtable<String, Boolean>();
            outputEnableMap = new Hashtable<String, Boolean>();
            stateValues= new Hashtable<String, Object>();
        }
   }
   
    public synchronized void setStepCount(int stepCount) { this.stepCount = stepCount; }
	public synchronized int getStepCount() { return stepCount; }
	
    public synchronized void setInputEnableMap(Map<String, Boolean> inputEnableMap) { this.inputEnableMap = inputEnableMap; }
	public synchronized Map<String, Boolean> getInputEnableMap() { return inputEnableMap; }
	
    public synchronized void setOutputEnableMap(Map<String, Boolean> outputEnableMap) { this.outputEnableMap = outputEnableMap; }
	public synchronized Map<String, Boolean> getOutputEnableMap() { return outputEnableMap; }
	
    public synchronized void setStateValues(Map<String, Object> stateValues) { this.stateValues = stateValues;}
	public synchronized Map<String, Object> getStateValues() { return stateValues; }
}
