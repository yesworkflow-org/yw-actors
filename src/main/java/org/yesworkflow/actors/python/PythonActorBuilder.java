package org.yesworkflow.actors.python;

import org.yesworkflow.actors.ScriptActorBuilder;

public class PythonActorBuilder extends ScriptActorBuilder {
	
    @Override
    public PythonActor build() throws Exception {
        return (PythonActor) super.build(new PythonActor());
    }
    
}
