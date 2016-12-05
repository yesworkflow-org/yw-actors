package org.yesworkflow.actors.groovy;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import org.yesworkflow.actors.ActorBuilder;

public class GroovyActorBuilder extends ActorBuilder {
	
    public GroovyActorBuilder errorStream(PrintStream errStream) {
        return (GroovyActorBuilder) super.errorStream(errStream);
    }

    public GroovyActorBuilder inputStream(InputStream inStream) {
    	return (GroovyActorBuilder) super.inputStream(inStream);
    }
    
    public GroovyActorBuilder outputStream(PrintStream outStream) {
        return (GroovyActorBuilder) super.outputStream(outStream);
    }
	
	public GroovyActorBuilder state(String name) {
		return (GroovyActorBuilder) super.state(name);
	}
	
	public GroovyActorBuilder input(String name) {
		return (GroovyActorBuilder) super.input(name);
	}
	
	public GroovyActorBuilder type(String variableName, String type) {
		return (GroovyActorBuilder) super.type(variableName, type);
	}

	public GroovyActorBuilder input(String name, Map<String,Object> properties) {
		return (GroovyActorBuilder) super.input(name, properties);
	}	
	
	public GroovyActorBuilder output(String name) {
		return (GroovyActorBuilder) super.output(name);
	}

	public GroovyActorBuilder output(String name, Map<String,Object> properties) {
		return (GroovyActorBuilder) super.output(name, properties);
	}

	public GroovyActorBuilder name(String name) {
		return (GroovyActorBuilder) super.name(name);
	}

	public GroovyActorBuilder types(Map<String, String> types) {
		return (GroovyActorBuilder) super.types(types);
	}

	public GroovyActor build() throws Exception {
		return (GroovyActor) super.build(new GroovyActor());
	}
}
