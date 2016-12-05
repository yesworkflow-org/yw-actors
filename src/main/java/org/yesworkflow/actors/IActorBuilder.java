package org.yesworkflow.actors;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

public interface IActorBuilder {
	IActorBuilder errorStream(PrintStream errStream);
	IActorBuilder inputStream(InputStream inStream);
	IActorBuilder outputStream(PrintStream outStream);
    IActorBuilder types(Map<String,String> types);
	IActor build() throws Exception;
}
