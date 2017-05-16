package org.yesworkflow.actors.r;

import org.yesworkflow.actors.ScriptActorBuilder;

public class RActorBuilder extends ScriptActorBuilder {
    
    @Override
    public RActor build() throws Exception {
        return (RActor) super.build(new RActor());
    }
}
