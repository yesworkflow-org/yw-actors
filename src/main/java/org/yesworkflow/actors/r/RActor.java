package org.yesworkflow.actors.r;

import org.yesworkflow.actors.IActorScriptBuilder;
import org.yesworkflow.actors.AugmentedScriptActor;
import org.yesworkflow.actors.AugmentedScriptActor.DataSerializationFormat;

import com.google.gson.Gson;

public class RActor extends AugmentedScriptActor {

	private static String OS_SPECIFIC_R_COMMAND;
	
	static {
		if (System.getProperty("os.name").startsWith("Windows")) {
			OS_SPECIFIC_R_COMMAND = "rterm --slave"; 
		} else {
			OS_SPECIFIC_R_COMMAND = "R --slave";
		}
	}
	
	public RActor() {
		super();
		_scriptExtension = "r";
	}
	
	@Override
	public IActorScriptBuilder getNewScriptBuilder() {
		return new RActor.ScriptBuilder();
	}
	
	@Override
	public synchronized String getScriptRunCommand() {
		return OS_SPECIFIC_R_COMMAND;
	}
	
	@Override
	public DataSerializationFormat getOutputSerializationFormat() {
		return DataSerializationFormat.YAML;
	}
	
	public static class ScriptBuilder implements IActorScriptBuilder {

		private StringBuilder _script = new StringBuilder();
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
			} else if (type.equals("Json")) {
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
		
		private ScriptBuilder _assignStringLiteral(String name, Object value) {
			
			_script.append(		name	)
				   .append( 	" <- "	)
				   .append( 	"'"		)
				   .append( 	value	)
				   .append( 	"'"		)
				   .append(		EOL		);
			
			return this;
		}
		
		private ScriptBuilder _assignJsonLiteral(String name, Object value) {

			Gson gson = new Gson();
			String json = gson.toJson(value);
			
			_script.append(		name				)
				   .append( 	" <- fromJSON('"	)
				   .append( 	json				)
				   .append( 	"')"				)
				   .append(		EOL					);
			
			return this;
		}

		private ScriptBuilder _assignBooleanLiteral(String name, Object value, String type) throws Exception {
			
			Boolean b = null;
			
			if (value instanceof Boolean) {
				b = ((Boolean)value == true);
			} else if (value instanceof Number) {
				b = (((Number)value).intValue() != 0);
			} else {
				throw new Exception("Error assigning value to R " + type + " variable '" + name + "': " + value);
			}
			
			_script.append(		name					)
				   .append( 	" <- "					)
				   .append( 	b ? "TRUE" : "FALSE"	)
				   .append(		EOL						);
			
			return this;
		}

		private ScriptBuilder _assignNumberLiteral(String name, Object value, String type) throws Exception {
			if (! (value instanceof Number)) {
				throw new Exception("Error assigning value to R " + type + " variable '" + name + "': " + value);
			}
			_script.append(		name	)
				   .append( 	" <- "	)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private ScriptBuilder _assignOtherLiteral(String name, Object value) {
			_script.append(		name	)
				   .append( 	" <- "	)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private ScriptBuilder _assignNullLiteral(String name) {
			_script.append(		name		)
				   .append( 	" <- NULL"	)
				   .append(		EOL			);
			return this;
		}
		
		public ScriptBuilder appendChangeDirectory(String path) {
			_script.append(		"setwd('"		)
				   .append( 	path			)
				   .append(		"')"			)
				   .append(		EOL				);
			return this;
		}

		public ScriptBuilder appendPrintStringStatement(String string) {
			_script.append(		"cat('"		)
				   .append( 	string		)
				   .append(		"\\n')"		)
				   .append(		EOL			);
			return this;
		}

		@Override
		public IActorScriptBuilder appendSerializationBeginStatement() {
			appendCode(		"outputList <- list();" 	);
			return this;
		}

		@Override
		public IActorScriptBuilder appendSerializationEndStatement() {
			_script.append( 	"cat(toJSON(outputList));"	);
			return this;
		}
		
		public ScriptBuilder appendVariableSerializationStatement(String name, String type) {
			
			_script.append(		"outputList <- c(outputList, list(")
			   .append(		name		)
			   .append(		"="			)
			   .append(		name		)
			   .append(		"));"		)
			   .append(		EOL			);
		
			return this;
		}
		
		public IActorScriptBuilder appendNonNullStringVariableSerializationPrintStatement(String name) {
			appendVariableSerializationStatement(name, null);
			return this;
		}


		public ScriptBuilder appendInputControlFunctions() {

			appendComment("define functions for enabling and disabling actor inputs");
			appendCode( "enableInput <- function(input) { enabledInputs <<- paste(enabledInputs, input) }" );
			appendCode( "disableInput <- function(input) { disabledInputs <<- paste(disabledInputs, input) }" );
			appendBlankLine();

			appendComment("initialize input control variables");
			appendCode( "enabledInputs   <- ''" );
			appendCode( "disabledInputs  <- ''" );

			return this;
		}

		public ScriptBuilder appendOutputControlFunctions() {
			
			appendComment("define functions for enabling and disabling actor outputs");
			appendCode( "enableOutput <- function(output) { enabledOutputs <<- paste(enabledOutputs, output) }" );
			appendCode( "disableOutput <- function(output) { disabledOutputs <<- paste(disabledOutputs, output) }" );
			appendBlankLine();

			appendComment("initialize output control variables");
			appendCode( "enabledOutputs  <- ''" );
			appendCode( "disabledOutputs <- ''" );

			return this;
		}

		
		public String toString() {
			return _script.toString();
		}

		@Override
		public void appendScriptHeader(IActorScriptBuilder script, String scriptType) {
			
			appendComment(		"load required libraries"		);
			appendCode(			"library(rjson)"			);
//			appendCode(			"library(RJSONIO)"			);
			appendBlankLine();
			
//			System.out.println("Using rjson");
		}
		
		
		
		@Override
		public IActorScriptBuilder appendScriptExitCommend() {
			return this;
		}

		@Override
		public IActorScriptBuilder appendOutputVariableSerializationStatement(
				String name, String type) {
			return appendVariableSerializationStatement(name, type);
		}
	}
}
