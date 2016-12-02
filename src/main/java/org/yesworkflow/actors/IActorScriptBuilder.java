package org.yesworkflow.actors;

import java.io.IOException;

public interface IActorScriptBuilder {
	IActorScriptBuilder appendBlankLine();
	IActorScriptBuilder appendChangeDirectory(String path);
	IActorScriptBuilder append(String text);
	IActorScriptBuilder appendCode(String code);
	IActorScriptBuilder appendComment(String text);
	IActorScriptBuilder appendInputControlFunctions();
	IActorScriptBuilder appendOutputControlFunctions();
	IActorScriptBuilder appendLiteralAssignment(String name, Object value, String type, boolean mutable, boolean nullable) throws Exception;
	IActorScriptBuilder appendPrintStringStatement(String string);
	IActorScriptBuilder appendSeparator();
	IActorScriptBuilder appendSerializationBeginStatement();
	IActorScriptBuilder appendSerializationEndStatement();
	IActorScriptBuilder appendVariableSerializationStatement(String name, String type);
	IActorScriptBuilder appendOutputVariableSerializationStatement(String name, String type);
	IActorScriptBuilder appendNonNullStringVariableSerializationPrintStatement(String name);
	void appendScriptHeader(IActorScriptBuilder script, String scriptType) throws IOException;
	IActorScriptBuilder appendScriptExitCommend();
}
