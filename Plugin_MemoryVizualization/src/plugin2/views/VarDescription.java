package plugin2.views;

public class VarDescription {
	private String address;
	private String type;
	private String value;
	private String name;
	
	public VarDescription(String address, String type, String value, String name) {
		super();
		this.address = address;
		this.type = type;
		this.value = value;
		this.name = name;
	}
	
	// getters and setters
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
