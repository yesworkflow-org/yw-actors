package org.yesworkflow.actors;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yesworkflow.actors.groovy.GroovyActor;

import junit.framework.TestCase;

public class TestGroovyActor extends TestCase {

	private GroovyActor actor;
	private Yaml yaml;
	
	private String EOL = System.getProperty("line.separator");
	
	public void setUp() throws Exception {
		actor = new GroovyActor();
		actor.setName("TestActor");
		yaml = new Yaml();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void _setActorInputsViaYaml(String yamlString) throws Exception {
		actor.setInputs((Map)yaml.load(yamlString));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void _setActorOutputsViaYaml(String yamlString) throws Exception {
		actor.setOutputs((Map)yaml.load(yamlString));
	}


	public void testSetInputs_nameAndDefault() throws Exception {

		_setActorInputsViaYaml(
			"a:" 								+ EOL +
	    	"  default: the default value"		+ EOL
		);

		_setActorOutputsViaYaml(
	    	"out:"								+ EOL
		);

		actor.setStep(
			"out = a;"							+ EOL
		);

		actor.initialize();
		actor.start();
		actor.step();
		
		assertEquals(actor.getOutputValue("out"),"the default value");
	}

	public void testSetInputs_testSetInputValue() throws Exception {

		_setActorInputsViaYaml(
			"a:" 				 	+ EOL
		);
		
		_setActorOutputsViaYaml(
			"out:"					+ EOL
		);

		actor.setStep(
			"out = a;"				+ EOL
		);

        actor.initialize();
        actor.start();
		actor.setInputValue("a", 3);
		actor.step();
		
		assertEquals(3, actor.getOutputValue("out") );
	}
	
	public void testStep_MissingOutput_NoException() throws Exception {

		_setActorOutputsViaYaml(
			"a:" 					+ EOL + 
			"b:" 					+ EOL +
			"c:"
		);

		actor.setStep(
			"a = 1;" 				+ EOL +
			"c = 3"
		);

        actor.initialize();
        actor.start();
		actor.step();
	}
	
	public void testStep_NullOutput() throws Exception {

		_setActorOutputsViaYaml(
			"a:" 					+ EOL +
			"b:" 					+ EOL +
			"  nullable: true" 		+ EOL +
			"c:"
		);

		actor.setStep(
			"a = 1;" 				+ EOL +
			"b = null;"				+ EOL +
			"c = 3"
		);
				
        actor.initialize();
        actor.start();
		actor.step();
		
		assertEquals(1, actor.getOutputValue("a"));
		assertNull(actor.getOutputValue("b"));
		assertEquals(3, actor.getOutputValue("c"));
	}

	public void testStep_MissingOutputFile() throws Exception {

		_setActorOutputsViaYaml(
			"a:" 									+ EOL +
			"b:" 									+ EOL +
			"  nullable: true" 						+ EOL +
			"c:" 									+ EOL
		);
		
		actor.setStep(
			"a = 1;" 								+ EOL +
			"b = new File(\"nonexistentfile\");" 	+ EOL +
			"c = 3;" 								+ EOL
		);

        actor.initialize();
        actor.start();
		actor.step();
		
		assertEquals(1, actor.getOutputValue("a"));
		assertNull(actor.getOutputValue("b"));
		assertEquals(3, actor.getOutputValue("c"));
	}
}
