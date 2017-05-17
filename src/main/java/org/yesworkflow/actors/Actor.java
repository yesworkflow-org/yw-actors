package org.yesworkflow.actors;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class Actor {    
    
	///////////////////////////////////////////////////////////////////////////
	////                    private instance fields                        ////
	
	private boolean 	stateful;
	private String 		stepDirectoryFileSystem;
	private int 		stepOfScratchDirectory;
	private boolean 	usesStepDirectory;
    
	///////////////////////////////////////////////////////////////////////////////////////////
	////                protected singleton instance fields                                ////
	protected ActorStatus 	actorStatus;
	protected String 		name;
	protected File 			stepDirectory;
    protected InputStream 	inStream;
    protected PrintStream 	outStream;
    protected PrintStream 	errStream;

	///////////////////////////////////////////////////////////////////////////////////////////
	////                protected collection instance fields                               ////

	protected Map<String,Object> 				 constants;
	protected Map<String,Object> 				 defaultInputValues;
	protected Map<String,InputSignatureElement>	 inputSignature;
	protected Map<String,Object> 				 inputValues;
	protected Map<String,OutputSignatureElement> outputSignature;
	protected Map<String,Object> 				 outputValues;
	protected Map<String,String> 				 variableTypes;
	protected Map<String,Object> 				 stateVariables;
	
	protected String runDirectoryPath;
	protected int runCount; 
	
	///////////////////////////////////////////////////////////////////////////
	////              public constructors and clone methods                ////
	
	public Actor() {

		synchronized(this) {

			// initialize singleton instance fields
			actorStatus 			 = new ActorStatus();
			name 				     = "";
			stateful 				 = false;
			stepDirectory 			 = null;
			stepDirectoryFileSystem  = "";
			stepOfScratchDirectory   = 0;
			usesStepDirectory 		 = false;
			inStream 				 = System.in;
		    outStream 				 = System.out;
		    errStream 				 = System.err;
		    
			// initialize collection instance fields
			stateVariables 		= new HashMap<String,Object>();
			constants	 		= new HashMap<String,Object>();
			inputValues 		= new HashMap<String, Object>();
			outputValues 		= new HashMap<String, Object>();
			defaultInputValues	= new HashMap<String, Object>();
			inputSignature 		= new LinkedHashMap<String,InputSignatureElement>();
			outputSignature 	= new LinkedHashMap<String,OutputSignatureElement>();
			variableTypes 		= new Hashtable<String,String>();			
		}
	}
	
	/**
	 * Clones this AbstractActor. It uses the clone method of the superclass 
	 * (Object) to shallow-copy all primitive fields.  It then performs a deep copy of
	 * the collection fields and specifically clones the actor status field.
	 * 
	 * @throws 	CloneNotSupportedException if the superclass clone() method or 
	 * 			ActorStatus.clone() throws it.
	 */
	public synchronized Object clone() throws CloneNotSupportedException {

		// call the superclass clone method
		Actor theClone = (Actor) super.clone();
		
		// perform deep copies of collection fields
		theClone.constants 			= new HashMap<String,Object>(constants);
		theClone.defaultInputValues 	= new HashMap<String,Object>(defaultInputValues);
		theClone.inputSignature 		= new HashMap<String,InputSignatureElement>(inputSignature);
		theClone.inputValues 			= new HashMap<String,Object>(inputValues);
		theClone.outputSignature 		= new HashMap<String,OutputSignatureElement>(outputSignature);
		theClone.outputValues 			= new HashMap<String,Object>(outputValues);
		theClone.variableTypes 		= new HashMap<String,String>(variableTypes);
		theClone.stateVariables 		= new HashMap<String,Object>(stateVariables);

		// specifically clone the actor status object
		theClone.actorStatus = (ActorStatus) actorStatus.clone();
		
		return theClone;
	}
	
    public synchronized void errorStream(PrintStream errStream) {
        this.errStream = errStream;
    }

    public synchronized void inputStream(InputStream inStream) {
        this.inStream = inStream;
    }
    
    public synchronized void outputStream(PrintStream outStream) {
        this.outStream = outStream;
    }
	
	
	///////////////////////////////////////////////////////////////////////////
	///     actor configuration setters -- PROPERTIES_UNSET state only     ////
	
	public synchronized void setName(String name) {
		this.name = name;		
	}

	/**
	 * Provide method for controlling what the step/scratch directory looks like.  Some systems
	 * may require restrictions on the directory structure naming.
	 * 
	 * Leave blank for default behavior.
	 * Set to 'is9660' to replace the . (often found in node names) with _
	 * 
	 * @param stepDirectoryFileSystem
	 */
	public synchronized void setStepDirectoryFileSystem(String stepDirectoryFileSystem) {
		this.stepDirectoryFileSystem = stepDirectoryFileSystem;
	}
	
	///////////////////////////////////////////////////////////////////////////
	///   actor configuration setters -- PROPERTES_UNSET or UNCONFIGURED   ////

		
	public synchronized final void setStateful(boolean stateful) {
		this.stateful = stateful;
	}

	public synchronized void setState(Map<String, Object> stateVariables) throws Exception {
		this.stateVariables = stateVariables;
		if (this.stateVariables.size() > 0) {
			setStateful(true);
		}
	}
	
	public synchronized void setUsesStepDirectory(boolean usesScratchDirectory) {
		this.usesStepDirectory = usesScratchDirectory;
	}
	
	public synchronized void setSettings(Map<String, Object> constants) {
		this.constants = constants;
	}

	public synchronized void setTypes(Map<String, String> types) throws Exception {
		variableTypes.putAll(types);
	}

	
	public synchronized void setInputs(Map<String, Object> inputs) throws Exception {
	    Map<String, Object> sortedInputs = new TreeMap<String, Object>(inputs);
		for (Map.Entry<String,Object> input : sortedInputs.entrySet()) {
			addInputToSignature(input.getKey(), input.getValue());
		}
	}
	
	public synchronized void setOutputs(Map<String, Object> outputs) throws Exception {

		for (String label : outputs.keySet()) {
			Object value = outputs.get(label);
			addOutputToSignature(label, value);
		}
	}

	
	///////////////////////////////////////////////////////////////////////////
	///    actor configuration mutators -- UNCONFIGURED state only         ////

	public void addImplicitInput(String inputName) throws Exception {
		
		if (! inputSignature.containsKey(inputName)) {
			addInputToSignature(inputName, null);				
		} else {
			throw new Exception("Cannot add implicit input '" + inputName +
					"' to actor " + this + ".");
		}
	}
	
	public void addImplicitOutput(String outputName) throws Exception {
		
		if (! outputSignature.containsKey(outputName)) {			
			addOutputToSignature(outputName, null);
		} else {
			throw new Exception("Cannot add implicit output " + outputName +
					" to actor " + this + ".");
		}
	}
	
	public void addLogOutput() {
		addOutputToSignature("__log__", null);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///                   configuration getters                            ////

	public synchronized String getName() {
		return name;
	}

	public synchronized boolean isStateful() {
		return stateful;
	}
	
	public Collection<String> getInputNames() {
		return inputSignature.keySet();
	}
	
	public Collection<String> getOutputNames() {
		return outputSignature.keySet();
	}

	public boolean hasOptionalInput(String name) {
		InputSignatureElement input = inputSignature.get(name);
		return input != null && input.isOptional();
	}
	
	public String getInputType(String name) {
		InputSignatureElement input = inputSignature.get(name);
		return input.getType();
	}

	public String getInputLocalPath(String name) {
		InputSignatureElement input = inputSignature.get(name);
		return input.getLocalPath();
	}
	
	public String getInputDescription(String name) {
		InputSignatureElement input = inputSignature.get(name);
		return input.getDescription();
	}
	
	public Object getDefaultInputValue(String name) {
		InputSignatureElement input = inputSignature.get(name);
		return input.getDefaultValue();
	}

	public synchronized String getStepDirectoryFileSystem() {
		return stepDirectoryFileSystem;
	}
	
	public synchronized boolean usesStepDirectory() {
		return usesStepDirectory;
	}
	
	///////////////////////////////////////////////////////////////////////////
	////               public actor lifecycle methods                      ////
	
			
	public void initialize()throws Exception {
		runCount = 0;
	}
	
	public void start() throws Exception {
		
		for (Map.Entry<String, Object> entry : defaultInputValues.entrySet()) {
			
			String label = entry.getKey();
			
			if (actorStatus.getInputEnable(label)) {
				this.setInputValue(label, entry.getValue());
			}
		}

		stepOfScratchDirectory = 0;
		
		resetInputEnables();
	}
	
	public synchronized void reset() throws Exception {
		runCount = 0;
	}

	public synchronized void loadInputValues(Map<String, Object> inputBindings, boolean ignoreExtraInputs) throws Exception {
		
		for (String inputName: inputSignature.keySet()) {
			if ( !defaultInputValues.containsKey(inputName) && !inputBindings.containsKey(inputName)) {
		    	throw new Exception(this + " requires missing input '" + inputName + "'"); 
			}
		}
		
		if (inputBindings != null) {
			for (Map.Entry<String,Object> entry : inputBindings.entrySet()) {
				String inputName = entry.getKey();
				Object inputValue = entry.getValue();
			    if ((!ignoreExtraInputs) && inputSignature.get(inputName) == null) {
			    	throw new Exception(this + " does not accept input '" + inputName + "'"); 
			    }
				setInputValue(inputName, inputValue);
			}
		}
	}
		
	public synchronized void loadStateValues(Map<String, Object> states) {
		
		if (states.size() > 0) {
			stateVariables.putAll(states);
		}
	}
	
	public synchronized boolean readyForInput(String label) throws Exception {
		return actorStatus.getInputEnable(label);
	}

	public synchronized boolean outputEnabled(String label) throws Exception {
		return actorStatus.getOutputEnable(label);
	}
	
	// shorthand method for setting inputs on workflows (and actors) from Java code using builders
	public void set(String label, Object value) throws Exception {
		setInputValue(label, value);
	}
	
	public synchronized void setInputValue(String label, Object value) throws Exception {

		inputValues.put(label, value);

		InputSignatureElement signatureElement = inputSignature.get(label);
		
		if (signatureElement == null) {
			
			if (constants.containsKey(label)) {
				throw new Exception("Attempt to reassign value of setting '" + label + "' " +
											"on actor " + this);
			}
			
		} else {
		
			if (value == null && !signatureElement.isNullable()) {
				throw new Exception("Null data received on input " + label);
			}
		
			actorStatus.disableInput(label);
		}
	}
	
	protected synchronized void abstractActorStep() throws Exception {

		runCount++;

		resetInputEnables();
		
		for (String label: outputSignature.keySet()) {
			boolean defaultOutputEnable = outputSignature.get(label).getDefaultOutEnable();
			actorStatus.setOutputEnable(label, defaultOutputEnable);
		}

		if (usesStepDirectory) {
			actorStatus.setStepDirectory(getCurrentStepDirectory());	
		}	
	}

	public synchronized void step() throws Exception {
		abstractActorStep();
	}


	public synchronized void setStepCount(int count) {
		actorStatus.setStepCount(count);
	}
	
	public synchronized void resetInputEnables() {

		for (String label: inputSignature.keySet()) {
			boolean defaultInputEnable = inputSignature.get(label).getDefaultInEnable();
			actorStatus.setReadyForInput(label, defaultInputEnable);
		}
	}

	public File getNextStepDirectory() throws Exception {
		return createStepScratchDirectoryIfNeeded(1);		
	}
	
	public synchronized Object get(String label) throws Exception {
		return getOutputValue(label);
	}
	
	
	public synchronized Object getOutputValue(String label) throws Exception {
		return outputValues.get(label);
	}

	public synchronized Object getStateValue(String label) throws Exception {
		return stateVariables.get(label);
	}
	
	public void wrapup() throws Exception {
	}

	public void dispose() throws Exception {
	}
	

	public Map<String,Object> getFinalOutputs() throws Exception {

		HashMap<String,Object> finalOutputs = new HashMap<String,Object>();
	
		for (String outputLabel : outputSignature.keySet()) {
			finalOutputs.put(outputLabel, outputValues.get(outputLabel));
		}
		
		return finalOutputs;
	}
	
	
	public ActorState getFinalState() throws Exception {
		
		ActorState state = actorStatus.copyState();
		
		Map <String,Object> stateValues = new HashMap<String,Object>();
		for (String label : stateVariables.keySet()) {
			stateValues.put(label, outputValues.get(label));
		}
		state.setStateValues(stateValues);
		
		return state;
	}	
	
	///////////////////////////////////////////////////////////////////////////
	////             protected actor lifecycle methods                     ////

	protected synchronized void storeOutputValue(String label, Object value) throws Exception {	

		if (value == null && !outputSignature.get(label).isNullable()) {
			throw new Exception(label);
		}
		
		outputValues.put(label, value);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	////                   private helper methods                          ////
	
	private void addInputToSignature(String label, Object value) throws Exception {
				
		InputSignatureElement inputElement = new InputSignatureElement(label);
		
		if (value != null) {
			
			if (!(value instanceof Map)) {
				throw new Exception(
						"Configuration for input '" + label + 
						"' on actor " + this + " must be a map");
			}
			
			@SuppressWarnings("unchecked")
			Map<String,Object> inputProperties = (Map<String,Object>) value;
			
			String localPath = (String) inputProperties.get("path");
			if (localPath != null) {
				inputElement.setLocalPath(localPath);
			}
			
			boolean defaultIsNull = false;
			if (inputProperties.containsKey("default")) {
				Object defaultValue = inputProperties.get("default");
				inputElement.setDefaultValue(defaultValue);
				defaultInputValues.put(label, defaultValue);
				defaultIsNull = (defaultValue == null);
			}
							
			String description = (String) inputProperties.get("description");
			if (description != null) {
				inputElement.setDescription(description);
			}
			
			String typeName = (String) inputProperties.get("type");
			if (typeName != null) {					
				variableTypes.put(label,typeName);
				inputElement.setType(typeName);
				if (typeName.contains("List")) {
					inputElement.setIsList();
				}
			}
			
			Boolean nullable = (Boolean) inputProperties.get("nullable");
			if (nullable != null) { 
				if (!nullable && defaultIsNull) throw new Exception("Null default not allowed for non-nullable input");
			} else {
				nullable = defaultIsNull;
			}
			if (nullable) inputElement.setIsNullable();

			Boolean optional = (Boolean) inputProperties.get("optional");
			if (optional != null && optional) {
				inputElement.setIsOptional();
			}				
			
			Boolean defaultReadiness = (Boolean) inputProperties.get("defaultReadiness");
			if (defaultReadiness != null) {
				inputElement.setDefaultInEnable(defaultReadiness);
			}
		}

		inputSignature.put(label, inputElement);
	}
	
	@SuppressWarnings("rawtypes")
	private synchronized void addOutputToSignature(String label, Object value) {
		OutputSignatureElement outputElement = new OutputSignatureElement(label);
		
		if (value != null && value instanceof Map) {
			
			String typeName = (String) ((Map)value).get("type");
			if (typeName != null) {		
				variableTypes.put(label,typeName);
			}

			Boolean nullable = (Boolean) ((Map)value).get("nullable");
			if (nullable != null && nullable) {
				outputElement.setIsNullable();
			}				
		}

		outputSignature.put(label, outputElement);
		
	}

	private File getCurrentStepDirectory() throws Exception {
		return createStepScratchDirectoryIfNeeded(0);
	}

	private synchronized File createStepScratchDirectoryIfNeeded(int delta) throws Exception {

		if (runDirectoryPath == null) {
			throw new Exception("No working directory exists for containing scratch directory.");
		}
	
		int step = actorStatus.getStepCount() + delta;
		
		if (stepOfScratchDirectory < step) {
			String scratchDirectory = runDirectoryPath + "/.steps/" + name + "_" + step;
			stepDirectory = new File(scratchDirectory);
			stepDirectory.mkdirs();
			stepOfScratchDirectory = step;
		}
		
		return stepDirectory;
	}
	
	public int getRunCount() {
		return runCount;
	}
}

