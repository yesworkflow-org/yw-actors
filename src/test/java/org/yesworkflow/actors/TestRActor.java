package org.yesworkflow.actors;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.yesworkflow.actors.r.RActorBuilder;

import junit.framework.TestCase;

public class TestRActor extends TestCase {

	protected final static String EOL = System.getProperty("line.separator");

    protected volatile OutputStream stdoutBuffer;
    protected volatile OutputStream stderrBuffer;
    
    protected volatile PrintStream stdoutStream;
    protected volatile PrintStream stderrStream;

    @Override
    public void setUp() throws Exception {
        
        super.setUp();
        
        stdoutBuffer = new ByteArrayOutputStream();
        stdoutStream = new PrintStream(stdoutBuffer);
    
        stderrBuffer = new ByteArrayOutputStream();
        stderrStream = new PrintStream(stderrBuffer);
        
        System.setProperty("yw.actors.r.command", "/usr/local/bin/R");
    }
    
    public void testGetAugmentedStepScript_NoInputsOutputsOrState() throws Exception {

		final Actor actor = new RActorBuilder()
                                 .step("cat('Hello world!')")
							     .outputStream(stdoutStream)
							     .errorStream(stderrStream)
								 .name("Hello")
								 .build();

        actor.initialize();
        actor.start();

        assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			""																							+ EOL +
			"# load required libraries"																	+ EOL +
			"library(rjson)"																			+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			"" 																							+ EOL +
			"cat('Hello world!')" 																		+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"cat('__END_OF_SCRIPT_OUTPUT__\\n')" 														+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs"															+ EOL +
			"outputList <- list();"																		+ EOL +
			"cat(toJSON(outputList));"																	+ EOL
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		actor.step();
			
		assertEquals("", stderrBuffer.toString());
		assertEquals("Hello world!", stdoutBuffer.toString());
	}
		
	public void testGetAugmentedStepScript_WithInputs_NoOutputsOrState() throws Exception {

		final Actor actor = new RActorBuilder()
                            .step("cat(greeting, 'world!')")
    	     				.outputStream(stdoutStream)
         				 	.errorStream(stderrStream)
    						.name("Hello")
    						.input("greeting")
    						.build();

        actor.initialize();
        actor.start();

        actor.setInputValue("greeting", "Goodbye");
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# load required libraries"																	+ EOL +
			"library(rjson)"																			+ EOL +
			""																							+ EOL +
			"# define functions for enabling and disabling actor inputs" 								+ EOL +
			"enableInput <- function(input) { enabledInputs <<- paste(enabledInputs, input) }" 			+ EOL +
			"disableInput <- function(input) { disabledInputs <<- paste(disabledInputs, input) }" 		+ EOL +
			"" 																							+ EOL +
			"# initialize input control variables"		 												+ EOL +
			"enabledInputs   <- ''" 																	+ EOL +
			"disabledInputs  <- ''" 																	+ EOL +
			"" 																							+ EOL +
			"# initialize actor input variables"														+ EOL +
			"greeting <- 'Goodbye'"																		+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT"															+ EOL +
			""																							+ EOL +
			"cat(greeting, 'world!')"																	+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"cat('__END_OF_SCRIPT_OUTPUT__\\n')"														+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs"															+ EOL +
			"outputList <- list();"																		+ EOL +
			"outputList <- c(outputList, list(enabledInputs=enabledInputs));"							+ EOL +
			"outputList <- c(outputList, list(disabledInputs=disabledInputs));"							+ EOL +
			""																							+ EOL +
			"cat(toJSON(outputList));"																	+ EOL
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		actor.step();

		assertEquals("", stderrBuffer.toString());
		assertEquals("Goodbye world!", stdoutBuffer.toString());
	}

	public void testGetAugmentedStepScript_WithOutputs_NoInputsOrState() throws Exception {

	    final Actor actor = new RActorBuilder()
                            .step("greeting<-'Nice to meet you.'")
                            .outputStream(stdoutStream)
                            .errorStream(stderrStream)
                            .name("Hello")
                            .output("greeting")
                            .build();

        actor.initialize();
        actor.start();

        assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# load required libraries"																	+ EOL +
			"library(rjson)"																			+ EOL +
			""																							+ EOL +
			"# define functions for enabling and disabling actor outputs" 								+ EOL +
			"enableOutput <- function(output) { enabledOutputs <<- paste(enabledOutputs, output) }"		+ EOL +
			"disableOutput <- function(output) { disabledOutputs <<- paste(disabledOutputs, output) }" 	+ EOL +
			"" 																							+ EOL +
			"# initialize output control variables" 													+ EOL +
			"enabledOutputs  <- ''" 																	+ EOL +
			"disabledOutputs <- ''" 																	+ EOL +
			"" 																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			"" 																							+ EOL +
			"greeting<-'Nice to meet you.'" 															+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"cat('__END_OF_SCRIPT_OUTPUT__\\n')" 														+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"outputList <- list();"																		+ EOL +
			"outputList <- c(outputList, list(greeting=greeting));"										+ EOL +
			""																							+ EOL +
			"outputList <- c(outputList, list(enabledOutputs=enabledOutputs));"							+ EOL +
			"outputList <- c(outputList, list(disabledOutputs=disabledOutputs));"						+ EOL +
			""																							+ EOL +
			"cat(toJSON(outputList));"																	+ EOL 
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		actor.step();
			
		assertEquals("", stderrBuffer.toString());
		assertEquals("", stdoutBuffer.toString());
		assertEquals("Nice to meet you.", actor.getOutputValue("greeting"));
	}

