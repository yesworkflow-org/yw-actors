package org.yesworkflow.actors;

/**
 * This class is thread safe. The types of all field values are immutable,
 * each field is marked volatile, no logic is performed jointly
 * on more than one field, and the superclass is thread safe.
 */
public class InputSignatureElement extends SignatureElement {

	private volatile Object  defaultValue = null;
	private volatile boolean defaultInEnable = true;
	private volatile String  localPath = "";

	public InputSignatureElement(String label) { super(label); }

	public void setDefaultInEnable(boolean defaultInEnable) { this.defaultInEnable = defaultInEnable; }
    public void setDefaultValue(Object value)               { this.defaultValue = value; }
	public void setLocalPath(String localPath)              { this.localPath = localPath; }

	public boolean getDefaultInEnable() { return defaultInEnable; }
    public Object getDefaultValue()     { return defaultValue; }
	public String getLocalPath()        { return localPath; }
}
