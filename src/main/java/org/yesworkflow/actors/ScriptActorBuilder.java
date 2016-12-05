package org.yesworkflow.actors;

public abstract class ScriptActorBuilder extends ActorBuilder {
	
	public ActorBuilder initialize(String initialize) {
		this.initialize = initialize;
		return this;
	}
	
	public ActorBuilder step(String step) {
		this.step = step;
		return this;
	}

	public ActorBuilder wrapup(String wrapup) {
		this.wrapup = wrapup;
		return this;
	}

	public Actor build(ScriptActor actor) throws Exception {
		
		super.build(actor);
		
		actor.setInitialize(initialize);
		actor.setStep(step);
		actor.setWrapup(wrapup);
		
		return actor;
	}
}
