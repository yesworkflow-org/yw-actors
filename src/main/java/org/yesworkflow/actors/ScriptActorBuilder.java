package org.yesworkflow.actors;

public abstract class ScriptActorBuilder extends ActorBuilder {
    
    protected String initialize = "";
    protected String start = "";
    protected String step = "";
    protected String wrapup = "";

    public ScriptActorBuilder initialize(String initialize) { this.initialize = initialize; return this; }
    public ScriptActorBuilder start(String start)           { this.start = start; return this; }
    public ScriptActorBuilder step(String step)             { this.step = step; return this; }
    public ScriptActorBuilder wrapup(String wrapup)         { this.wrapup = wrapup; return this; }
    
	public ScriptActor build(ScriptActor actor) throws Exception {
		super.build(actor);
		actor.setInitialize(initialize);
        actor.setStart(start);
		actor.setStep(step);
		actor.setWrapup(wrapup);
		return actor;
	}
}
