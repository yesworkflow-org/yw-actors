package org.yesworkflow.actors;

public abstract class SignatureElement {

	private final String _label;
	
	private volatile String _description = "";
	private volatile boolean _isList = false;
	private volatile boolean _isNullable = false;
	private volatile boolean _isOptional = false;
	private volatile int _count = 1;	
	private volatile String _type = "";

	public SignatureElement(String label) {
		_label = label;
	}

	public void setDescription(String description) {
		_description = description;
	}
	
	public void setIsList() {
		_isList = true;
	}
	
	public void setIsNullable() {
		_isNullable = true;
	}

	public void setIsOptional() {
		_isOptional = true;
	}

	public void setCount(int count) {
		_count = count;
	}
	
	public void setType(String typeName) {
		_type = typeName;
	}

	public boolean isList() { return _isList;}
	public boolean isNullable() { return _isNullable;}
	public boolean isOptional() { return _isOptional;}
	public String getDescription() { return _description; }
	public int getCount() { return _count;}
	public String getLabel() { return _label;}
	public String getType() { return _type;}
}
