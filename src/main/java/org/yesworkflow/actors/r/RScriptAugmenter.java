package org.yesworkflow.actors.r;

import org.yesworkflow.actors.IActorScriptAugmenter;

import com.google.gson.Gson;

public class RScriptAugmenter implements IActorScriptAugmenter {

		private StringBuilder _script = new StringBuilder();
		private final static String EOL = System.getProperty("line.separator");

		public IActorScriptAugmenter append(String text) {
			_script.append(		text	);
			return this;
		}
		public IActorScriptAugmenter appendCode(String code) {
			
			_script.append(		code	)
				   .append(		EOL		);
			
			return this;
		}

		public RScriptAugmenter appendSeparator() {
			
			_script.append(		"######################################################################################"	)
				   .append(		EOL																							);
			
			return this;
		}

		public RScriptAugmenter appendBlankLine() {
			
			_script.append(	EOL	);
			
			return this;
		}

		
		public RScriptAugmenter appendComment(String text) {
			
			_script.append(		"# "	)
				   .append(		text	)
			   	   .append(		EOL		);
			
			return this;
		}

		@Override
		public IActorScriptAugmenter appendLiteralAssignment(String name, Object value, String type, boolean mutable, boolean nullable) throws Exception {

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
		
		private RScriptAugmenter _assignStringLiteral(String name, Object value) {
			
			_script.append(		name	)
				   .append( 	" <- "	)
				   .append( 	"'"		)
				   .append( 	value	)
				   .append( 	"'"		)
				   .append(		EOL		);
			
			return this;
		}
		
		private RScriptAugmenter _assignJsonLiteral(String name, Object value) {

			Gson gson = new Gson();
			String json = gson.toJson(value);
			
			_script.append(		name				)
				   .append( 	" <- fromJSON('"	)
				   .append( 	json				)
				   .append( 	"')"				)
				   .append(		EOL					);
			
			return this;
		}

		private RScriptAugmenter _assignBooleanLiteral(String name, Object value, String type) throws Exception {
			
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

		private RScriptAugmenter _assignNumberLiteral(String name, Object value, String type) throws Exception {
			if (! (value instanceof Number)) {
				throw new Exception("Error assigning value to R " + type + " variable '" + name + "': " + value);
			}
			_script.append(		name	)
				   .append( 	" <- "	)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private RScriptAugmenter _assignOtherLiteral(String name, Object value) {
			_script.append(		name	)
				   .append( 	" <- "	)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private RScriptAugmenter _assignNullLiteral(String name) {
			_script.append(		name		)
				   .append( 	" <- NULL"	)
				   .append(		EOL			);
			return this;
		}
		
		public RScriptAugmenter appendChangeDirectory(String path) {
			_script.append(		"setwd('"		)
				   .append( 	path			)
				   .append(		"')"			)
				   .append(		EOL				);
			return this;
		}

		public RScriptAugmenter appendPrintStringStatement(String string) {
			_script.append(		"cat('"		)
				   .append( 	string		)
				   .append(		"\\n')"		)
				   .append(		EOL			);
			return this;
		}

		@Override
		public RScriptAugmenter appendSerializationBeginStatement() {
			appendCode(		"outputList <- list();" 	);
			return this;
		}

		@Override
		public RScriptAugmenter appendSerializationEndStatement() {
			_script.append( 	"cat(toJSON(outputList));"	);
			return this;
		}
		
		public RScriptAugmenter appendVariableSerializationStatement(String name, String type) {
			
			_script.append(		"outputList <- c(outputList, list(")
			   .append(		name		)
			   .append(		"="			)
			   .append(		name		)
			   .append(		"));"		)
			   .append(		EOL			);
		
			return this;
		}
		
		public RScriptAugmenter appendNonNullStringVariableSerializationPrintStatement(String name) {
			appendVariableSerializationStatement(name, null);
			return this;
		}


		public RScriptAugmenter appendInputControlFunctions() {

			appendComment("define functions for enabling and disabling actor inputs");
			appendCode( "enableInput <- function(input) { enabledInputs <<- paste(enabledInputs, input) }" );
			appendCode( "disableInput <- function(input) { disabledInputs <<- paste(disabledInputs, input) }" );
			appendBlankLine();

			appendComment("initialize input control variables");
			appendCode( "enabledInputs   <- ''" );
			appendCode( "disabledInputs  <- ''" );

			return this;
		}

		public RScriptAugmenter appendOutputControlFunctions() {
			
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
		public RScriptAugmenter appendScriptHeader(IActorScriptAugmenter script, String scriptType) {
			
			appendComment(		"load required libraries"		);
			appendCode(			"library(rjson)"			);
			appendBlankLine();
			
			return this;
		}
		
		@Override
		public RScriptAugmenter appendScriptExitCommand() {
			return this;
		}

		@Override
		public RScriptAugmenter appendOutputVariableSerializationStatement(
				String name, String type) {
			return appendVariableSerializationStatement(name, type);
		}
	}