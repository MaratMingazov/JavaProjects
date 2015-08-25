package plugin2.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.swt.SWT;

public class Heap extends ViewPart{
	//public static TableViewer viewer;
	private VirtualMachine JVM;
	//private DebugBreakPointListener _DebugBreakPointListener = null;
	private DebugEventSetListener debugEventSetListener = null;
	private Tree treeClass;
	private Tree treeInstance;

	class SecondaryThread1 implements Runnable{
		public void run() {
			while (true) {
				try { Thread.sleep(100); } catch (Exception e) { }
				SecondaryThread2 SThread2 = new SecondaryThread2();
				Thread myThread2 = new Thread(SThread2);	        	 
				Display.getDefault().asyncExec(myThread2);
			}			
		}
	}
	
	class SecondaryThread2 implements Runnable{
		public void run() {
			VizualizateHeap();
		}
	}
		
	@Override
	public void createPartControl(Composite parent) {
		
		treeClass = new Tree(parent, SWT.MIN);
		treeClass.setHeaderVisible(true);
		treeClass.setLinesVisible(true);		
		treeClass.setVisible(true);

		
		TreeColumn columnName = new TreeColumn(treeClass, SWT.LEFT);
		columnName.setText("classes");
		columnName.setWidth(300);
	
		//columnName = new TreeColumn(treeClass, SWT.LEFT);
		//columnName.setText("name");
		//columnName.setWidth(150);
		
		//columnName = new TreeColumn(treeClass, SWT.LEFT);
		//columnName.setText("value");
		//columnName.setWidth(150);
		
		treeInstance = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		treeInstance.setHeaderVisible(true);
		treeInstance.setLinesVisible(true);		
		treeInstance.setVisible(true);
		
		columnName = new TreeColumn(treeInstance, SWT.LEFT);
		columnName.setText("instances");
		columnName.setWidth(300);
		
		//columnName = new TreeColumn(treeInstance, SWT.LEFT);
		//columnName.setText("name");
		//columnName.setWidth(150);	
		
		//columnName = new TreeColumn(treeInstance, SWT.LEFT);
		//columnName.setText("value");
		//columnName.setWidth(150);		
		
		debugEventSetListener = DebugEventSetListener.GetInstance();
		
		SecondaryThread1 SThread1 = new SecondaryThread1();
		Thread myThread1 = new Thread(SThread1);
		myThread1.start();		
	}

	@Override
	public void setFocus() {		
	}
	
	public void VizualizateHeap(){
				
		if (debugEventSetListener == null){return;}
		if (!debugEventSetListener.isItIsNewBreakpointHit_Heap()){return;}
		 
		IJavaThread CurrentThread =  debugEventSetListener.getCurrentThread_Heap();		
		IStackFrame topFrame = debugEventSetListener.GetTopStackFrame(CurrentThread);		
		
		JVM = GetJVM(topFrame);				
		if (JVM == null){return;}
	
		for (TreeItem item : treeClass.getItems()){item.dispose();}
		for (TreeItem item : treeInstance.getItems()){item.dispose();}
		
		List<ReferenceType> AllMyClasses = GetAllMyClasses(JVM);
		for(ReferenceType Class : AllMyClasses){
			VizualizateClass(Class);
			List<ObjectReference> Instances = Class.instances(0);
			for (ObjectReference instance : Instances){VizualizateClassInstance(instance);}	
		}
		
		JVM = null;	
	}
	