	public void testGetAugmentedStepScript_WithState_NoInputsOrOutput() throws Exception {

		final Actor actor = new RActorBuilder()
                            .step("greeting <- 'Nice to meet you.'")
			     			.outputStream(stdoutStream)
		     				.errorStream(stderrStream)
							.name("Hello")
							.state("greeting")
							.build();

        actor.initialize();
        actor.start();
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# load required libraries"																	+ EOL +
			"library(rjson)"																			+ EOL +
			""																							+ EOL +
			"# initialize actor state variables" 														+ EOL +
			"greeting <- NULL" 																			+ EOL +
			""							 																+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			""							 																+ EOL +
			"greeting <- 'Nice to meet you.'" 															+ EOL +
			""							 																+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			""							 																+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"cat('__END_OF_SCRIPT_OUTPUT__\\n')" 														+ EOL +
			""							 																+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"outputList <- list();"																		+ EOL +
			"outputList <- c(outputList, list(greeting=greeting));"										+ EOL +
			""																							+ EOL +
			"cat(toJSON(outputList));"																	+ EOL 
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		actor.step();
			
		assertEquals("", stderrBuffer.toString());
		assertEquals("", stdoutBuffer.toString());
		assertEquals("Nice to meet you.", actor.getStateValue("greeting"));
	}
	
	public void testGetAugmentedStepScript_WithInputsAndOutput_NoState() throws Exception {

		final Actor actor = new RActorBuilder()
                            .step("z <- x * y")
  						    .outputStream(stdoutStream)
						    .errorStream(stderrStream)
						    .name("Multiplier")
						    .input("x")
						    .input("y")
						    .output("z")
						    .type("x", "Integer")
						    .type("y", "Integer")
						    .type("z", "Integer")
						    .build();

        actor.initialize();
        actor.start();
		
		actor.setInputValue("x", 3);
		actor.setInputValue("y", 12);
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Multiplier"												+ EOL +
			""																							+ EOL +
			"# load required libraries"																	+ EOL +
			"library(rjson)"																			+ EOL +
			""																							+ EOL +
			"# define functions for enabling and disabling actor inputs" 								+ EOL +
			"enableInput <- function(input) { enabledInputs <<- paste(enabledInputs, input) }" 			+ EOL +
			"disableInput <- function(input) { disabledInputs <<- paste(disabledInputs, input) }" 		+ EOL +
			""																							+ EOL +
			"# initialize input control variables"														+ EOL +
			"enabledInputs   <- ''"																		+ EOL +
			"disabledInputs  <- ''"																		+ EOL +
			""																							+ EOL +
			"# define functions for enabling and disabling actor outputs" 								+ EOL +
			"enableOutput <- function(output) { enabledOutputs <<- paste(enabledOutputs, output) }"		+ EOL +
			"disableOutput <- function(output) { disabledOutputs <<- paste(disabledOutputs, output) }" 	+ EOL +
			""																							+ EOL +
			"# initialize output control variables"														+ EOL +
			"enabledOutputs  <- ''"																		+ EOL +
			"disabledOutputs <- ''"																		+ EOL +
			""																							+ EOL +
			"# initialize actor input variables"														+ EOL +
			"x <- 3"																					+ EOL +
			"y <- 12"																					+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT"															+ EOL +
			""																							+ EOL +
			"z <- x * y"																				+ EOL +
			""																							+ EOL +
			"# END OF ORIGINAL SCRIPT"																	+ EOL +
			""																							+ EOL +
			"# signal end of output from original script"												+ EOL +
			"cat('__END_OF_SCRIPT_OUTPUT__\\n')"														+ EOL +
			""																							+ EOL +
			"# Serialization of actor outputs"															+ EOL +
			"outputList <- list();"																		+ EOL +
			"outputList <- c(outputList, list(z=z));"													+ EOL +
			""																							+ EOL +
			"outputList <- c(outputList, list(enabledInputs=enabledInputs));"							+ EOL +
			"outputList <- c(outputList, list(disabledInputs=disabledInputs));"							+ EOL +
			""																							+ EOL +
			"outputList <- c(outputList, list(enabledOutputs=enabledOutputs));"							+ EOL +
			"outputList <- c(outputList, list(disabledOutputs=disabledOutputs));"						+ EOL +
			""																							+ EOL +
			"cat(toJSON(outputList));"																	+ EOL 
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		actor.step();
			
		assertEquals("", stderrBuffer.toString());
		assertEquals("", stdoutBuffer.toString());
		assertEquals(36, actor.getOutputValue("z"));
	}
}
