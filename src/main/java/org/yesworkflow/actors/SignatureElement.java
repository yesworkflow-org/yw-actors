package org.yesworkflow.actors;

public abstract class SignatureElement {

	private final String label;
	private volatile String  description = "";
	private volatile boolean isList = false;
	private volatile boolean isNullable = false;
	private volatile boolean isOptional = false;
	private volatile int     count = 1;	
	private volatile String  type = "";

	public SignatureElement(String label)          { this.label = label; }
	
	public void setDescription(String description) { this.description = description; }
	public void setIsList()                        { this.isList = true; }
	public void setIsNullable()                    { this.isNullable = true; }
	public void setIsOptional()                    { this.isOptional = true; }
	public void setCount(int count)                { this.count = count; }
	public void setType(String typeName)           { this.type = typeName; }

	public boolean isList()        { return isList;}
	public boolean isNullable()    { return isNullable;}
	public boolean isOptional()    { return isOptional;}
	public String getDescription() { return description; }
	public int getCount()          { return count;}
	public String getLabel()       { return label;}
	public String getType()        { return type;}
}