	private void VizualizateClass(ReferenceType Class){

		int hashCode = Class.hashCode();
		
		TreeItem item = new TreeItem(treeClass, SWT.LEFT);
		item.setText(0, Class.toString() + " : @" + hashCode);	
		
		TreeItem subItem = new TreeItem(item, SWT.LEFT);
		subItem.setText(0, "this : @" + hashCode);
		//subItem.setText(2, "@"+hashCode);
					
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

		TreeItem item = new TreeItem(treeInstance, SWT.LEFT);
		item.setText(0, Instance.toString());

		List<ReferenceType> ParentClasses = new ArrayList<ReferenceType>();
		List<Method> methods = Instance.referenceType().allMethods();
		for (Method method : methods){
			ReferenceType Type =  method.declaringType();
			boolean isExist = false;
			for (ReferenceType ParentClass : ParentClasses){if (ParentClass.equals(Type)){isExist = true;}}
			if (!isExist){ParentClasses.add(Type);}
		}		
		ParentClasses = SortByHashCode(ParentClasses);
		
		for (ReferenceType ParentClass : ParentClasses){
			
			TreeItem subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, ParentClass.toString());
			
			TreeItem subsubItem = new TreeItem(subItem, SWT.LEFT);
			if (Instance.type() != null){subsubItem.setText(0, Instance.type().name());}
			subsubItem.setText(0, "this : @" + Instance.hashCode());
			//subsubItem.setText(2, "@" + Instance.hashCode());
			
			subsubItem = new TreeItem(subItem, SWT.LEFT);
			subsubItem.setText(0, "class : @" + ParentClass.hashCode());
			//subsubItem.setText(2, "@" + ParentClass.hashCode());			

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
					//subsubItem.setText(1, ""+field);
					//subsubItem.setText(2, valueString);				
			}	
		}		
	}
	
	private List<ReferenceType> SortByHashCode (List<ReferenceType>  ParentClasses){
		if (ParentClasses==null){return null;}
		if (ParentClasses.size()<2){return ParentClasses;}
		
		for (int i = 0; i < ParentClasses.size(); i++){
			
			for (int k = i; k<ParentClasses.size(); k++){
				if (ParentClasses.get(k).hashCode()<ParentClasses.get(i).hashCode()){
					ReferenceType temp = ParentClasses.get(i);
					ParentClasses.set(i, ParentClasses.get(k));
					ParentClasses.set(k, temp);					
				}
			}
		}
		ReferenceType temp = ParentClasses.get(0);
		ParentClasses.remove(0);
		ParentClasses.add(temp);
		return ParentClasses;
	}
	
	public void VizualizateClass(ReferenceType Class, Table table, String ColumnName, int ColumnCounter){
		int CurrentItemNumber = 0;
		TableColumn column = new TableColumn (table, SWT.LEFT);
		column.setText (ColumnName);
		column.setWidth(150);
		
		TableItem item = null;

		if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		CurrentItemNumber++;
		item.setText(ColumnCounter, "" + Class);		
		
		int HashCode = Class.hashCode();
		if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		CurrentItemNumber++;
		item.setText(ColumnCounter, "this : @" + HashCode);
		
		//if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		//CurrentItemNumber++;
		//item.setText(ColumnCounter, "Fields : ");		
		List<Field> fields = Class.fields();
		for (Field field : fields){
			//String fieldTypeName = field.typeName();
			//String fieldValue = "";
			//if (field.isStatic()){Value value = Class.getValue(field);if (value == null){fieldValue = "null";}else{fieldValue = value.toString();}}
					
			//if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			//CurrentItemNumber++;
			//item.setText(ColumnCounter, "  " + fieldTypeName + " " + field + " " + fieldValue);	
			
			if (!field.isStatic()){continue;}
			if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			CurrentItemNumber++;
			item.setText(ColumnCounter, ""+ field + " : " + Class.getValue(field));			
		}	
		//if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		//CurrentItemNumber++;
		//item.setText(ColumnCounter, "Methods : ");		
		
		//List<Method> methods = Class.methods();		
		//for (Method method : methods){
		//	String ReturnTypeName = method.returnTypeName();
			
		//	if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		//	CurrentItemNumber++;
		//	item.setText(ColumnCounter, "  " + method + " () : " + ReturnTypeName);			
		//}
		
		
		
		/*String TableElement;
		TableElement = "";
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
			

		String ID = Long.toString(Class.classObject().uniqueID());
		TableElement = "ID = " + ID;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		String ClassName = Class.toString();
		TableElement = "Class = " + ClassName;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);		

		String ClasshashCode = Integer.toString(Class.hashCode());
		TableElement = "hashCode = " + ClasshashCode;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);		
		
		TableElement = "Fields:";
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);	
		
		List<Field> fields = Class.fields();
		for (int i = 0; i<fields.size(); i++){
			Field field = fields.get(i);
			String name = field.name();
			TableElement = "  " + name;
			Heap.viewer.add(TableElement);
			Heap.TableElements.add(TableElement);
		}

		TableElement = "Methods:";
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);	
		
		List<Method> methods = Class.methods();		
		for (int i = 0; i<methods.size(); i++){
			Method method = methods.get(i);
			String MethodName = method.name();
			TableElement = "  " + MethodName + "()";
			Heap.viewer.add(TableElement);
			Heap.TableElements.add(TableElement);				
		}	*/	
	}
	
	public void VizualizateClassInstance(ObjectReference Instance, Table table, String ColumnName, int ColumnCounter){

		int CurrentItemNumber = 0;
		
		TableColumn column = new TableColumn (table, SWT.LEFT);
		column.setText (ColumnName);
		column.setWidth(150);
		
		TableItem item = null;
		
		if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		CurrentItemNumber++;
		item.setText(ColumnCounter, "" + Instance);		
		
		if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		CurrentItemNumber++;
		item.setText(ColumnCounter, "this : @" + Instance.hashCode());
	
		if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		CurrentItemNumber++;
		item.setText(ColumnCounter, "class : @" + Instance.referenceType().hashCode());				
		
		if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
		CurrentItemNumber++;
		item.setText(ColumnCounter, "super : ");			

		List<Field> fields = Instance.referenceType().fields();
		for (Field field : fields){
			if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			CurrentItemNumber++;
			item.setText(ColumnCounter, field + " : " + Instance.getValue(field));				
		}

		List<ReferenceType> ParentClasses = new ArrayList<ReferenceType>();
		List<Method> methods = Instance.referenceType().allMethods();
		for (Method method : methods){
			ReferenceType Type =  method.declaringType();
			if (Type.equals(Instance.referenceType())){continue;}
			boolean isExist = false;
			for (ReferenceType ParentClass : ParentClasses){if (ParentClass.equals(Type)){isExist = true;}}
			if (!isExist){ParentClasses.add(Type);}
		}

		for (ReferenceType ParentClass : ParentClasses){
	
			if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			CurrentItemNumber++;
			item.setText(ColumnCounter, "");	
			
			if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			CurrentItemNumber++;
			item.setText(ColumnCounter, "this :");			
			
			String HashCode = Integer.toString(ParentClass.hashCode());
			if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			CurrentItemNumber++;
			item.setText(ColumnCounter, "class : @" + HashCode);
			
			if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
			CurrentItemNumber++;
			item.setText(ColumnCounter, "super :");	
			
			List<Field> Parentfields = ParentClass.fields();
			for (Field field : Parentfields){
				if (field.isStatic()){continue;}
				if (table.getItemCount() > CurrentItemNumber){item = table.getItem(CurrentItemNumber);}else{item = new TableItem(table, SWT.LEFT);}
				CurrentItemNumber++;
				item.setText(ColumnCounter, field + " : " + Instance.getValue(field));				
			}	
		}
		
	
		
		
		/*String TableElement;
		TableElement = "";
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		String ID = Long.toString(Instance.uniqueID());
		TableElement = "ID = " + ID;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		
		TableElement = "Reference type = " + Instance.referenceType().toString();
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		TableElement = "Class object id = " + Instance.getClass().getTypeParameters();
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		

		String InstanceName = Instance.toString();
		TableElement = "Instance = " + InstanceName;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		String hashCode = Integer.toString(Instance.hashCode());
		TableElement = "Instance hashCode = " + hashCode;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);		
		
		String classID = Long.toString(Instance.referenceType().classObject().uniqueID());
		TableElement = "class : ID = " + classID;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);	

		String ClasshashCode = Integer.toString(Instance.referenceType().classObject().hashCode());
		TableElement = "Class hashCode = " + ClasshashCode;
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);
		
		TableElement = "Fields:";
		Heap.viewer.add(TableElement);
		Heap.TableElements.add(TableElement);		
		
		List<Field> fields = Instance.referenceType().allFields();		
		for (int i = 0; i<fields.size(); i++){

			Field field = fields.get(i);
			String name = field.name();		
			Value value = Instance.getValue(field);
			String evalue = "";
			if (value != null){evalue = value.toString();}
			TableElement = "  " + name + " : " + evalue;
			Heap.viewer.add(TableElement);
			Heap.TableElements.add(TableElement);	
		}*/
	}
	
	public List<ReferenceType> GetAllMyClasses(VirtualMachine jvm){
		List<ReferenceType> AllClasses = JVM.allClasses();
		List<ReferenceType> AllMyClasses = new  ArrayList<ReferenceType>();
		for (ReferenceType Class : AllClasses){
			  String className = Class.name();
			  boolean print = true;
			  if (className.contains("java.")){print = false;}
			  if (className.contains("sun.")){print = false;}
			  if (className.contains("short[]")){print = false;}
			  if (className.contains("long[]")){print = false;}
			  if (className.contains("boolean[]")){print = false;}
			  if (className.contains("byte[]")){print = false;}
			  if (className.contains("byte[][]")){print = false;}
			  if (className.contains("char[]")){print = false;}
			  if (className.contains("double[]")){print = false;}
			  if (className.contains("float[]")){print = false;}
			  if (className.contains("int[]")){print = false;}	
			  
			 // if (className.contains("java.lang.Integer")){print = true;}
			  //if (className.contains("java.lang.String")){print = true;}
			  if (className.contains("java.lang.Integer[]")){print = false;}
			  if (className.contains("java.lang.IntegerC")){print = false;}
			  if (className.contains("java.lang.Integer&")){print = false;}
			  if (className.contains("java.lang.StringB")){print = false;}
			  if (className.contains("java.lang.String[")){print = false;}
			  if (className.contains("java.lang.StringC")){print = false;}
			  if (className.contains("java.lang.Integer$")){print = false;}
			  if (className.contains("java.lang.String$")){print = false;}
			  if (print){AllMyClasses.add(Class);}		
			  
		}	
		return AllMyClasses;
  }

	private VirtualMachine GetJVM(IStackFrame frame){
		if (JVM != null){return JVM;}
		ILaunch launch = frame.getLaunch();	
		Object[] LaucnChildren = launch.getChildren();
		for (Object child : LaucnChildren){
			if (child instanceof org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget){
				JDIDebugTarget DebugTarget = (JDIDebugTarget) child;
				JVM = DebugTarget.getVM();
				break;
			}						
		}			
		return JVM;
	}
	
}
