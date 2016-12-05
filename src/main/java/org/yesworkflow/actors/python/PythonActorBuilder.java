package org.yesworkflow.actors.python;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import org.yesworkflow.actors.ScriptActorBuilder;

public class PythonActorBuilder extends ScriptActorBuilder {

    public PythonActorBuilder errorStream(PrintStream errStream) {
        return (PythonActorBuilder) super.errorStream(errStream);
    }

    public PythonActorBuilder inputStream(InputStream inStream) {
    	return (PythonActorBuilder) super.inputStream(inStream);
    }
    
    public PythonActorBuilder outputStream(PrintStream outStream) {
        return (PythonActorBuilder) super.outputStream(outStream);
    }
	
	public PythonActorBuilder state(String name) {
		return (PythonActorBuilder) super.state(name);
	}
	
	public PythonActorBuilder input(String name) {
		return (PythonActorBuilder) super.input(name);
	}
	
	public PythonActorBuilder type(String variableName, String type) {
		return (PythonActorBuilder) super.type(variableName, type);
	}

	public PythonActorBuilder input(String name, Map<String,Object> properties) {
		return (PythonActorBuilder) super.input(name, properties);
	}	
	
	public PythonActorBuilder output(String name) {
		return (PythonActorBuilder) super.output(name);
	}

	public PythonActorBuilder output(String name, Map<String,Object> properties) {
		return (PythonActorBuilder) super.output(name, properties);
	}

	public PythonActorBuilder name(String name) {
		return (PythonActorBuilder) super.name(name);
	}

	public PythonActorBuilder types(Map<String, String> types) {
		return (PythonActorBuilder) super.types(types);
	}
	
	public PythonActorBuilder initialize(String initialize) {
		return (PythonActorBuilder) super.initialize(initialize);
	}
	
	public PythonActorBuilder step(String step) {
		return (PythonActorBuilder) super.step(step);
	}

	public PythonActorBuilder wrapup(String wrapup) {
		return (PythonActorBuilder) super.wrapup(wrapup);
	}
	
	public PythonActor build() throws Exception {
		return (PythonActor) super.build(new PythonActor());
	}

}
