package org.yesworkflow.actors;

public class OutputSignatureElement extends SignatureElement {
	
	private volatile boolean _defaultOutputEnable = true;
	
	public OutputSignatureElement(String label) {
		super(label);
	}
	
	public void setDefaultOutputEnable(boolean defaultOutputEnable) {
		_defaultOutputEnable = defaultOutputEnable;
	}
	
	public boolean getDefaultOutputEnable() { 
		return _defaultOutputEnable; 
	}
}
