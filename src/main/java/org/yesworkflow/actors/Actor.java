package org.yesworkflow.actors;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * This class is the default base class for all implementations of the Actor
 * interface. 
 */
public abstract class Actor implements IActor {
	
	///////////////////////////////////////////////////////////////////////////
	////                    private instance fields                        ////
	
	private Boolean 	_cloneable;
	private boolean 	_stateful;
	private String 	_stepDirectoryFileSystem;
	private int 		_stepOfScratchDirectory;
	private boolean 	_usesStepDirectory;
		
	///////////////////////////////////////////////////////////////////////////////////////////
	////                protected singleton instance fields                                ////
	protected ActorStatus 				_actorStatus;
	protected String 					_beanName;	
	protected String 					_name;
	protected File 						_stepDirectory;

	///////////////////////////////////////////////////////////////////////////////////////////
	////                protected collection instance fields                               ////

	protected Map<String,Object> 				_constants;
	protected Map<String,Object> 				_defaultInputValues;
	protected Map<String,InputSignatureElement> 	_inputSignature;
	protected Map<String,Object> 				_inputValues;
	protected Map<String,OutputSignatureElement> _outputSignature;
	protected Map<String,Object> 				_outputValues;
	protected Map<String,String> 				_variableTypes;
	protected Map<String,Object> 				_stateVariables;
	
