package plugin2.views;

public class ActivationRecord {
	private String functionName;
	private String fileName;
	private String startAddress;
	private String endAddress;
	private VarDescription[] vars;
	private VarDescription[] args;
	private String returnValue;
	private String returnValueType;
	


	public ActivationRecord(String functionName, String fileName, String startAddress, String endAddress,
			VarDescription[] vars, VarDescription[] args, String returnValue, String returnValueType) {
		super();
		this.functionName = functionName;
		this.fileName = fileName;
		this.startAddress = startAddress;
		this.endAddress = endAddress;
		this.vars = vars;
		this.args = args;
		this.returnValue = returnValue;
		this.returnValueType = returnValueType;
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
	public VarDescription[] getVars() {
		return vars;
	}
	public void setVars(VarDescription[] vars) {
		this.vars = vars;
	}
	public VarDescription[] getArgs() {
		return args;
	}
	public void setArgs(VarDescription[] args) {
		this.args = args;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public String getReturnValueType() {
		return returnValueType;
	}

	public void setReturnValueType(String returnValueType) {
		this.returnValueType = returnValueType;
	}
	
	
}
