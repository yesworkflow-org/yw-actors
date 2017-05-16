package org.yesworkflow.actors.groovy;

import org.yesworkflow.actors.ScriptActorBuilder;

public class GroovyActorBuilder extends ScriptActorBuilder {
	
    @Override
    public GroovyActor build() throws Exception {
        return (GroovyActor) super.build(new GroovyActor());
    }
}
