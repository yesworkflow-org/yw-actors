package org.yesworkflow.actors;

import java.io.IOException;

public abstract class ScriptAugmenter {
    
    protected final static String EOL = System.getProperty("line.separator");

    protected StringBuilder _script = new StringBuilder();
        
    public ScriptAugmenter append(String text) {
        _script.append(text);
        return this;
    }

    public ScriptAugmenter appendBlankLine() {
        _script.append(EOL);
        return this;
    }

    public String toString() {
        return _script.toString();
    }

    public abstract ScriptAugmenter appendChangeDirectory(String string);
    public abstract ScriptAugmenter appendCode(String originalScript);
    public abstract ScriptAugmenter appendComment(String string);
    public abstract ScriptAugmenter appendHeader(ScriptAugmenter script, String scriptType) throws IOException;
    public abstract ScriptAugmenter appendInputControlFunctions();
    public abstract ScriptAugmenter appendLiteralAssignment(String name, Object object, String string, boolean b, boolean nullable) throws Exception;
    public abstract ScriptAugmenter appendNonNullStringVariableSerializationPrintStatement(String name);
    public abstract ScriptAugmenter appendOutputControlFunctions();
    public abstract ScriptAugmenter appendOutputVariableSerializationStatement(String name, String string);
    public abstract ScriptAugmenter appendPrintStringStatement(String scriptoutputdelimiter);
    public abstract ScriptAugmenter appendScriptExitCommand();
    public abstract ScriptAugmenter appendSerializationBeginStatement();
    public abstract ScriptAugmenter appendSerializationEndStatement();
    public abstract ScriptAugmenter appendVariableSerializationStatement(String name, String string);
}
