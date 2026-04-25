package io;

public class Attribute{
	public String attribute_name = "";
	public String attribute_type = "float";
	public boolean active = true;	
	
	public Attribute(String attribute_name){
		this.attribute_name = attribute_name;
	}
	
	public Attribute(String attribute_name, String attribute_type){
		this(attribute_name);
		this.attribute_type = attribute_type;
	}	
	
	public Attribute(String attribute_name, String attribute_type, boolean active){
		this(attribute_name, attribute_type);
		this.active = active;
	}		
	
	public boolean isActive(){return active;}
	
}