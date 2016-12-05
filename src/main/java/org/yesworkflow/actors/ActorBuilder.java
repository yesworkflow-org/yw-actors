package org.yesworkflow.actors;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ActorBuilder implements IActorBuilder {

    protected InputStream inStream = System.in;
    protected PrintStream outStream = System.out;
    protected PrintStream errStream = System.err;
    
	protected String				actorName = "";
	protected String 				initialize = "";
	protected String 				step = "";
	protected String 				wrapup = "";
	protected Map<String,Object>	inputs = new HashMap<String,Object>(); 
	protected Map<String,Object> 	outputs = new HashMap<String,Object>(); 
	protected Map<String,Object> 	state = new HashMap<String,Object>();
	protected Map<String,String> 	types = new HashMap<String,String>();

    
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
		state.put(name,null);
		return this;
	}
	
	public ActorBuilder input(String name) {
		inputs.put(name, null);
		return this;
	}
	
	public ActorBuilder type(String variableName, String type) {
		types.put(variableName, type);
		return this;
	}

	public ActorBuilder input(String name, Map<String,Object> properties) {
		inputs.put(name, properties);
		return this;
	}	
	
	public ActorBuilder output(String name) {
		outputs.put(name, null);
		return this;
	}

	public ActorBuilder output(String name, Map<String,Object> properties) {
		outputs.put(name, properties);
		return this;
	}

	public ActorBuilder name(String name) {
		actorName = name;
		return this;
	}

	public ActorBuilder types(Map<String, String> types) {
		this.types.putAll(types);
		return this;
	}
	
	public Actor build(Actor actor) throws Exception {
		
		actor.setName(actorName);
		actor.setInputs(inputs);
		actor.setOutputs(outputs);
		actor.setState(state);
		actor.setTypes(types);
		
		return actor;
	}
}
