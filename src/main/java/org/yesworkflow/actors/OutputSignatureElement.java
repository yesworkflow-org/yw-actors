package org.yesworkflow.actors;

public class OutputSignatureElement extends SignatureElement {
	
	private volatile boolean defaultOutEnable = true;
	
	public OutputSignatureElement(String label) { super(label); }
	
	public void setDefaultOutEnable(boolean defaultOutputEnable) { this.defaultOutEnable = defaultOutputEnable; }
	
	public boolean getDefaultOutEnable() { return defaultOutEnable; }
}
