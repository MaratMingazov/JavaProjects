package plugin2.views;

import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.event.ICDIEvent;
import org.eclipse.cdt.debug.core.cdi.event.ICDIEventListener;
import org.eclipse.cdt.debug.core.cdi.model.ICDIArgument;
import org.eclipse.cdt.debug.core.cdi.model.ICDIArgumentDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIInstruction;
import org.eclipse.cdt.debug.core.cdi.model.ICDILocalVariable;
import org.eclipse.cdt.debug.core.cdi.model.ICDILocalVariableDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIObject;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegister;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegisterDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegisterGroup;
import org.eclipse.cdt.debug.core.cdi.model.ICDIStackFrame;
import org.eclipse.cdt.debug.core.cdi.model.ICDITarget;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;
import org.eclipse.cdt.debug.core.cdi.model.ICDIValue;
import org.eclipse.cdt.debug.core.cdi.model.ICDIVariable;
import org.eclipse.cdt.debug.core.cdi.model.ICDIVariableDescriptor;
import org.eclipse.cdt.debug.mi.core.cdi.model.Variable;

public class CDIEventListener implements ICDIEventListener{

	private ICDIThread currentThread = null; 
	private boolean itIsUpdatedThread = false;
	
	public void handleDebugEvents(ICDIEvent[] event) {
		//System.out.println("");
		for (ICDIEvent ev : event){
			ICDIObject source = ev.getSource();	
			if (source == null){
				setCurrentThread(null);
				setItIsUpdatedThread(false);
				return;
			}
			ICDITarget target = source.getTarget();
			if (target.isTerminated()){
				setCurrentThread(null);
				setItIsUpdatedThread(false);
				return;	
			}
			try {
				ICDIThread thread = target.getCurrentThread();
				setCurrentThread(thread);
				setItIsUpdatedThread(true);
				
			} catch (CDIException e) {}
		}	
	}
	
	private void setCurrentThread(ICDIThread thread) {
		currentThread = thread;
	}
	
	private void setItIsUpdatedThread(boolean value){
		itIsUpdatedThread =value;
	}
	
	public ICDIThread getCurrentThread() {
		setItIsUpdatedThread(false);
		return currentThread;		
	}	
	
	public boolean isItUpdatedThread(){
		return itIsUpdatedThread;
	}
	
	public ActivationRecord[] getActivationRecords() {
		
		ICDIStackFrame[] frames = CDIEventListener.getStackFrames(getCurrentThread());
		ActivationRecord[] records = new ActivationRecord[frames.length];

		for (int i = 0; i < frames.length; i++) {
			
			ICDILocalVariableDescriptor[] variabledescriptors = CDIEventListener.GetStackFrameLocalVariableDescriptors(frames[i]);
			VarDescription[] vars = new VarDescription[variabledescriptors.length];//extra space for return value
			for (int k = 0; k < variabledescriptors.length; k++) {
				ICDILocalVariable icdilovalvariable =  CDIEventListener.getLocalVariable(variabledescriptors[k]);
				String hexaddress = CDIEventListener.getHexAddress((Variable)icdilovalvariable);
				String typename = CDIEventListener.getLocalVariableTypeName(icdilovalvariable);
				ICDIValue icdvalue = CDIEventListener.getLocalVariableValue(icdilovalvariable);
				String valuestring = CDIEventListener.getValueString(icdvalue);
				String qualifiedname = CDIEventListener.getQualifiedName(icdilovalvariable);
				
				vars[k] = new VarDescription(hexaddress, typename, valuestring, qualifiedname);
			}
			
			ICDIArgumentDescriptor[] argumentdescriptors = CDIEventListener.getStackFrameArgumentDescriptors(frames[i]);
			VarDescription[] args = new VarDescription[argumentdescriptors.length];//extra space for return value
			for (int k = 0; k < argumentdescriptors.length; k++) {
				ICDILocalVariable icdilovalvariable =  CDIEventListener.getArgument(argumentdescriptors[k]);
				String hexaddress = CDIEventListener.getHexAddress((Variable)icdilovalvariable);
				String typename = CDIEventListener.getLocalVariableTypeName(icdilovalvariable);
				ICDIValue icdvalue = CDIEventListener.getLocalVariableValue(icdilovalvariable);
				String valuestring = CDIEventListener.getValueString(icdvalue);
				String qualifiedname = CDIEventListener.getQualifiedName(icdilovalvariable);
				
				args[k] = new VarDescription(hexaddress, typename, valuestring, qualifiedname);
			}			
			


			String functionname = frames[i].getLocator().getFunction();
			String filename = frames[i].getLocator().getFile();
			ICDIValue registerBasePointer = CDIEventListener.findRegisterValueByQualifiedName(frames[i], "$rbp");
			String startaddress = CDIEventListener.getValueString(registerBasePointer);
			if (startaddress.length() == 0) {
				registerBasePointer = CDIEventListener.findRegisterValueByQualifiedName(frames[i], "$ebp");
				startaddress = CDIEventListener.getValueString(registerBasePointer);
			}
			
			ICDIValue registerStackPointer = CDIEventListener.findRegisterValueByQualifiedName(frames[i], "$rsp");
			String endaddress = CDIEventListener.getValueString(registerStackPointer);
			if (endaddress.length() == 0) {
				registerStackPointer = CDIEventListener.findRegisterValueByQualifiedName(frames[i], "$esp");
				endaddress = CDIEventListener.getValueString(registerStackPointer);
			}

			
			records[i] = new ActivationRecord(functionname,filename,startaddress,endaddress,vars, args);
		}
		return records;
	}
	
