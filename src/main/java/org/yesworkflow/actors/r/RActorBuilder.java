package org.yesworkflow.actors.r;

import java.util.HashMap;
import java.util.Map;

import org.yesworkflow.actors.ActorBuilder;

public class RActorBuilder extends ActorBuilder {
	
	private String				_name = "";
	private String 				_initialize = "";
	private String 				_step = "";
	private String 				_wrapup = "";
	private Map<String,Object>	_inputs = new HashMap<String,Object>(); 
	private Map<String,Object> 	_outputs = new HashMap<String,Object>(); 
	private Map<String,Object> 	_state = new HashMap<String,Object>();
	private Map<String,String> 	_types = new HashMap<String,String>();
		
	public RActorBuilder state(String name) {
		_state.put(name,null);
		return this;
	}
	
	public RActorBuilder initialize(String initialize) {
		_initialize = initialize;
		return this;
	}
	
	public RActorBuilder step(String step) {
		_step = step;
		return this;
	}

	public RActorBuilder wrapup(String wrapup) {
		_wrapup = wrapup;
		return this;
	}

	public RActorBuilder input(String name) {
		_inputs.put(name, null);
		return this;
	}
	
	public RActorBuilder type(String variableName, String type) {
		_types.put(variableName, type);
		return this;
	}

	public RActorBuilder input(String name, Map<String,Object> properties) {
		_inputs.put(name, properties);
		return this;
	}	
	
	public RActorBuilder output(String name) {
		_outputs.put(name, null);
		return this;
	}

	public RActorBuilder output(String name, Map<String,Object> properties) {
		_outputs.put(name, properties);
		return this;
	}

	public RActorBuilder name(String name) {
		_name = name;
		return this;
	}

	public RActorBuilder types(Map<String, String> types) {
		_types.putAll(types);
		return this;
	}
	
	public RActor build() throws Exception {
		
		RActor actor = new RActor();
		
		actor.setName(_name);
		actor.setInputs(_inputs);
		actor.setOutputs(_outputs);
		actor.setState(_state);
		actor.setTypes(_types);
		actor.setInitialize(_initialize);
		actor.setStep(_step);
		actor.setWrapup(_wrapup);
		
		return actor;
	}

}
