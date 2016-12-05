package org.yesworkflow.actors.python;

import org.yesworkflow.actors.ActorBuilder;

public class PythonActorBuilder extends ActorBuilder {
		
	public PythonActor build() throws Exception {
		
		PythonActor actor = new PythonActor();
		
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
