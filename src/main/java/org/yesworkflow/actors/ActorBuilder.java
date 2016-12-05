package org.yesworkflow.actors;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ActorBuilder implements IActorBuilder {

    protected InputStream inStream = System.in;
    protected PrintStream outStream = System.out;
    protected PrintStream errStream = System.err;
    
	protected String				_name = "";
	protected String 				_initialize = "";
	protected String 				_step = "";
	protected String 				_wrapup = "";
	protected Map<String,Object>	_inputs = new HashMap<String,Object>(); 
	protected Map<String,Object> 	_outputs = new HashMap<String,Object>(); 
	protected Map<String,Object> 	_state = new HashMap<String,Object>();
	protected Map<String,String> 	_types = new HashMap<String,String>();

    
    public synchronized ActorBuilder errorStream(PrintStream errStream) {
        this.errStream = errStream;
        return this;
    }

    public synchronized ActorBuilder inputStream(InputStream inStream) {
        this.inStream = inStream;
        return this;
    }
    
    public synchronized ActorBuilder outputStream(PrintStream outStream) {
        this.outStream = outStream;
        return this;
    }
	
	public ActorBuilder state(String name) {
		_state.put(name,null);
		return this;
	}
	
	public ActorBuilder initialize(String initialize) {
		_initialize = initialize;
		return this;
	}
	
	public ActorBuilder step(String step) {
		_step = step;
		return this;
	}

	public ActorBuilder wrapup(String wrapup) {
		_wrapup = wrapup;
		return this;
	}

	public ActorBuilder input(String name) {
		_inputs.put(name, null);
		return this;
	}
	
	public ActorBuilder type(String variableName, String type) {
		_types.put(variableName, type);
		return this;
	}

	public ActorBuilder input(String name, Map<String,Object> properties) {
		_inputs.put(name, properties);
		return this;
	}	
	
	public ActorBuilder output(String name) {
		_outputs.put(name, null);
		return this;
	}

	public ActorBuilder output(String name, Map<String,Object> properties) {
		_outputs.put(name, properties);
		return this;
	}

	public ActorBuilder name(String name) {
		_name = name;
		return this;
	}

	public ActorBuilder types(Map<String, String> types) {
		_types.putAll(types);
		return this;
	}
	
	public Actor build(Actor actor) throws Exception {
		
		actor.setName(_name);
		actor.setInputs(_inputs);
		actor.setOutputs(_outputs);
		actor.setState(_state);
		actor.setTypes(_types);
		
		return actor;
	}
}
