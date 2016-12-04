package org.yesworkflow.actors.python;

import java.io.IOException;

import org.yesworkflow.actors.IActorScriptBuilder;
import org.yesworkflow.actors.AugmentedScriptActor;

import com.google.gson.Gson;

public class PythonActor extends AugmentedScriptActor {

	
	public PythonActor() {
		super();
		_scriptExtension = "py";
	}
	
	@Override
	public IActorScriptBuilder getNewScriptBuilder() {
		return new PythonActor.ScriptBuilder();
	}
		
	@Override
	public synchronized String getScriptRunCommand() {
		return "python -";
	}
	
	@Override
	public DataSerializationFormat getOutputSerializationFormat() {
		return DataSerializationFormat.YAML;
	}
	
	public static class ScriptBuilder implements IActorScriptBuilder {

		protected StringBuilder _script = new StringBuilder();
		private final static String EOL = System.getProperty("line.separator");

		public IActorScriptBuilder append(String text) {
			_script.append(		text	);
			return this;
		}

		public IActorScriptBuilder appendCode(String code) {
			_script.append(		code	)
				   .append(		EOL		);
			return this;
		}

		public ScriptBuilder appendSeparator() {
			_script.append(		"######################################################################################"	)
				   .append(		EOL																							);
			return this;
		}

		public ScriptBuilder appendBlankLine() {
			_script.append(	EOL	);
			return this;
		}

		
		public ScriptBuilder appendComment(String text) {
			_script.append(		"# "	)
				   .append(		text	)
			   	   .append(		EOL		);
			return this;
		}

		@Override
		public IActorScriptBuilder appendLiteralAssignment(String name, Object value, String type, boolean mutable, boolean nullable) throws Exception {

		if (value == null) {
				_assignNullLiteral(name);
			} else if (type == null) {
				_assignStringLiteral(name, value);
			} else if (type.equals("Collection")) {
				_assignJsonLiteral(name, value);
			} else if (type.equals("String")) {
				_assignStringLiteral(name, value);
			} else if (type.equals("File")) {
				_assignStringLiteral(name, value);
			} else if (type.equals("Boolean")) {
				_assignBooleanLiteral(name, value, type);
			} else if (type.equals("Integer")) {
				_assignNumberLiteral(name, value, type);
			} else {
				_assignOtherLiteral(name, value);
			}
			return this;
		}
		
		private ScriptBuilder _assignJsonLiteral(String name, Object value) throws ClassNotFoundException {

//			System.out.println( value.getClass().getSimpleName() );
//			Class collectionType = Class.forName("java.util.Collection");
//			System.out.println( value.getClass().isArray());
//			System.out.println( collectionType.isAssignableFrom(value.getClass()));
			
			Gson gson = new Gson();
			String json = gson.toJson(value);
			
			_script.append(		name						)
				   .append( 	" = json.load(StringIO('"	)
				   .append( 	json						)
				   .append(		"'))"						)
				   .append(		EOL							);
			
			return this;
		}
		
		private ScriptBuilder _assignStringLiteral(String name, Object value) {
			_script.append(		name	)
				   .append( 	"="		)
				   .append( 	"'"		)
				   .append( 	value	)
				   .append( 	"'"		)
				   .append(		EOL		);
			return this;
		}

		private ScriptBuilder _assignBooleanLiteral(String name, Object value, String type) throws Exception {
			
			Boolean b = null;
			if (value instanceof Boolean) {
				b = ((Boolean)value == true);
			} else if (value instanceof Number) {
				b = (((Number)value).intValue() != 0);
			} else {
				throw new Exception("Error assigning value to python " + type + " variable '" + name + "': " + value);
			}
			_script.append(		name		)
				   .append( 	"="			)
				   .append( 	b ? 1 : 0	)
				   .append(		EOL			);
			return this;
		}

		private ScriptBuilder _assignNumberLiteral(String name, Object value, String type) throws Exception {
			if (! (value instanceof Number)) {
				throw new Exception("Error assigning value to python " + type + " variable '" + name + "': " + value);
			}
			_script.append(		name	)
				   .append( 	"="		)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private ScriptBuilder _assignOtherLiteral(String name, Object value) {
			_script.append(		name	)
				   .append( 	"="		)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private ScriptBuilder _assignNullLiteral(String name) {
			_script.append(		name	)
				   .append( 	"=None"		)
				   .append(		EOL		);
			return this;
		}
		
		public ScriptBuilder appendChangeDirectory(String path) {
			_script.append(		"os.chdir('"	)
				   .append( 	path			)
				   .append(		"')"			)
				   .append(		EOL				);
			return this;
		}

		public ScriptBuilder appendPrintStringStatement(String string) {
			_script.append(		"print('"	)
				   .append( 	string		)
				   .append(		"')"		)
				   .append(		EOL			);
			return this;
		}

		public IActorScriptBuilder appendSerializationBeginStatement() {
			_script.append( 	"_outputMap = dict()" 			+ EOL );
			return this;
		}
		
		public IActorScriptBuilder appendSerializationEndStatement() {
			_script.append( 	"if (len(_outputMap) > 0) : " 		+
								"  print(json.dumps(_outputMap))" 	+ EOL );
			return this;
		}
		
		public ScriptBuilder appendOutputVariableSerializationStatement(String name, String type) {
			appendVariableSerializationStatement(name, type);			
			return this;
		}
		
		public ScriptBuilder appendVariableSerializationStatement(String name, String type) {
			
			_script.append(			"_outputMap['"			)
				   .append(			name					)
				   .append(			"'] = "					);
			
			if (type != null && type.equals("File")) {
				
				_script.append(		name + ".__str__()"		);
			
			} else {
				_script.append(		name					);
			
			}
			
			_script.append(			EOL						);
			
			return this;
		}

		public IActorScriptBuilder _appendNullStringYamlPrintStatement(String name) {
			appendVariableSerializationStatement(name, null);
			return this;
		}
		
		public IActorScriptBuilder appendNonNullStringVariableSerializationPrintStatement(String name) {
			appendVariableSerializationStatement(name, null);
			return this;
		}

		public ScriptBuilder appendInputControlFunctions() {

			appendComment("define functions for enabling and disabling actor inputs");
			appendCode( "def enableInput(input)      :   global enabledInputs;    enabledInputs   += ' ' + input"  );
			appendCode( "def disableInput(input)     :   global disabledInputs;   disabledInputs  += ' ' + input"  );
			appendBlankLine();

			appendComment("initialize input control variables");
			appendCode( "enabledInputs   = ''" );
			appendCode( "disabledInputs  = ''" );

			return this;
		}

		public ScriptBuilder appendOutputControlFunctions() {
			
			appendComment("define functions for enabling and disabling actor outputs");
			appendCode( "def enableOutput(output)    :   global enabledOutputs;   enabledOutputs  += ' ' + output" );
			appendCode( "def disableOutput(output)   :   global disabledOutputs;  disabledOutputs += ' ' + output" );
			appendBlankLine();

			appendComment("initialize output control variables");
			appendCode( "enabledOutputs  = ''" );
			appendCode( "disabledOutputs = ''" );

			return this;
		}

		
		public String toString() {
			return _script.toString();
		}

		@Override
		public void appendScriptHeader(IActorScriptBuilder script,
				String scriptType) throws IOException {

			appendComment("import packages required by all python actors");
			appendCode( "import os, json" );
			appendCode( "from io import StringIO");
			appendBlankLine();
		}

		@Override
		public IActorScriptBuilder appendScriptExitCommend() {
			return this;
		}
	}
}
