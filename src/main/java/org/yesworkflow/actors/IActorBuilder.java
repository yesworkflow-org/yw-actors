package org.yesworkflow.actors;

import java.util.Map;

public interface IActorBuilder {
	public IActorBuilder types(Map<String,String> types);
	public IActor build() throws Exception;
}
