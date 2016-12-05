package org.yesworkflow.actors;

public interface IScriptActor extends IActor {
	void setStep(String script);
	void setWrapup(String script);
	void setDispose(String script);
	void setInitialize(String script);
	void setConfigure(String script);
	void setStderrMode(String mode) throws Exception;
}