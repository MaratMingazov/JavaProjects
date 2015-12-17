




package plugin2.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.browser.*;
import org.eclipse.ui.part.*;


import org.eclipse.swt.SWT;


import java.util.ArrayList;

import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.cdi.ICDISession;
import org.eclipse.cdt.debug.core.cdi.model.ICDILocalVariableDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegister;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegisterDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegisterGroup;
import org.eclipse.cdt.debug.core.cdi.model.ICDIStackFrame;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;
import org.eclipse.cdt.debug.core.cdi.model.ICDIValue;
import org.eclipse.cdt.debug.core.cdi.model.ICDIVariable;
import org.eclipse.cdt.debug.mi.core.cdi.Session;
import org.eclipse.cdt.debug.mi.core.cdi.model.Variable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaThread;




public class Stack extends ViewPart {


	private CDIEventListener cdiEventListener = null;
	private Session cdiDebugSession = null;
	private Tree tree = null;
	private Browser browser;

	class RunnableForThread2 implements Runnable{
		public void run() {
			while (true) {
				try { Thread.sleep(1000); } catch (Exception e) { }
				Runnable task = () -> { VizualizateStack—pp();};
				Display.getDefault().asyncExec(task);
			}			
		}
	}
	
	public void createPartControl(Composite parent) {
		this.cdiEventListener    = new CDIEventListener();
	    tryGetCdiSession();
	    
	    Runnable runnable = new RunnableForThread2();
	    Thread Thread2 = new Thread(runnable);
	    Thread2.start();
		
		browser = new Browser(parent, SWT.NONE);
		browser.setText("<html><body>Here will appear stack-related debug information</body></html>");
	}
	
	

	@Override
	public void setFocus() {
	}	
	
	private void tryGetCdiSession(){	
		Session session = CDIDebugger.getSession();
		if (session == null){return;}
		if (session.equals(this.cdiDebugSession)){return;}
		else{
			this.cdiDebugSession = session;
			if (this.cdiDebugSession != null){this.cdiDebugSession.getEventManager().addEventListener(this.cdiEventListener);}	
		}
	}
	
	private void VizualizateStack—pp(){		
		tryGetCdiSession();
		if (cdiEventListener == null){return;}
		if (!cdiEventListener.isItUpdatedThread()){return;}
		
		
		String tabContent = VisualizationUtils.composeStackTab(cdiEventListener.getActivationRecords());
		
		browser.setText(tabContent);
		

		
//		ICDIThread CurrentThread =  cdiEventListener.getCurrentThread();	
//		ICDIStackFrame[] Frames = CDIEventListener.getStackFrames(CurrentThread);		
//		
//		for (TreeItem item : tree.getItems()){item.dispose();}
//	
//		for (int i = 0; i< Frames.length; i++){
//
//			
//			ICDIStackFrame frame = Frames[i];
//			String FrameName 	= frame.getLocator().getFunction();
//			String Location		= frame.getLocator().getFile();
//
//			TreeItem item = new TreeItem(tree, SWT.LEFT);
//			item.setText(0, Location + " " + FrameName);			
//			
//			TreeItem subItem;
//			
//			ICDIValue registerInstructionPointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rip");
//			String registerInstructionPointerString = CDIEventListener.getValueString(registerInstructionPointer);		
//			subItem = new TreeItem(item, SWT.LEFT);
//			subItem.setText(0, "InstructionPointer : " + registerInstructionPointerString);			
//			
//			ICDIValue registerBasePointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rbp");
//			String registerBasePointerString = CDIEventListener.getValueString(registerBasePointer);		
//			subItem = new TreeItem(item, SWT.LEFT);
//			subItem.setText(0, "BasePointer : " + registerBasePointerString);	
//	
//			
//			
//			ICDIValue registerStackPointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rsp");
//			String registerStackPointerString = CDIEventListener.getValueString(registerStackPointer);		
//		
//			ArrayList<ICDIVariable> varlist = new ArrayList<ICDIVariable>();
//			ICDILocalVariableDescriptor[] descriptors = CDIEventListener.GetStackFrameLocalVariableDescriptors(frame);
//			ICDIVariable[] variables = new ICDIVariable[descriptors.length];
//			for (int k = 0; k<descriptors.length; k++){variables[k] = CDIEventListener.getLocalVariable(descriptors[k]);}
//			fillVarList(varlist, variables);
//			
//			for (ICDIVariable cdiVariable : varlist){
//				Variable variable = (Variable)cdiVariable;
//					
//				ICDIValue value 			= CDIEventListener.getLocalVariableValue(variable);
//				String valuestring			= CDIEventListener.getValueString(value);
//				String QualifiedName		= CDIEventListener.getQualifiedName(variable);
//				String hexAddress = CDIEventListener.getHexAddress(variable);
//				
//				if (hexAddress.compareTo(registerStackPointerString) >=0  && hexAddress.compareTo(registerBasePointerString) <=0 ){
//					subItem = new TreeItem(item, SWT.LEFT);
//					subItem.setText(0, hexAddress + " : " + valuestring + " (" + QualifiedName + ")");	
//				}		
//			}
//	
//			ICDIValue eax = CDIEventListener.findRegisterValueByQualifiedName(frame, "$eax");
//			String eaxString = CDIEventListener.getValueString(eax);		
//			subItem = new TreeItem(item, SWT.LEFT);
//			subItem.setText(0, "Return value : " + eaxString);				
//			
//			subItem = new TreeItem(item, SWT.LEFT);
//			subItem.setText(0, "StackPointer : " + registerStackPointerString);				
//		}
	}	
	
	private boolean isExist(ArrayList<ICDIVariable> varlist, ICDIVariable variable){
		Variable v = (Variable)variable;
		String hexAddress1 = CDIEventListener.getHexAddress(v);
		for (ICDIVariable var : varlist){
			Variable variab = (Variable)var;
			String hexAddress2 = CDIEventListener.getHexAddress(variab);
			if (hexAddress1.equals(hexAddress2)){return true;}	
		}
		return false;
	}
	
	private void fillVarList(ArrayList<ICDIVariable> varlist, ICDIVariable[] variables){		
		for (ICDIVariable variable : variables){
			if (!isExist(varlist, variable)){varlist.add(variable);}
			ICDIValue value 			= CDIEventListener.getLocalVariableValue(variable);
			ICDIVariable[] subvariables = CDIEventListener.getLocalVariablesFromValue(value);
			fillVarList(varlist, subvariables);
		}
	}
	
	private void vizualizateCdiVariables(TreeItem item, ICDIVariable[] variables){
		if (variables == null){return;}
		for (ICDIVariable lvariable : variables){
			Variable variable = (Variable)lvariable;	
			
			ICDIValue value 			= CDIEventListener.getLocalVariableValue(lvariable);
			String valuestring			= CDIEventListener.getValueString(value);
			String typename				= CDIEventListener.getLocalVariableTypeName(lvariable);
			String QualifiedName		= CDIEventListener.getQualifiedName(lvariable);
			TreeItem subItem = new TreeItem(item, SWT.LEFT);
			String hexAddress = CDIEventListener.getHexAddress(variable);
			
			subItem.setText(0,typename + " " + QualifiedName + " : " + valuestring + " address: " + hexAddress);
			vizualizateCdiVariables(subItem, CDIEventListener.getLocalVariablesFromValue(value));		
		}
	}

}