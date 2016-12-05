package org.yesworkflow.actors;

import org.yesworkflow.actors.python.PythonActor;
import org.yesworkflow.actors.python.PythonActorBuilder;
import org.yesworkflow.actors.util.StdoutRecorder;

import junit.framework.TestCase;

public class TestPythonActor extends TestCase {

	protected final static String EOL = System.getProperty("line.separator");
	
	public void testGetAugmentedStepScript_NoInputsOutputsOrState() throws Exception {

		final PythonActor actor = (PythonActor) new PythonActorBuilder()
			.name("Hello")
			.step("print('Hello world!')")
			.build();

		actor.configure();
		actor.initialize();
				
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages required by all python actors" 											+ EOL +
			"import os, json" 																			+ EOL +
			"from io import StringIO" 																	+ EOL +
			"" 																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			"" 																							+ EOL +
			"print('Hello world!')"																		+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print('__END_OF_SCRIPT_OUTPUT__')" 														+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs"															+ EOL +
			"_outputMap = dict()"																		+ EOL +
			"if (len(_outputMap) > 0) :   print(json.dumps(_outputMap))"								+ EOL +
			"" 																							+ EOL 
			, actor.getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals(
			"Hello world!" 					+ EOL ,
			recorder.getStdoutRecording());
	}
		
	public void testGetAugmentedStepScript_WithInputs_NoOutputsOrState() throws Exception {

		final IActor actor = new PythonActorBuilder()
								.name("Hello")
								.input("greeting")
								.step("print(greeting + ' world!')")
								.build();

		actor.configure();
		actor.initialize();
		
		actor.setInputValue("greeting", "Goodbye");
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages required by all python actors" 											+ EOL +
			"import os, json" 																			+ EOL +
			"from io import StringIO" 																	+ EOL +
			"" 																							+ EOL +
			"# define functions for enabling and disabling actor inputs" 								+ EOL +
			"def enableInput(input)      :   global enabledInputs;    enabledInputs   += ' ' + input" 	+ EOL +
			"def disableInput(input)     :   global disabledInputs;   disabledInputs  += ' ' + input" 	+ EOL +
			"" 																							+ EOL +
			"# initialize input control variables"		 												+ EOL +
			"enabledInputs   = ''" 																		+ EOL +
			"disabledInputs  = ''" 																		+ EOL +
			"" 																							+ EOL +
			"# initialize actor input variables"														+ EOL +
			"greeting='Goodbye'"																		+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT"															+ EOL +
			""																							+ EOL +
			"print(greeting + ' world!')"																+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print('__END_OF_SCRIPT_OUTPUT__')" 														+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"_outputMap = dict()" 																		+ EOL +
			"_outputMap['enabledInputs'] = enabledInputs" 												+ EOL +
			"_outputMap['disabledInputs'] = disabledInputs" 											+ EOL +
			""																							+ EOL +
			"if (len(_outputMap) > 0) :   print(json.dumps(_outputMap))" 								+ EOL +
			"" 																							+ EOL 
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals(
			"Goodbye world!" 					+ EOL ,
			recorder.getStdoutRecording());
	}

	public void testGetAugmentedStepScript_WithOutputs_NoInputsOrState() throws Exception {

		final IActor actor = new PythonActorBuilder()
								.name("Hello")
								.step("greeting='Nice to meet you.'")
								.output("greeting")
								.build();

		actor.configure();
		actor.initialize();
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages required by all python actors" 											+ EOL +
			"import os, json" 																			+ EOL +
			"from io import StringIO" 																	+ EOL +
			"" 																							+ EOL +
			"# define functions for enabling and disabling actor outputs" 								+ EOL +
			"def enableOutput(output)    :   global enabledOutputs;   enabledOutputs  += ' ' + output" 	+ EOL +
			"def disableOutput(output)   :   global disabledOutputs;  disabledOutputs += ' ' + output" 	+ EOL +
			"" 																							+ EOL +
			"# initialize output control variables" 													+ EOL +
			"enabledOutputs  = ''" 																		+ EOL +
			"disabledOutputs = ''" 																		+ EOL +
			"" 																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			"" 																							+ EOL +
			"greeting='Nice to meet you.'" 																+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print('__END_OF_SCRIPT_OUTPUT__')"															+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"_outputMap = dict()" 																		+ EOL +
			"_outputMap['greeting'] = greeting" 														+ EOL +
			""							 																+ EOL +
			"_outputMap['enabledOutputs'] = enabledOutputs"												+ EOL +
			"_outputMap['disabledOutputs'] = disabledOutputs"											+ EOL +
			""																							+ EOL +
			"if (len(_outputMap) > 0) :   print(json.dumps(_outputMap))"								+ EOL +
			"" 																							+ EOL 
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals("", recorder.getStdoutRecording());
		
		assertEquals("Nice to meet you.", actor.getOutputValue("greeting"));
	}

