package plugin2.views;

public class ActivationRecord {
	private String functionName;
	private String fileName;
	private String startAddress;
	private String endAddress;
	private String returnValue;
	private VarDescription[] vars;
	
	public ActivationRecord(String functionName, String fileName, String startAddress, String endAddress,
			String returnValue,	VarDescription[] vars) {
		super();
		this.functionName = functionName;
		this.fileName = fileName;
		this.startAddress = startAddress;
		this.endAddress = endAddress;
		this.returnValue = returnValue;
		this.vars = vars;
	}
	
	// getters and setters
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}
	public String getEndAddress() {
		return endAddress;
	}
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public VarDescription[] getVars() {
		return vars;
	}
	public void setVars(VarDescription[] vars) {
		this.vars = vars;
	}
	
	
}
