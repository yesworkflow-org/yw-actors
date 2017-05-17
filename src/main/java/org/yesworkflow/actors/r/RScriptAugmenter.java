package org.yesworkflow.actors.r;

import org.yesworkflow.actors.ScriptAugmenter;

import com.google.gson.Gson;

public class RScriptAugmenter extends ScriptAugmenter {

		public ScriptAugmenter appendCode(String code) {
			scriptBuilder.append(code).append(EOL);
			return this;
		}

		public RScriptAugmenter appendSeparator() {
			scriptBuilder.append("######################################################################################").append(EOL);
			return this;
		}

		public RScriptAugmenter appendComment(String text) {
			scriptBuilder.append("# ").append(text).append(EOL);
			return this;
		}

		public ScriptAugmenter appendLiteralAssignment(String name, Object value, String type, boolean mutable, boolean nullable) throws Exception {
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
			
			scriptBuilder.append(		name	)
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
			
			scriptBuilder.append(		name				)
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
			
			scriptBuilder.append(		name					)
				   .append( 	" <- "					)
				   .append( 	b ? "TRUE" : "FALSE"	)
				   .append(		EOL						);
			
			return this;
		}

		private RScriptAugmenter _assignNumberLiteral(String name, Object value, String type) throws Exception {
			if (! (value instanceof Number)) {
				throw new Exception("Error assigning value to R " + type + " variable '" + name + "': " + value);
			}
			scriptBuilder.append(		name	)
				   .append( 	" <- "	)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private RScriptAugmenter _assignOtherLiteral(String name, Object value) {
			scriptBuilder.append(		name	)
				   .append( 	" <- "	)
				   .append( 	value	)
				   .append(		EOL		);
			return this;
		}

		private RScriptAugmenter _assignNullLiteral(String name) {
			scriptBuilder.append(		name		)
				   .append( 	" <- NULL"	)
				   .append(		EOL			);
			return this;
		}
		
		public RScriptAugmenter appendChangeDirectory(String path) {
			scriptBuilder.append(		"setwd('"		)
				   .append( 	path			)
				   .append(		"')"			)
				   .append(		EOL				);
			return this;
		}

		public RScriptAugmenter appendPrintStringStatement(String string) {
			scriptBuilder.append(		"cat('"		)
				   .append( 	string		)
				   .append(		"\\n')"		)
				   .append(		EOL			);
			return this;
		}

		public RScriptAugmenter appendSerializationBeginStatement() {
			appendCode(		"outputList <- list();" 	);
			return this;
		}

		public RScriptAugmenter appendSerializationEndStatement() {
			scriptBuilder.append( 	"cat(toJSON(outputList));"	);
			return this;
		}
		
		public RScriptAugmenter appendVariableSerializationStatement(String name, String type) {
			
			scriptBuilder.append(		"outputList <- c(outputList, list(")
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
		
		public RScriptAugmenter appendHeader(ScriptAugmenter script, String scriptType) {
			
			appendComment(		"load required libraries"		);
			appendCode(			"library(rjson)"			);
			appendBlankLine();
			
			return this;
		}
		
		public RScriptAugmenter appendScriptExitCommand() {
			return this;
		}

		public RScriptAugmenter appendOutputVariableSerializationStatement(
				String name, String type) {
			return appendVariableSerializationStatement(name, type);
		}
	}