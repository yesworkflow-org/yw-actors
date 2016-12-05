package org.yesworkflow.actors.groovy;

import org.yesworkflow.actors.ActorBuilder;

public class GroovyActorBuilder extends ActorBuilder {
	
	public GroovyActor build() throws Exception {
		
		GroovyActor actor = new GroovyActor();
		
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
