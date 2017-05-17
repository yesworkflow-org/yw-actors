package org.yesworkflow.actors;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ActorBuilder {

    protected InputStream inStream = System.in;
    protected PrintStream outStream = System.out;
    protected PrintStream errStream = System.err;
    
	protected String				actorName = "";
	protected Map<String,Object>	inputs = new HashMap<String,Object>(); 
	protected Map<String,Object> 	outputs = new HashMap<String,Object>(); 
	protected Map<String,Object> 	state = new HashMap<String,Object>();
	protected Map<String,String> 	types = new HashMap<String,String>();
    
    public ActorBuilder errorStream(PrintStream errStream)  { this.errStream = errStream; return this; }
    public ActorBuilder inputStream(InputStream inStream)   { this.inStream = inStream; return this; }    
    public ActorBuilder outputStream(PrintStream outStream) { this.outStream = outStream; return this; }
	public ActorBuilder state(String name)                  { this.state.put(name,null); return this; }	
	public ActorBuilder input(String name)                  { this.inputs.put(name, null); return this; }
	public ActorBuilder type(String name, String type)      { this.types.put(name, type); return this; }
	public ActorBuilder output(String name)                 { this.outputs.put(name, null); return this; }
    public ActorBuilder name(String actorName)              { this.actorName = actorName; return this; }
    public ActorBuilder types(Map<String,String> types)     { this.types.putAll(types); return this; }
    public ActorBuilder input(String name, Map<String,Object> properties)  { this.inputs.put(name, properties); return this; }
	public ActorBuilder output(String name, Map<String,Object> properties) { this.outputs.put(name, properties); return this;}
    
	public Actor build(Actor actor) throws Exception {
		actor.setName(actorName);
		actor.setInputs(inputs);
		actor.setOutputs(outputs);
		actor.setState(state);
		actor.setTypes(types);
		actor.inputStream(inStream);
		actor.outputStream(outStream);
		actor.errorStream(errStream);
		return actor;
	}

    public abstract Actor build() throws Exception;
}
