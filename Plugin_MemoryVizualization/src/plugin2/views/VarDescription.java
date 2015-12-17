package plugin2.views;

import java.util.ArrayList;

public class VarDescription {
	private String address;
	private String type;
	private String value;
	private String name;
	private ArrayList<VarDescription> nested;
	
	public VarDescription(String address, String type, String value, String name) {
		super();
		this.address = address.replace("<", "&lt;").replace("&", "&amp;");
		this.type = type.replace("<", "&lt;").replace("&", "&amp;");
		this.value = value.replace("<", "&lt;").replace("&", "&amp;");
		this.name = name.replace("<", "&lt;").replace("&", "&amp;");
		this.nested = new ArrayList<>();
	}
	
	// getters and setters
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = filter(address);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = filter(type);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = filter(value);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = filter(name);
	}
	
	public void addNested(VarDescription descr) {
		this.nested.add(descr);
	}
	
	public VarDescription[] getNested() {
		VarDescription[] ret = new VarDescription[this.nested.size()];
		this.nested.toArray(ret);
		return ret;
	}
	
	private String filter(String val) {
		return val.replace("&", "&amp;").replace("<", "&lt;");
	}
}