	private String _scratchDirectoryPrefix;
	protected String runDirectoryPath;
	protected int _runCount; 	// step count since latest call of configure() or reset(),
								//   unlike 'step' which is step count last call to configure(), reset(), or initialize()

	
	///////////////////////////////////////////////////////////////////////////
	////              public constructors and clone methods                ////

	
	/**
	 * Creates and initializes the fields of a new instance.
	 */
	public Actor() {

		synchronized(this) {

			// initialize singleton instance fields
			_actorStatus 			 = new ActorStatus();
			_name 				 = "";
			_cloneable 				 = null;
			_stateful 				 = false;
			_stepDirectory 			 = null;
			_stepDirectoryFileSystem = "";
			_stepOfScratchDirectory  = 0;
			_usesStepDirectory 		 = false;
			
			// initialize collection instance fields
			_stateVariables 		= new HashMap<String,Object>();
			_constants	 			= new HashMap<String,Object>();
			_inputValues 			= new HashMap<String, Object>();
			_outputValues 			= new HashMap<String, Object>();
			_defaultInputValues		= new HashMap<String, Object>();
			_inputSignature 		= new LinkedHashMap<String,InputSignatureElement>();
			_outputSignature 		= new LinkedHashMap<String,OutputSignatureElement>();
			_variableTypes 			= new Hashtable<String,String>();
			
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
		theClone._constants 			= new HashMap<String,Object>(_constants);
		theClone._defaultInputValues 	= new HashMap<String,Object>(_defaultInputValues);
		theClone._inputSignature 		= new HashMap<String,InputSignatureElement>(_inputSignature);
		theClone._inputValues 			= new HashMap<String,Object>(_inputValues);
		theClone._outputSignature 		= new HashMap<String,OutputSignatureElement>(_outputSignature);
		theClone._outputValues 			= new HashMap<String,Object>(_outputValues);
		theClone._variableTypes 		= new HashMap<String,String>(_variableTypes);
		theClone._stateVariables 		= new HashMap<String,Object>(_stateVariables);

		// specifically clone the actor status object
		theClone._actorStatus = (ActorStatus) _actorStatus.clone();
		
		return theClone;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	///     actor configuration setters -- PROPERTIES_UNSET state only     ////
	
	public synchronized void setName(String name) {
		_name = name;		
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
		_stepDirectoryFileSystem = stepDirectoryFileSystem;
	}
	
	///////////////////////////////////////////////////////////////////////////
	///   actor configuration setters -- PROPERTES_UNSET or UNCONFIGURED   ////

		
	public synchronized final void setStateful(boolean newValue) {
		_stateful = newValue;
	}

	public synchronized void setState(Map<String, Object> state) throws Exception {
		_stateVariables = state;
		if (_stateVariables.size() > 0) {
			setStateful(true);
		}
	}
	
	public synchronized void setUsesStepDirectory(boolean usesScratchDirectory) {
		_usesStepDirectory = usesScratchDirectory;
	}
	
	public synchronized void setSettings(Map<String, Object> constants) {
		_constants = constants;
	}

	public synchronized void setTypes(Map<String, String> types) throws Exception {
		_variableTypes.putAll(types);
	}

	
	public synchronized void setInputs(Map<String, Object> inputs) throws Exception {

		for (String label : inputs.keySet()) {
			Object value = inputs.get(label);
			_addInputToSignature(label, value);
		}
	}
	
	public synchronized void setOutputs(Map<String, Object> outputs) throws Exception {

		for (String label : outputs.keySet()) {
			Object value = outputs.get(label);
			_addOutputToSignature(label, value);
		}
	}

	
	///////////////////////////////////////////////////////////////////////////
	///    actor configuration mutators -- UNCONFIGURED state only         ////

	public void addImplicitInput(String inputName) throws Exception {
		
		if (! _inputSignature.containsKey(inputName)) {
			_addInputToSignature(inputName, null);				
		} else {
			throw new Exception("Cannot add implicit input '" + inputName +
					"' to actor " + this + ".");
		}
	}
	
	public void addImplicitOutput(String outputName) throws Exception {
		
		if (! _outputSignature.containsKey(outputName)) {			
			_addOutputToSignature(outputName, null);
		} else {
			throw new Exception("Cannot add implicit output " + outputName +
					" to actor " + this + ".");
		}
	}
	
	public void addLogOutput() {
		_addOutputToSignature("__log__", null);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///                   configuration getters                            ////

	public synchronized String getName() {
		return _name;
	}

	public synchronized boolean isStateful() {
		return _stateful;
	}
	
	public Collection<String> getInputNames() {
		return _inputSignature.keySet();
	}
	
	public Collection<String> getOutputNames() {
		return _outputSignature.keySet();
	}

	public boolean hasOptionalInput(String name) {
		InputSignatureElement input = _inputSignature.get(name);
		return input != null && input.isOptional();
	}
	
	public String getInputType(String name) {
		InputSignatureElement input = _inputSignature.get(name);
		return input.getType();
	}

	public String getInputLocalPath(String name) {
		InputSignatureElement input = _inputSignature.get(name);
		return input.getLocalPath();
	}
	
	public String getInputDescription(String name) {
		InputSignatureElement input = _inputSignature.get(name);
		return input.getDescription();
	}
	
	public Object getDefaultInputValue(String name) {
		InputSignatureElement input = _inputSignature.get(name);
		return input.getDefaultValue();
	}

	public synchronized String getStepDirectoryFileSystem() {
		return _stepDirectoryFileSystem;
	}
	
	public synchronized boolean usesStepDirectory() {
		return _usesStepDirectory;
	}
	
	///////////////////////////////////////////////////////////////////////////
	////               public actor lifecycle methods                      ////
	
			
	public void configure()throws Exception {
		_runCount = 0;
	}
	
	public void initialize() throws Exception {
		
		for (Map.Entry<String, Object> entry : _defaultInputValues.entrySet()) {
			
			String label = entry.getKey();
			
			if (_actorStatus.getInputEnable(label)) {
				this.setInputValue(label, entry.getValue());
			}
		}

		_stepOfScratchDirectory = 0;
		
		resetInputEnables();
	}
	
	public synchronized void reset() throws Exception {
		_runCount = 0;
	}

	// This method is called only on top-level workflows (and solitary actors) from WorkflowRunner.run()
	public synchronized void loadInputValues(Map<String, Object> inputBindings, boolean ignoreExtraInputs) throws Exception {
		
		for (String inputName: _inputSignature.keySet()) {
			if ( !_defaultInputValues.containsKey(inputName) && !inputBindings.containsKey(inputName)) {
		    	throw new Exception(this + " requires missing input '" + inputName + "'"); 
			}
		}
		
		if (inputBindings != null) {
			for (Map.Entry<String,Object> entry :  inputBindings.entrySet()) {
				String inputName = entry.getKey();
				Object inputValue = entry.getValue();
			    if ((!ignoreExtraInputs) && _inputSignature.get(inputName) == null) {
			    	throw new Exception(this + " does not accept input '" + inputName + "'"); 
			    }
				setInputValue(inputName, inputValue);
			}
		}
	}
		
	public synchronized void loadStateValues(Map<String, Object> states) {
		
		if (states.size() > 0) {
			_stateVariables.putAll(states);
		}
	}
	
	public synchronized boolean readyForInput(String label) throws Exception {
		return _actorStatus.getInputEnable(label);
	}

	public synchronized boolean outputEnabled(String label) throws Exception {
		return _actorStatus.getOutputEnable(label);
	}
	
	// shorthand method for setting inputs on workflows (and actors) from Java code using builders
	public void set(String label, Object value) throws Exception {
		setInputValue(label, value);
	}
	
	public synchronized void setInputValue(String label, Object value) throws Exception {

		_inputValues.put(label, value);

		InputSignatureElement signatureElement = _inputSignature.get(label);
		
		if (signatureElement == null) {
			
			if (_constants.containsKey(label)) {
				throw new Exception("Attempt to reassign value of setting '" + label + "' " +
											"on actor " + this);
			}
			
		} else {
		
			if (value == null && !signatureElement.isNullable()) {
				throw new Exception("Null data received on input " + label);
			}
		
			_actorStatus.disableInput(label);
		}
	}
	
	protected synchronized void _abstractActorStep() throws Exception {

		_runCount++;

		resetInputEnables();
		
		for (String label: _outputSignature.keySet()) {
			boolean defaultOutputEnable = _outputSignature.get(label).getDefaultOutputEnable();
			_actorStatus.setOutputEnable(label, defaultOutputEnable);
		}

		if (_usesStepDirectory) {
			_actorStatus.setStepDirectory(_getCurrentStepDirectory());	
		}	
	}
	
	public synchronized void step() throws Exception {
		_abstractActorStep();
	}


	public synchronized void setStepCount(int count) {
		_actorStatus.setStepCount(count);
	}
	
	public synchronized void resetInputEnables() {

		for (String label: _inputSignature.keySet()) {
			boolean defaultInputEnable = _inputSignature.get(label).getDefaultInputEnable();
			_actorStatus.setReadyForInput(label, defaultInputEnable);
		}
	}

	public File getNextStepDirectory() throws Exception {
		return _createStepScratchDirectoryIfNeeded(1);		
	}
	
	public synchronized Object get(String label) throws Exception {
		return getOutputValue(label);
	}
	
	
	public synchronized Object getOutputValue(String label) throws Exception {
//		Contract.requires(_state == ActorFSM.STEPPED);
		return _outputValues.get(label);
	}

	public synchronized Object getStateValue(String label) throws Exception {
		return _stateVariables.get(label);
	}
	
	public void wrapup() throws Exception {
	}

	public void dispose() throws Exception {
	}
	

	public Map<String,Object> getFinalOutputs() throws Exception {

		HashMap<String,Object> outputValues = new HashMap<String,Object>();
	
		for (String outputLabel : _outputSignature.keySet()) {
			outputValues.put(outputLabel, _outputValues.get(outputLabel));
		}
		
		return outputValues;
	}
	
	
	public ActorState getFinalState() throws Exception {
		
		ActorState state = _actorStatus.copyState();
		
		Map <String,Object> stateValues = new HashMap<String,Object>();
		for (String label : _stateVariables.keySet()) {
			stateValues.put(label, _outputValues.get(label));
		}
		state.setStateValues(stateValues);
		
		return state;
	}	
	
	///////////////////////////////////////////////////////////////////////////
	////             protected actor lifecycle methods                     ////

	protected synchronized void _storeOutputValue(String label, Object value) throws Exception {	

		if (value == null && !_outputSignature.get(label).isNullable()) {
			throw new Exception(label);
		}
		
		_outputValues.put(label, value);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	////                   private helper methods                          ////
	
	private void _addInputToSignature(String label, Object value) throws Exception {
				
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
				_defaultInputValues.put(label, defaultValue);
				defaultIsNull = (defaultValue == null);
			}
							
			String description = (String) inputProperties.get("description");
			if (description != null) {
				inputElement.setDescription(description);
			}
			
			String typeName = (String) inputProperties.get("type");
			if (typeName != null) {					
				_variableTypes.put(label,typeName);
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
				inputElement.setDefaultInputEnable(defaultReadiness);
			}
		}

		_inputSignature.put(label, inputElement);
	}
	
	@SuppressWarnings("rawtypes")
	private synchronized void _addOutputToSignature(String label, Object value) {
		OutputSignatureElement outputElement = new OutputSignatureElement(label);
		
		if (value != null && value instanceof Map) {
			
			String typeName = (String) ((Map)value).get("type");
			if (typeName != null) {		
				_variableTypes.put(label,typeName);
			}

			Boolean nullable = (Boolean) ((Map)value).get("nullable");
			if (nullable != null && nullable) {
				outputElement.setIsNullable();
			}				
		}

		_outputSignature.put(label, outputElement);
		
	}

	private File _getCurrentStepDirectory() throws Exception {
		return _createStepScratchDirectoryIfNeeded(0);
	}

	private synchronized File _createStepScratchDirectoryIfNeeded(int delta) throws Exception {

		if (runDirectoryPath == null) {
			throw new Exception("No working directory exists for containing scratch directory.");
		}
	
		int step = _actorStatus.getStepCount() + delta;
		
		if (_stepOfScratchDirectory < step) {
			String scratchDirectory = runDirectoryPath + "/.steps/" + _name + "_" + step;
			_stepDirectory = new File(scratchDirectory);
			_stepDirectory.mkdirs();
			_stepOfScratchDirectory = step;
		}
		
		return _stepDirectory;
	}
	
	public int getRunCount() {
		return _runCount;
	}
}

