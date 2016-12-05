package org.yesworkflow.actors.r;

import org.yesworkflow.actors.ActorBuilder;

public class RActorBuilder extends ActorBuilder {
	
	public RActor build() throws Exception {
		
		RActor actor = new RActor();
		
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