	public void testGetAugmentedStepScript_WithState_NoInputsOrOutput() throws Exception {

		final IActor actor = new PythonActorBuilder()
								.name("Hello")
								.state("greeting")
								.step("greeting='Nice to meet you.'")
								.build();

		actor.configure();
		actor.initialize();
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages required by all python actors" 											+ EOL +
			"import os, json" 																			+ EOL +
			"from io import StringIO" 																	+ EOL +
			"" 																							+ EOL +
			"# initialize actor state variables" 														+ EOL +
			"greeting=None" 																			+ EOL +
			""							 																+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			""							 																+ EOL +
			"greeting='Nice to meet you.'" 																+ EOL +
			""							 																+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			""							 																+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print('__END_OF_SCRIPT_OUTPUT__')" 														+ EOL +
			""							 																+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"_outputMap = dict()" 																		+ EOL +
			"_outputMap['greeting'] = greeting" 														+ EOL +
			""							 																+ EOL +
			"if (len(_outputMap) > 0) :   print(json.dumps(_outputMap))"								+ EOL +
			"" 																							+ EOL 
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals("", recorder.getStdoutRecording());
		
		assertEquals("Nice to meet you.", actor.getStateValue("greeting"));
	}
	
	public void testGetAugmentedStepScript_WithInputsAndOutput_NoState() throws Exception {

		final IActor actor = new PythonActorBuilder()
								.name("Multiplier")
								.input("x")
								.input("y")
								.step("z = x * y")
								.output("z")
								.type("x", "Integer")
								.type("y", "Integer")
								.type("z", "Integer")
								.build();

		actor.configure();
		actor.initialize();
		
		actor.setInputValue("x", 3);
		actor.setInputValue("y", 12);
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Multiplier"												+ EOL +
			""																							+ EOL +
			"# import packages required by all python actors" 											+ EOL +			
			"import os, json" 																			+ EOL +
			"from io import StringIO" 																	+ EOL +
			"" 																							+ EOL +
			"# define functions for enabling and disabling actor inputs"								+ EOL +
			"def enableInput(input)      :   global enabledInputs;    enabledInputs   += ' ' + input"	+ EOL +
			"def disableInput(input)     :   global disabledInputs;   disabledInputs  += ' ' + input"	+ EOL +
			""																							+ EOL +
			"# initialize input control variables"														+ EOL +
			"enabledInputs   = ''"																		+ EOL +
			"disabledInputs  = ''"																		+ EOL +
			""																							+ EOL +
			"# define functions for enabling and disabling actor outputs"								+ EOL +
			"def enableOutput(output)    :   global enabledOutputs;   enabledOutputs  += ' ' + output"	+ EOL +
			"def disableOutput(output)   :   global disabledOutputs;  disabledOutputs += ' ' + output"	+ EOL +
			""																							+ EOL +
			"# initialize output control variables"														+ EOL +
			"enabledOutputs  = ''"																		+ EOL +
			"disabledOutputs = ''"																		+ EOL +
			""																							+ EOL +
			"# initialize actor input variables"														+ EOL +
			"x=3"																						+ EOL +
			"y=12"																						+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT"															+ EOL +
			""																							+ EOL +
			"z = x * y"																					+ EOL +
			""																							+ EOL +
			"# END OF ORIGINAL SCRIPT"																	+ EOL +
			""																							+ EOL +
			"# signal end of output from original script"												+ EOL +
			"print('__END_OF_SCRIPT_OUTPUT__')"															+ EOL +
			""																							+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"_outputMap = dict()"																		+ EOL +
			"_outputMap['z'] = z"		 																+ EOL +
			""																							+ EOL +
			"_outputMap['enabledInputs'] = enabledInputs"												+ EOL +
			"_outputMap['disabledInputs'] = disabledInputs"												+ EOL +
			""							 																+ EOL +
			"_outputMap['enabledOutputs'] = enabledOutputs"												+ EOL +
			"_outputMap['disabledOutputs'] = disabledOutputs"											+ EOL +
			""							 																+ EOL +
			"if (len(_outputMap) > 0) :   print(json.dumps(_outputMap))"								+ EOL +
			"" 																							+ EOL
			, ((AugmentedScriptActor)actor).getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals("", recorder.getStdoutRecording());
		
		assertEquals(36, actor.getOutputValue("z"));
	}
}