	public static ICDIStackFrame[] getStackFrames(ICDIThread thread){
		if (thread == null){return null;}
		ICDIStackFrame[] Frames = new ICDIStackFrame[0];
		try {
			Frames = thread.getStackFrames();
		} catch (CDIException e) {e.printStackTrace();}
		return Frames;
	}
	
	public static ICDIStackFrame getTopStackFrame(ICDIThread thread){
		if (thread == null){return null;}
		ICDIStackFrame Frame = null;
		try {
			Frame = thread.getStackFrames()[0];
		} catch (CDIException e) {}		
		return Frame;
	}	
	
	public static ICDILocalVariableDescriptor[] GetStackFrameLocalVariableDescriptors(ICDIStackFrame frame){
		ICDILocalVariableDescriptor[] descriptor = new ICDILocalVariableDescriptor[0];
		try {
			descriptor = frame.getLocalVariableDescriptors();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return descriptor;
	}
	
	public static ICDIArgumentDescriptor[] getStackFrameArgumentDescriptors(ICDIStackFrame frame){
		ICDIArgumentDescriptor[] descriptor = new ICDIArgumentDescriptor[0];
		try {
			descriptor = frame.getArgumentDescriptors();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return descriptor;
	}
	
	public static ICDIValue getLocalVariableValue(ICDIVariable variable){
		ICDIValue value = null;
		try {
			value = variable.getValue();
		} catch (CDIException e) {
			e.printStackTrace();
		}	
		return value;
	}
	
	public static String getLocalVariableTypeName(ICDIVariable variable){
		String typeName = null;
		try {
			typeName = variable.getTypeName();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return typeName;
	}
	
	public static String getQualifiedName(ICDIVariableDescriptor variable){
		String QualifiedName = null;
		try {
			QualifiedName = variable.getQualifiedName();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return QualifiedName;
	}
	
	public static String getValueString(ICDIValue value){
		String valuestring = "";
		if (value == null){return valuestring;}
		try {
			valuestring = value.getValueString();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return valuestring;
	}
	
	public static ICDIVariable[] getLocalVariablesFromValue(ICDIValue value){
		ICDIVariable[] variables = null;
		try {
			variables = value.getVariables();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return variables;
	}
	
	public static ICDILocalVariable getLocalVariable(ICDILocalVariableDescriptor descriptor){
		ICDILocalVariable variable = null;
		try {
			 variable = descriptor.getStackFrame().createLocalVariable(descriptor);
		} catch (CDIException e) {
			e.printStackTrace();
		}		
		return variable;
	}
	
	public static ICDIArgument getArgument(ICDIArgumentDescriptor descriptor){
		ICDIArgument argument = null;
		try {
			argument = descriptor.getStackFrame().createArgument(descriptor);
		} catch (CDIException e) {
			e.printStackTrace();
		}		
		return argument;
	}
	
	public static String getHexAddress (Variable variable){
		String hexAddress = "";
		try {
			hexAddress = variable.getHexAddress();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return hexAddress;
	}
	
	public static ICDIRegisterGroup[]  getICDIRegisterGroups (ICDIStackFrame frame){
		ICDIRegisterGroup[] registerGroup = new ICDIRegisterGroup[0];
		try {
			registerGroup = frame.getThread().getTarget().getRegisterGroups();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return registerGroup;
	}
	
	public static ICDIRegisterDescriptor[] getICDIRegisterDescriptors(ICDIRegisterGroup registerGroup){
		ICDIRegisterDescriptor[] regDescriptors = new ICDIRegisterDescriptor[0];
		try {
			regDescriptors = registerGroup.getRegisterDescriptors();
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return regDescriptors;
	}
	
	public static ICDIRegister createICDIRegister(ICDIStackFrame frame, ICDIRegisterDescriptor regDescriptor){
		ICDIRegister register = null;
		try {
			register = frame.getTarget().createRegister(regDescriptor);
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return register;
	}
	
	public static ICDIValue getRegisterValue(ICDIStackFrame frame, ICDIRegister register){
		ICDIValue value = null;
		try {
			value = register.getValue(frame);
		} catch (CDIException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static ICDIValue findRegisterValueByQualifiedName(ICDIStackFrame frame, String qualifedName){
		ICDIValue value = null;
		ICDIRegisterGroup[] registerGroups = CDIEventListener.getICDIRegisterGroups(frame);
		for (ICDIRegisterGroup registerGroup : registerGroups){
			ICDIRegisterDescriptor[] regDescriptors = CDIEventListener.getICDIRegisterDescriptors(registerGroup);
			for (ICDIRegisterDescriptor regDescriptor : regDescriptors){
				ICDIRegister cdiRegister = CDIEventListener.createICDIRegister(frame, regDescriptor);
				String qName = CDIEventListener.getQualifiedName(cdiRegister);
				if (qName.equals(qualifedName)){value = CDIEventListener.getRegisterValue(frame, cdiRegister);}
			}
		}		
		return value;
	}
	
	public static ICDIInstruction[] getInstructions(ICDIStackFrame frame){
		ICDIInstruction[] instructions = new ICDIInstruction[0];
		try {
			instructions = frame.getTarget().getInstructions(
					frame.getLocator().getFile(), frame.getLocator().getLineNumber());
		}
		catch (CDIException e) {e.printStackTrace();}
		return instructions;
	}
}
