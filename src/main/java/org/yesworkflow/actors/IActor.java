package org.yesworkflow.actors;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface IActor {

	/*************************
	 * Configuration setters *
	 *************************/
	

	/** returns true if actor has any state variables */
	boolean isStateful();

	void addImplicitInput(String inputName) throws Exception;
	void addImplicitOutput(String outputName) throws Exception;

	/*************************
	 * Configuration getters *
	 *************************/
	
	Collection<String> getInputNames();
	Collection<String> getOutputNames();
	boolean hasOptionalInput(String name);
	String getInputType(String label);
	String getInputLocalPath(String label);	
	
	/*************************
	 *    Actor lifecycle    *
	 *************************/

	/** called once prior to first call to step */
	void configure() throws Exception;
	
	/** called once prior to first call to step */
	void initialize() throws Exception;

	/** returns true if actor ready to accept value for input with given label */
	boolean readyForInput(String label) throws Exception;

	/** gets the scratch directory for storing copied file resources to be used as input */
	File getNextStepDirectory() throws Exception;

	/** provides a value for actor input variable with given label */
	void setInputValue(String label, Object value) throws Exception;

	/** carry out the next step and compute actor outputs from actor inputs */
	void step() throws Exception;

	/** returns true if actor has produced a value for the output with given label */
	public boolean outputEnabled(String label) throws Exception;

	Object getStateValue(String label) throws Exception;

	/** returns latest value produced by the actor on output with the given label */
	Object getOutputValue(String label) throws Exception;

	/** called once following any calls to step */
	void wrapup() throws Exception;

	void resetInputEnables();

	void setStepCount(int count);

	Object clone() throws CloneNotSupportedException;
	
	
	/**
	 * Return the description of the input from the input signature.
	 * This is used from the command line to print the description
	 * of missing input parameters.
	 * @param name
	 * @return
	 */
	public String getInputDescription(String name);
	
	
	/**
	 * Return the default value for an named input by looking it up in the
	 * inputSignature.
	 * 
	 * @param name
	 * @return
	 */
	public Object getDefaultInputValue(String name);
	
	
	Map<String,Object> getFinalOutputs() throws Exception;
	
	/**
	 * Provide a way for an actor to return its state variables, inputEnable flags, and outputEnable flags.
	 * This can be used to persist the state of the actor-- used by the metadata manager.
	 * 
	 * @return
	 */
	public ActorState getFinalState() throws Exception;
	
	/**Provide method for setting the state of an actor all at once.
	 * Useful for restoring state from the command line.
	 * 
	 * @param state
	 * @throws Exception
	 */
	public void setState(Map<String, Object> state) throws Exception;

	public void dispose() throws Exception;

	void loadStateValues(Map<String, Object> _previousStateValues);

	void loadInputValues(Map<String, Object> inputBindings, boolean ignoreExtraInputs) throws Exception;

	int getRunCount();

	void reset() throws Exception;
	
	enum ActorFSM {
		CONSTRUCTED,
		PROPERTIES_SET,
		ELABORATED,
		CONFIGURED,
		INITIALIZED,
		STEPPED,
		WRAPPED_UP,
		DISPOSED
	}

	String getName();

	void setName(String actorName);
}