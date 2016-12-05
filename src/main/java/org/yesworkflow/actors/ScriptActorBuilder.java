package org.yesworkflow.actors;

public abstract class ScriptActorBuilder extends ActorBuilder {
	public Actor build(ScriptActor actor) throws Exception {
		
		super.build(actor);
		
		actor.setInitialize(_initialize);
		actor.setStep(_step);
		actor.setWrapup(_wrapup);
		
		return actor;
	}
}
