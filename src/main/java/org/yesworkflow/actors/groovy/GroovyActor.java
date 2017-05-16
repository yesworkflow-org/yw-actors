package org.yesworkflow.actors.groovy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yesworkflow.actors.ActorStatus;
import org.yesworkflow.actors.ScriptActor;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GroovyActor extends ScriptActor {

    private String _wrapperScript;
    private Map<String,Script> _compiledScripts;    
    
	public GroovyActor() {
		super();
		
		synchronized(this) {
			_compiledScripts = new HashMap<String, Script>();
		}
	}

	public synchronized Object clone() throws CloneNotSupportedException {
		GroovyActor theClone = (GroovyActor) super.clone();
		theClone._compiledScripts = new HashMap<String, Script>();
		return theClone;
	}
		
	public synchronized void setWrapperScript(String script) {
		_wrapperScript = script;
	}

	public synchronized void configure() throws Exception {
		
		super.configure();
	
		if (_configureScript != null) {

			Binding binding = new Binding();
	
			_bindConstants(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);
	
			runScript(_configureScript, binding);
		}	
	}
	
	@Override
	public synchronized void initialize() throws Exception {
	
		super.initialize();
		
		if (_initializeScript != null) {
			
			Binding binding = new Binding();

			_bindConstants(binding);
			_bindStateVariables(binding);
			_bindInputs(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);

			runScript(_initializeScript, binding);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void step() throws Exception {
		
		super.step();

		if (_stepScript != null) {
			
			Binding binding = new Binding();

			_bindConstants(binding);
			_bindStateVariables(binding);
			_bindInputs(binding);
			binding.setVariable("_outputs", outputSignature.keySet());
			bindSpecial(binding);
			bindDirectories(binding);

			try {
				runScript(_stepScript, binding);
			} catch (Exception e) {
				throw e;
			}
			
			_updateOutputVariables((Map<String,Object>)binding.getVariables());			
		}
	}

	
	@Override
	public synchronized void wrapup() throws Exception {
		
		super.wrapup();
		
		if (_wrapupScript != null) {
			
			Binding binding = new Binding();
			
			_bindConstants(binding);
			_bindStateVariables(binding);
			_bindInputs(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);
	
			runScript(_wrapupScript, binding);
		}
	}

	@Override
	public synchronized void dispose() throws Exception {
		
		super.dispose();
		
		if (_disposeScript != null) {
			
			Binding binding = new Binding();
	
			_bindConstants(binding);			
			_bindStateVariables(binding);
			_bindInputs(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);
	
			runScript(_disposeScript, binding);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected synchronized void runScript(String script, Binding binding) throws Exception {
		
		runTheScript(script, binding);
		
		actorStatus = (ActorStatus)binding.getVariable("_status");
		
		_updateStateVariables((Map<String,Object>)binding.getVariables());
	}

	protected synchronized void runTheScript(String script, Binding binding) throws Exception {
		
		Script groovyScript;
		
		if (_wrapperScript == null ) {
			groovyScript = _getCompiledGroovyScript(script);
		} else {
			groovyScript = _getCompiledGroovyScript(_wrapperScript);
		}
		
		binding.setVariable("_script", script);
		
		_executeGroovyScript(groovyScript, binding);	
	}
	
	private synchronized void _executeGroovyScript(Script script, Binding binding) throws Exception {
		
		script.setBinding(binding);
		
		try {
			script.run();
		} catch (Exception e) {
			throw e;
		}		
	}
	
	private synchronized Script _getCompiledGroovyScript(String script) {
		
		Script compiledScript = _compiledScripts.get(script);
		
		if (compiledScript == null) {
			String prefixedScript =  script;
			compiledScript =  new GroovyShell().parse(prefixedScript);
			_compiledScripts.put(script, compiledScript);
		}
		
		return compiledScript;
	}
	
	private synchronized void bindSpecial(Binding binding) {
		
		binding.setVariable("_inputs", inputValues);
		binding.setVariable("_states", stateVariables);
		binding.setVariable("_status" , actorStatus);
		binding.setVariable("_type", variableTypes);
		binding.setVariable("_this" , this);
		binding.setVariable("STEP", actorStatus.getStepCount());
	}
	
	private synchronized void bindDirectories(Binding binding) throws Exception {
		
		if (runDirectoryPath != null) {

			binding.setVariable("_runDir", runDirectoryPath);
			
			Map<String,String> outflowDirectoryMap = new HashMap<String,String>();
			binding.setVariable("_outflowDirectory", outflowDirectoryMap);
		}		
	}

	private synchronized void _bindConstants(Binding binding) {
		for (String name: constants.keySet()) {
			Object value = constants.get(name);
			binding.setVariable(name,value);
		}
	}

	private synchronized void _bindStateVariables(Binding binding) {
		for (String name: stateVariables.keySet()) {
			Object value = stateVariables.get(name);
			binding.setVariable(name, value);
		}
	}	
	
	private synchronized void _bindInputs(Binding binding) {
		for (String name: inputSignature.keySet()) {
			binding.setVariable( name, inputValues.get(name) );
		}
	}	
}