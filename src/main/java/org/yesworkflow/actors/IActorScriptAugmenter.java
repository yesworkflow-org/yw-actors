package org.yesworkflow.actors;

import java.io.IOException;

public interface IActorScriptAugmenter {
	IActorScriptAugmenter appendBlankLine();
	IActorScriptAugmenter appendChangeDirectory(String path);
	IActorScriptAugmenter append(String text);
	IActorScriptAugmenter appendCode(String code);
	IActorScriptAugmenter appendComment(String text);
	IActorScriptAugmenter appendInputControlFunctions();
	IActorScriptAugmenter appendOutputControlFunctions();
	IActorScriptAugmenter appendLiteralAssignment(String name, Object value, String type, boolean mutable, boolean nullable) throws Exception;
	IActorScriptAugmenter appendPrintStringStatement(String string);
	IActorScriptAugmenter appendSeparator();
	IActorScriptAugmenter appendSerializationBeginStatement();
	IActorScriptAugmenter appendSerializationEndStatement();
	IActorScriptAugmenter appendVariableSerializationStatement(String name, String type);
	IActorScriptAugmenter appendOutputVariableSerializationStatement(String name, String type);
	IActorScriptAugmenter appendNonNullStringVariableSerializationPrintStatement(String name);
	IActorScriptAugmenter appendScriptHeader(IActorScriptAugmenter script, String scriptType) throws IOException;
	IActorScriptAugmenter appendScriptExitCommand();
}
