package org.yesworkflow.actors;

import java.util.Hashtable;
import java.util.Map;

public class ActorState {

	private int stepCount = 0;
	private Map<String,Boolean> inEnableMap;
	private Map<String,Boolean> outEnableMap;
	private Map<String,Object> stateValues;

   public ActorState() {
        synchronized(this) {
            stepCount = 0;
            inEnableMap  = new Hashtable<String, Boolean>();
            outEnableMap = new Hashtable<String, Boolean>();
            stateValues  = new Hashtable<String, Object>();
        }
   }
   
    public synchronized void setStepCount(int stepCount)                           { this.stepCount = stepCount; }
    public synchronized void setInEnableMap(Map<String, Boolean> inputEnableMap)   { this.inEnableMap = inputEnableMap; }
    public synchronized void setOutEnableMap(Map<String, Boolean> outputEnableMap) { this.outEnableMap = outputEnableMap; }
    public synchronized void setStateValues(Map<String, Object> stateValues)       { this.stateValues = stateValues;}

    public synchronized int getStepCount()                     { return stepCount; }	
	public synchronized Map<String, Boolean> getInEnableMap()  { return inEnableMap; }
	public synchronized Map<String, Boolean> getOutEnableMap() { return outEnableMap; }
	public synchronized Map<String, Object>  getStateValues()  { return stateValues; }
}
