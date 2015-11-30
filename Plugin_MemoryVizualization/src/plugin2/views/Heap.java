package plugin2.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.Value;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.model.ICDIExpression;
import org.eclipse.cdt.debug.core.cdi.model.ICDILocalVariableDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIStackFrame;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;
import org.eclipse.cdt.debug.core.cdi.model.ICDIValue;
import org.eclipse.cdt.debug.core.cdi.model.ICDIVariable;
import org.eclipse.cdt.debug.mi.core.cdi.Session;
import org.eclipse.cdt.debug.mi.core.cdi.model.Variable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.swt.SWT;


public class Heap extends ViewPart{
	private DebugEventListener jdiEventListener = null;
	private CDIEventListener cdiEventListener = null;
	private Session cdiDebugSession = null;
	private Tree treeOne;
	private Tree treeTwo;

	class RunnableForThread2 implements Runnable{
		public void run() {
			while (true) {
				try { Thread.sleep(1000); } catch (Exception e) { }
				Runnable task = () -> { VizualizateHeapJava(); VizualizateHeapCpp();};
				Display.getDefault().asyncExec(task);
			}			
		}
	}
		
	@Override
	public void createPartControl(Composite parent) {
		
		createTreeOne(parent);
		createTreeTwo(parent);

		jdiEventListener = new DebugEventListener();
		DebugPlugin.getDefault().addDebugEventListener(jdiEventListener);
		
		this.cdiEventListener		= new CDIEventListener();
		tryGetCdiSession();
		
		Runnable runnable = new RunnableForThread2();
		Thread Thread2 = new Thread(runnable);
		Thread2.start();	
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
	
	private void createTreeOne(Composite parent){
		treeOne = new Tree(parent, SWT.MIN);
		treeOne.setHeaderVisible(true);
		treeOne.setLinesVisible(true);		
		treeOne.setVisible(true);

		
		TreeColumn columnName = new TreeColumn(treeOne, SWT.LEFT);
		columnName.setText("classes");
		columnName.setWidth(300);
	}
	
	private void createTreeTwo(Composite parent){
		treeTwo = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		treeTwo.setHeaderVisible(true);
		treeTwo.setLinesVisible(true);		
		treeTwo.setVisible(true);
		
		TreeColumn columnName = new TreeColumn(treeTwo, SWT.LEFT);
		columnName.setText("instances");
		columnName.setWidth(300);		
	}
	
	private void VizualizateHeapCpp(){
		tryGetCdiSession();
		if (cdiEventListener ==null){return;}
		if (!cdiEventListener.isItUpdatedThread()){return;}
		
		ICDIThread CurrentThread =  cdiEventListener.getCurrentThread();	
		ICDIStackFrame[] Frames = CDIEventListener.getStackFrames(CurrentThread);		
		
		for (TreeItem item : treeOne.getItems()){item.dispose();}
		for (TreeItem item : treeTwo.getItems()){item.dispose();}
		
		for (int i = 0; i< Frames.length; i++){

					
			ICDIStackFrame frame = Frames[i];
			String FrameName 	= frame.getLocator().getFunction();
			String Location		= frame.getLocator().getFile();

			TreeItem item = new TreeItem(treeOne, SWT.LEFT);
			item.setText(0, Location + " " + FrameName);	
				
			
			try {
				ICDIExpression[] expres = frame.getTarget().getExpressions();
				System.out.println("expressions count = " + expres.length);
				for (ICDIExpression ex : expres){
					System.out.println("  text = " + ex.getExpressionText());
					ICDIValue value = ex.getValue(frame);
					String s = CDIEventListener.getValueString(value);
					System.out.println("  valuestring = " + s);
					
				}
			} catch (CDIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			TreeItem subItem;
			
			ICDIValue registerInstructionPointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rip");
			String registerInstructionPointerString = CDIEventListener.getValueString(registerInstructionPointer);		
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "InstructionPointer : " + registerInstructionPointerString);			
			
			ICDIValue registerBasePointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rbp");
			String registerBasePointerString = CDIEventListener.getValueString(registerBasePointer);		
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "BasePointer : " + registerBasePointerString);	
	
			
			
			ICDIValue registerStackPointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rsp");
			String registerStackPointerString = CDIEventListener.getValueString(registerStackPointer);		
		
			
			
			
			ArrayList<ICDIVariable> varlist = new ArrayList<ICDIVariable>();
			ICDILocalVariableDescriptor[] descriptors = CDIEventListener.GetStackFrameLocalVariableDescriptors(frame);
			ICDIVariable[] variables = new ICDIVariable[descriptors.length];
			for (int k = 0; k<descriptors.length; k++){variables[k] = CDIEventListener.getLocalVariable(descriptors[k]);}
			fillVarList(varlist, variables);
			
			for (ICDIVariable cdiVariable : varlist){
				Variable variable = (Variable)cdiVariable;
					
				ICDIValue value 			= CDIEventListener.getLocalVariableValue(variable);
				String valuestring			= CDIEventListener.getValueString(value);
				String QualifiedName		= CDIEventListener.getQualifiedName(variable);
				String hexAddress = CDIEventListener.getHexAddress(variable);
				
				if (hexAddress.compareTo(registerStackPointerString) >=0  && hexAddress.compareTo(registerBasePointerString) <=0 ){
					subItem = new TreeItem(item, SWT.LEFT);
					subItem.setText(0, hexAddress + " : " + valuestring + " (" + QualifiedName + ")");	
				}else{
					subItem = new TreeItem(treeTwo, SWT.LEFT);
					subItem.setText(0, hexAddress + " : " + valuestring + " (" + QualifiedName + ")");	
				}
					
			}
	
			ICDIValue eax = CDIEventListener.findRegisterValueByQualifiedName(frame, "$eax");
			String eaxString = CDIEventListener.getValueString(eax);		
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "Return value : " + eaxString);				
			
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "StackPointer : " + registerStackPointerString);				

		}
		

	}	
	
	private void fillVarList(ArrayList<ICDIVariable> varlist, ICDIVariable[] variables){		
		for (ICDIVariable variable : variables){
			varlist.add(variable);
			ICDIValue value 			= CDIEventListener.getLocalVariableValue(variable);
			ICDIVariable[] subvariables = CDIEventListener.getLocalVariablesFromValue(value);
			fillVarList(varlist, subvariables);
		}
	}
	
	private void VizualizateHeapJava(){
		if(jdiEventListener == null){return;}		
		if (!jdiEventListener.isItUpdatedThread()){return;}
		
		IJavaThread CurrentThread =  jdiEventListener.getCurrentThread();	
		IStackFrame topFrame = DebugEventListener.getTopStackFrame(CurrentThread);		
		
		VirtualMachine JVM = DebugEventListener.getJVM(topFrame);	
		if (JVM == null){return;}
	
		for (TreeItem item : treeOne.getItems()){item.dispose();}
		for (TreeItem item : treeTwo.getItems()){item.dispose();}
		
		List<ReferenceType> AllMyClasses = DebugEventListener.getAllMyClasses(JVM);
		AllMyClasses = DebugEventListener.sortByHashCode(AllMyClasses);
		for(ReferenceType Class : AllMyClasses){
			VizualizateClass(Class);
			List<ObjectReference> Instances = Class.instances(0);
			for (ObjectReference instance : Instances){VizualizateClassInstance(instance);}	
		}

	}
	
	private void VizualizateClass(ReferenceType Class){

		int hashCode = Class.hashCode();
		
		TreeItem item = new TreeItem(treeOne, SWT.LEFT);
		item.setText(0, Class.toString() + " : @" + hashCode);	
		
		TreeItem subItem = new TreeItem(item, SWT.LEFT);
		subItem.setText(0, "this : @" + hashCode);
					
		List<Field> fields = Class.fields();
		for (Field field : fields){
			if (!field.isStatic()){continue;}
			String stringValue = "";
			Value value = Class.getValue(field);
			if (value != null){stringValue = value.toString();}
			if (stringValue.contains("id")){stringValue = "@"+Class.getValue(field).hashCode();}
			if (field.typeName() != null && value !=null && field.typeName().equals("java.lang.String") ){stringValue = "@"+Class.getValue(field).hashCode();}			
			
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, field.typeName() + " " + field.toString() + " : " + stringValue);		
		}	
	}
	
	private void VizualizateClassInstance(ObjectReference Instance){

		TreeItem item = new TreeItem(treeTwo, SWT.LEFT);
		item.setText(0, Instance.toString());

		List<ReferenceType> ParentClasses = new ArrayList<ReferenceType>();
		List<Method> methods = Instance.referenceType().allMethods();
		for (Method method : methods){
			ReferenceType Type =  method.declaringType();
			boolean isExist = false;
			for (ReferenceType ParentClass : ParentClasses){if (ParentClass.equals(Type)){isExist = true;}}
			if (!isExist){ParentClasses.add(Type);}
		}		
		ParentClasses = DebugEventListener.sortByHashCode(ParentClasses);
		
		//the first item placed to the end 
		ReferenceType temp = ParentClasses.get(0);
		ParentClasses.remove(0);
		ParentClasses.add(temp);
		
		for (ReferenceType ParentClass : ParentClasses){
			
			TreeItem subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, ParentClass.toString());
			
			TreeItem subsubItem = new TreeItem(subItem, SWT.LEFT);
			if (Instance.type() != null){subsubItem.setText(0, Instance.type().name());}
			subsubItem.setText(0, "this : @" + Instance.hashCode());

			
			subsubItem = new TreeItem(subItem, SWT.LEFT);
			subsubItem.setText(0, "class : @" + ParentClass.hashCode());
		

			List<Field> Parentfields = ParentClass.fields();
			for (Field field : Parentfields){
				if (field.isStatic()){continue;}
					String valueString = "";
					Value value = Instance.getValue(field);
					if(value == null){valueString = "null";}else{valueString = value.toString();}
					if (valueString.contains("id")){valueString = "@"+Instance.getValue(field).hashCode();}
					if (field.typeName() != null && value !=null && field.typeName().equals("java.lang.String")){valueString = "@"+Instance.getValue(field).hashCode();}	
				
					subsubItem = new TreeItem(subItem, SWT.LEFT);
					subsubItem.setText(0, ""+field.typeName() + " " + field + " : " + valueString);			
			}	
		}		
	}

}
