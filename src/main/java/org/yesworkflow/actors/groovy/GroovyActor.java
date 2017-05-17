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

    private String wrapperScript;
    private Map<String,Script> compiledScripts;    
    
	public GroovyActor() {
		super();
		synchronized(this) {
			compiledScripts = new HashMap<String, Script>();
		}
	}

	public synchronized Object clone() throws CloneNotSupportedException {
		GroovyActor theClone = (GroovyActor) super.clone();
		theClone.compiledScripts = new HashMap<String, Script>();
		return theClone;
	}
		
	public synchronized void setWrapperScript(String script) {
		wrapperScript = script;
	}

	@Override
	public synchronized void initialize() throws Exception {
		
		super.initialize();
	
		if (initializeScript != null) {

			Binding binding = new Binding();
	
			bindConstants(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);
	
			runScript(initializeScript, binding);
		}	
	}
	
	@Override
	public synchronized void start() throws Exception {
	
		super.start();
		
		if (startScript != null) {
			
			Binding binding = new Binding();

			bindConstants(binding);
			bindStateVariables(binding);
			bindInputs(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);

			runScript(startScript, binding);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void step() throws Exception {
		
		super.step();

		if (stepScript != null) {
			
			Binding binding = new Binding();

			bindConstants(binding);
			bindStateVariables(binding);
			bindInputs(binding);
			binding.setVariable("_outputs", outputSignature.keySet());
			bindSpecial(binding);
			bindDirectories(binding);

			try {
				runScript(stepScript, binding);
			} catch (Exception e) {
				throw e;
			}
			
			updateOutputVariables((Map<String,Object>)binding.getVariables());			
		}
	}

	
	@Override
	public synchronized void wrapup() throws Exception {
		
		super.wrapup();
		
		if (wrapupScript != null) {
			
			Binding binding = new Binding();
			
			bindConstants(binding);
			bindStateVariables(binding);
			bindInputs(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);
	
			runScript(wrapupScript, binding);
		}
	}

	@Override
	public synchronized void dispose() throws Exception {
		
		super.dispose();
		
		if (disposeScript != null) {
			
			Binding binding = new Binding();
	
			bindConstants(binding);			
			bindStateVariables(binding);
			bindInputs(binding);
			binding.setVariable("_outputs", new ArrayList<String>());
			bindSpecial(binding);
	
			runScript(disposeScript, binding);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected synchronized void runScript(String script, Binding binding) throws Exception {
		runTheScript(script, binding);
		actorStatus = (ActorStatus)binding.getVariable("_status");
		updateStateVariables((Map<String,Object>)binding.getVariables());
	}

	protected synchronized void runTheScript(String script, Binding binding) throws Exception {
		Script groovyScript;
		if (wrapperScript == null ) {
			groovyScript = compileGroovyScript(script);
		} else {
			groovyScript = compileGroovyScript(wrapperScript);
		}
		binding.setVariable("_script", script);
	    groovyScript.setBinding(binding);
	    groovyScript.run();
	}
	
	private synchronized Script compileGroovyScript(String script) {
		Script compiledScript = compiledScripts.get(script);
		if (compiledScript == null) {
			String prefixedScript =  script;
			compiledScript =  new GroovyShell().parse(prefixedScript);
			compiledScripts.put(script, compiledScript);
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

	private synchronized void bindConstants(Binding binding) {
		for (String name: constants.keySet()) {
			Object value = constants.get(name);
			binding.setVariable(name,value);
		}
	}

	private synchronized void bindStateVariables(Binding binding) {
		for (String name: stateVariables.keySet()) {
			Object value = stateVariables.get(name);
			binding.setVariable(name, value);
		}
	}	
	
	private synchronized void bindInputs(Binding binding) {
		for (String name: inputSignature.keySet()) {
			binding.setVariable( name, inputValues.get(name) );
		}
	}	
}