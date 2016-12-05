package org.yesworkflow.actors;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface IActor {

	boolean isStateful();
	void addImplicitInput(String inputName) throws Exception;
	void addImplicitOutput(String outputName) throws Exception;
	Collection<String> getInputNames();
	Collection<String> getOutputNames();
	boolean hasOptionalInput(String name);
	String getInputType(String label);
	String getInputLocalPath(String label);	
	void configure() throws Exception;
	void initialize() throws Exception;
	boolean readyForInput(String label) throws Exception;
	File getNextStepDirectory() throws Exception;
	void setInputValue(String label, Object value) throws Exception;
	void step() throws Exception;
	public boolean outputEnabled(String label) throws Exception;
	Object getStateValue(String label) throws Exception;
	Object getOutputValue(String label) throws Exception;
	void wrapup() throws Exception;
	void resetInputEnables();
	void setStepCount(int count);
	Object clone() throws CloneNotSupportedException;
	String getInputDescription(String name);
	Object getDefaultInputValue(String name);
	Map<String,Object> getFinalOutputs() throws Exception;
	ActorState getFinalState() throws Exception;
	void setState(Map<String, Object> state) throws Exception;
	void dispose() throws Exception;
	void loadStateValues(Map<String, Object> _previousStateValues);
	void loadInputValues(Map<String, Object> inputBindings, boolean ignoreExtraInputs) throws Exception;
	int getRunCount();
	void reset() throws Exception;
	String getName();
	void setName(String actorName);

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
}