package org.yesworkflow.actors.r;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import org.yesworkflow.actors.ScriptActorBuilder;

public class RActorBuilder extends ScriptActorBuilder {
	
    public RActorBuilder errorStream(PrintStream errStream) {
        return (RActorBuilder) super.errorStream(errStream);
    }

    public RActorBuilder inputStream(InputStream inStream) {
    	return (RActorBuilder) super.inputStream(inStream);
    }
    
    public RActorBuilder outputStream(PrintStream outStream) {
        return (RActorBuilder) super.outputStream(outStream);
    }
	
	public RActorBuilder state(String name) {
		return (RActorBuilder) super.state(name);
	}
	
	public RActorBuilder input(String name) {
		return (RActorBuilder) super.input(name);
	}
	
	public RActorBuilder type(String variableName, String type) {
		return (RActorBuilder) super.type(variableName, type);
	}

	public RActorBuilder input(String name, Map<String,Object> properties) {
		return (RActorBuilder) super.input(name, properties);
	}	
	
	public RActorBuilder output(String name) {
		return (RActorBuilder) super.output(name);
	}

	public RActorBuilder output(String name, Map<String,Object> properties) {
		return (RActorBuilder) super.output(name, properties);
	}

	public RActorBuilder name(String name) {
		return (RActorBuilder) super.name(name);
	}

	public RActorBuilder types(Map<String, String> types) {
		return (RActorBuilder) super.types(types);
	}
	
	public RActorBuilder initialize(String initialize) {
		return (RActorBuilder) super.initialize(initialize);
	}
	
	public RActorBuilder step(String step) {
		return (RActorBuilder) super.step(step);
	}

	public RActorBuilder wrapup(String wrapup) {
		return (RActorBuilder) super.wrapup(wrapup);
	}

	public RActor build() throws Exception {
		return (RActor) super.build(new RActor());
	}

}
