




package plugin2.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.*;

import org.eclipse.swt.SWT;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaThread;


//import org.eclipse.jdi.*;



public class Stack extends ViewPart {

	//public static final String ID = "plugin2.views.Stack";
	//private TableViewer viewer;
	private DebugEventSetListener debugEventSetListener = null;
	private Tree tree;


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
			VizualizateStack();
		}
	}
	
	public void createPartControl(Composite parent) {
		
		tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);		
		tree.setVisible(true);
		
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("frames:");
		columnName.setWidth(300);
		
		//columnName = new TreeColumn(tree, SWT.LEFT);
		//columnName.setText("name");
		//columnName.setWidth(150);
		
		//TreeColumn columnValue = new TreeColumn(tree, SWT.LEFT);
		//columnValue.setText("value");
		//columnValue.setWidth(150);
	
		debugEventSetListener = DebugEventSetListener.GetInstance();
		
		SecondaryThread1 SThread1 = new SecondaryThread1();
		Thread myThread1 = new Thread(SThread1);
		myThread1.start();
		
	}

	@Override
	public void setFocus() {
	}	
	
	public void VizualizateStack(){		
		if (debugEventSetListener == null){return;}
		if (!debugEventSetListener.isItIsNewBreakpointHit_Stack()){return;}
		 
		IJavaThread CurrentThread =  debugEventSetListener.getCurrentThread_Stack();	
		IStackFrame[] Frames = debugEventSetListener.GetStackFrames(CurrentThread);		
		
		if (Frames == null){return;}
		for (TreeItem item : tree.getItems()){item.dispose();}
	
		for (int i = 0; i< Frames.length; i++){

			IStackFrame frame = Frames[i];
			String FrameName = debugEventSetListener.GetStackFrameName(frame);
			
			TreeItem item = new TreeItem(tree, SWT.LEFT);
			item.setText(0, FrameName);	
			

			
			TreeItem subItem;
			
			subItem = new TreeItem(item, SWT.LEFT);
			int lineNumber = debugEventSetListener.GetStackFrameLineNumber(frame);
			subItem.setText(0, "line number : " + lineNumber);
		
			IVariable[] variables = GetStackFrameVariables(frame);
			if (variables != null){
				for (IVariable variable : variables){
					String valueString = "";
					String referenceTypeName = "";
					try {valueString =  variable.getValue().toString();} catch (DebugException e) {}
					if (valueString.contains("id")){try {valueString = "@"+variable.getValue().hashCode();} catch (DebugException e) {}}
					try {if (variable.getReferenceTypeName().equals("java.lang.String") ){valueString = "@"+variable.getValue().hashCode();}} catch (DebugException e) {}
					try {referenceTypeName = variable.getReferenceTypeName();} catch (DebugException e) {}
					
					subItem = new TreeItem(item, SWT.LEFT);
					try {subItem.setText(0, variable.getReferenceTypeName());} catch (DebugException e) {}
					subItem.setText(0,referenceTypeName + " " + variable.toString() + " : " + valueString);							
				}
			}
		}
	}	
	
	
	private IVariable[] GetStackFrameVariables(IStackFrame frame){
		IVariable[] vars = null;
		try {vars = frame.getVariables();} catch (DebugException e) {}
		return vars;
	}	
		
	public void unUsable(){
		//ICommandService commandService = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
		//ExecutionListener ExeListener = new ExecutionListener();
		//commandService.addExecutionListener(ExeListener);		
	}

}